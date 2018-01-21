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
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader
import com.badlogic.gdx.assets.loaders.ModelLoader
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.assets.AssetManager












class KotlinGame : ApplicationAdapter() {

    lateinit var environment: Environment

    lateinit var camera : PerspectiveCamera
    lateinit var camController: CameraInputController

    var assetMan = AssetManager()

    var instances = mutableListOf<ModelInstance>()
    lateinit var modelBatch: ModelBatch

    var loading : Boolean = false

    override fun create() {
        modelBatch = ModelBatch()

        environment = createEnv()

        createCamera()


        assetMan.load("data/scene.g3db", Model::class.java)
        loading = true
    }

    override fun render() {
        if (loading && assetMan.update())
            finishLoading()

        Gdx.gl.glViewport(0, 0, Gdx.graphics.width, Gdx.graphics.height)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

        modelBatch.begin(camera)
        modelBatch.render(instances, environment)
        modelBatch.end()
    }

    override fun dispose() {
        modelBatch.dispose()
        instances.clear()
        assetMan.dispose()
    }

    fun finishLoading() {
        val scene = assetMan.get("data/scene.g3db", Model::class.java)
        instances.add(createInstance(scene,"Ship",0f,0f,0f))
        instances.add(createInstance(scene,"Enemy",0f,2f,0f))
        instances.add(createInstance(scene,"Cube",0f,-2f,0f))
    }

    fun createCubeModel() : Model {
        val modelBuilder = ModelBuilder()
        return modelBuilder.createBox(5f, 5f, 5f,
                Material(ColorAttribute.createDiffuse(Color.GREEN)),
                VertexAttributes.Usage.Position.toLong()
                        or VertexAttributes.Usage.Normal.toLong())
    }

    fun createInstance(model: Model, id: String, x: Float, y: Float, z: Float) : ModelInstance {
        var instance = ModelInstance(model, id)
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
