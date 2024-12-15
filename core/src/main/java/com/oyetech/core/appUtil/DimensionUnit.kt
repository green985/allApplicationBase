package com.oyetech.core.appUtil

import android.content.res.Resources
import androidx.annotation.Dimension
import androidx.annotation.Px

/**
 * Core utility for converting different dimensional values.
 */
enum class DimensionUnit {
    PIXELS {
        @Px
        override fun toPixels(@Px pixels: Float): Float {
            return pixels
        }

        @Dimension(unit = Dimension.DP)
        override fun toDp(@Px pixels: Float): Float {
            return pixels / Resources.getSystem().displayMetrics.density
        }

        @Dimension(unit = Dimension.SP)
        override fun toSp(@Px pixels: Float): Float {
            return pixels / Resources.getSystem().displayMetrics.scaledDensity
        }
    },
    DP {
        @Px
        override fun toPixels(@Dimension(unit = Dimension.DP) dp: Float): Float {
            return dp * Resources.getSystem().displayMetrics.density
        }

        @Dimension(unit = Dimension.DP)
        override fun toDp(@Dimension(unit = Dimension.DP) dp: Float): Float {
            return dp
        }

        @Dimension(unit = Dimension.SP)
        override fun toSp(@Dimension(unit = Dimension.DP) dp: Float): Float {
            return PIXELS.toSp(toPixels(dp))
        }
    },
    SP {
        @Px
        override fun toPixels(@Dimension(unit = Dimension.SP) sp: Float): Float {
            return sp * Resources.getSystem().displayMetrics.scaledDensity
        }

        @Dimension(unit = Dimension.DP)
        override fun toDp(@Dimension(unit = Dimension.SP) sp: Float): Float {
            return PIXELS.toDp(toPixels(sp))
        }

        @Dimension(unit = Dimension.SP)
        override fun toSp(@Dimension(unit = Dimension.SP) sp: Float): Float {
            return sp
        }
    };

    abstract fun toPixels(value: Float): Float
    abstract fun toDp(value: Float): Float
    abstract fun toSp(value: Float): Float
}

private fun usageExample() {
}
