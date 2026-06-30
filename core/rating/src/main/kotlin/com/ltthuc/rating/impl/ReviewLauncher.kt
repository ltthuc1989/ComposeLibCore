package com.ltthuc.rating.impl

import android.app.Activity
import android.content.pm.ApplicationInfo
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.testing.FakeReviewManager
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

@Singleton
internal class ReviewLauncher @Inject constructor() {

    suspend fun launch(
        activity: Activity,
        onComplete: () -> Unit = {},
    ): Boolean = suspendCancellableCoroutine { cont ->
        val manager = if (activity.isDebuggable()) {
            FakeReviewManager(activity.applicationContext)
        } else {
            ReviewManagerFactory.create(activity.applicationContext)
        }
        manager.requestReviewFlow().addOnCompleteListener { req ->
            if (!req.isSuccessful) {
                onComplete()
                if (cont.isActive) cont.resume(false)
                return@addOnCompleteListener
            }
            manager.launchReviewFlow(activity, req.result).apply {
                addOnCompleteListener {
                    onComplete()
                    if (cont.isActive) cont.resume(true)
                }
                addOnFailureListener {
                    onComplete()
                    if (cont.isActive) cont.resume(false)
                }
            }
        }
    }

    private fun Activity.isDebuggable(): Boolean =
        (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
}
