package ru.tinkoff.android.coursework.model.request

import kotlinx.serialization.Serializable

@Serializable
internal data class NarrowRequest (
    val operator: String,
    val operand: String
) {
    override fun toString(): String {
        return "{\"operator\": \"$operator\", \"operand\": \"$operand\"}"
    }
}
