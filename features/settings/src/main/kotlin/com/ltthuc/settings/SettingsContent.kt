package com.ltthuc.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material.icons.outlined.PanTool
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ltthuc.ui.themes.Dimens
import com.ltthuc.utils.extensions.findActivity
import com.ltthuc.utils.extensions.openAppOnStore
import com.ltthuc.utils.extensions.openUrl
import com.ltthuc.utils.extensions.suggestFeedback

private val accentBlue = Color(0xFF2A8DFF)

@Composable
fun SettingsContent(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val isPurchased by viewModel.isPurchased.collectAsStateWithLifecycle()
    val price by viewModel.removeAdsPrice.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = context.findActivity()
    val versionName = context.packageManager.getPackageInfo(context.packageName, 0).versionName.orEmpty()

    LaunchedEffect(viewModel) {
        viewModel.userMessage.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(
            start = Dimens.spacingM,
            end = Dimens.spacingM,
            top = Dimens.spacingM,
            bottom = Dimens.spacingXl,
        ),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingL),
    ) {
        if (!isPurchased) {
            item { UpgradeToProSection(price, viewModel, activity) }
        }
        item { MoreAppsSection(activity) }
        item { AboutSupportSection(versionName, activity) }
        item { PrivacyRow(onClick = { context.openUrl(PRIVACY_URL) }) }
    }
}

@Composable
private fun UpgradeToProSection(
    price: String,
    viewModel: SettingsViewModel,
    activity: android.app.Activity?,
) {
    Column {
        SectionHeader(icon = Icons.Outlined.Star, label = stringResource(R.string.settings_section_upgrade))
        SettingsCard {
            SettingRow(
                icon = Icons.Outlined.VisibilityOff,
                label = stringResource(R.string.settings_remove_ads),
                trailing = {
                    if (price.isNotBlank()) {
                        Text(
                            text = price,
                            style = MaterialTheme.typography.bodyMedium,
                            color = LocalContentColor.current.copy(alpha = 0.6f),
                        )
                    }
                },
                onClick = activity?.let { { viewModel.removeAds(it) } },
            )
            ThinDivider()
            SettingRow(
                icon = Icons.Outlined.Refresh,
                label = stringResource(R.string.settings_restore_purchase),
                onClick = { viewModel.restorePurchase() },
            )
        }
        Text(
            text = stringResource(R.string.settings_upgrade_footer),
            style = MaterialTheme.typography.bodySmall,
            color = LocalContentColor.current.copy(alpha = 0.6f),
            modifier = Modifier.padding(start = Dimens.spacingM, top = Dimens.spacingS, end = Dimens.spacingM),
        )
    }
}

@Composable
private fun MoreAppsSection(activity: android.app.Activity?) {
    Column {
        SectionHeader(icon = Icons.Outlined.Apps, label = stringResource(R.string.settings_section_more_apps))
        SettingsCard {
            MoreAppsCatalog.entries.forEachIndexed { index, app ->
                if (index > 0) ThinDivider(startPadding = 76.dp)
                MoreAppRow(app = app, onClick = { activity?.openAppOnStore(app.packageName) })
            }
        }
    }
}

@Composable
private fun AboutSupportSection(versionName: String, activity: android.app.Activity?) {
    Column {
        SectionHeader(icon = Icons.Outlined.Info, label = stringResource(R.string.settings_section_about))
        SettingsCard {
            SettingRow(
                icon = null,
                label = stringResource(R.string.settings_version),
                trailing = {
                    Text(
                        text = versionName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = LocalContentColor.current.copy(alpha = 0.6f),
                    )
                },
            )
            ThinDivider()
            SettingRow(
                icon = Icons.Outlined.Send,
                label = stringResource(R.string.settings_suggest_feedback),
                onClick = activity?.let { { it.suggestFeedback(SUPPORT_EMAIL) } },
            )
            ThinDivider()
            SettingRow(
                icon = Icons.Outlined.StarBorder,
                label = stringResource(R.string.settings_rate_on_store),
                trailing = {
                    Icon(
                        imageVector = Icons.Outlined.OpenInNew,
                        contentDescription = null,
                        tint = accentBlue,
                        modifier = Modifier.size(16.dp),
                    )
                },
                onClick = activity?.let { { it.openAppOnStore(it.packageName) } },
            )
        }
    }
}

@Composable
private fun PrivacyRow(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = Dimens.spacingM, vertical = Dimens.spacingM),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Outlined.PanTool,
            contentDescription = null,
            tint = LocalContentColor.current.copy(alpha = 0.6f),
        )
        Spacer(Modifier.width(Dimens.spacingM))
        Text(
            text = stringResource(R.string.settings_privacy),
            style = MaterialTheme.typography.bodyLarge,
            color = LocalContentColor.current.copy(alpha = 0.7f),
        )
    }
}

@Composable
private fun SectionHeader(icon: ImageVector, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = Dimens.spacingS, vertical = Dimens.spacingS),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = LocalContentColor.current.copy(alpha = 0.6f),
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.width(Dimens.spacingS))
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            color = LocalContentColor.current.copy(alpha = 0.7f),
        )
    }
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 1.dp,
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column { content() }
    }
}

@Composable
private fun SettingRow(
    icon: ImageVector?,
    label: String,
    trailing: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    val labelColor = if (onClick != null) accentBlue else LocalContentColor.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 52.dp)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = Dimens.spacingM, vertical = Dimens.spacingS),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = labelColor,
                modifier = Modifier.size(22.dp),
            )
            Spacer(Modifier.width(Dimens.spacingM))
        }
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = labelColor,
            modifier = Modifier.weight(1f),
        )
        if (trailing != null) {
            Spacer(Modifier.width(Dimens.spacingS))
            trailing()
        }
    }
}

@Composable
private fun MoreAppRow(app: MoreApp, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = Dimens.spacingM, vertical = Dimens.spacingS),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(app.icon),
            contentDescription = app.name,
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(10.dp)),
        )
        Spacer(Modifier.width(Dimens.spacingM))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = app.name,
                style = MaterialTheme.typography.bodyLarge,
                color = accentBlue,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = app.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = LocalContentColor.current.copy(alpha = 0.55f),
            )
        }
        Icon(
            imageVector = Icons.Outlined.OpenInNew,
            contentDescription = null,
            tint = accentBlue,
            modifier = Modifier.size(18.dp),
        )
    }
}

@Composable
private fun ThinDivider(startPadding: androidx.compose.ui.unit.Dp = Dimens.spacingM) {
    Box(modifier = Modifier.padding(start = startPadding)) {
        Divider(
            thickness = 0.5.dp,
            color = LocalContentColor.current.copy(alpha = 0.12f),
        )
    }
}
