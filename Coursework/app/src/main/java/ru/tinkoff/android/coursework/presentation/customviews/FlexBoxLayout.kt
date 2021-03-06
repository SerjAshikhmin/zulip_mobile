package ru.tinkoff.android.coursework.presentation.customviews

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.core.view.*
import ru.tinkoff.android.coursework.R
import ru.tinkoff.android.coursework.utils.measuredHeightWithMargins
import ru.tinkoff.android.coursework.utils.measuredWidthWithMargins

internal class FlexBoxLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ViewGroup(context, attrs) {

    private var maxWidth: Int?

    init {
        val a: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.FlexBoxLayout)
        maxWidth = a.getDimensionPixelSize(R.styleable.FlexBoxLayout_flexBoxMaxWidth, 0)
        if (maxWidth == 0) maxWidth = null
        a.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var currentStringWidth = 0
        var currentStringHeight = 0
        var totalHeight = 0
        var maxStringWidth = 0
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        children.forEach { child ->
            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0)
            currentStringHeight = maxOf(currentStringHeight, child.measuredHeightWithMargins)
            if (currentStringWidth + child.measuredWidthWithMargins <= maxWidth ?: widthSize) {
                currentStringWidth += child.measuredWidthWithMargins
            } else {
                maxStringWidth = maxOf(currentStringWidth, maxStringWidth)
                currentStringWidth = child.measuredWidthWithMargins
                totalHeight += currentStringHeight
            }
        }
        if (childCount > 1 && maxStringWidth == 0) {
            maxStringWidth = currentStringWidth
            totalHeight = currentStringHeight
        } else {
            maxStringWidth = maxOf(currentStringWidth, maxStringWidth)
            totalHeight += currentStringHeight
        }
        setMeasuredDimension(
            resolveSize(maxStringWidth, widthMeasureSpec),
            resolveSize(totalHeight, heightMeasureSpec)
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (childCount > 0) {
            var currentStart = 0
            var currentTop = getChildAt(0).marginTop
            children.forEach { child ->
                currentStart += child.marginStart
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
                currentStart += child.measuredWidth + child.marginRight
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
