package com.calia.internal.test

import com.calia.KeyBindingBase
import java.awt.KeyEventDispatcher
import java.awt.KeyboardFocusManager
import java.awt.event.KeyEvent
import javax.swing.JComponent

class KetListener(comp: JComponent, var main: Main) : KeyBindingBase(comp) {

    override fun onKeyWPress() {
        main.player.setTrueMoveUp()
    }

    override fun onKeyWRelease() {
        main.player.setFalseMoveUp()
    }

    override fun onKeyAPress() {
        main.player.setTrueMoveLeft()
    }

    override fun onKeyARelease() {
        main.player.setFalseMoveLeft()
    }

    override fun onKeySPress() {
        main.player.setTrueMoveDown()
    }

    override fun onKeySRelease() {
        main.player.setFalseMoveDown()
    }

    override fun onKeyDPress() {
        main.player.setTrueMoveRight()
    }

    override fun onKeyDRelease() {
        main.player.setFalseMoveRight()
    }

}