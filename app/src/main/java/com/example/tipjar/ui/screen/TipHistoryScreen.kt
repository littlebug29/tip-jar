package com.example.tipjar.ui.screen

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.widget.DatePicker
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.tipjar.MainViewModel
import com.example.tipjar.R
import com.example.tipjar.database.entity.TipHistory
import com.example.tipjar.ui.util.toColor
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TipHistoryScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
    val tipHistories by viewModel.tipHistories
    var showSearchFields by remember { mutableStateOf(false) }
    var showProgress by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(300)
        viewModel.loadAllTipHistories()
        showProgress = false
    }

    Scaffold(
        topBar = {
            HistoryHeader(
                navController = navController,
                showSearchFields = showSearchFields,
                viewModel = viewModel,
                onSearchClick = {
                    showSearchFields = !showSearchFields
                    if (!showSearchFields) {
                        viewModel.loadAllTipHistories()
                    }
                }
            )
        }
    ) { paddingValues ->
        if (showProgress) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(strokeWidth = 2.dp)
            }
        } else {
            HistoryContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                showSearchFields = showSearchFields,
                tipHistories = tipHistories,
                viewModel = viewModel
            )
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryHeader(
    navController: NavController,
    showSearchFields: Boolean,
    viewModel: MainViewModel,
    onSearchClick: () -> Unit
) {
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
            actions = {
                IconButton(onClick = onSearchClick) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search"
                    )
                }
            }
        )
        AnimatedVisibility(
            visible = showSearchFields,
            enter = slideInVertically(initialOffsetY = { 0 }) + fadeIn(),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { 0 })
        ) {
            SearchBox(viewModel = viewModel)
        }
    }
}

@Composable
fun SearchBox(viewModel: MainViewModel) {
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    val calendar = Calendar.getInstance()
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val context = LocalContext.current

    var startDateInMillis by remember { mutableStateOf<Long?>(null) }
    var endDateInMillis by remember { mutableStateOf<Long?>(null) }

    val startDatePickerDialog = rememberUpdatedState(newValue = DatePickerDialog(context))
    val endDatePickerDialog = rememberUpdatedState(newValue = DatePickerDialog(context))

    LaunchedEffect(startDatePickerDialog, endDatePickerDialog) {
        startDatePickerDialog.value.apply {
            setOnDateSetListener { _: DatePicker, year: Int, month: Int, day: Int ->
                calendar.set(year, month, day)
                startDate = dateFormat.format(calendar.time)
                startDateInMillis = calendar.timeInMillis
                endDatePickerDialog.value.datePicker.minDate = calendar.timeInMillis
                if (startDateInMillis != null && endDateInMillis != null) {
                    viewModel.searchTipHistories(startDateInMillis!!, endDateInMillis!!)
                }
            }
        }
        endDatePickerDialog.value.apply {
            setOnDateSetListener { _: DatePicker, year: Int, month: Int, day: Int ->
                calendar.set(year, month, day)
                endDate = dateFormat.format(calendar.time)
                endDateInMillis = calendar.timeInMillis
                startDatePickerDialog.value.datePicker.maxDate = calendar.timeInMillis
                if (startDateInMillis != null && endDateInMillis != null) {
                    viewModel.searchTipHistories(startDateInMillis!!, endDateInMillis!!)
                }
            }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.Absolute.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(
            modifier = Modifier
                .weight(1f)
                .height(32.dp),
            onClick = { startDatePickerDialog.value.show() }
        ) {
            Text(text = startDate.ifEmpty { "Start Date" }, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.width(8.dp))

        OutlinedButton(
            modifier = Modifier
                .weight(1f)
                .height(32.dp),
            onClick = { endDatePickerDialog.value.show() }
        ) {
            Text(text = endDate.ifEmpty { "End Date" }, fontSize = 14.sp)
        }
    }
}

@Composable
fun HistoryContent(
    modifier: Modifier,
    showSearchFields: Boolean,
    tipHistories: List<TipHistory>,
    viewModel: MainViewModel
) {
    Column(
        modifier = modifier.background(Color.White)
    ) {
        HorizontalDivider(color = "#EBEBEB".toColor(), thickness = 1.dp)
        Spacer(modifier = Modifier.height(8.dp))
        val label = if (showSearchFields) "Search Results:" else "All Tip Histories"
        Text(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp),
            text = label,
            style = MaterialTheme.typography.titleMedium
        )
        if (showSearchFields && tipHistories.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Not found")
            }
        } else {
            HistoryList(histories = tipHistories, viewModel)
        }
    }
}

@Composable
fun HistoryList(
    histories: List<TipHistory>,
    viewModel: MainViewModel,
) {
    var showReceiptDialog by remember { mutableStateOf(false) }
    LazyColumn {
        items(histories) { payment ->
            TipHistoryItem(
                payment = payment,
                onClick = {
                    viewModel.selectTipHistory(payment)
                    showReceiptDialog = true
                },
                onConfirm = {
                    viewModel.deleteTipHistory(payment)
                }
            )
        }
    }
    if (showReceiptDialog) {
        val selectedTipHistory by viewModel.selectedTipHistory.collectAsState()
        val payment = selectedTipHistory ?: return
        ReceiptDialog(payment, onDismiss = { showReceiptDialog = false })
    }
}

@Composable
fun TipHistoryItem(
    payment: TipHistory,
    onClick: () -> Unit,
    onConfirm: () -> Unit
) {
    val date = Date(payment.timestamp)
    val format = SimpleDateFormat("yyyy MMMM dd", Locale.getDefault())
    val dateString = format.format(date)

    var offsetX by remember { mutableStateOf(0f) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Tip History") },
            text = { Text("Are you sure you want to delete this tip history?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirm()
                        offsetX = 0f
                        showDeleteDialog = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectHorizontalDragGestures { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount
                    if (offsetX > 0) offsetX = 0f
                    if (offsetX < -200f) offsetX = -200f
                }
            }
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetX.toInt(), 0) }
                .padding(PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp))
                .clickable { onClick() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            PaymentInfoArea(
                modifier = Modifier.weight(1f),
                date = dateString,
                amount = payment.amount,
                tip = payment.tip
            )
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
        if (offsetX < -100f) {
            Box(
                modifier = Modifier
                    .height(53.dp)
                    .align(Alignment.BottomEnd)
                    .background(Color.Red)
                    .width(80.dp)
                    .clickable {
                        showDeleteDialog = true
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Delete",
                    color = Color.White,
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Composable
fun PaymentInfoArea(
    modifier: Modifier,
    date: String,
    amount: Double,
    tip: Double
) {
    Column(modifier = modifier) {
        Text(
            text = date,
            style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal),
            color = Color.Black
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "$$amount",
                style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                color = Color.Black
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Tip: $$tip",
                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal),
                color = Color.Gray
            )
        }
    }
}