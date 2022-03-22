package ru.tinkoff.android.homework_2.ui.customviews

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import ru.tinkoff.android.homework_2.R
import ru.tinkoff.android.homework_2.model.EmojiWithCount

internal class EmojiWithCountView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paint = TextPaint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val tempBounds = Rect()
    private val tempTextPoint = PointF()

    var emojiCount = ""
        set(value) {
            field = if ( value == "1") "" else value
            requestLayout()
        }

    var emojiCode = ""
    var mMinWidth = 40

    private val resultText: String
        get() = "$emojiCode $emojiCount"

    init {
        val a: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.EmojiWithCountView)
        emojiCount = a.getString(R.styleable.EmojiWithCountView_emojiCount) ?: ""
        emojiCode = a.getString(R.styleable.EmojiWithCountView_emojiCode) ?: "\uD83D\uDE05"
        mMinWidth = a.getDimensionPixelSize(R.styleable.EmojiWithCountView_emojiMinWidth, 40)
        paint.color = a.getColor(R.styleable.EmojiWithCountView_emojiCountColor, Color.BLACK)
        paint.textSize = a.getDimension(
            R.styleable.EmojiWithCountView_emojiCountTextSize,
            DEFAULT_TEXT_SIZE_SP * resources.displayMetrics.density
        )
        a.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        paint.getTextBounds(resultText, 0, resultText.length, tempBounds)

        val textWidth = tempBounds.width()
        val textHeight = tempBounds.height()

        var sumWidth = textWidth + paddingLeft + paddingRight
        val sumHeight = textHeight + paddingTop + paddingBottom

        if (sumWidth < mMinWidth) sumWidth = mMinWidth

        setMeasuredDimension(
            resolveSize(sumWidth, widthMeasureSpec),
            resolveSize(sumHeight, heightMeasureSpec)
        )
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

        private val emojiClickFunc: (v: View) -> Unit = { view ->
            view.isSelected = !view.isSelected
            (view as EmojiWithCountView).apply {
                if (isSelected) emojiCount++ else emojiCount--
                if (emojiCount == "0") {
                    val emojiBox = (parent as FlexBoxLayout)
                    emojiBox.removeView(this)
                    if (emojiBox.childCount == 1) {
                        emojiBox.getChildAt(0).visibility = GONE
                    }
                }
            }
        }

        fun createEmojiWithCountView(
            emojiBox: FlexBoxLayout,
            emoji: EmojiWithCount
        ): EmojiWithCountView {
            val emojiView = LayoutInflater.from(emojiBox.context).inflate(
                R.layout.layout_emoji_with_count_view,
                emojiBox,
                false
            ) as EmojiWithCountView
            emojiView.setOnClickListener(emojiClickFunc)
            emojiView.emojiCode = emoji.code
            emojiView.emojiCount = emoji.count.toString()
            return emojiView
        }
    }
}

internal operator fun String.inc(): String {
    val int = if (this.isEmpty()) 1 else this.toInt()
    return (int + 1).toString()
}

internal operator fun String.dec(): String {
    val int = if (this.isEmpty()) 1 else this.toInt()
    return (int - 1).toString()
}
