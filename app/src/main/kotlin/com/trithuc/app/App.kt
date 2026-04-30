package com.trithuc.app

import android.app.Application
import com.ltthuc.ads.AdsSettings
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import timber.log.Timber

@HiltAndroidApp
internal class App : Application() {

    @Inject lateinit var billingAcknowledger: BillingAcknowledger

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        if (!BuildConfig.FORCE_HIDE_ADS) {
            MobileAds.initialize(applicationContext)
            AdsSettings.addDeviceTest(this)
        }
        billingAcknowledger.start()
    }
}
