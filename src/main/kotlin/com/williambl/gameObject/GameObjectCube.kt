package com.williambl.gameObject

import com.williambl.renderObject.RenderObject
import com.williambl.renderObject.RenderObjectCube
import main.kotlin.com.williambl.Engine
import org.joml.Vector3d

class GameObjectCube(override val engine: Engine, override var position: Vector3d) : GameObject() {

    override val renderObj = RenderObjectCube(engine, this)
}