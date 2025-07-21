package com.cacaosd.ui_theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Primary color palette
object AppColors {
    // Light Theme Color Palette
    val LightColorScheme = lightColorScheme(
        primary = Color(0xFF25D366),           // WhatsApp's signature green
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFA8F0C0),
        onPrimaryContainer = Color(0xFF004C1A),
        inversePrimary = Color(0xFF6CDE7E),

        secondary = Color(0xFF006D3E),
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFF97F0B8),
        onSecondaryContainer = Color(0xFF00210D),

        tertiary = Color(0xFF006C4A),
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFF87F7C4),
        onTertiaryContainer = Color(0xFF002114),

        background = Color(0xFFFAFAFA),
        onBackground = Color(0xFF1C1C1C),
        surface = Color(0xFFFFFFFF),
        onSurface = Color(0xFF121212),
        surfaceVariant = Color(0xFFF0F0F0),
        onSurfaceVariant = Color(0xFF424242),
        surfaceTint = Color(0xFF25D366),

        inverseSurface = Color(0xFF2C2C2C),
        inverseOnSurface = Color(0xFFF5F5F5),

        error = Color(0xFFBA1A1A),
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFFFFDAD6),
        onErrorContainer = Color(0xFF410002),

        outline = Color(0xFF757575),
        outlineVariant = Color(0xFFBDBDBD),
        scrim = Color(0xFF000000).copy(alpha = 0.5f)
    )

    // Dark Theme Color Palette
    val DarkColorScheme = darkColorScheme(
        primary = Color(0xFF6CDE7E),
        onPrimary = Color(0xFF003910),
        primaryContainer = Color(0xFF005317),
        onPrimaryContainer = Color(0xFF87F7A4),
        inversePrimary = Color(0xFF25D366),

        secondary = Color(0xFF7CDA9D),
        onSecondary = Color(0xFF003920),
        secondaryContainer = Color(0xFF00522E),
        onSecondaryContainer = Color(0xFF97F0B8),

        tertiary = Color(0xFF6BDBA7),
        onTertiary = Color(0xFF003826),
        tertiaryContainer = Color(0xFF005338),
        onTertiaryContainer = Color(0xFF87F7C4),

        background = Color(0xFF121212),
        onBackground = Color(0xFFE0E0E0),
        surface = Color(0xFF1E1E1E),
        onSurface = Color(0xFFE0E0E0),
        surfaceVariant = Color(0xFF2C2C2C),
        onSurfaceVariant = Color(0xFFB0B0B0),
        surfaceTint = Color(0xFF6CDE7E),

        inverseSurface = Color(0xFFF5F5F5),
        inverseOnSurface = Color(0xFF121212),

        error = Color(0xFFFFB4AB),
        onError = Color(0xFF690005),
        errorContainer = Color(0xFF93000A),
        onErrorContainer = Color(0xFFFFDAD6),

        outline = Color(0xFF8C8C8C),
        outlineVariant = Color(0xFF424242),
        scrim = Color(0xFF000000).copy(alpha = 0.7f)
    )

    // Chat-specific colors (can be used as extension)
    object ChatColors {
        val LightChatBackground = Color(0xFFECE5DD)
        val LightSentMessageBackground = Color(0xFFDCF8C6)
        val LightReceivedMessageBackground = Color(0xFFFFFFFF)

        val DarkChatBackground = Color(0xFF121212)
        val DarkSentMessageBackground = Color(0xFF075E54)
        val DarkReceivedMessageBackground = Color(0xFF262D31)
    }

}
// Extended color scheme with additional surface containers
data class ExtendedColorScheme(
    val colorScheme: ColorScheme,
    val surfaceDim: Color,
    val surfaceBright: Color,
    val surfaceContainer: Color,
    val surfaceContainerLow: Color,
    val surfaceContainerLowest: Color,
    val surfaceContainerHigh: Color,
    val surfaceContainerHighest: Color,
    val chatBackground: Color,
    val sentMessageBackground: Color,
    val receivedMessageBackground: Color
)

// Function to create extended color scheme
fun createExtendedColorScheme(
    colorScheme: ColorScheme,
    isDarkTheme: Boolean
): ExtendedColorScheme {
    val chatColors = if (isDarkTheme) {
        AppColors.ChatColors.DarkChatBackground to
                Pair(AppColors.ChatColors.DarkSentMessageBackground,
                    AppColors.ChatColors.DarkReceivedMessageBackground)
    } else {
        AppColors.ChatColors.LightChatBackground to
                Pair(AppColors.ChatColors.LightSentMessageBackground,
                    AppColors.ChatColors.LightReceivedMessageBackground)
    }

    return ExtendedColorScheme(
        colorScheme = colorScheme,
        surfaceDim = if (isDarkTheme) Color(0xFF121212) else Color(0xFFE0E0E0),
        surfaceBright = if (isDarkTheme) Color(0xFF2C2C2C) else Color(0xFFF5F5F5),
        surfaceContainer = if (isDarkTheme) Color(0xFF1E1E1E) else Color(0xFFF1F1F1),
        surfaceContainerLow = if (isDarkTheme) Color(0xFF191919) else Color(0xFFF7F7F7),
        surfaceContainerLowest = if (isDarkTheme) Color(0xFF0E0E0E) else Color(0xFFFFFFFF),
        surfaceContainerHigh = if (isDarkTheme) Color(0xFF292929) else Color(0xFFEBEBEB),
        surfaceContainerHighest = if (isDarkTheme) Color(0xFF333333) else Color(0xFFE5E5E5),
        chatBackground = chatColors.first,
        sentMessageBackground = chatColors.second.first,
        receivedMessageBackground = chatColors.second.second
    )
}
