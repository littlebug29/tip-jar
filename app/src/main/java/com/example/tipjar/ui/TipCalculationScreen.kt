package com.example.tipjar.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tipjar.MainActivity
import com.example.tipjar.MainViewModel
import com.example.tipjar.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TipCalculationScreen(
    navController: NavController,
    viewModel: MainViewModel,
    shouldTakePhoto: Boolean,
    onCheckChange: (Boolean) -> Unit,
    onSavePaymentClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.width(24.dp)) // Placeholder for symmetry
                        Image(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_logo),
                            contentDescription = "Logo",
                            modifier = Modifier
                                .width(114.dp)
                                .height(29.dp)
                        )
                        IconButton(
                            onClick = { navController.navigate(MainActivity.TIP_HISTORY_DESTINATION) }
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.ic_history),
                                contentDescription = "History"
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        val amount by viewModel.amount
        val tipPercent by viewModel.tipPercent
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color.White)
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            CustomOutlineTextField(
                modifier = Modifier.fillMaxWidth(),
                label = "Enter amount",
                value = amount,
                leadingLabel = "$",
                visualTransformation = MoneyTransformation(),
                onValueChange = {
                    viewModel.amount.value = cleanAmountInput(it)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            PeopleAdjustmentBox(viewModel)

            Spacer(modifier = Modifier.height(16.dp))

            CustomOutlineTextField(
                label = "% TIP",
                value = tipPercent.toString(),
                trailingLabel = "%",
                visualTransformation = NumberTransformation(0, 100),
                onValueChange = {
                    viewModel.tipPercent.intValue = it.toIntOrNull() ?: 0
                })

            Spacer(modifier = Modifier.height(16.dp))

            SummaryTipInfoView(viewModel)

            Spacer(modifier = Modifier.height(82.dp))

            TakePhotoCheckBox(shouldTakePhoto, onCheckChange)

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onSavePaymentClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            listOf(
                                "#F27A0A".toColor(),
                                "#D26E11".toColor()
                            )
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                shape = RoundedCornerShape(12.dp),
                interactionSource = remember { MutableInteractionSource() },
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text(text = "Save payment", color = Color.White, fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }

}

@Composable
fun PeopleAdjustmentBox(
    viewModel: MainViewModel
) {
    val people by viewModel.people
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "How many people?",
            style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                modifier = Modifier.size(71.dp),
                shape = CircleShape,
                border = BorderStroke(1.dp, Color(0xFFD2D2D2)),
                onClick = { if (people >= 1) viewModel.people.intValue-- }
            ) {
                Text(text = "-", fontSize = 24.sp)
            }

            Text(text = "$people", fontSize = 42.sp, fontWeight = FontWeight.Bold)

            OutlinedButton(
                modifier = Modifier.size(71.dp),
                shape = CircleShape,
                border = BorderStroke(1.dp, Color(0xFFD2D2D2)),
                onClick = {
                    viewModel.people.intValue++
                }
            ) {
                Text(text = "+", fontSize = 24.sp)
            }
        }
    }
}

@Composable
fun SummaryTipInfoView(
    viewModel: MainViewModel
) {
    val totalTip by viewModel.totalTipString
    val perPerson by viewModel.perPersonString

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Total Tip", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Text(text = "$$totalTip", fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Per Person", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(text = "$$perPerson", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun TakePhotoCheckBox(
    shouldTakePhoto: Boolean,
    onCheckChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            modifier = Modifier.size(31.dp),
            checked = shouldTakePhoto,
            onCheckedChange = onCheckChange,
            colors = CheckboxColors(
                checkedCheckmarkColor = "#F27A0A".toColor(),
                uncheckedCheckmarkColor = "#E5E5E5".toColor(),
                checkedBoxColor = Color.Transparent,
                uncheckedBoxColor = Color.Transparent,
                checkedBorderColor = "#F27A0A".toColor(),
                uncheckedBorderColor = "#E5E5E5".toColor(),
                disabledBorderColor = "#E5E5E5".toColor(),
                disabledIndeterminateBorderColor = "#E5E5E5".toColor(),
                disabledCheckedBoxColor = "#E5E5E5".toColor(),
                disabledUncheckedBoxColor = "#E5E5E5".toColor(),
                disabledUncheckedBorderColor = "#E5E5E5".toColor(),
                disabledIndeterminateBoxColor = "#E5E5E5".toColor()
            )
        )
        TextButton(onClick = { onCheckChange(!shouldTakePhoto) }) {
            Text(
                text = "Take photo of receipt",
                fontSize = 16.sp,
                color = Color.Black,
                style = TextStyle(fontWeight = FontWeight.Medium)
            )
        }
    }
}

@Composable
fun CustomOutlineTextField(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    leadingLabel: String = "",
    trailingLabel: String = "",
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onValueChange: (String) -> Unit,
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium),
            color = Color.Black
        )
        // Label

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                fontSize = 42.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Normal
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            visualTransformation = visualTransformation,
            leadingIcon = {
                Text(
                    leadingLabel,
                    style = MaterialTheme.typography.labelLarge
                )
            }, // Prefix
            trailingIcon = {
                Text(
                    trailingLabel,
                    style = MaterialTheme.typography.labelLarge
                )
            }, // Suffix
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = "#000000".toColor(),
                unfocusedTextColor = "#DADADA".toColor(),
                unfocusedBorderColor = "#CBCBCB".toColor(),
                focusedBorderColor = "#CBCBCB".toColor(),
            )
        )
    }
}

private fun cleanAmountInput(input: String): String = input.replace(Regex("[^\\d.]"), "")