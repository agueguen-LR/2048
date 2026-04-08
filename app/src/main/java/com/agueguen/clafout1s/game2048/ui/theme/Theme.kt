package com.agueguen.clafout1s.game2048.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

// TODO: Find good color schemes

val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    surface = surfaceLight
)

val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    surface = surfaceDark,
)

val lightSchemeColor = lightColorScheme(
    primary = primaryLightColor,
    onPrimary = onPrimaryLightColor,
    primaryContainer = primaryContainerLightColor,
    onPrimaryContainer = onPrimaryContainerLightColor,
    secondary = secondaryLightColor,
    onSecondary = onSecondaryLightColor,
    secondaryContainer = secondaryContainerLightColor,
    tertiary = tertiaryLightColor,
    onTertiary = onTertiaryLightColor,
    error = errorLightColor,
    onError = onErrorLightColor,
    errorContainer = errorContainerLightColor,
    onErrorContainer = onErrorContainerLightColor,
    background = backgroundLightColor,
    surface = surfaceLightColor
)


@Immutable
data class ColorFamily(
    val color: Color,
    val onColor: Color,
    val colorContainer: Color,
    val onColorContainer: Color
)

// TODO: getting better fonts settings
@Composable
fun AppTheme(
	theme: Int?,
	content: @Composable() () -> Unit
) {
	val colors = when (theme) {
		null, 0 -> lightScheme
		1 -> darkScheme
        2 -> lightSchemeColor
		else -> lightScheme // fallback
	}

	MaterialTheme(
		colorScheme = colors,
		typography = MyTypography,
		content = content
	)
}

