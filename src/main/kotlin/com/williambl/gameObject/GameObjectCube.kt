package com.williambl.gameObject

import com.williambl.renderObject.RenderObjectCube
import org.joml.Vector3d

class GameObjectCube(override var renderObj: RenderObjectCube, override var position: Vector3d) : GameObject()