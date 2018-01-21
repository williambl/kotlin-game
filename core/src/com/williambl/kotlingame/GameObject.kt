package com.williambl.kotlingame

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox

class GameObject(model: Model, rootNode: String, mergeTransform: Boolean) : ModelInstance(model, rootNode, mergeTransform) {
	var center = Vector3()
	var dimensions = Vector3()
	var radius : Float = 0.0f

    var bounds = BoundingBox()

	init {
		calculateBoundingBox(bounds)
		bounds.getCenter(center)
		bounds.getDimensions(dimensions)
		radius = dimensions.len() / 2f
	}

	fun isVisible(camera: Camera) : Boolean {
		return true
	}
}