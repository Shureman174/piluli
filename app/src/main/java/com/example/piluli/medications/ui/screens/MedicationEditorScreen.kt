package com.example.piluli.medications.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.example.piluli.medications.ui.MedicationEditorViewModel
import java.util.Calendar
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationEditorScreen(
    viewModel: MedicationEditorViewModel,
    title: String,
    onBack: () -> Unit,
    onSaved: (Long) -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    val pickImage = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) viewModel.importPhoto(uri)
    }

    val cameraTempFile = remember { File(context.cacheDir, "camera").apply { mkdirs() } }
    val photoFile = remember { File(cameraTempFile, "capture.jpg") }
    val photoUriForCamera = remember(photoFile) {
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", photoFile)
    }

    val takePicture = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { ok ->
        if (ok) viewModel.importPhoto(photoUriForCamera)
    }

    val calendar = remember { Calendar.getInstance() }
    val startAtMillis = state.startAtMillis
    val scroll = rememberScrollState()
    var photoMenuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .padding(WindowInsets.safeDrawing.asPaddingValues())
                .verticalScroll(scroll),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            Card(
                colors = CardDefaults.cardColors(),
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Фото", style = MaterialTheme.typography.titleMedium)
                    Box(modifier = Modifier.fillMaxWidth()) {
                        AsyncImage(
                            model = state.photoUri,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clickable { photoMenuExpanded = true }
                        )
                        DropdownMenu(
                            expanded = photoMenuExpanded,
                            onDismissRequest = { photoMenuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Выбрать из галереи") },
                                onClick = {
                                    photoMenuExpanded = false
                                    pickImage.launch("image/*")
                                },
                                leadingIcon = { Icon(Icons.Default.Image, contentDescription = null) }
                            )
                            DropdownMenuItem(
                                text = { Text("Сделать фото") },
                                onClick = {
                                    photoMenuExpanded = false
                                    takePicture.launch(photoUriForCamera)
                                },
                                leadingIcon = { Icon(Icons.Default.CameraAlt, contentDescription = null) }
                            )
                        }
                    }
                    Text(
                        "Нажми на превью, чтобы выбрать фото",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            OutlinedTextField(
                value = state.name,
                onValueChange = viewModel::setName,
                label = { Text("Название") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.description,
                onValueChange = viewModel::setDescription,
                label = { Text("Описание") },
                modifier = Modifier.fillMaxWidth()
            )

            Card(
                colors = CardDefaults.cardColors(),
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Расписание", style = MaterialTheme.typography.titleMedium)
                    OutlinedTextField(
                        value = state.intakesPerDay.toString(),
                        onValueChange = { viewModel.setIntakesPerDay(it.toIntOrNull() ?: 1) },
                        label = { Text("Сколько раз в день") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    val desired = state.intakesPerDay.coerceAtLeast(1).coerceAtMost(8)
                    val normalizedTimes = remember(state.intakeTimes, desired) {
                        val times = state.intakeTimes.toMutableList()
                        while (times.size < desired) times.add("09:00")
                        while (times.size > desired) times.removeLast()
                        times.toList()
                    }
                    if (normalizedTimes != state.intakeTimes) {
                        viewModel.setIntakeTimes(normalizedTimes)
                    }

                    normalizedTimes.forEachIndexed { idx, value ->
                        OutlinedTextField(
                            value = value,
                            onValueChange = { /* редактирование только через пикер */ },
                            readOnly = true,
                            label = { Text("Время приёма #${idx + 1}") },
                            leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val parts = value.split(":")
                                    val h = parts.getOrNull(0)?.toIntOrNull() ?: 9
                                    val m = parts.getOrNull(1)?.toIntOrNull() ?: 0
                                    TimePickerDialog(context, { _, hour, minute ->
                                        val newTimes = normalizedTimes.toMutableList()
                                        newTimes[idx] = "%02d:%02d".format(hour, minute)
                                        viewModel.setIntakeTimes(newTimes)
                                    }, h, m, true).show()
                                }
                        )
                    }
                }
            }

            Text(
                text = if (startAtMillis == null) "Старт курса: не задан"
                else "Старт курса: ${java.text.DateFormat.getDateTimeInstance().format(java.util.Date(startAtMillis))}",
                modifier = Modifier.clickable {
                    val now = Calendar.getInstance()
                    DatePickerDialog(
                        context,
                        { _, y, mo, d ->
                            calendar.set(y, mo, d)
                            TimePickerDialog(context, { _, h, mi ->
                                calendar.set(Calendar.HOUR_OF_DAY, h)
                                calendar.set(Calendar.MINUTE, mi)
                                viewModel.setStartAtMillis(calendar.timeInMillis)
                            }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true).show()
                        },
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                    ).show()
                },
                style = MaterialTheme.typography.bodyLarge
            )

            OutlinedTextField(
                value = state.totalCourseAmount,
                onValueChange = viewModel::setTotalCourseAmount,
                label = { Text("Количество на курс (всего)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))
            Button(
                onClick = { viewModel.save(onSaved) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Сохранить")
            }
            Spacer(Modifier.height(80.dp))
        }
    }
}

