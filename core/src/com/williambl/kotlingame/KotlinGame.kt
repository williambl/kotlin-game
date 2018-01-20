package com.williambl.kotlingame

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g3d.*
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.sun.scenario.effect.impl.prism.PrEffectHelper.render
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController






class KotlinGame : ApplicationAdapter() {

    lateinit var environment: Environment
    lateinit var camera : PerspectiveCamera
    lateinit var model: Model
    lateinit var instance: ModelInstance
    lateinit var camController: CameraInputController
    lateinit var modelBatch: ModelBatch

    override fun create() {
        modelBatch = ModelBatch()

        camera = PerspectiveCamera(67f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        camera.position.set(10f, 10f, 10f)
        camera.lookAt(0f,0f,0f)
        camera.near = 1f
        camera.far = 300f
        camera.update()

        camController = CameraInputController(camera)
        Gdx.input.inputProcessor = camController

        val modelBuilder = ModelBuilder()
        model = modelBuilder.createBox(5f, 5f, 5f,
                Material(ColorAttribute.createDiffuse(Color.GREEN)),
                VertexAttributes.Usage.Position.toLong()
                        or VertexAttributes.Usage.Normal.toLong())
        instance = ModelInstance(model)

        environment = Environment()
        environment.set(ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f))
        environment.add(DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f))

    }

    override fun render() {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.width, Gdx.graphics.height)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

        modelBatch.begin(camera)
        modelBatch.render(instance, environment)
        modelBatch.end()
    }

    override fun dispose() {
        model.dispose()
    }
}
