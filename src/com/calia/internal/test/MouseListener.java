package com.calia.internal.test;

import com.calia.internal.test.Main;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseListener extends MouseAdapter {

    Main main;

    public MouseListener(Main main) {
        this.main = main;
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }
}
