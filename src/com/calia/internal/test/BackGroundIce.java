package com.calia.internal.test;

import com.calia.object.Entity;
import com.calia.object.EntityConfig;

import java.awt.*;
import java.util.Random;

public class BackGroundIce extends Entity {

    final int iceWidth = 900;
    final int iceHeight = 900;

    public BackGroundIce() {
        super(
                new Random().nextDouble(19201) - 19200.0/2, // x
                new Random().nextDouble(10801) - 10800.0/2, // y
                new EntityConfig(
                        100,       // 기존 radius 변수 혹은 값
                        true,        // isCollisionEnabled
                        false,        // isUpdateEnabled
                        -100,         // renderingLayer
                        1000.0,          // maxSpeed (배경이므로 0)
                        0.1,          // mass
                        0.1,          // friction
                        0.0,          // acceleration
                        1.0           // restitution
                )
        );
    }

    public void render(Graphics g,double x,double y) {
        //g.setColor(new Color(135, 206, 255));
        g.setColor(new Color(190,190,190));
        g.fillRect((int)x - iceWidth/2, (int)y - iceHeight/2, iceWidth, iceHeight);
    }

    @Override
    public void update(double dt) {

    }
}
