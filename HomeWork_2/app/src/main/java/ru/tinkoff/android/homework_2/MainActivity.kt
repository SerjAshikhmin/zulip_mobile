package ru.tinkoff.android.homework_2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.tinkoff.android.homework_2.customviews.EmojiWithCountView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<EmojiWithCountView>(R.id.emoji).setOnClickListener { view ->
            view.isSelected = !view.isSelected
            val emojiView = view as EmojiWithCountView
            val oldEmojiCount = emojiView.emojiCount
            val newEmojiCount = if (view.isSelected) ++emojiView.emojiCount else --emojiView.emojiCount
            emojiView.emojiCount = newEmojiCount
            if (oldEmojiCount.toString().length != newEmojiCount.toString().length) {
                emojiView.requestLayout()
            }
        }
    }

}
