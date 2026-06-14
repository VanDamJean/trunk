package com.yacoo.rpg.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yacoo.rpg.R

// ── Quicksand Round Font Family (Capybara Go style) ──────────────────

val QuicksandFamily = FontFamily(
    Font(R.font.jua_regular, FontWeight.Normal),
    Font(R.font.jua_regular, FontWeight.SemiBold),
    Font(R.font.jua_regular, FontWeight.Bold),
    Font(R.font.jua_regular, FontWeight.ExtraBold),
    Font(R.font.jua_regular, FontWeight.Black)
)

// ── Material3 Typography (preserved for M3 components) ───────────────

val YacooTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = QuicksandFamily,
        fontWeight = FontWeight.Black,
        fontSize = 28.sp,
        lineHeight = 32.sp
    ),
    titleLarge = TextStyle(
        fontFamily = QuicksandFamily,
        fontWeight = FontWeight.Black,
        fontSize = 20.sp,
        lineHeight = 24.sp
    ),
    titleMedium = TextStyle(
        fontFamily = QuicksandFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 16.sp,
        lineHeight = 20.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = QuicksandFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelLarge = TextStyle(
        fontFamily = QuicksandFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 13.sp,
        lineHeight = 16.sp
    ),
    labelSmall = TextStyle(
        fontFamily = QuicksandFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        lineHeight = 14.sp
    )
)

// ── Game Typography (named roles for game UI) ────────────────────────

object GameTypography {
    val screenTitle = TextStyle(
        fontFamily = QuicksandFamily,
        fontWeight = FontWeight.Black,
        fontSize = 28.sp,
        lineHeight = 32.sp
    )
    val sectionTitle = TextStyle(
        fontFamily = QuicksandFamily,
        fontWeight = FontWeight.Black,
        fontSize = 20.sp,
        lineHeight = 24.sp
    )
    val buttonLarge = TextStyle(
        fontFamily = QuicksandFamily,
        fontWeight = FontWeight.Black,
        fontSize = 18.sp,
        lineHeight = 22.sp
    )
    val buttonSmall = TextStyle(
        fontFamily = QuicksandFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 15.sp,
        lineHeight = 18.sp
    )
    val chipLabel = TextStyle(
        fontFamily = QuicksandFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 11.sp,
        lineHeight = 14.sp
    )
    val chipValue = TextStyle(
        fontFamily = QuicksandFamily,
        fontWeight = FontWeight.Black,
        fontSize = 14.sp,
        lineHeight = 18.sp
    )
    val statLabel = TextStyle(
        fontFamily = QuicksandFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 12.sp,
        lineHeight = 16.sp
    )
    val statValue = TextStyle(
        fontFamily = QuicksandFamily,
        fontWeight = FontWeight.Black,
        fontSize = 16.sp,
        lineHeight = 20.sp
    )
    val bodyText = TextStyle(
        fontFamily = QuicksandFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        lineHeight = 20.sp
    )
    val caption = TextStyle(
        fontFamily = QuicksandFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        lineHeight = 16.sp
    )
    val navLabel = TextStyle(
        fontFamily = QuicksandFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        lineHeight = 14.sp
    )
    val navLabelSelected = TextStyle(
        fontFamily = QuicksandFamily,
        fontWeight = FontWeight.Black,
        fontSize = 12.sp,
        lineHeight = 16.sp
    )
    val badge = TextStyle(
        fontFamily = QuicksandFamily,
        fontWeight = FontWeight.Black,
        fontSize = 10.sp,
        lineHeight = 12.sp
    )
}

// ── Spacing Tokens ───────────────────────────────────────────────────

object GameSpacing {
    val xs   = 4.dp
    val sm   = 8.dp
    val md   = 12.dp
    val lg   = 16.dp
    val xl   = 20.dp
    val xxl  = 28.dp
    val xxxl = 36.dp

    val screenPadding = 16.dp
    val cardPadding   = 16.dp
    val chipPaddingH  = 12.dp
    val chipPaddingV  = 6.dp
    val itemGap       = 8.dp
    val sectionGap    = 20.dp
}

// ── Corner Radius Tokens ─────────────────────────────────────────────

object GameRadius {
    val sm     = 8.dp
    val md     = 12.dp
    val lg     = 16.dp
    val xl     = 20.dp
    val pill   = 24.dp
    val circle = 50.dp

    val cardRadius   = 16.dp
    val buttonRadius = 16.dp
    val chipRadius   = 24.dp
    val panelRadius  = 20.dp
}

// ── Elevation Tokens ─────────────────────────────────────────────────

object GameElevation {
    val flat       = 0.dp
    val low        = 2.dp
    val medium     = 4.dp
    val high       = 8.dp
    val prominent  = 16.dp
}

// ── Stroke Width Tokens ──────────────────────────────────────────────

object GameStroke {
    val thin   = 2.dp
    val medium = 3.dp
    val thick  = 4.dp
    val heavy  = 6.dp
}
