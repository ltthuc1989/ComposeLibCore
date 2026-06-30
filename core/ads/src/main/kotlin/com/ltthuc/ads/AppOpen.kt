package com.ltthuc.ads

interface AppOpen {
    fun restoreAds()
    fun closeAds()

    companion object {
        val NoOp: AppOpen = object : AppOpen {
            override fun restoreAds() = Unit
            override fun closeAds() = Unit
        }
    }
}
