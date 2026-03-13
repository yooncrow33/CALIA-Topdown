package com.calia.object;

public final class Camera {
    double x,y = 0;
    public double getX() {return x;}
    public double getY() {return y;}
    public void move(double xValue, double yValue) {x += xValue; y += yValue; }
    public void setX(double xValue) {x = xValue; }
    public void setY(double yValue) {y = yValue; }

    public void follow(double targetX, double targetY, double lerp) {
        this.x += (targetX - this.x) * lerp;
        this.y += (targetY - this.y) * lerp;
    }
}
