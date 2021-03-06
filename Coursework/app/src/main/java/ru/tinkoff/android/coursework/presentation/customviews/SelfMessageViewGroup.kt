package ru.tinkoff.android.coursework.presentation.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.children
import androidx.core.view.marginBottom
import androidx.core.view.marginEnd
import ru.tinkoff.android.coursework.databinding.LayoutSelfMessageViewGroupBinding
import ru.tinkoff.android.coursework.utils.measuredHeightWithMargins
import ru.tinkoff.android.coursework.utils.measuredWidthWithMargins

internal class SelfMessageViewGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ViewGroup(context, attrs) {

    var binding: LayoutSelfMessageViewGroupBinding =
        LayoutSelfMessageViewGroupBinding.inflate(LayoutInflater.from(context), this)

    var messageId = 0L

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
            r - message.measuredWidthWithMargins - marginEnd,
            0,
            r - marginEnd,
            message.measuredHeightWithMargins
        )

        val emojiBoxTop = message.measuredHeightWithMargins
        val childWithMaxRight = emojiBox.children
            .filter { right == emojiBox.children.maxOfOrNull { right } }.first()
        val lastChildMarginEnd = childWithMaxRight.marginEnd
        emojiBox.layout(
            r - emojiBox.measuredWidthWithMargins + lastChildMarginEnd - marginEnd,
            emojiBoxTop,
            r + lastChildMarginEnd - marginEnd,
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
