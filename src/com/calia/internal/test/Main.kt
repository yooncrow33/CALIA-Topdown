package com.calia.internal.test

import com.calia.CaliaBase
import com.calia.internal.viewMetrics.IMouse
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints


class Main(profileId : Int) : CaliaBase("physics test",19200,10800), IMouse {

    var gamePause = false

    var playerX = 0.0
    var playerY = 0.0

    val ketListener: KetListener = KetListener(this,this)

    lateinit var player : Player;

    override fun init() {
        for (i in 0..400) {
            addEntity(BackGroundIce())
        }
        player = Player(this);
        addEntity(player)

        launch()
    }
    override fun update(delat: Double) {
        setHitboxRender(true)
        val dt : Double = delat / (16.0 / 1000.0)
        if (!gamePause) {
            camera.follow(player.x,player.y,0.05)
        }
    }

    override fun backGroundRender(g: Graphics) {
    }

    override fun render(g : Graphics) {
    }

    fun esc() {
        gamePause = !gamePause
        setPause(gamePause)
    }

    fun setHitboxRender(b : Boolean) { super.setHitBoxRender(b) }
    override fun getVirtualMouseY(): Int {
        return super.getMouseY()
    }

    override fun getVirtualMouseX(): Int {
        return super.getMouseX()
    }

    fun kill() {super.exit()}
}
fun main() {
    Main(1)
}
//어떻게 작동하는지는 모른다..
//어쩄든 기적적으로 작동한다.

