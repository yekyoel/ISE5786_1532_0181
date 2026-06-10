package geometries.impl;

import geometries.api.BoundingBox;
import geometries.api.Intersectable;
import primitives.Point;
import primitives.Ray;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class RegularGrid extends Intersectable {
    private final BoundingBox sceneBox;
    private int Nx, Ny, Nz;
    private List<Intersectable>[][][] grid;
    private final List<Intersectable> infiniteGeometries = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public RegularGrid(Geometries sceneGeometries) {
        List<Intersectable> finiteGeometries = new ArrayList<>();
        extractGeometries(sceneGeometries, finiteGeometries);

        double minX = Double.POSITIVE_INFINITY, minY = Double.POSITIVE_INFINITY, minZ = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY, maxZ = Double.NEGATIVE_INFINITY;
        boolean hasFinite = false;

        for (Intersectable geo : finiteGeometries) {
            BoundingBox b = geo.getBoundingBox();
            if (b != null) {
                hasFinite = true;
                if (b.min.getX() < minX) minX = b.min.getX();
                if (b.min.getY() < minY) minY = b.min.getY();
                if (b.min.getZ() < minZ) minZ = b.min.getZ();
                if (b.max.getX() > maxX) maxX = b.max.getX();
                if (b.max.getY() > maxY) maxY = b.max.getY();
                if (b.max.getZ() > maxZ) maxZ = b.max.getZ();
            }
        }

        if (!hasFinite) {
            this.sceneBox = null;
            return;
        }

        // Box padding to prevent floating point inaccuracies on boundaries
        this.sceneBox = new BoundingBox(
                new Point(minX - 0.1, minY - 0.1, minZ - 0.1),
                new Point(maxX + 0.1, maxY + 0.1, maxZ + 0.1)
        );

        int N = finiteGeometries.size();
        double wx = sceneBox.max.getX() - sceneBox.min.getX();
        double wy = sceneBox.max.getY() - sceneBox.min.getY();
        double wz = sceneBox.max.getZ() - sceneBox.min.getZ();
        double volume = wx * wy * wz;

        // Cube root multiplier
        double s = Math.pow(2.0 * N / volume, 1.0 / 3.0);

        this.Nx = Math.max(1, (int) (wx * s));
        this.Ny = Math.max(1, (int) (wy * s));
        this.Nz = Math.max(1, (int) (wz * s));

        this.grid = new List[Nx][Ny][Nz];
        for (int x = 0; x < Nx; x++)
            for (int y = 0; y < Ny; y++)
                for (int z = 0; z < Nz; z++)
                    grid[x][y][z] = new ArrayList<>();

        for (Intersectable geo : finiteGeometries) {
            BoundingBox b = geo.getBoundingBox();
            if (b == null) continue;

            int minXIdx = getVoxelCoord(b.min.getX(), sceneBox.min.getX(), sceneBox.max.getX(), Nx);
            int maxXIdx = getVoxelCoord(b.max.getX(), sceneBox.min.getX(), sceneBox.max.getX(), Nx);
            int minYIdx = getVoxelCoord(b.min.getY(), sceneBox.min.getY(), sceneBox.max.getY(), Ny);
            int maxYIdx = getVoxelCoord(b.max.getY(), sceneBox.min.getY(), sceneBox.max.getY(), Ny);
            int minZIdx = getVoxelCoord(b.min.getZ(), sceneBox.min.getZ(), sceneBox.max.getZ(), Nz);
            int maxZIdx = getVoxelCoord(b.max.getZ(), sceneBox.min.getZ(), sceneBox.max.getZ(), Nz);

            for (int x = minXIdx; x <= maxXIdx; x++)
                for (int y = minYIdx; y <= maxYIdx; y++)
                    for (int z = minZIdx; z <= maxZIdx; z++)
                        grid[x][y][z].add(geo);
        }
    }

    private void extractGeometries(Intersectable current, List<Intersectable> target) {
        if (current instanceof Geometries composite) {
            for (Intersectable child : composite.getChildren()) {
                extractGeometries(child, target);
            }
        } else {
            if (current.getBoundingBox() == null) infiniteGeometries.add(current);
            else target.add(current);
        }
    }

    private int getVoxelCoord(double val, double min, double max, int res) {
        int cell = (int) (((val - min) / (max - min)) * res);
        return Math.max(0, Math.min(res - 1, cell));
    }

    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray, double maxDistance) {
        List<Intersection> infiniteIntersections = null;
        for (Intersectable infiniteGeo : infiniteGeometries) {
            var hits = infiniteGeo.calcIntersections(ray, maxDistance);
            if (hits != null) {
                if (infiniteIntersections == null) infiniteIntersections = new LinkedList<>();
                infiniteIntersections.addAll(hits);
            }
        }

        if (sceneBox == null) return infiniteIntersections;

        // 1. Check if the ray enters the grid at all
        double tEntry = sceneBox.intersect(ray, maxDistance);
        if (tEntry < 0) return infiniteIntersections; // Missed the grid completely

        // 2. Advance ray origin to the exact grid entry point (if it started outside)
        Point entryPoint = tEntry > 0 ? ray.getPoint(tEntry + 0.00001) : ray.origin();

        double startX = entryPoint.getX();
        double startY = entryPoint.getY();
        double startZ = entryPoint.getZ();

        double minBoundsX = sceneBox.min.getX();
        double minBoundsY = sceneBox.min.getY();
        double minBoundsZ = sceneBox.min.getZ();

        double maxBoundsX = sceneBox.max.getX();
        double maxBoundsY = sceneBox.max.getY();
        double maxBoundsZ = sceneBox.max.getZ();

        // Start coordinates within the array
        int ix = getVoxelCoord(startX, minBoundsX, maxBoundsX, Nx);
        int iy = getVoxelCoord(startY, minBoundsY, maxBoundsY, Ny);
        int iz = getVoxelCoord(startZ, minBoundsZ, maxBoundsZ, Nz);

        double cellSizeX = (maxBoundsX - minBoundsX) / Nx;
        double cellSizeY = (maxBoundsY - minBoundsY) / Ny;
        double cellSizeZ = (maxBoundsZ - minBoundsZ) / Nz;

        double originX = ray.origin().getX();
        double originY = ray.origin().getY();
        double originZ = ray.origin().getZ();

        double dirX = ray.direction().getX();
        double dirY = ray.direction().getY();
        double dirZ = ray.direction().getZ();

        int stepX = dirX > 0 ? 1 : (dirX < 0 ? -1 : 0);
        int stepY = dirY > 0 ? 1 : (dirY < 0 ? -1 : 0);
        int stepZ = dirZ > 0 ? 1 : (dirZ < 0 ? -1 : 0);

        // Safe Delta calculations
        double tDeltaX = stepX != 0 ? Math.abs(cellSizeX / dirX) : Double.POSITIVE_INFINITY;
        double tDeltaY = stepY != 0 ? Math.abs(cellSizeY / dirY) : Double.POSITIVE_INFINITY;
        double tDeltaZ = stepZ != 0 ? Math.abs(cellSizeZ / dirZ) : Double.POSITIVE_INFINITY;

        // Safe Max calculations
        double tMaxX = stepX != 0 ? (minBoundsX + (ix + (stepX > 0 ? 1 : 0)) * cellSizeX - originX) / dirX : Double.POSITIVE_INFINITY;
        double tMaxY = stepY != 0 ? (minBoundsY + (iy + (stepY > 0 ? 1 : 0)) * cellSizeY - originY) / dirY : Double.POSITIVE_INFINITY;
        double tMaxZ = stepZ != 0 ? (minBoundsZ + (iz + (stepZ > 0 ? 1 : 0)) * cellSizeZ - originZ) / dirZ : Double.POSITIVE_INFINITY;

        List<Intersection> finiteIntersections = null;

        // 3. The 3DDDA Traversal Loop
        while (ix >= 0 && ix < Nx && iy >= 0 && iy < Ny && iz >= 0 && iz < Nz) {
            List<Intersectable> voxelGeometries = grid[ix][iy][iz];
            double cellNextT = Math.min(tMaxX, Math.min(tMaxY, tMaxZ));

            if (!voxelGeometries.isEmpty()) {
                for (Intersectable geo : voxelGeometries) {
                    var hits = geo.calcIntersections(ray, maxDistance);
                    if (hits != null) {
                        if (finiteIntersections == null) finiteIntersections = new LinkedList<>();
                        finiteIntersections.addAll(hits);
                    }
                }

                // Safe Early Exit: Did we actually find a hit inside THIS cell boundary?
                if (finiteIntersections != null) {
                    boolean foundInsideVoxel = false;
                    for (Intersection hit : finiteIntersections) {
                        if (ray.origin().distance(hit.point) <= cellNextT + 0.0001) {
                            foundInsideVoxel = true;
                            break;
                        }
                    }
                    if (foundInsideVoxel) break;
                }
            }

            // 4. Advance to the next voxel
            if (tMaxX < tMaxY) {
                if (tMaxX < tMaxZ) {
                    ix += stepX;
                    tMaxX += tDeltaX;
                } else {
                    iz += stepZ;
                    tMaxZ += tDeltaZ;
                }
            } else {
                if (tMaxY < tMaxZ) {
                    iy += stepY;
                    tMaxY += tDeltaY;
                } else {
                    iz += stepZ;
                    tMaxZ += tDeltaZ;
                }
            }
        }

        if (finiteIntersections == null) return infiniteIntersections;
        if (infiniteIntersections == null) return finiteIntersections;
        finiteIntersections.addAll(infiniteIntersections);
        return finiteIntersections;
    }
}