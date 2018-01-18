package com.williambl.gameObject

import com.williambl.renderObject.RenderObject
import com.williambl.renderObject.RenderObjectCube
import main.kotlin.com.williambl.Engine
import org.joml.Vector3f

class GameObjectCube(override val engine: Engine, override var position: Vector3f) : GameObject() {

    override val renderObj = RenderObjectCube(engine, this)
}