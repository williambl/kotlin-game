package com.williambl.gameObject

import com.williambl.renderObject.RenderObject
import main.kotlin.com.williambl.Engine
import org.joml.Vector3f

abstract class GameObject {

    abstract val engine : Engine
    abstract val renderObj : RenderObject?
    abstract var position : Vector3f
}