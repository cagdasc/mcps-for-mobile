package com.cacaosd.ui_theme.typography

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import mcpsformobile.ui.theme.generated.resources.Res
import mcpsformobile.ui.theme.generated.resources.roboto_bold
import mcpsformobile.ui.theme.generated.resources.roboto_medium
import mcpsformobile.ui.theme.generated.resources.roboto_regular
import org.jetbrains.compose.resources.Font

// Extended Typography Object
@Composable
fun AppTypography(): Typography {
    val robotoFontFamily = FontFamily(
        Font(Res.font.roboto_regular, FontWeight.Normal),
        Font(Res.font.roboto_medium, FontWeight.Medium),
        Font(Res.font.roboto_bold, FontWeight.Bold)
    )
    return Typography(
        displayLarge = TextStyle(
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 40.sp,
            lineHeight = 46.sp,
            letterSpacing = (-0.25).sp
        ),
        displayMedium = TextStyle(
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 34.sp,
            lineHeight = 40.sp,
            letterSpacing = 0.sp
        ),
        displaySmall = TextStyle(
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 28.sp,
            lineHeight = 34.sp,
            letterSpacing = 0.sp
        ),
        headlineLarge = TextStyle(
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 24.sp,
            lineHeight = 30.sp,
            letterSpacing = 0.sp
        ),
        headlineMedium = TextStyle(
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 20.sp,
            lineHeight = 26.sp,
            letterSpacing = 0.sp
        ),
        headlineSmall = TextStyle(
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 18.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.sp
        ),
        titleLarge = TextStyle(
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            lineHeight = 22.sp,
            letterSpacing = 0.sp
        ),
        titleMedium = TextStyle(
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.1.sp
        ),
        titleSmall = TextStyle(
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            letterSpacing = 0.05.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.25.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            letterSpacing = 0.2.sp
        ),
        bodySmall = TextStyle(
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.3.sp
        ),
        labelLarge = TextStyle(
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            letterSpacing = 0.1.sp
        ),
        labelMedium = TextStyle(
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.4.sp
        ),
        labelSmall = TextStyle(
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 10.sp,
            lineHeight = 14.sp,
            letterSpacing = 0.5.sp
        )
    )
}
