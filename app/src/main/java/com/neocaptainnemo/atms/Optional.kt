package com.neocaptainnemo.atms

data class Optional<T>(val value: T?) {
    fun isNull() = value == null

    fun isNotNull() = value != null

    fun safeValue() = value!!

    companion object {
        fun<T> nullValue():Optional<T> = Optional(null)
    }
}

fun <T> T?.carry() = Optional(this)

