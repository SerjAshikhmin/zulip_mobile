package ru.tinkoff.android.homework_2.customviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.tinkoff.android.homework_2.databinding.MessageViewGroupLayoutBinding
import kotlin.math.abs

class MessageViewGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : ViewGroup(context, attrs) {

    private var binding: MessageViewGroupLayoutBinding =
        MessageViewGroupLayoutBinding.inflate(LayoutInflater.from(context), this)

    private var messageTop = 0
    private var usernameStart = 0

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val avatar = binding.avatar
        val username = binding.username
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
            username,
            widthMeasureSpec,
            avatar.measuredWidth,
            heightMeasureSpec,
            0
        )

        var contentWidth = avatar.measuredWidthWithMargins + username.measuredWidthWithMargins
        var contentHeight = maxOf(avatar.measuredHeightWithMargins, username.measuredHeightWithMargins)

        measureChildWithMargins(
            message,
            widthMeasureSpec,
            avatar.measuredWidth,
            heightMeasureSpec,
            0
        )

        contentWidth += abs(message.measuredWidthWithMargins - username.measuredWidthWithMargins)
        contentHeight += message.measuredHeightWithMargins

        measureChildWithMargins(
            emojiBox,
            widthMeasureSpec,
            avatar.measuredWidth,
            heightMeasureSpec,
            0
        )

        contentWidth += abs(emojiBox.measuredWidthWithMargins - message.measuredWidthWithMargins)
        contentHeight += emojiBox.measuredHeightWithMargins

        setMeasuredDimension(
            resolveSize(contentWidth, widthMeasureSpec),
            resolveSize(contentHeight, heightMeasureSpec)
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val avatar = binding.avatar
        val username = binding.username
        val message = binding.message
        val emojiBox = binding.emojiBox

        avatar.layout(
            avatar.marginStart,
            avatar.marginTop,
            avatar.marginStart + avatar.measuredWidth,
            avatar.marginTop + avatar.measuredHeight
        )

        usernameStart = avatar.right + avatar.marginStart + avatar.marginEnd
        username.layout(
            usernameStart + username.marginStart,
            username.marginTop,
            usernameStart + username.measuredWidthWithMargins,
            username.marginTop + username.measuredHeight
        )

        messageTop = maxOf(avatar.measuredHeightWithMargins, username.measuredHeightWithMargins)
        message.layout(
            usernameStart + message.marginStart,
            messageTop,
            usernameStart + message.measuredWidth + message.marginEnd,
            messageTop + message.marginBottom + message.measuredHeight
        )

        val emojiBoxTop = messageTop + message.measuredHeightWithMargins
        emojiBox.layout(
            usernameStart,
            emojiBoxTop,
            usernameStart + emojiBox.measuredWidthWithMargins,
            emojiBoxTop + emojiBox.marginBottom + emojiBox.measuredHeight
        )
    }

    private val paint = Paint().apply {
        color = Color.GRAY
    }

    override fun dispatchDraw(canvas: Canvas) {
        canvas.drawRoundRect(
            usernameStart.toFloat(),
            binding.username.marginTop.toFloat() / 2,
            usernameStart.toFloat() + binding.message.measuredWidthWithMargins,
            messageTop.toFloat() + binding.message.measuredHeightWithMargins,
            20f,
            20f,
            paint
        )
        super.dispatchDraw(canvas)
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
