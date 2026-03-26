package com.agueguen.clafout1s.game2048.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.agueguen.clafout1s.game2048.R


val blockyFont = FontFamily(
    Font(R.font.f8bitoperatorplus_regular, FontWeight.Normal),
    Font(R.font.f8bitoperatorplus_bold, FontWeight.Bold),
    Font(R.font.f8bitoperatorplus8_regular, FontWeight.Light)
)

val MyTypography = Typography(
    bodyMedium = TextStyle(
        fontFamily = blockyFont, fontWeight = FontWeight.Normal, fontSize = 12.sp/*...*/
    ),
    bodyLarge = TextStyle(
        fontFamily = blockyFont,
        fontWeight = FontWeight.Bold,
        letterSpacing = 2.sp,
        /*...*/
    ),
    headlineMedium = TextStyle(
        fontFamily = blockyFont, fontWeight = FontWeight.SemiBold/*...*/
    ),
    /*...*/
)
val AppTypography = Typography()
