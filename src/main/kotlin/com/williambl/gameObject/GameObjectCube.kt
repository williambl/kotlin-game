package com.williambl.gameObject

import com.williambl.renderObject.RenderObjectCube

class GameObjectCube(var renderObj: RenderObjectCube, override var position: Triple<Float, Float, Float>) : GameObject()