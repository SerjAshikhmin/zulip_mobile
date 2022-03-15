package ru.tinkoff.android.homework_2.data

import ru.tinkoff.android.homework_2.model.Message
import ru.tinkoff.android.homework_2.model.Reaction
import java.time.LocalDateTime

class Messages {

    companion object {

        internal const val SELF_USER_NAME = "Сергей Ашихмин"
        internal val messages = listOf(
            Message(
                1,
                "Дмитрий Макаров",
                "Не следует, однако, забывать о том, что социально-экономическое развитие способствует подготовке и реализации ключевых компонентов планируемого обновления.",
                listOf(
                    Reaction(1, "👍"),
                    Reaction(2, "👍"),
                    Reaction(1, "😌"),
                    Reaction(1, "😎"),
                    Reaction(1, "😛"),
                    Reaction(1, "😏"),
                    Reaction(3, "👍"),
                    Reaction(4, "👍"),
                    Reaction(5, "👍"),
                    Reaction(6, "👍"),
                    Reaction(7, "👍"),
                    Reaction(8, "👍"),
                    Reaction(9, "👍"),
                ),
                LocalDateTime.now()
            ),
            Message(
                2,
                "Сергей Ашихмин",
                "Практический опыт показывает, что социально-экономическое развитие напрямую зависит от форм воздействия",
                listOf(),
                LocalDateTime.now()
            ),
            Message(
                3,
                "Дмитрий Макаров",
                "Равным образом реализация намеченного плана развития напрямую зависит от дальнейших направлений развитая системы массового участия.",
                listOf(),
                LocalDateTime.now()
            ),
        )
    }
}
