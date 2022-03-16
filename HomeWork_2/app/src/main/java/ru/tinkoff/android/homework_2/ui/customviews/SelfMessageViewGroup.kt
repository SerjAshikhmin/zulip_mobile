package ru.tinkoff.android.homework_2.ui.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.marginBottom
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import ru.tinkoff.android.homework_2.databinding.LayoutSelfMessageViewGroupBinding

class SelfMessageViewGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ViewGroup(context, attrs) {

    var binding: LayoutSelfMessageViewGroupBinding =
        LayoutSelfMessageViewGroupBinding.inflate(LayoutInflater.from(context), this)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val message = binding.message
        val emojiBox = binding.emojiBox

        measureChildWithMargins(
            message,
            widthMeasureSpec,
            0,
            heightMeasureSpec,
            0
        )

        var contentWidth = message.measuredWidthWithMargins
        var contentHeight = message.measuredHeightWithMargins

        measureChildWithMargins(
            emojiBox,
            widthMeasureSpec,
            0,
            heightMeasureSpec,
            0
        )

        if (emojiBox.measuredWidthWithMargins > message.measuredWidthWithMargins) {
            contentWidth += emojiBox.measuredWidthWithMargins - message.measuredWidthWithMargins
        }
        contentHeight += emojiBox.measuredHeightWithMargins

        setMeasuredDimension(
            resolveSize(contentWidth, widthMeasureSpec),
            resolveSize(contentHeight, heightMeasureSpec)
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val message = binding.message
        val emojiBox = binding.emojiBox

        message.layout(
            r - (message.measuredWidthWithMargins + message.paddingStart + message.paddingEnd),
            0,
            r - (message.marginStart + message.marginEnd + message.paddingStart + message.paddingEnd),
            message.measuredHeight
        )

        val emojiBoxTop = message.measuredHeightWithMargins
        emojiBox.layout(
            r - emojiBox.measuredWidthWithMargins,
            emojiBoxTop,
            r - emojiBox.marginEnd * 2,
            emojiBoxTop + emojiBox.marginBottom + emojiBox.measuredHeight
        )
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
