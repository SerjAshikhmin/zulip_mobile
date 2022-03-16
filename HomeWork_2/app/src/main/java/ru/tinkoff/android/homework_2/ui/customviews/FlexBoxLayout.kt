package ru.tinkoff.android.homework_2.ui.customviews

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.*
import ru.tinkoff.android.homework_2.R

class FlexBoxLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ViewGroup(context, attrs) {

    private var mMaxWidth: Int? = null

    private val emojiClickFunc: (v: View) -> Unit = { view ->
        view.isSelected = !view.isSelected
        (view as EmojiWithCountView).apply {
            if (view.isSelected) emojiCount++ else emojiCount--
        }
    }

    private val addEmojiClickFunc: (v: View) -> Unit = {
        val newEmoji = LayoutInflater.from(context).inflate(
            R.layout.layout_emoji_with_count_view,
            this,
            false
        ) as EmojiWithCountView
        newEmoji.setOnClickListener(emojiClickFunc)
        newEmoji.emojiCode = "\uD83E\uDD76"
        newEmoji.emojiCount = 9
        this.addView(newEmoji, childCount - 1)
    }

    init {
        val a: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.FlexBoxLayout)
        mMaxWidth = a.getDimensionPixelSize(a.getIndex(0), 250)
        a.recycle()
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
                mMaxWidth ?: MeasureSpec.getSize(widthMeasureSpec)
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
}
