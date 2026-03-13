package com.calia.object;

import com.calia.CaliaBase;

import java.awt.*;

public abstract class Entity {
    boolean isRemove = false;
    protected double x = 0;
    protected double y = 0;
    final Color hitBoxColor = new Color(130,225,255,155);
    double angle = Math.PI;
    boolean stop = false;
    boolean acceleration = false;
    protected int damageTimer = 0;

    protected double vx = 0; // x축 속도
    protected double vy = 0; // y축 속도

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    public void setAcceleration(boolean acceleration) {
        this.acceleration = acceleration;
    }

    final EntityConfig config;
    public Entity(double x,double y, EntityConfig entityConfig) {
        this.x = x;
        this.y = y;
        this.config = entityConfig;
    }

    public boolean isCollisionEnabled() {
        return config.isCollisionEnabled();
    }

    public abstract void render(Graphics g,double x,double y);
    public abstract void update(double dt);

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public double getAngle() {
        return angle;
    }

    public void addVx(double vx) {
        this.vx += vx;
    }

    public void addVy(double vy) {
        this.vy += vy;
    }

    public double getVx() {
        return vx;
    }

    public double getVy() {
        return vy;
    }

    public EntityConfig getConfig() {
        return config;
    }

    public void EntityUpdate(double delta) {
        double dt = delta / (16.0 / 1000.0);

        if (acceleration) {
            vx += Math.cos(angle) * config.getAcceleration() * dt;
            vy += Math.sin(angle) * config.getAcceleration() * dt;
        }

        if (stop) {
            vx = vx * config.getFriction() * dt;
            vy = vy * config.getFriction() * dt;

            // 속도가 아주 낮아지면 완전히 멈춤 (떨림 방지)
            if (Math.abs(vx) < 0.1) vx = 0;
            if (Math.abs(vy) < 0.1) vy = 0;
        }

        // 3. 최고 속도 제한 (Speed Limit)
        double currentSpeed = Math.sqrt(vx * vx + vy * vy);
        if (currentSpeed > config.getMaxSpeed()) {
            vx = (vx / currentSpeed) * config.getMaxSpeed();
            vy = (vy / currentSpeed) * config.getMaxSpeed();
        }

        x += vx * dt;
        y += vy * dt;
    }

    public int getLayer() {
        return config.getRenderingLayer();
    }

    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public int getRadius() {
        return config.getRadius();
    }
    public boolean isRemove() {
        return isRemove;
    }
    public void addX(double value) { x += value;}
    public void addY(double value) { y += value;}

    public void checkBound(CaliaBase ssBase) {
        if (x > ssBase.MAX_X) {
            x = ssBase.MAX_X;
        } else if (x < ssBase.MIN_X) {
            x = ssBase.MIN_X;
        }

        if (y > ssBase.MAX_Y) {
            y = ssBase.MAX_Y;
        } else if (y < ssBase.MIN_Y) {
            y = ssBase.MIN_Y;
        }
    }

    protected void remove() {
        isRemove = true;
    }

    public void renderHitbox(Graphics g,double x,double y) {
        g.setColor(hitBoxColor);
        g.fillOval((int)(x - config.getRadius()), (int)(y - config.getRadius()), config.getRadius() * 2, config.getRadius() * 2);
    }
}
