package com.williambl.kotlingame

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g3d.Model

class SkySphere (model: Model, rootNode: String, mergeTransform: Boolean): GameObject(model, rootNode, mergeTransform) {

    fun updatePosition (camera: Camera) {
       transform.setToTranslation(camera.position)
    }
}