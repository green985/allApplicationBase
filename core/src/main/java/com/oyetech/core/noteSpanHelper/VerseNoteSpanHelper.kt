package com.oyetech.core.noteSpanHelper

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.text.Spannable
import android.text.SpannedString
import android.text.style.ClickableSpan
import android.text.style.DynamicDrawableSpan
import android.text.style.ImageSpan
import android.view.View
import androidx.annotation.NonNull
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.text.buildSpannedString
import com.oyetech.models.utils.const.HelperConstant
import org.koin.java.KoinJavaComponent
import timber.log.Timber
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
Created by Erdi Özbek
-21.11.2023-
-16:58-
 **/

class VerseNoteSpanHelper(var spanImageResId: Int) {

    val context: Context by KoinJavaComponent.inject<Context>(Context::class.java)

    var verseNoteRegexRule = """\{NOTE:\d+\.\d+!n\.\d+\}"""

    var repository: SpanClickInterface? = null

    companion object {
        var viewArrayListWithString = HashMap<String, Pair<Int, Int>>()
    }

    fun findMatchedRegexInString(versePosition: Int, string: String?): SpannedString {
        if (string.isNullOrEmpty()) {
            return SpannedString(string)
        }
        var tmpString = string!!
        var spanString = SpannedString(string)

        var pattern: Pattern = Pattern.compile(
            verseNoteRegexRule,
            Pattern.CASE_INSENSITIVE
        )

        val matcher = pattern.matcher(string)
        while (matcher.find()) {

            var subMatcher = pattern.matcher(tmpString)
            if (subMatcher.find()) {
                spanString =
                    textSpanCreator(
                        versePosition = versePosition,
                        text = tmpString,
                        subMatcher = subMatcher
                    )
                tmpString = spanString.toString()
            }
        }

        return spanString
    }

    fun textSpanCreator(text: String, subMatcher: Matcher, versePosition: Int): SpannedString {
        var startIndex = subMatcher.start()
        var endIndex = subMatcher.end()
        var noteText = subMatcher.toMatchResult().toString()
        Timber.d("messageE = == " + subMatcher.toMatchResult().toString())
        var newText = buildSpannedString {
            append(text.substring(0, startIndex))
            append(HelperConstant.SPAN_DUMMY_STRING)
            setSpan(
                getImageSpan(noteText),
                startIndex,
                startIndex + 1,
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE
            )
            setSpan(
                getClickSpan(subMatcher.group(), versePosition),
                startIndex,
                startIndex + 1,
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE
            )
            append(text.substring(endIndex, text.length))
        }
        return newText

    }

    private fun getClickSpan(clickValue: String, versePosition: Int): ClickableSpan {
        val clickSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                repository?.spanIconClick(clickValue, versePosition, showingView = widget)
                Timber.d("make toast clickkkk = " + clickValue)
            }
        }
        return clickSpan
    }

    fun getImageSpan(noteText: String): ImageSpan {
        val d = AppCompatResources.getDrawable(
            context,
            spanImageResId
        )

        d!!.setBounds(0, 0, d.intrinsicWidth, d.intrinsicHeight)
        val span: ImageSpan = object : ImageSpan(d, ALIGN_BASELINE) {

            override fun draw(
                canvas: Canvas, text: CharSequence?, start: Int,
                end: Int, x: Float, top: Int, y: Int, bottom: Int,
                @NonNull paint: Paint,
            ) {
                if (mVerticalAlignment != DynamicDrawableSpan.ALIGN_BASELINE) {
                    super.draw(canvas, text, start, end, x, top, y, bottom, paint)
                    return
                }
                val b = drawable

                /*
                                Timber.d("denemeee === x" + x)
                                Timber.d("denemeee ===top " + top)
                                Timber.d("denemeee === y" + y)
                                Timber.d("denemeee === bottom" + bottom)



                                var width = b.intrinsicWidth / 2
                                var height = b.intrinsicHeight / 2
                                Timber.d("denemeee ===width " + width)
                                Timber.d("denemeee ===height " + height)
                 */

                canvas.save()
                val transY = (y - (b.bounds.bottom - 3))
                canvas.translate(x + 5, transY.toFloat())

                viewArrayListWithString.put(noteText, Pair((x + b.intrinsicWidth + 10).toInt(), y))

                b.draw(canvas)

                canvas.restore()
            }

            override fun getSource(): String? {
                return noteText
            }
        }
        return span

    }

}

interface SpanClickInterface {
    fun spanIconClick(value: String, versePosition: Int, showingView: View)
}