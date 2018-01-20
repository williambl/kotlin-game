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
import com.badlogic.gdx.graphics.g3d.ModelInstance








class KotlinGame : ApplicationAdapter() {

    lateinit var environment: Environment

    lateinit var camera : PerspectiveCamera
    lateinit var camController: CameraInputController

    lateinit var model: Model

    var instances = mutableListOf<ModelInstance>()
    lateinit var modelBatch: ModelBatch

    override fun create() {
        modelBatch = ModelBatch()

        environment = createEnv()

        createCamera()

        model = createCubeModel()

        instances.add(createInstance(model,0f,0f,0f))
        instances.add(createInstance(model,0f,6f,0f))
    }

    override fun render() {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.width, Gdx.graphics.height)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

        modelBatch.begin(camera)
        modelBatch.render(instances, environment)
        modelBatch.end()
    }

    override fun dispose() {
        model.dispose()
    }

    fun createCubeModel() : Model {
        val modelBuilder = ModelBuilder()
        return modelBuilder.createBox(5f, 5f, 5f,
                Material(ColorAttribute.createDiffuse(Color.GREEN)),
                VertexAttributes.Usage.Position.toLong()
                        or VertexAttributes.Usage.Normal.toLong())
    }

    fun createInstance(model: Model, x: Float, y: Float, z: Float) : ModelInstance {
        var instance = ModelInstance(model)
        instance.transform.setToTranslation(x,y,z)
        return instance
    }

    fun createCamera() {
        camera = PerspectiveCamera(67f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        with(camera) {
            position.set(10f, 10f, 10f)
            lookAt(0f, 0f, 0f)
            near = 1f
            far = 300f
            update()
        }

        camController = CameraInputController(camera)
        Gdx.input.inputProcessor = camController
    }

    fun createEnv() : Environment {
        environment = Environment()
        environment.run {
            set(ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f))
            add(DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f))
        }
        return environment
    }
}
