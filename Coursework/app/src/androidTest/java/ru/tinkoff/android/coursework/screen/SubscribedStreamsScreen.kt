package ru.tinkoff.android.coursework.screen

import android.view.View
import com.kaspersky.kaspresso.screens.KScreen
import io.github.kakaocup.kakao.image.KImageView
import io.github.kakaocup.kakao.recycler.KRecyclerItem
import io.github.kakaocup.kakao.recycler.KRecyclerView
import org.hamcrest.Matcher
import ru.tinkoff.android.coursework.R
import ru.tinkoff.android.coursework.presentation.screens.SubscribedStreamsListFragment

internal object SubscribedStreamsScreen : KScreen<SubscribedStreamsScreen>() {

    override val layoutId: Int = R.layout.fragment_streams_list
    override val viewClass: Class<*> = SubscribedStreamsListFragment::class.java

    val streamsList = KRecyclerView(
        { withId(R.id.streams_list) },
        { itemType { StreamItem(it) } }
    )

    class StreamItem(parent: Matcher<View>) : KRecyclerItem<StreamItem>(parent) {

        val arrowIcon = KImageView(parent) { withId(R.id.arrow_icon) }
        val topicsList = KRecyclerView(
            parent,
            { withId(R.id.topics_list) },
            { itemType { TopicItem(parent) } }
        )
    }

    class TopicItem(parent: Matcher<View>) : KRecyclerItem<TopicItem>(parent)

}
