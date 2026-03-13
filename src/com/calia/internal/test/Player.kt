package com.calia.internal.test

import com.calia.`object`.Entity
import com.calia.`object`.EntityConfig
import java.awt.Color
import java.awt.Graphics


class Player(val main: Main) : Entity(0.0,0.0, EntityConfig(
    15,  // radius (반지름 15)
    true,  // isCollisionEnabled (충돌 활성화)
    true,  // isUpdateEnabled (업데이트 활성화)
    0,  // renderingLayer (기본 레이어)
    80.0,  // maxSpeed (예시: 최대 속도)
    4.0,  // mass (예시: 질량)
    0.1,  // friction (예시: 마찰력)
    1.2,  // acceleration (예시: 가속도)
    1.0 // restitution (예시: 탄성 계수)
)) {

    var moveUp: Boolean = false
    var moveLeft: Boolean = false
    var moveDown: Boolean = false
    var moveRight: Boolean = false

    val PLAYER_WIDTH: Int = 30
    val PLAYER_HEIGHT: Int = 30

    var PLAYER_MOVE_SPEED: Int = 10

    val weaponDistance: IntArray = intArrayOf(120, 200, 400, 600)
    var weapon: Int = 0


    var elixir: Double = 20.0
    val maxElixir = 200
    var hp : Double = 100.0
    val maxHp = 100

    var takeItem : Boolean = false;

    override fun update(delta : Double) {
        val dt : Double = delta / (16.0 / 1000.0)

        super.setStop(!moveUp && !moveDown && !moveLeft && !moveRight)

        super.setAcceleration(moveUp || moveDown || moveLeft || moveRight)
        //super.setStop(!moveUp || !moveDown || !moveLeft || !moveRight)


        val currentAngle = angle
        val angle : Double = when {
            moveUp && moveRight -> 315.0  // 우상단
            moveUp && moveLeft  -> 225.0  // 좌상단
            moveDown && moveRight -> 45.0  // 우하단
            moveDown && moveLeft -> 135.0  // 좌하단
            moveUp    -> 270.0            // 상
            moveDown  -> 90.0             // 하
            moveLeft  -> 180.0            // 좌
            moveRight -> 0.0              // 우
            else -> angle        // 변화 없음
        }

        super.angle = angle
    }

    override fun render(g: Graphics, x: Double, y: Double) {
        g.color = Color.black
        g.fillOval((x - PLAYER_WIDTH/2).toInt(), (y - PLAYER_HEIGHT/2).toInt(), PLAYER_WIDTH,PLAYER_HEIGHT)
    }

    fun shot() {

    }

    fun useWeapon() {

    }

    fun addElixir(value : Double) {
        elixir += value
    }



    fun setTrueMoveUp() { moveUp = true }
    fun setTrueMoveLeft() { moveLeft = true }
    fun setTrueMoveDown() { moveDown = true }
    fun setTrueMoveRight() { moveRight = true }
    fun setFalseMoveUp() { moveUp = false }
    fun setFalseMoveLeft() { moveLeft = false }
    fun setFalseMoveDown() { moveDown = false }
    fun setFalseMoveRight() { moveRight = false }}
