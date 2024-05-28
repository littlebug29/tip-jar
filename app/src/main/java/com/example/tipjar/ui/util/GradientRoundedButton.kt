package com.example.tipjar.ui.util

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun GradientRoundedButton(
    modifier: Modifier = Modifier,
    text: String,
    textStyle: TextStyle,
    enable: Boolean,
    enabledGradient: Brush,
    disabledGradient: Brush,
    disableColor: Color = Color.Gray,
    radius: Dp,
    onClick: () -> Unit
) {
    val gradient = if (enable) enabledGradient else disabledGradient

    Box(
        modifier = modifier
            .background(brush = gradient, shape = RoundedCornerShape(radius))
            .padding(0.dp)
    ) {
        Button(
            onClick = onClick,

            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.Transparent,
                disabledContainerColor = disableColor,
                disabledContentColor = disableColor
            ),
            shape = RoundedCornerShape(radius),
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp), // Remove any extra padding
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
            interactionSource = remember { MutableInteractionSource() } // Remove ripple effect
        ) {
            Text(text = text, color = Color.White, style = textStyle)
        }
    }
}