package com.compose.app.android.model

import com.compose.app.android.R

/**
 * Since we can't decode the android resource ID on other
 * platforms like iOS and the web, NoteColorResourceIDs and
 * NoteColorUniversalIDs correlate an integer value to each color,
 * which is recognized on all clients. Each item in NoteColorResourceIDs
 * correlates to the code at the same index in NoteColorUniversalIDs.
 *
 * Used on notes and their cards on the main list in ProductivityView
 */

const val CardColorRedLight = 1000
const val CardColorRedAlt = 1001
const val CardColorFuschia = 2000
const val CardColorPurpleLight = 3000
const val CardColorPurpleAlt = 3001
const val CardColorBlueLight = 4000
const val CardColorBlueAlt = 4001
const val CardColorTealLight = 5000
const val CardColorTealAlt = 5001
const val CardColorGreen = 6000
const val CardColorGreenAlt = 6001
const val CardColorYellowDark = 7000
const val CardColorYellowLight = 7001
const val CardColorOrangeLight = 8000
const val CardColorOrangeAlt = 8001
const val CardColorRedDark = 1002

val NoteColorResourceIDs = listOf(
    R.color.color_red_light,
    R.color.color_red_alt,
    R.color.color_fuschia,
    R.color.color_purple_light,
    R.color.color_purple_alt,
    R.color.color_blue_light,
    R.color.color_blue_alt,
    R.color.color_teal_light,
    R.color.color_teal_alt,
    R.color.color_green_reg,
    R.color.color_green_alt,
    R.color.color_yellow_dark,
    R.color.color_yellow_light,
    R.color.color_orange_light,
    R.color.color_orange_alt,
    R.color.color_red_dark
)

val NoteColorUniversalIDs = listOf(
    1000, 1001, 2000, 3000,
    3001, 4000, 4001, 5000,
    5001, 6000, 6001, 7000,
    7001, 8000, 8001, 1002
)