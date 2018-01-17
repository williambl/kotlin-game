package com.williambl.gameObject

import com.williambl.renderObject.RenderObject

abstract class GameObject {
    abstract var renderObj : RenderObject

    abstract var position : Triple<Float, Float, Float>
}