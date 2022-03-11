package ru.tinkoff.android.homework_2.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import ru.tinkoff.android.homework_2.R

class FlexBoxLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ViewGroup(context, attrs) {

    private val emojiClickFunc: (v: View) -> Unit = { view ->
        view.isSelected = !view.isSelected
        val emojiView = view as EmojiWithCountView
        emojiView.emojiCount =
            if (view.isSelected) ++emojiView.emojiCount else --emojiView.emojiCount
    }

    private val addEmojiClickFunc: (v: View) -> Unit = { view ->
        val newEmoji = LayoutInflater.from(context).inflate(
            R.layout.emoji_with_count_view_layout,
            this,
            false
        )as EmojiWithCountView
        newEmoji.setOnClickListener(emojiClickFunc)
        newEmoji.emojiCode = "\uD83E\uDD76"
        newEmoji.emojiCount = 9
        this.addView(newEmoji, childCount - 1)
    }

    override fun onViewAdded(child: View?) {
        super.onViewAdded(child)
        when (child) {
            is EmojiWithCountView -> child.setOnClickListener(emojiClickFunc)
            is ImageView -> child.setOnClickListener(addEmojiClickFunc)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var totalWidth = 0
        var totalHeight = 0
        var maxWidth = totalWidth
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            measureChild(child, widthMeasureSpec, heightMeasureSpec)
            if (totalWidth + child.measuredWidthWithMargins >
                MeasureSpec.getSize(widthMeasureSpec)
            ) {
                maxWidth = maxOf(totalWidth + child.measuredWidthWithMargins, maxWidth)
                totalWidth = child.measuredWidthWithMargins
                totalHeight += child.measuredHeightWithMargins
            } else {
                totalWidth += child.measuredWidthWithMargins
            }
            if (i == 0) {
                totalHeight += child.measuredHeightWithMargins
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
                currentTop += child.measuredHeightWithMargins
            }
            child.layout(currentStart, currentTop, currentStart + child.measuredWidth, currentTop + child.measuredHeight)
            currentStart += child.measuredWidthWithMargins
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

    private val View.marginTop: Int
        get() = (layoutParams as MarginLayoutParams).topMargin

    private val View.marginBottom: Int
        get() = (layoutParams as MarginLayoutParams).bottomMargin

    private val View.marginEnd: Int
        get() = (layoutParams as MarginLayoutParams).rightMargin

    private val View.marginStart: Int
        get() = (layoutParams as MarginLayoutParams).leftMargin

    private val View.measuredWidthWithMargins: Int
        get() {
            val params = layoutParams as MarginLayoutParams
            return measuredWidth + params.rightMargin + params.leftMargin
        }

    private val View.measuredHeightWithMargins: Int
        get() {
            val params = layoutParams as MarginLayoutParams
            return measuredHeight + params.topMargin + params.bottomMargin
        }

}
