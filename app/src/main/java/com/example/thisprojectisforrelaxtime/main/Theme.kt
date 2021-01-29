package com.example.thisprojectisforrelaxtime.main

import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.font
import androidx.compose.ui.text.font.fontFamily
import com.example.thisprojectisforrelaxtime.R

val DarkColors = darkColors(
    primary = Color(R.color.red_light),
    secondary = Color(R.color.teal_200),
    surface = Color(R.color.black)
)

val Poppin = fontFamily(
    font(R.font.poppins_pat_vietnamese_medium)
)
