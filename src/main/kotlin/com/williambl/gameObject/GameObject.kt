package com.williambl.gameObject

import com.williambl.renderObject.RenderObject
import org.joml.Vector3d

abstract class GameObject {

    abstract val renderObj : RenderObject?
    abstract var position : Vector3d
}