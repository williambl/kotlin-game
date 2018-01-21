package com.williambl.kotlingame

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController

class PlayerInputController(camera: Camera, var player: Player) : CameraInputController(camera) {

    override fun keyDown(keycode: Int): Boolean {
        var moveAmount = 0f

        if (keycode == Input.Keys.LEFT) {
            moveAmount = 1.0f
        }
        if (keycode == Input.Keys.RIGHT)
            moveAmount = -1.0f
        player.transform.translate(-moveAmount, 0f, 0f)
        camera.translate(moveAmount, 0f, 0f)
        return true
    }

}