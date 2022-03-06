package ru.tinkoff.android.homework_2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import ru.tinkoff.android.homework_2.customviews.EmojiWithCountView
import ru.tinkoff.android.homework_2.customviews.FlexBoxLayout

class MainActivity : AppCompatActivity() {

    private val emojiClickFunc: (v: View) -> Unit = { view ->
        view.isSelected = !view.isSelected
        val emojiView = view as EmojiWithCountView
        emojiView.emojiCount =
            if (view.isSelected) ++emojiView.emojiCount else --emojiView.emojiCount
    }

    private val addEmojiClickFunc: (v: View) -> Unit = { view ->
        val addEmojiView = view as ImageView
        val flexBoxView = addEmojiView.parent as FlexBoxLayout
        val newEmoji = layoutInflater.inflate(
            R.layout.emoji_with_count_view_layout,
            flexBoxView,
            false
        ) as EmojiWithCountView
        newEmoji.setOnClickListener(emojiClickFunc)
        //newEmoji.emojiCode = "\uD83E\uDD76"
        //newEmoji.emojiCount = 9
        flexBoxView.addView(newEmoji, flexBoxView.childCount - 1)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<EmojiWithCountView>(R.id.emoji).setOnClickListener(emojiClickFunc)
        findViewById<EmojiWithCountView>(R.id.emoji2).setOnClickListener(emojiClickFunc)
        findViewById<EmojiWithCountView>(R.id.emoji3).setOnClickListener(emojiClickFunc)
        findViewById<EmojiWithCountView>(R.id.emoji4).setOnClickListener(emojiClickFunc)
        findViewById<EmojiWithCountView>(R.id.emoji5).setOnClickListener(emojiClickFunc)
        findViewById<EmojiWithCountView>(R.id.emoji6).setOnClickListener(emojiClickFunc)
        findViewById<ImageView>(R.id.add_emoji).setOnClickListener(addEmojiClickFunc)
    }

}
