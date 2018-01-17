package com.williambl.gameObject

import com.williambl.renderObject.RenderObject

abstract class GameObject {

    abstract val renderObj : RenderObject?
    abstract var position : Triple<Float, Float, Float>
}