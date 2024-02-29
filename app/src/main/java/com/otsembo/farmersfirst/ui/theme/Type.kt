package com.otsembo.farmersfirst.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.otsembo.farmersfirst.R

val nunitoFamily =
    FontFamily(
        Font(R.font.nunito_black, FontWeight.Black),
        Font(R.font.nunito_bold, FontWeight.Bold),
        Font(R.font.nunito_extrabold, FontWeight.ExtraBold),
        Font(R.font.nunito_extralight, FontWeight.ExtraLight),
        Font(R.font.nunito_regular, FontWeight.Normal),
        Font(R.font.nunito_medium, FontWeight.Medium),
        Font(R.font.nunito_semibold, FontWeight.SemiBold),
        Font(R.font.nunito_light, FontWeight.Light),
    )

// Set of Material typography styles to start with
val Typography =
    Typography(
        // Display
        displayLarge =
            TextStyle(
                fontFamily = nunitoFamily,
                fontWeight = FontWeight.W500, // Medium
                fontSize = 57.sp,
                lineHeight = 72.sp,
                letterSpacing = (-0.5).sp,
            ),
        displayMedium =
            TextStyle(
                fontFamily = nunitoFamily,
                fontWeight = FontWeight.W400, // Regular
                fontSize = 45.sp,
                lineHeight = 56.sp,
                letterSpacing = 0.sp,
            ),
        displaySmall =
            TextStyle(
                fontFamily = nunitoFamily,
                fontWeight = FontWeight.W400, // Regular
                fontSize = 36.sp,
                lineHeight = 48.sp,
                letterSpacing = 0.sp,
            ),
        // Headline
        headlineLarge =
            TextStyle(
                fontFamily = nunitoFamily,
                fontWeight = FontWeight.W400, // Regular
                fontSize = 32.sp,
                lineHeight = 40.sp,
                letterSpacing = 0.sp,
            ),
        headlineMedium =
            TextStyle(
                fontFamily = nunitoFamily,
                fontWeight = FontWeight.W400, // Regular
                fontSize = 24.sp,
                lineHeight = 32.sp,
                letterSpacing = 0.sp,
            ),
        headlineSmall =
            TextStyle(
                fontFamily = nunitoFamily,
                fontWeight = FontWeight.W400, // Regular
                fontSize = 20.sp,
                lineHeight = 24.sp,
                letterSpacing = 0.sp,
            ),
        // Title
        titleLarge =
            TextStyle(
                fontFamily = nunitoFamily,
                fontWeight = FontWeight.W500, // Medium
                fontSize = 16.sp,
                lineHeight = 24.sp,
                letterSpacing = 0.15.sp,
            ),
        titleMedium =
            TextStyle(
                fontFamily = nunitoFamily,
                fontWeight = FontWeight.W400, // Regular
                fontSize = 14.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.1.sp,
            ),
        titleSmall =
            TextStyle(
                fontFamily = nunitoFamily,
                fontWeight = FontWeight.W400, // Regular
                fontSize = 12.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.1.sp,
            ),
        // Label
        labelLarge =
            TextStyle(
                fontFamily = nunitoFamily,
                fontWeight = FontWeight.W500, // Medium
                fontSize = 14.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.1.sp,
            ),
        labelMedium =
            TextStyle(
                fontFamily = nunitoFamily,
                fontWeight = FontWeight.W400, // Regular
                fontSize = 12.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.1.sp,
            ),
        labelSmall =
            TextStyle(
                fontFamily = nunitoFamily,
                fontWeight = FontWeight.W400, // Regular
                fontSize = 10.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.1.sp,
            ),
        // Body
        bodyLarge =
            TextStyle(
                fontFamily = nunitoFamily,
                fontWeight = FontWeight.W400, // Regular
                fontSize = 16.sp,
                lineHeight = 24.sp,
                letterSpacing = 0.15.sp,
            ),
        bodyMedium =
            TextStyle(
                fontFamily = nunitoFamily,
                fontWeight = FontWeight.W400, // Regular
                fontSize = 14.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.1.sp,
            ),
        bodySmall =
            TextStyle(
                fontFamily = nunitoFamily,
                fontWeight = FontWeight.W400, // Regular
                fontSize = 12.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.1.sp,
            ),
    )
