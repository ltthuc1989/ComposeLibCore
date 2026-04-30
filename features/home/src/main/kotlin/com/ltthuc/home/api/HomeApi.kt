package com.ltthuc.home.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ltthuc.home.HomeContent
import com.ltthuc.utils.secrets.ISecretAdsKey

@Composable
fun Home(
    adsKey: ISecretAdsKey,
    modifier: Modifier = Modifier,
) {
    HomeContent(adsKey = adsKey, modifier = modifier)
}
