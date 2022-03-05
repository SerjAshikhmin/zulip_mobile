package ru.tinkoff.android.homework_2.customviews

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import ru.tinkoff.android.homework_2.R

class EmojiWithCountView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    var emojiCount = 0
    private var emojiCode = "\uD83D\uDE05"
    private val paint = TextPaint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    private val tempBounds = Rect()
    private val tempTextPoint = PointF()

    init {
        val typedArray: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.EmojiWithCountView)
        emojiCount = typedArray.getInt(R.styleable.EmojiWithCountView_emojiCount, 0)
        emojiCode = typedArray.getString(R.styleable.EmojiWithCountView_emojiCode).orEmpty()
        paint.color = typedArray.getColor(R.styleable.EmojiWithCountView_emojiCountColor, Color.BLACK)
        paint.textSize = typedArray.getDimension(R.styleable.EmojiWithCountView_emojiCountTextSize, 30f)
        typedArray.recycle()
    }

    fun setEmojiCode(code: String) {
        emojiCode = code
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val resultText = "$emojiCode $emojiCount"
        paint.getTextBounds(resultText, 0, resultText.length, tempBounds)

        val textWidth = tempBounds.width()
        val textHeight = tempBounds.height()

        val sumWidth = textWidth + paddingLeft + paddingRight
        val sumHeight = textHeight + paddingTop + paddingBottom

        val resultWidth = resolveSize(sumWidth, widthMeasureSpec)
        val resultHeight = resolveSize(sumHeight, heightMeasureSpec)

        setMeasuredDimension(resultWidth, resultHeight)
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
        canvas.drawText("$emojiCode $emojiCount", tempTextPoint.x, tempTextPoint.y, paint)
    }

    companion object {
        private val SUPPORTED_DRAWABLE_STATE = intArrayOf(android.R.attr.state_selected)
    }

}
