package com.calia.util;

public class AngleUtils {
    public double getDistance(double x, double y, double ox, double oy) {
        double dx = ox - x;
        double dy = oy - y;
        return Math.sqrt(dx * dx + dy * dy);
    }
    public double getAngle(double x, double y, double ox, double oy) {
        double dx = ox - x; double dy = oy - y;
        return Math.atan2(dy, dx);
    }
}
