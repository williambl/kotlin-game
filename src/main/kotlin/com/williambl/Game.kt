package com.williambl

import com.williambl.gameObject.GameObjectCube
import com.williambl.gameObject.GameObject
import com.williambl.renderObject.RenderObjectCube
import main.kotlin.com.williambl.Engine

class Game constructor(engine: Engine){

    private var engine = engine;

    internal var gameObjs = mutableListOf<GameObject>()

    internal fun init () {
        gameObjs.add(GameObjectCube(RenderObjectCube(engine), Triple(0f,0f,0f)))
    }

    internal var angle = 0.0f
    internal var lastTime = System.nanoTime()

    internal fun update() {
        engine.projMatrix.setPerspective(Math.toRadians(30.0).toFloat(), engine.width.toFloat() / engine.height, 0.01f, 50.0f)
        val thisTime = System.nanoTime()
        val diff = (thisTime - lastTime) / 1E9f
        angle += diff
        engine.viewMatrix.setLookAt(0.0f, 2.0f, 5.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f).rotateY(angle)
        lastTime = thisTime
    }

}