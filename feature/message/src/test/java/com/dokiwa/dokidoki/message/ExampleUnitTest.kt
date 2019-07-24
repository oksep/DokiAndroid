package com.dokiwa.dokidoki.message

import android.graphics.Rect
import android.graphics.drawable.Drawable
import com.dokiwa.dokidoki.message.widget.TabData
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
        val data = TabData(
            "guaiqiaogif/icon/guaiqiao_s_normal@2x.png",
            "guaiqiaogif/icon/guaiqiao_s_highlighted@2x.png",
            18
        ) { index ->
            Pair(index, "guaiqiaogif/content/guaiqiaogif0${String.format("%02d", index)}@2x.gif")
        }
        val b = Array(data.count) {
            data.pathGen(it)
        }
        println(b.size)
        val group = b.groupBy { it.first / 10 }
        println(group)
    }
}