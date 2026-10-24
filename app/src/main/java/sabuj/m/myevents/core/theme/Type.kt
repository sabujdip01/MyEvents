package sabuj.m.myevents.core.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import sabuj.m.myevents.R

/**
 * Inter font family (bundled in res/font/).
 * Falls back to Roboto if the font resource is missing.
 */
val InterFamily = FontFamily(
    Font(R.font.inter_regular,    FontWeight.Normal),
    Font(R.font.inter_medium,     FontWeight.Medium),
    Font(R.font.inter_semibold,   FontWeight.SemiBold),
    Font(R.font.inter_bold,       FontWeight.Bold),
    Font(R.font.inter_extrabold,  FontWeight.ExtraBold),
)

// ---------------------------------------------------------------------------
// Named text styles matching design.md section 2.1
// ---------------------------------------------------------------------------

/** 30 / 36, ExtraBold — app name on Home header */
val display = TextStyle(
    fontFamily    = InterFamily,
    fontWeight    = FontWeight.ExtraBold,
    fontSize      = 30.sp,
    lineHeight    = 36.sp,
    letterSpacing = (-0.5).sp,
)

/** 22 / 28, Bold — screen titles, sheet titles */
val titleL = TextStyle(
    fontFamily    = InterFamily,
    fontWeight    = FontWeight.Bold,
    fontSize      = 22.sp,
    lineHeight    = 28.sp,
    letterSpacing = (-0.2).sp,
)

/** 18 / 24, Bold — card titles, dialog titles */
val titleM = TextStyle(
    fontFamily    = InterFamily,
    fontWeight    = FontWeight.Bold,
    fontSize      = 18.sp,
    lineHeight    = 24.sp,
    letterSpacing = 0.sp,
)

/** 16 / 22, SemiBold — row titles, list item names */
val titleS = TextStyle(
    fontFamily    = InterFamily,
    fontWeight    = FontWeight.SemiBold,
    fontSize      = 16.sp,
    lineHeight    = 22.sp,
    letterSpacing = 0.sp,
)

/** 16 / 24, Normal — form text, long text */
val bodyL = TextStyle(
    fontFamily    = InterFamily,
    fontWeight    = FontWeight.Normal,
    fontSize      = 16.sp,
    lineHeight    = 24.sp,
    letterSpacing = 0.sp,
)

/** 14 / 20, Normal — secondary text, table cells */
val bodyM = TextStyle(
    fontFamily    = InterFamily,
    fontWeight    = FontWeight.Normal,
    fontSize      = 14.sp,
    lineHeight    = 20.sp,
    letterSpacing = 0.sp,
)

/** 14 / 20, SemiBold — buttons, chips, tabs */
val labelL = TextStyle(
    fontFamily    = InterFamily,
    fontWeight    = FontWeight.SemiBold,
    fontSize      = 14.sp,
    lineHeight    = 20.sp,
    letterSpacing = 0.1.sp,
)

/** 12 / 16, SemiBold — captions, pills, table headers */
val labelM = TextStyle(
    fontFamily    = InterFamily,
    fontWeight    = FontWeight.SemiBold,
    fontSize      = 12.sp,
    lineHeight    = 16.sp,
    letterSpacing = 0.3.sp,
)

/** 11 / 14, SemiBold — section overlines (UPPERCASE) */
val labelS = TextStyle(
    fontFamily    = InterFamily,
    fontWeight    = FontWeight.SemiBold,
    fontSize      = 11.sp,
    lineHeight    = 14.sp,
    letterSpacing = 0.5.sp,
)

/** 30 / 34, ExtraBold, tabular — big card values (₹85,000) */
val numberXl = TextStyle(
    fontFamily        = InterFamily,
    fontWeight        = FontWeight.ExtraBold,
    fontSize          = 30.sp,
    lineHeight        = 34.sp,
    letterSpacing     = (-0.5).sp,
    fontFeatureSettings = "\"tnum\"",
)

/** 22 / 26, ExtraBold, tabular — booking card amounts */
val numberL = TextStyle(
    fontFamily        = InterFamily,
    fontWeight        = FontWeight.ExtraBold,
    fontSize          = 22.sp,
    lineHeight        = 26.sp,
    letterSpacing     = (-0.3).sp,
    fontFeatureSettings = "\"tnum\"",
)

/** 16 / 20, Bold, tabular — table totals */
val numberM = TextStyle(
    fontFamily        = InterFamily,
    fontWeight        = FontWeight.Bold,
    fontSize          = 16.sp,
    lineHeight        = 20.sp,
    letterSpacing     = 0.sp,
    fontFeatureSettings = "\"tnum\"",
)

// ---------------------------------------------------------------------------
// Material 3 Typography mapped to our styles
// ---------------------------------------------------------------------------
val AppTypography = Typography(
    displayLarge      = display,
    headlineLarge     = titleL,
    headlineMedium    = titleM,
    titleLarge        = titleL,
    titleMedium       = titleM,
    titleSmall        = titleS,
    bodyLarge         = bodyL,
    bodyMedium        = bodyM,
    bodySmall         = bodyM,
    labelLarge        = labelL,
    labelMedium       = labelM,
    labelSmall        = labelS,
)
