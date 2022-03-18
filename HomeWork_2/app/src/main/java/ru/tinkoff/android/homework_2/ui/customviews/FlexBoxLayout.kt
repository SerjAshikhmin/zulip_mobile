package ru.tinkoff.android.homework_2.ui.customviews

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.core.view.*
import ru.tinkoff.android.homework_2.R

class FlexBoxLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ViewGroup(context, attrs) {

    private var mMaxWidth: Int? = null

    init {
        val a: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.FlexBoxLayout)
        mMaxWidth = a.getDimensionPixelSize(R.styleable.FlexBoxLayout_flexBoxMaxWidth, 250)
        a.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var currentStringWidth = 0
        var totalHeight = 0
        var maxWidth = currentStringWidth
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0)
            if (currentStringWidth + child.measuredWidthWithMargins >
                mMaxWidth ?: widthSize
            ) {
                maxWidth = maxOf(currentStringWidth + child.measuredWidthWithMargins, maxWidth)
                currentStringWidth = child.measuredWidthWithMargins
                totalHeight += child.measuredHeightWithMargins
            } else {
                currentStringWidth += child.measuredWidthWithMargins
            }
            if (i == 0) {
                totalHeight += child.measuredHeightWithMargins
            }
        }
        if (childCount > 0 && maxWidth == 0) maxWidth = currentStringWidth
        setMeasuredDimension(
            resolveSize(maxWidth, widthMeasureSpec),
            resolveSize(totalHeight, heightMeasureSpec)
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (childCount > 0) {
            var currentStart = getChildAt(0).marginStart
            var currentTop = getChildAt(0).marginTop
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                if (currentStart + child.measuredWidth + child.marginEnd > width) {
                    currentStart = child.marginStart
                    currentTop += child.measuredHeightWithMargins
                }
                child.layout(
                    currentStart,
                    currentTop,
                    currentStart + child.measuredWidth,
                    currentTop + child.measuredHeight
                )
                currentStart += child.measuredWidthWithMargins
            }
        }
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    override fun checkLayoutParams(p: LayoutParams): Boolean {
        return p is MarginLayoutParams
    }

    override fun generateLayoutParams(p: LayoutParams): LayoutParams {
        return MarginLayoutParams(p)
    }
}
