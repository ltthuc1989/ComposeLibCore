package com.ltthuc.network.impl

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

internal fun Context.getNetworkCapabilities(): NetworkCapabilities? {
    val connectivityManager =
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = connectivityManager.activeNetwork
    return connectivityManager.getNetworkCapabilities(activeNetwork)
}

internal fun NetworkCapabilities?.isConnected(): Boolean {
    this ?: return false
    return hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
            hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
            hasTransport(NetworkCapabilities.TRANSPORT_VPN)
}

internal fun NetworkCapabilities?.isWifiError(): Boolean {
    this ?: return false
    return hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
}