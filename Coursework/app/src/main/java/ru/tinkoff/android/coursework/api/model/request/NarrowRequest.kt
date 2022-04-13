package ru.tinkoff.android.coursework.api.model.request

internal class NarrowRequest (
    val operator: String,
    val operand: String
) {

    override fun toString(): String {
        return "{\"operator\": \"$operator\", \"operand\": \"$operand\"}"
    }
}
