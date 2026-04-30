package com.ltthuc.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ltthuc.ads.AdBanner
import com.ltthuc.ads.BannerType
import com.ltthuc.ui.themes.Dimens
import com.ltthuc.utils.secrets.ISecretAdsKey

@Composable
fun HomeContent(
    adsKey: ISecretAdsKey,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        AdBanner(
            secret = adsKey,
            bannerType = BannerType.ADAPTIVE_BANNER,
            isAnchored = false,
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimens.spacingL),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Home",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            )
        }
    }
}
