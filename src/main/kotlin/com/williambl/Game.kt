package com.williambl

import main.kotlin.com.williambl.Engine

class Game constructor(engine: Engine){

    private var engine = engine;

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