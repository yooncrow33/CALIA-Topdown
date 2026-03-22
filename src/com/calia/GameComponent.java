package com.calia;


import java.awt.*;

abstract public class GameComponent {
    abstract public void update(double dt);
    abstract public void init();

    //public void

    protected GameComponent() {

        //CALIA.internal.register
    }

    public void renderBackGround(Graphics g) {

    }

    public void renderHud(Graphics g) {

    }
}
