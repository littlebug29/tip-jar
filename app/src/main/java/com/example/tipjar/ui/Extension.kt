package com.example.tipjar.ui

import androidx.compose.ui.graphics.Color

 fun String.toColor() = Color(android.graphics.Color.parseColor(this))