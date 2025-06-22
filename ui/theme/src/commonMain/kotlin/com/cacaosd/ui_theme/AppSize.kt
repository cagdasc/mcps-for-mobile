package com.cacaosd.ui_theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class AppSize(
    val xsmall: Dp,
    val small: Dp,
    val medium: Dp,
    val xmedium: Dp,
    val large: Dp,
    val xlarge: Dp,
    val xxlarge: Dp,
    val xxxlarge: Dp,
    val x4large: Dp,
)

fun getSizes(): AppSize {
    return AppSize(
        xsmall = 2.dp,
        small = 4.dp,
        medium = 8.dp,
        xmedium = 12.dp,
        large = 16.dp,
        xlarge = 20.dp,
        xxlarge = 24.dp,
        xxxlarge = 32.dp,
        x4large = 48.dp
    )
}
