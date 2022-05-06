package ru.tinkoff.android.coursework

import androidx.test.core.app.ActivityScenario
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import org.junit.Assert
import org.junit.Test
import ru.tinkoff.android.coursework.screen.SubscribedStreamsScreen
import ru.tinkoff.android.coursework.screen.SubscribedStreamsScreen.StreamItem

internal class SubscribedStreamsListTest : TestCase() {

    @Test
    fun showSubscribedStreamsList_ByDefault() = run {
        ActivityScenario.launch(MainActivity::class.java)

        step("Список топиков скрыт") {
            SubscribedStreamsScreen.streamsList.children<StreamItem> {
                topicsList {
                    isNotDisplayed()
                }
            }
        }
        step("Открыть список топиков") {
            SubscribedStreamsScreen.streamsList.childAt<StreamItem>(0) {
                arrowIcon.click()
            }
        }
        step("Список топиков открыт") {
            SubscribedStreamsScreen.streamsList.childAt<StreamItem>(0) {
                topicsList {
                    isDisplayed()
                    var childCount = 0
                    children<SubscribedStreamsScreen.TopicItem> { childCount++ }
                    // тут может падать, т.к. кол-во топиков может меняться
                    // нужно поменять значение или мокать апи
                    Assert.assertEquals(12, childCount)
                }
            }
        }
    }

}
