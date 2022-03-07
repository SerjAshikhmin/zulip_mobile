package ru.tinkoff.android.homework_2.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.core.view.*

class FlexBoxLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : ViewGroup(context, attrs) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var totalWidth = 0
        var totalHeight = 0
        var maxWidth = totalWidth
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            measureChild(child, widthMeasureSpec, heightMeasureSpec)
            if (totalWidth + child.measuredWidth + child.marginStart + child.marginEnd >
                MeasureSpec.getSize(widthMeasureSpec)
            ) {
                maxWidth = maxOf(totalWidth + child.measuredWidth + child.marginStart + child.marginEnd, maxWidth)
                totalWidth = child.measuredWidth + child.marginStart + child.marginEnd
                totalHeight += child.measuredHeight + child.marginTop + child.marginBottom
            } else {
                totalWidth += child.measuredWidth + child.marginStart + child.marginEnd
            }
            if (i == 0) {
                totalHeight += child.measuredHeight + child.marginTop + child.marginBottom
            }
        }
        setMeasuredDimension(
            resolveSize(maxWidth, widthMeasureSpec),
            resolveSize(totalHeight, heightMeasureSpec)
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var currentStart = getChildAt(0).marginStart
        var currentTop = getChildAt(0).marginTop
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (currentStart + child.measuredWidth + child.marginEnd > width) {
                currentStart = child.marginStart
                currentTop += child.measuredHeight + child.marginBottom + child.marginTop
            }
            child.layout(currentStart, currentTop, currentStart + child.measuredWidth, currentTop + child.measuredHeight)
            currentStart += child.measuredWidth + child.marginEnd + child.marginStart
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
