package ru.tinkoff.android.coursework.model

internal data class Topic (
    val name: String,
    val channelName: String,
    val color: Int,
    val messages: MutableList<Any>
)
