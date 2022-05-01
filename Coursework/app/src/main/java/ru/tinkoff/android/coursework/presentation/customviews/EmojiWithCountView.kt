package ru.tinkoff.android.coursework.presentation.customviews

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import ru.tinkoff.android.coursework.R
import ru.tinkoff.android.coursework.domain.model.EmojiWithCount
import ru.tinkoff.android.coursework.presentation.screens.adapters.OnEmojiClickListener

internal class EmojiWithCountView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : View(context, attrs) {

    var emojiCount = 0
        set(value) {
            val oldValue = field
            field = value
            if (oldValue.toString().length != value.toString().length) {
                requestLayout()
            }
        }

    var emojiCode = ""

    var messageId = 0L

    private val paint = TextPaint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val tempBounds = Rect()
    private val tempTextPoint = PointF()
    private var minWidth = 40

    private val resultText: String
        get() = "$emojiCode $emojiCount"

    init {
        val attrs: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.EmojiWithCountView)
        emojiCount = attrs.getInt(R.styleable.EmojiWithCountView_emojiCount, 0)
        emojiCode = attrs.getString(R.styleable.EmojiWithCountView_emojiCode) ?: "\uD83D\uDE05"
        minWidth = attrs.getDimensionPixelSize(R.styleable.EmojiWithCountView_emojiMinWidth, 40)
        paint.color = attrs.getColor(R.styleable.EmojiWithCountView_emojiCountColor, Color.BLACK)
        paint.textSize = attrs.getDimension(
            R.styleable.EmojiWithCountView_emojiCountTextSize,
            DEFAULT_TEXT_SIZE_SP * resources.displayMetrics.density
        )
        attrs.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (visibility == VISIBLE) {
            paint.getTextBounds(resultText, 0, resultText.length, tempBounds)

            val textWidth = tempBounds.width()
            val textHeight = tempBounds.height()

            var sumWidth = textWidth + paddingLeft + paddingRight
            val sumHeight = textHeight + paddingTop + paddingBottom

            if (sumWidth < minWidth) sumWidth = minWidth

            setMeasuredDimension(
                resolveSize(sumWidth, widthMeasureSpec),
                resolveSize(sumHeight, heightMeasureSpec)
            )
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        tempTextPoint.x = w / 2f - tempBounds.width() / 2f
        tempTextPoint.y = h / 2f + tempBounds.height() / 2f - paint.descent()
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace + SUPPORTED_DRAWABLE_STATE.size)
        if (isSelected) {
            mergeDrawableStates(drawableState, SUPPORTED_DRAWABLE_STATE)
        }
        return drawableState
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawText(resultText, tempTextPoint.x, tempTextPoint.y, paint)
    }

    companion object {

        private val SUPPORTED_DRAWABLE_STATE = intArrayOf(android.R.attr.state_selected)
        private const val DEFAULT_TEXT_SIZE_SP = 15

        fun createEmojiWithCountView(
            emojiBox: FlexBoxLayout,
            emoji: EmojiWithCount,
            messageId: Long,
            emojiClickListener: OnEmojiClickListener
        ): EmojiWithCountView {
            val emojiView = LayoutInflater.from(emojiBox.context).inflate(
                R.layout.layout_emoji_with_count_view,
                emojiBox,
                false
            ) as EmojiWithCountView
            emojiView.messageId = messageId
            emojiView.emojiCode = if (emoji.code.any { it in 'a'..'f' }) {
                String(Character.toChars(emoji.code.toInt(16)))
            } else {
                emoji.code
            }
            emojiView.setOnClickListener {
                emojiClickListener.onEmojiClick(emojiView)
            }
            emojiView.emojiCount = emoji.count
            return emojiView
        }
    }

}
