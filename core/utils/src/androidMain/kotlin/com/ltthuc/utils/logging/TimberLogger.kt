package com.ltthuc.utils.logging

import timber.log.Timber

class TimberLogger : ILogger {
    override fun d(tag: String, message: String, throwable: Throwable?) {
        Timber.tag(tag).d(throwable, message)
    }

    override fun i(tag: String, message: String, throwable: Throwable?) {
        Timber.tag(tag).i(throwable, message)
    }

    override fun w(tag: String, message: String, throwable: Throwable?) {
        Timber.tag(tag).w(throwable, message)
    }

    override fun e(tag: String, message: String, throwable: Throwable?) {
        Timber.tag(tag).e(throwable, message)
    }
}
