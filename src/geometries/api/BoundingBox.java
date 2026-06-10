package geometries.api;

import primitives.Point;
import primitives.Ray;
import primitives.Util;

/**
 * Axis-Aligned Bounding Box (AABB) for spatial partitioning acceleration.
 */
public class BoundingBox {
    public final Point min;
    public final Point max;

    public BoundingBox(Point min, Point max) {
        this.min = min;
        this.max = max;
    }

    /**
     * Slabs algorithm for fast ray-box intersection.
     *
     * @return The tMin (distance to entry point), or -1.0 if no valid intersection.
     */
    public double intersect(Ray ray, double maxDistance) {
        double dirX = ray.direction().getX();
        double dirY = ray.direction().getY();
        double dirZ = ray.direction().getZ();

        double origX = ray.origin().getX();
        double origY = ray.origin().getY();
        double origZ = ray.origin().getZ();

        double minX = min.getX(), minY = min.getY(), minZ = min.getZ();
        double maxX = max.getX(), maxY = max.getY(), maxZ = max.getZ();

        double tMin = 0;
        double tMax = maxDistance;

        // X axis
        if (Util.isZero(dirX)) {
            if (origX < minX || origX > maxX) return -1.0;
        } else {
            double t0 = (minX - origX) / dirX;
            double t1 = (maxX - origX) / dirX;
            if (t0 > t1) {
                double temp = t0;
                t0 = t1;
                t1 = temp;
            }
            tMin = Math.max(tMin, t0);
            tMax = Math.min(tMax, t1);
            if (tMin > tMax) return -1.0;
        }

        // Y axis
        if (Util.isZero(dirY)) {
            if (origY < minY || origY > maxY) return -1.0;
        } else {
            double t0 = (minY - origY) / dirY;
            double t1 = (maxY - origY) / dirY;
            if (t0 > t1) {
                double temp = t0;
                t0 = t1;
                t1 = temp;
            }
            tMin = Math.max(tMin, t0);
            tMax = Math.min(tMax, t1);
            if (tMin > tMax) return -1.0;
        }

        // Z axis
        if (Util.isZero(dirZ)) {
            if (origZ < minZ || origZ > maxZ) return -1.0;
        } else {
            double t0 = (minZ - origZ) / dirZ;
            double t1 = (maxZ - origZ) / dirZ;
            if (t0 > t1) {
                double temp = t0;
                t0 = t1;
                t1 = temp;
            }
            tMin = Math.max(tMin, t0);
            tMax = Math.min(tMax, t1);
            if (tMin > tMax) return -1.0;
        }

        return tMin;
    }
}