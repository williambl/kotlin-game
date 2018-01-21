package com.williambl.kotlingame

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g3d.*
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label

class KotlinGame : ApplicationAdapter() {

    lateinit var environment: Environment

    lateinit var camera : PerspectiveCamera
    lateinit var camController: CameraInputController

    var assetMan = AssetManager()

    lateinit var skySphere : SkySphere
    var gameObjects = mutableListOf<GameObject>()
    lateinit var modelBatch: ModelBatch

    var loading : Boolean = false

    lateinit var stage : Stage
    lateinit var font : BitmapFont
    lateinit var label : Label
    var stringBuilder = com.badlogic.gdx.utils.StringBuilder()

    override fun create() {
        stage = Stage()
        font = BitmapFont()
        label = Label(" ", Label.LabelStyle(font, Color.GREEN))
        stage.addActor(label)

        modelBatch = ModelBatch()

        environment = createEnv()

        createCamera()


        assetMan.load("data/scene.g3db", Model::class.java)
        loading = true
    }

    var visibleCount = 0
    override fun render() {
        if (loading && assetMan.update())
            finishLoading()
        else if (loading && !assetMan.update())
            return

        Gdx.gl.glViewport(0, 0, Gdx.graphics.width, Gdx.graphics.height)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

        modelBatch.begin(camera)

        visibleCount = 0

        gameObjects.forEach {
            if (it.isVisible(camera)) {
                modelBatch.render(it, environment)
                visibleCount++
            }
        }

        skySphere.updatePosition(camera)
        modelBatch.render(skySphere)
        visibleCount++

        modelBatch.end()

        stringBuilder.setLength(0)
        stringBuilder.append(" FPS: ").append(Gdx.graphics.framesPerSecond)
        stringBuilder.append(" Visible: ").append(visibleCount)
        label.setText(stringBuilder)
        stage.draw()
    }

    override fun dispose() {
        modelBatch.dispose()
        gameObjects.clear()
        assetMan.dispose()
    }

    fun finishLoading() {
        val scene = assetMan.get("data/scene.g3db", Model::class.java)
        gameObjects.add(createGameObject(scene,"Ship",0f,0f,0f))
        gameObjects.add(createGameObject(scene,"Enemy",5f,0f,0f))
        gameObjects.add(createGameObject(scene,"Cube",-5f,0f,0f))
        skySphere = SkySphere(scene, "SkySphere", true)
        loading = false
    }

    fun createGameObject(model: Model, id: String, x: Float, y: Float, z: Float) : GameObject {
        var gameObject = GameObject(model, id, true)
        gameObject.transform.setToTranslation(x,y,z)
        return gameObject
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
