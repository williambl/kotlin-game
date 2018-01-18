package com.williambl.renderObject

import com.williambl.gameObject.GameObject

abstract class RenderObject {

    abstract val gameObj : GameObject
    abstract fun render ()
}