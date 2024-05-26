package com.example.tipjar.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.tipjar.MainViewModel
import com.example.tipjar.R
import com.example.tipjar.database.entity.TipHistory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TipHistoryScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
    val savedPayments by viewModel.savedPayments.collectAsState(emptyList())
    var showReceiptDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "SAVED PAYMENTS",
                            style = TextStyle(
                                fontStyle = FontStyle.Normal,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal
                            ),
                            textAlign = TextAlign.Center
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.ic_back),
                                contentDescription = "Back"
                            )
                        }
                    },
                )
                HorizontalDivider(color = "#EBEBEB".toColor(), thickness = 1.dp)
            }

        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            items(savedPayments) { payment ->
                TipHistoryItem(payment) { payment ->
                    viewModel.selectTipHistory(payment)
                    showReceiptDialog = true
                }
            }
        }
        if (showReceiptDialog) {
            val selectedTipHistory by viewModel.selectedTipHistory.collectAsState()
            val payment = selectedTipHistory ?: return@Scaffold
            ReceiptDialog(payment, onDismiss = { showReceiptDialog = false })
        }
    }
}

@Composable
fun TipHistoryItem(
    payment: TipHistory,
    onClick: (TipHistory) -> Unit
) {
    val date = Date(payment.timestamp)
    val format = SimpleDateFormat("yyyy MMMM dd", Locale.getDefault())
    val dateString = format.format(date)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp))
            .clickable {
                onClick(payment)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = dateString,
                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal),
                color = Color.Black
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$${payment.amount}",
                    style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                    color = Color.Black
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Tip: $${payment.tip}",
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal),
                    color = Color.Gray
                )
            }
        }
        if (payment.photoUri != null) {
            Image(
                painter = rememberAsyncImagePainter(payment.photoUri),
                contentDescription = "Receipt Photo",
                modifier = Modifier
                    .size(53.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}