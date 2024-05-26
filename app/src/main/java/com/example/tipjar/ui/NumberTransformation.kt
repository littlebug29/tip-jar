package com.example.tipjar.ui

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class NumberTransformation(private val min: Int, private val max: Int) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text.filter { it.isDigit() }
        val value = originalText.toIntOrNull()?.coerceIn(min, max) ?: min
        val transformedText = AnnotatedString(value.toString())

        return TransformedText(
            transformedText,
            object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int {
                    // Map original offset to transformed offset
                    return if (offset <= transformedText.length) offset else transformedText.length
                }

                override fun transformedToOriginal(offset: Int): Int {
                    // Map transformed offset to original offset
                    return if (offset <= text.length) offset else text.length
                }
            }
        )
    }
}