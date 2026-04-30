package com.ltthuc.ui.themes

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes

internal val AppShapesM3 = Shapes(
    extraSmall = RoundedCornerShape(Dimens.radiusS / 2),
    small = RoundedCornerShape(Dimens.radiusS),
    medium = RoundedCornerShape(Dimens.radiusM),
    large = RoundedCornerShape(Dimens.radiusL),
    extraLarge = RoundedCornerShape(Dimens.radiusXl),
)
