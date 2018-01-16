package com.williambl.renderObject

import com.williambl.util.createShader
import main.kotlin.com.williambl.Engine
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL15.*
import org.lwjgl.opengl.GL20.*
import org.lwjgl.opengl.GL30.*
import org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER
import java.nio.IntBuffer

class Cube constructor(engine: Engine) {

    private var viewMatrixUniform: Int = 0
    private var projMatrixUniform: Int = 0
    private var viewportSizeUniform: Int = 0
    private var engine : Engine = engine

    private var vao : Int
    private var program : Int

    init {
        vao = createVAO()
        program = createProgram()
        initProgram(program)
    }

    private fun createVAO () : Int {
        val vao = glGenVertexArrays()
        glBindVertexArray(vao)
        val vb = BufferUtils.createIntBuffer(6 * 6)
        val pb = BufferUtils.createFloatBuffer(3 * 6 * 6)
        quadPattern(vb)
        pb.put(0.5f).put(0.5f).put(-0.5f)
        pb.put(0.5f).put(-0.5f).put(-0.5f)
        pb.put(-0.5f).put(-0.5f).put(-0.5f)
        pb.put(-0.5f).put(-0.5f).put(-0.5f)
        pb.put(-0.5f).put(0.5f).put(-0.5f)
        pb.put(0.5f).put(0.5f).put(-0.5f)
        quadWithDiagonalPattern(vb)
        pb.put(0.5f).put(-0.5f).put(0.5f)
        pb.put(0.5f).put(0.5f).put(0.5f)
        pb.put(-0.5f).put(0.5f).put(0.5f)
        pb.put(-0.5f).put(0.5f).put(0.5f)
        pb.put(-0.5f).put(-0.5f).put(0.5f)
        pb.put(0.5f).put(-0.5f).put(0.5f)
        quadPattern(vb)
        pb.put(0.5f).put(-0.5f).put(-0.5f)
        pb.put(0.5f).put(0.5f).put(-0.5f)
        pb.put(0.5f).put(0.5f).put(0.5f)
        pb.put(0.5f).put(0.5f).put(0.5f)
        pb.put(0.5f).put(-0.5f).put(0.5f)
        pb.put(0.5f).put(-0.5f).put(-0.5f)
        quadWithDiagonalPattern(vb)
        pb.put(-0.5f).put(-0.5f).put(0.5f)
        pb.put(-0.5f).put(0.5f).put(0.5f)
        pb.put(-0.5f).put(0.5f).put(-0.5f)
        pb.put(-0.5f).put(0.5f).put(-0.5f)
        pb.put(-0.5f).put(-0.5f).put(-0.5f)
        pb.put(-0.5f).put(-0.5f).put(0.5f)
        quadPattern(vb)
        pb.put(0.5f).put(0.5f).put(0.5f)
        pb.put(0.5f).put(0.5f).put(-0.5f)
        pb.put(-0.5f).put(0.5f).put(-0.5f)
        pb.put(-0.5f).put(0.5f).put(-0.5f)
        pb.put(-0.5f).put(0.5f).put(0.5f)
        pb.put(0.5f).put(0.5f).put(0.5f)
        quadWithDiagonalPattern(vb)
        pb.put(0.5f).put(-0.5f).put(-0.5f)
        pb.put(0.5f).put(-0.5f).put(0.5f)
        pb.put(-0.5f).put(-0.5f).put(0.5f)
        pb.put(-0.5f).put(-0.5f).put(0.5f)
        pb.put(-0.5f).put(-0.5f).put(-0.5f)
        pb.put(0.5f).put(-0.5f).put(-0.5f)
        pb.flip()
        vb.flip()
        // setup vertex positions buffer
        val posVbo = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, posVbo)
        glBufferData(GL_ARRAY_BUFFER, pb, GL_STATIC_DRAW)
        glEnableVertexAttribArray(0)
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0L)
        // setup vertex visibility buffer
        val visVbo = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, visVbo)
        glBufferData(GL_ARRAY_BUFFER, vb, GL_STATIC_DRAW)
        glEnableVertexAttribArray(1)
        glVertexAttribPointer(1, 1, GL_UNSIGNED_INT, false, 0, 0L)
        glBindVertexArray(0)

        return vao;
    }

    private fun createProgram () : Int {
        val program = glCreateProgram()
        val vshader = createShader("com/williambl/kotlin-game/vs.glsl", GL_VERTEX_SHADER)
        val fshader = createShader("com/williambl/kotlin-game/fs.glsl", GL_FRAGMENT_SHADER)
        val gshader = createShader("com/williambl/kotlin-game/gs.glsl", GL_GEOMETRY_SHADER)
        glAttachShader(program, vshader)
        glAttachShader(program, fshader)
        glAttachShader(program, gshader)
        glBindAttribLocation(program, 0, "position")
        glBindAttribLocation(program, 1, "visible")
        glBindFragDataLocation(program, 0, "color")
        glLinkProgram(program)
        val linked = glGetProgrami(program, GL_LINK_STATUS)
        val programLog = glGetProgramInfoLog(program)
        if (programLog != null && programLog.trim({ it <= ' ' }).length > 0) {
            System.err.println(programLog)
        }
        if (linked == 0) {
            throw AssertionError("Could not link cubeProgram")
        }
        return program
    }

    private fun initProgram (program: Int) {
        glUseProgram(program)
        viewMatrixUniform = glGetUniformLocation(program, "viewMatrix")
        projMatrixUniform = glGetUniformLocation(program, "projMatrix")
        viewportSizeUniform = glGetUniformLocation(program, "viewportSize")
        glUseProgram(0)
    }

    private fun quadPattern(vb: IntBuffer) {
        vb.put(1).put(0).put(1).put(1).put(0).put(1)
    }

    private fun quadWithDiagonalPattern(vb: IntBuffer) {
        vb.put(1).put(1).put(1).put(1).put(1).put(1)
    }

    fun render () {
        glUseProgram(program)

        glUniformMatrix4fv(viewMatrixUniform, false, engine.viewMatrix.get(engine.matrixBuffer))
        glUniformMatrix4fv(projMatrixUniform, false, engine.projMatrix.get(engine.matrixBuffer))
        glUniform2f(viewportSizeUniform, engine.width.toFloat(), engine.width.toFloat())

        glBindVertexArray(vao)
        glDrawArrays(GL_TRIANGLES, 0, 3 * 2 * 6)
        glBindVertexArray(0)
    }
}