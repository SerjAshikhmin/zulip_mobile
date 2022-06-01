package ru.tinkoff.android.coursework.data.api.model.request

internal class SubscriptionsRequest (
    val name: String,
    val description: String = ""
) {

    override fun toString(): String {
        return "{\"name\": \"$name\", \"description\": \"$description\"}"
    }

}
