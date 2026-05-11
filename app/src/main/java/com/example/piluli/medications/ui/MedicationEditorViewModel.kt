package com.example.piluli.medications.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.piluli.medications.data.MedicationEntity
import com.example.piluli.medications.di.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MedicationEditorState(
    val id: Long? = null,
    val name: String = "",
    val description: String = "",
    val photoUri: String? = null,
    val startAtMillis: Long? = null,
    val intakesPerDay: Int = 1,
    val intakeTimes: List<String> = emptyList(),
    val totalCourseAmount: String = "",
    val createdAtMillis: Long? = null,
)

class MedicationEditorViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = ServiceLocator.medicationRepository(application)

    private val _state = MutableStateFlow(MedicationEditorState())
    val state: StateFlow<MedicationEditorState> = _state.asStateFlow()

    fun loadForEdit(medicationId: Long) {
        viewModelScope.launch {
            val entity = repo.getMedication(medicationId) ?: return@launch
            _state.value = MedicationEditorState(
                id = entity.id,
                name = entity.name,
                description = entity.description,
                photoUri = entity.photoUri,
                startAtMillis = entity.startAtMillis,
                intakesPerDay = entity.intakesPerDay,
                intakeTimes = entity.intakeTimes,
                totalCourseAmount = entity.totalCourseAmount?.toString().orEmpty(),
                createdAtMillis = entity.createdAtMillis,
            )
        }
    }

    fun setName(v: String) = _state.value.let { _state.value = it.copy(name = v) }
    fun setDescription(v: String) = _state.value.let { _state.value = it.copy(description = v) }
    fun setIntakesPerDay(v: Int) = _state.value.let { _state.value = it.copy(intakesPerDay = v.coerceAtLeast(1)) }
    fun setIntakeTimes(times: List<String>) = _state.value.let { _state.value = it.copy(intakeTimes = times) }
    fun setTotalCourseAmount(v: String) = _state.value.let { _state.value = it.copy(totalCourseAmount = v) }

    fun setStartAtMillis(v: Long?) = _state.value.let { _state.value = it.copy(startAtMillis = v) }

    fun setPhotoUriString(v: String?) = _state.value.let { _state.value = it.copy(photoUri = v) }

    fun importPhoto(source: Uri) {
        viewModelScope.launch {
            val uri = repo.importPhotoToAppStorage(source)
            setPhotoUriString(uri.toString())
        }
    }

    fun save(onSaved: (Long) -> Unit) {
        val s = _state.value
        val name = s.name.trim()
        if (name.isBlank()) return

        viewModelScope.launch {
            val id = repo.upsertMedication(
                MedicationEntity(
                    id = s.id ?: 0L,
                    name = name,
                    description = s.description.trim(),
                    photoUri = s.photoUri,
                    createdAtMillis = s.createdAtMillis ?: System.currentTimeMillis(),
                    startAtMillis = s.startAtMillis,
                    intakesPerDay = s.intakesPerDay.coerceAtLeast(1),
                    totalCourseAmount = s.totalCourseAmount.toIntOrNull(),
                    intakeTimes = s.intakeTimes,
                )
            )
            onSaved(id)
        }
    }
}

