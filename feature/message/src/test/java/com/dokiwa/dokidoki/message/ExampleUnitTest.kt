package com.dokiwa.dokidoki.message

import android.graphics.Rect
import android.graphics.drawable.Drawable
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class ExampleUnitTest {

    private val rows = 4

    private val columns = 7

    private val items = Array(rows * columns) {
        GridItem(null, Rect())
    }

    private class GridItem(val drawable: Drawable?, val rect: Rect)

    @Test
    fun testGridItem() {
        val width = 70
        val height = 40
        val w = width / columns
        val h = height / rows
        println("$w, $h")
        items.forEachIndexed { index, gridItem ->
            val top = index / columns
            val left = index % columns
            println("$index -> $top, $left")
        }
    }

    @Test
    fun testGridItemData() {
    }
}