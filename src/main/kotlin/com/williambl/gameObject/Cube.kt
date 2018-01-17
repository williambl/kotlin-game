package com.williambl.gameObject

import com.williambl.renderObject.Cube
import com.williambl.renderObject.RenderObject

class Cube(renderObj : Cube) : GameObject() {

    override var renderObj: RenderObject = renderObj

    override var position: Triple<Float, Float, Float> = Triple(0f,0f,0f)

}