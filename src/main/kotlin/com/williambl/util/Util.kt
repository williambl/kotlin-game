package com.williambl.util

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL20.*
import org.lwjgl.system.MemoryUtil.memUTF8
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.Channels
import java.nio.channels.FileChannel

/**
 * Create a shader object from the given classpath resource.
 *
 * @param resource
 * the class path
 * @param type
 * the shader type
 * @param version
 * the GLSL version to prepend to the shader source, or null
 *
 * @return the shader object id
 *
 * @throws IOException
 */
@Throws(IOException::class)
fun createShader(resource: String, type: Int, version: String?): Int {
    val shader = glCreateShader(type)

    val source = ioResourceToByteBuffer(resource, 8192)

    if (version == null) {
        val strings = BufferUtils.createPointerBuffer(1)
        val lengths = BufferUtils.createIntBuffer(1)

        strings.put(0, source)
        lengths.put(0, source.remaining())

        glShaderSource(shader, strings, lengths)
    } else {
        val strings = BufferUtils.createPointerBuffer(2)
        val lengths = BufferUtils.createIntBuffer(2)

        val preamble = memUTF8("#version " + version + "\n", false)

        strings.put(0, preamble)
        lengths.put(0, preamble.remaining())

        strings.put(1, source)
        lengths.put(1, source.remaining())

        glShaderSource(shader, strings, lengths)
    }

    glCompileShader(shader)
    val compiled = glGetShaderi(shader, GL_COMPILE_STATUS)
    val shaderLog = glGetShaderInfoLog(shader)
    if (shaderLog != null && shaderLog.trim({ it <= ' ' }).length > 0) {
        System.err.println(shaderLog)
    }
    if (compiled == 0) {
        throw AssertionError("Could not compile shader")
    }
    return shader
}

/**
 * Reads the specified resource and returns the raw data as a ByteBuffer.
 *
 * @param resource   the resource to read
 * @param bufferSize the initial buffer size
 *
 * @return the resource data
 *
 * @throws IOException if an IO error occurs
 */
@Throws(IOException::class)
fun ioResourceToByteBuffer(resource: String, bufferSize: Int): ByteBuffer {
    var buffer: ByteBuffer
    val url = Thread.currentThread().contextClassLoader.getResource(resource)
    val file = File(url!!.file)
    if (file.isFile) {
        val fis = FileInputStream(file)
        val fc = fis.channel
        buffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size())
        fc.close()
        fis.close()
    } else {
        buffer = BufferUtils.createByteBuffer(bufferSize)
        val source = url.openStream() ?: throw FileNotFoundException(resource)
        try {
            val rbc = Channels.newChannel(source)
            try {
                while (true) {
                    val bytes = rbc.read(buffer)
                    if (bytes == -1)
                        break
                    if (buffer.remaining() == 0)
                        buffer = resizeBuffer(buffer, buffer.capacity() * 2)
                }
                buffer.flip()
            } finally {
                rbc.close()
            }
        } finally {
            source.close()
        }
    }
    return buffer
}

/**
 * Create a shader object from the given classpath resource.
 *
 * @param resource
 * the class path
 * @param type
 * the shader type
 *
 * @return the shader object id
 *
 * @throws IOException
 */
@Throws(IOException::class)
fun createShader(resource: String, type: Int): Int {
    return createShader(resource, type, null)
}

private fun resizeBuffer(buffer: ByteBuffer, newCapacity: Int): ByteBuffer {
    val newBuffer = BufferUtils.createByteBuffer(newCapacity)
    buffer.flip()
    newBuffer.put(buffer)
    return newBuffer
}