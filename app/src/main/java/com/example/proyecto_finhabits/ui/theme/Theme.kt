package com.example.proyecto_finhabits.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Brand colors
val PrimaryGreen   = Color(0xFF1B8A5A)
val PrimaryGreenDark = Color(0xFF0F5C3A)
val SecondaryBlue  = Color(0xFF1565C0)
val TertiaryAmber  = Color(0xFFF57F17)
val ErrorRed       = Color(0xFFB71C1C)
val IncomeGreen    = Color(0xFF2E7D32)
val ExpenseRed     = Color(0xFFC62828)
val SurfaceCard    = Color(0xFFF8FAF9)
val BackgroundMain = Color(0xFFF1F5F2)

private val LightColors = lightColorScheme(
    primary = PrimaryGreen,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFB7E4C7),
    onPrimaryContainer = Color(0xFF002112),
    secondary = SecondaryBlue,
    onSecondary = Color.White,
    tertiary = TertiaryAmber,
    onTertiary = Color.Black,
    background = BackgroundMain,
    surface = SurfaceCard,
    surfaceVariant = Color(0xFFE0EDE6),
    error = ErrorRed,
    onError = Color.White,
)

val FinHabitsTypography = Typography(
    headlineLarge  = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Bold,  fontSize = 28.sp, lineHeight = 34.sp),
    headlineMedium = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Bold,  fontSize = 22.sp, lineHeight = 28.sp),
    headlineSmall  = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.SemiBold, fontSize = 18.sp),
    titleLarge     = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.SemiBold, fontSize = 20.sp),
    titleMedium    = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Medium, fontSize = 16.sp),
    bodyLarge      = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium     = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
    labelSmall     = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Medium, fontSize = 11.sp),
)

@Composable
fun FinHabitsTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = FinHabitsTypography,
        content = content
    )
}
