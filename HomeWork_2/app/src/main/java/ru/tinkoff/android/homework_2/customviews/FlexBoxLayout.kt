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
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, totalHeight)
            if (totalWidth + child.measuredWidth + child.marginLeft + child.marginRight > width) {
                totalWidth = 0
                totalHeight += child.measuredHeight + child.marginTop + child.marginBottom
            } else {
                totalWidth += child.measuredWidth + child.marginLeft + child.marginRight
            }

        }
        setMeasuredDimension(
            resolveSize(totalWidth, widthMeasureSpec),
            resolveSize(totalHeight, heightMeasureSpec)
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var currentTop = getChildAt(0).marginTop
        var currentStart = getChildAt(0).marginStart
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (currentStart + child.measuredWidth + child.marginLeft + child.marginRight > width) {
                currentStart = getChildAt(0).marginStart
                currentTop += child.measuredHeight + child.marginTop + child.marginBottom
            }
            child.layout(currentStart, currentTop, currentStart + child.measuredWidth, currentTop + child.measuredHeight)
            currentStart += child.measuredWidth + child.marginLeft + child.marginRight
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
