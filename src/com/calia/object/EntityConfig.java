package com.calia.object;

public class EntityConfig {
    private final int radius;
    private final boolean isCollisionEnabled;
    private final boolean isUpdateEnabled;
    private final int RenderingLayer;
    private final double MaxSpeed;
    private final double Mass;
    private final double friction;
    private final double acceleration;
    private final double restitution;

    public EntityConfig(int radius, boolean isCollisionEnabled, boolean isUpdateEnabled, int renderingLayer, double maxSpeed, double mass, double friction, double acceleration, double restitution) {
        this.radius = radius;
        this.isCollisionEnabled = isCollisionEnabled;
        this.isUpdateEnabled = isUpdateEnabled;
        RenderingLayer = renderingLayer;
        MaxSpeed = maxSpeed;
        Mass = mass;
        this.friction = friction;
        this.acceleration = acceleration;
        this.restitution = restitution;
    }

    public int getRadius() {
        return radius;
    }

    public boolean isCollisionEnabled() {
        return isCollisionEnabled;
    }

    public boolean isUpdateEnabled() {
        return isUpdateEnabled;
    }

    public int getRenderingLayer() {
        return RenderingLayer;
    }

    public double getMaxSpeed() {
        return MaxSpeed;
    }

    public double getMass() {
        return Mass;
    }

    public double getFriction() {
        return friction;
    }

    public double getAcceleration() {
        return acceleration;
    }

    public double getRestitution() {
        return restitution;
    }
}
