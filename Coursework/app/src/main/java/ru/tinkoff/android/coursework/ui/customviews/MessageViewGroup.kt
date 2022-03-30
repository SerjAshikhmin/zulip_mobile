package ru.tinkoff.android.coursework.ui.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.*
import ru.tinkoff.android.coursework.databinding.LayoutMessageViewGroupBinding

internal class MessageViewGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ViewGroup(context, attrs) {

    var binding: LayoutMessageViewGroupBinding =
        LayoutMessageViewGroupBinding.inflate(LayoutInflater.from(context), this)

    private var messageStart = 0

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val avatar = binding.avatar
        val message = binding.message
        val emojiBox = binding.emojiBox

        measureChildWithMargins(
            avatar,
            widthMeasureSpec,
            0,
            heightMeasureSpec,
            0
        )

        measureChildWithMargins(
            message,
            widthMeasureSpec,
            avatar.measuredWidth,
            heightMeasureSpec,
            0
        )

        var contentWidth = avatar.measuredWidthWithMargins + message.measuredWidthWithMargins
        var contentHeight = avatar.measuredHeightWithMargins + message.measuredHeightWithMargins

        measureChildWithMargins(
            emojiBox,
            widthMeasureSpec,
            avatar.measuredWidth,
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
        val avatar = binding.avatar
        val message = binding.message
        val emojiBox = binding.emojiBox

        avatar.layout(
            avatar.marginStart,
            avatar.marginTop,
            avatar.marginStart + avatar.measuredWidth,
            avatar.marginTop + avatar.measuredHeight
        )

        messageStart = avatar.right + avatar.marginStart + avatar.marginEnd
        message.layout(
            messageStart + message.marginStart,
            0,
            messageStart + message.measuredWidth + message.marginEnd,
            message.marginBottom + message.measuredHeight
        )

        val emojiBoxTop = message.measuredHeightWithMargins
        val firstChild = emojiBox.getChildAt(0)
        emojiBox.layout(
            messageStart - firstChild.marginStart,
            emojiBoxTop,
            messageStart + emojiBox.measuredWidthWithMargins - firstChild.marginStart,
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
