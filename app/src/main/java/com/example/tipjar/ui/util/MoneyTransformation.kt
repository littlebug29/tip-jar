package com.example.tipjar.ui.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.text.DecimalFormat
import java.text.NumberFormat

class MoneyTransformation : VisualTransformation {

    private val numberFormat: NumberFormat = DecimalFormat("#,###.##")

    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text
        val parsedNumber = originalText.toDoubleOrNull() ?: 0.0
        val formattedText = numberFormat.format(parsedNumber)
        val formattedAnnotatedString = AnnotatedString(formattedText)

        return TransformedText(
            formattedAnnotatedString,
            MoneyOffsetMapping(originalText, formattedText)
        )
    }
}

class MoneyOffsetMapping(
    private val original: String,
    private val transformed: String
) : OffsetMapping {

    override fun originalToTransformed(offset: Int): Int {
        // Remove non-numeric characters from both original and transformed texts
        val cleanedOriginal = original.filter { it.isDigit() || it == '.' }
        val cleanedTransformed = transformed.filter { it.isDigit() || it == '.' }

        return if (offset >= cleanedOriginal.length) {
            transformed.length
        } else {
            var transformedOffset = 0
            var originalOffset = 0
            while (originalOffset < offset && transformedOffset < cleanedTransformed.length) {
                if (cleanedOriginal[originalOffset] == cleanedTransformed[transformedOffset]) {
                    originalOffset++
                }
                transformedOffset++
            }
            transformedOffset
        }
    }

    override fun transformedToOriginal(offset: Int): Int {
        // Remove non-numeric characters from both original and transformed texts
        val cleanedOriginal = original.filter { it.isDigit() || it == '.' }
        val cleanedTransformed = transformed.filter { it.isDigit() || it == '.' }

        return if (offset >= cleanedTransformed.length) {
            original.length
        } else {
            var transformedOffset = 0
            var originalOffset = 0
            while (transformedOffset < offset && originalOffset < cleanedOriginal.length) {
                if (cleanedOriginal[originalOffset] == cleanedTransformed[transformedOffset]) {
                    transformedOffset++
                }
                originalOffset++
            }
            originalOffset
        }
    }
}