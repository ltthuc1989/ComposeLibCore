package com.ltthuc.utils

import android.content.Context
import androidx.annotation.StringRes

sealed class Text {

    data class Plain(val value: String) : Text()

    data class Resource(
        @StringRes val resId: Int,
        val formatArgs: List<Any>? = null
    ) : Text()
}

fun Text.asString(context: Context): String {
    return when (this) {
        is Text.Plain -> value
        is Text.Resource -> if (formatArgs == null) {
            context.getString(resId)
        } else {
            context.getString(resId, *formatArgs.toTypedArray())
        }
    }
}