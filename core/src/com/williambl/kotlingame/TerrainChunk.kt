package com.williambl.kotlingame

internal class TerrainChunk(val heightMap: MutableList<MutableList<Int>>, val vertexSize: Int) {
    val width: Short
    val height: Short
    val vertices: FloatArray
    val indices: ShortArray

    var flatHeightMap = MutableList(0) {0}


    init {
        this.width = heightMap.size.toShort()
        this.height = heightMap[0].size.toShort()

        heightMap.forEach { row ->
            val j = heightMap.indexOf(row)
            row.forEach { tile ->
                val i = row.indexOf(tile)
                flatHeightMap.add(row[i])
            }
        }

        this.vertices = FloatArray(flatHeightMap.size * vertexSize)
        this.indices = ShortArray(width * height * 6)

        println(flatHeightMap.size)
        println(flatHeightMap.toString())
        buildIndices()
        buildVertices()
    }

    fun buildVertices() {
        val heightPitch = height + 1
        val widthPitch = width + 1

        var idx = 0
        var hIdx = 0
        val inc = vertexSize - 3


        for (z in 0 until height) {
            for (x in 0 until width) {
                println(idx)
                vertices[idx++] = x.toFloat()
                vertices[idx++] = flatHeightMap[hIdx++].toFloat()
                vertices[idx++] = z.toFloat()
                idx += inc
            }
        }
    }

    private fun buildIndices() {
        var idx = 0
        val pitch= (width + 1).toShort()
        var i1: Short = 0
        var i2: Short = 1
        var i3 = (1 + pitch).toShort()
        var i4 = pitch

        var row: Short = 0

        for (z in 0 until height) {
            for (x in 0 until width) {
                indices[idx++] = i1
                indices[idx++] = i2
                indices[idx++] = i3

                indices[idx++] = i3
                indices[idx++] = i4
                indices[idx++] = i1

                i1++
                i2++
                i3++
                i4++
            }

            row = (row + pitch).toShort()
            i1 = row
            i2 = (row + 1).toShort()
            i3 = (i2 + pitch).toShort()
            i4 = (row + pitch).toShort()
        }
    }
}