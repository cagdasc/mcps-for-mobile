package com.cacaosd.ui_theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cacaosd.ui_theme.typography.AppTypography

object AppTheme {

    val shapes: AppShapes
        @Composable
        get() = LocalAppShapes.current

    val sizes: AppSize
        @Composable
        get() = LocalAppSizes.current

    @Composable
    operator fun invoke(
        isDarkTheme: Boolean = isSystemInDarkTheme(),
        content: @Composable () -> Unit
    ) {
        val colorScheme = if (isDarkTheme) {
            AppColors.DarkColorScheme
        } else {
            AppColors.LightColorScheme
        }

        val extendedColorScheme = createExtendedColorScheme(colorScheme, isDarkTheme)
        val typography = AppTypography()

        CompositionLocalProvider(LocalAppSizes provides getSizes()) {
            MaterialTheme(
                colorScheme = colorScheme,
                typography = typography,
                content = content
            )
        }
    }
}

val LocalAppShapes = staticCompositionLocalOf {
    AppShapes(
        small = RoundedCornerShape(0.dp),
        medium = RoundedCornerShape(0.dp),
        large = RoundedCornerShape(0.dp),
    )
}

val LocalAppSizes = staticCompositionLocalOf {
    AppSize(
        xsmall = Dp.Unspecified,
        small = Dp.Unspecified,
        medium = Dp.Unspecified,
        xmedium = Dp.Unspecified,
        large = Dp.Unspecified,
        xlarge = Dp.Unspecified,
        xxlarge = Dp.Unspecified,
        xxxlarge = Dp.Unspecified,
        x4large = Dp.Unspecified,
    )
}
