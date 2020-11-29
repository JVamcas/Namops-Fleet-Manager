package com.pet001kambala.namopsfleetmanager.utils

import android.graphics.*
import com.squareup.picasso.Picasso
import com.squareup.picasso.RequestCreator
import com.squareup.picasso.Transformation

class ImageTransformer {
    object CircleTransformation : Transformation {
        private var x: Int = 0
        private var y: Int = 0
        override fun transform(source: Bitmap): Bitmap {

            val size = source.width.coerceAtMost(source.height)
            x = (source.width - size) / 2
            y = (source.height - size) / 2

            val squareBitmap = Bitmap.createBitmap(source, x, y, size, size)
            if (squareBitmap != source)
                source.recycle()
            val bitmap = Bitmap.createBitmap(size, size, source.config)
            val canvas = Canvas(bitmap)
            val paint = Paint()
            val shader = BitmapShader(squareBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

            paint.shader = shader
            paint.isAntiAlias = true
            val r = size / 2f
            canvas.drawCircle(r, r, r, paint)
            squareBitmap.recycle()
            return bitmap
        }

        override fun key(): String {
            return "circle(x=$x, y=$y)"
        }
    }

    object SquareTransformation : Transformation {

        private var x: Int = 0
        private var y: Int = 0
        override fun transform(source: Bitmap): Bitmap {
            val size = source.width.coerceAtMost(source.height)
            x = (source.width - size) / 2
            y = (source.height - size) / 2
            val result = Bitmap.createBitmap(source, x, y, size, size)
            if (result != source) {
                source.recycle()
            }
            return result
        }

        override fun key(): String {
            return Const.SQUARE
        }
    }

    object ImageUtil {
        fun requestCreator(
            transform: Transformation,
            iconUrl: String?,
            size: Int,
            default_icon: Int
        ): RequestCreator {

            val mCreator = if (iconUrl.isNullOrEmpty())
                Picasso.get().load(default_icon)
            else
                Picasso.get().load(iconUrl)
            mCreator.transform(transform)
                .centerCrop()
                .resize(size, size)
            return mCreator
        }
    }
}