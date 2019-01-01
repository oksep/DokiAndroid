package com.dokiwa.dokidoki.ui.ext

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import androidx.annotation.ColorRes
import androidx.core.content.res.ResourcesCompat
import androidx.renderscript.Allocation
import androidx.renderscript.Element
import androidx.renderscript.RenderScript
import androidx.renderscript.ScriptIntrinsicBlur
import java.io.File
import java.io.FileOutputStream

fun Bitmap.toCircleBitmap(): Bitmap {
    val size = Math.min(width, height)

    val x = (width - size) / 2
    val y = (height - size) / 2

    val squaredBitmap = Bitmap.createBitmap(this, x, y, size, size)
    if (squaredBitmap != this) {
        recycle()
    }

    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)

    val canvas = Canvas(bitmap)
    val paint = Paint()
    val shader = BitmapShader(squaredBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
    paint.shader = shader
    paint.isAntiAlias = true

    val r = size / 2f
    canvas.drawCircle(r, r, r, paint)

    squaredBitmap.recycle()
    return bitmap
}

fun Bitmap.toRoundCornerBitmap(
    leftTop: Float,
    rightTop: Float,
    leftBottom: Float,
    rightBottom: Float,
    recycleOrigin: Boolean = false
): Bitmap {
    val result = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
    result.setHasAlpha(true)

    val canvas = Canvas(result)

    val paint = Paint()
    paint.isAntiAlias = true
    paint.shader = BitmapShader(this, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

    val rect = RectF(0f, 0f, this.width.toFloat(), this.height.toFloat())
    val radii = floatArrayOf(leftTop, leftTop, rightTop, rightTop, rightBottom, rightBottom, leftBottom, leftBottom)
    val path = Path()
    path.addRoundRect(rect, radii, Path.Direction.CCW)

    canvas.drawPath(path, paint)

    if (recycleOrigin) {
        recycle()
    }

    return result
}

fun Bitmap.scaleWidth(desiredWidth: Int): Bitmap {
    val newWidth = Math.min(desiredWidth, width)
    val newHeight = (1f * height / width * newWidth).toInt()

    if (width == newWidth && height == newHeight) {
        return this
    }

    val scaleWidth = newWidth.toFloat() / width
    val scaleHeight = newHeight.toFloat() / height

    val matrix = Matrix()
    matrix.postScale(scaleWidth, scaleHeight)

    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}

fun Bitmap.maskColor(context: Context, @ColorRes color: Int, recycleOrigin: Boolean = false): Bitmap {
    val result = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)

    val canvas = Canvas(result)
    canvas.drawBitmap(this, 0f, 0f, Paint())
    canvas.drawColor(ResourcesCompat.getColor(context.resources, color, null))
    canvas.setBitmap(null)

    if (recycleOrigin) {
        this.recycle()
    }

    return result
}

fun Bitmap.scaleByRatio(ratio: Float): Bitmap {
    return Bitmap.createScaledBitmap(this, (this.width * ratio).toInt(), (this.height * ratio).toInt(), false)
}

fun Bitmap.toTmpFile(parentFile: String, quality: Int = 85): String? {
    try {
        val pFile = File(parentFile)
        if (!pFile.exists()) {
            pFile.mkdirs()
        }
        val shareFile = File.createTempFile("tmp_", ".png", pFile)
        val output = FileOutputStream(shareFile)
        this.compress(Bitmap.CompressFormat.JPEG, quality, output)
        output.flush()
        output.close()
        return shareFile.absolutePath
    } catch (ignored: OutOfMemoryError) {
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}

fun Bitmap.cut(left: Int, top: Int, newWidth: Int, newHeight: Int): Bitmap {
    val l = if (left < 0) 0 else left
    val t = if (top < 0) 0 else top
    val w = if (newWidth + l > width) width - l else newWidth
    val h = if (newHeight + t > height) height - t else newHeight
    return Bitmap.createBitmap(this, l, t, w, h)
}

fun Bitmap.blurBitmap(context: Context, radius: Float, recycleOrigin: Boolean = false): Bitmap {
    //Let's create an empty bitmap with the same size of the bitmap we want to blur
    val outBitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)

    //Instantiate a new Renderscript
    val rs = RenderScript.create(context)

    //Create an Intrinsic Blur Script using the Renderscript
    val blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))

    //Create the Allocations (in/out) with the Renderscript and the in/out bitmaps
    val allIn = Allocation.createFromBitmap(rs, this)
    val allOut = Allocation.createFromBitmap(rs, outBitmap)
    // final Allocation output = Allocation.createTyped(rs, input.getType());

    //Set the radius of the blur: 0 < radius <= 25
    blurScript.setRadius(
        when {
            radius > 5 -> 25f
            radius < 0 -> 0f
            else -> radius
        }
    )

    //Perform the Renderscript
    blurScript.setInput(allIn)
    blurScript.forEach(allOut)

    //Copy the final bitmap created by the out Allocation to the outBitmap
    allOut.copyTo(outBitmap)

    //recycle the original bitmap
    if (recycleOrigin) {
        this.recycle()
    }

    //After finishing everything, we destroy the Renderscript.
    rs.destroy()

    return outBitmap
}