package ru.tinkoff.android.coursework.domain.model

internal data class Stream(
    val streamId: Long = 0,
    val name: String,
    var topics: List<Topic>
)
