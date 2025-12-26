package com.example.notuygulamasi.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notuygulamasi.domain.model.Note
import com.example.notuygulamasi.domain.repository.AiRepository
import com.example.notuygulamasi.domain.repository.NoteRepository
import com.example.notuygulamasi.domain.repository.SensorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/**
 * Not Detay ViewModel
 */
@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    private val noteRepository: NoteRepository,
    private val aiRepository: AiRepository,
    private val sensorRepository: SensorRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(NoteDetailUiState())
    val uiState: StateFlow<NoteDetailUiState> = _uiState.asStateFlow()
    
    fun loadNote(id: String) {
        viewModelScope.launch {
            try {
                val note = noteRepository.getNoteById(id)
                _uiState.value = _uiState.value.copy(
                    note = note,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Not yüklenirken hata oluştu"
                )
            }
        }
    }
    
    fun initNewNote() {
        _uiState.value = _uiState.value.copy(isLoading = false)
        // Yeni not oluşturulurken konum güncellemelerini başlat
        viewModelScope.launch {
            try {
                sensorRepository.startLocationUpdates()
            } catch (e: Exception) {
                // İzin yoksa veya hata varsa sessizce devam et
            }
        }
    }
    
    fun saveNote(title: String, content: String, tags: List<String>) {
        viewModelScope.launch {
            try {
                val currentNote = _uiState.value.note
                val now = Date()
                
                // Konum verisini al (varsa)
                var locationData: com.example.notuygulamasi.domain.model.LocationData? = null
                try {
                    // Konum güncellemelerini başlat
                    sensorRepository.startLocationUpdates()
                    // Kısa bir süre bekle ki konum alınabilsin
                    kotlinx.coroutines.delay(1000)
                    // Mevcut konumu al
                    locationData = sensorRepository.getCurrentLocation().first()
                } catch (e: Exception) {
                    // Konum alınamazsa devam et (opsiyonel özellik)
                    // İzin yoksa veya hata varsa sessizce devam et
                }
                
                val note = if (currentNote != null) {
                    currentNote.copy(
                        title = title,
                        content = content,
                        tags = tags,
                        updatedAt = now,
                        location = locationData ?: currentNote.location // Yeni konum varsa güncelle, yoksa eskisini koru
                    )
                } else {
                    Note(
                        id = UUID.randomUUID().toString(),
                        title = title,
                        content = content,
                        createdAt = now,
                        updatedAt = now,
                        tags = tags,
                        location = locationData // Yeni not için konum ekle
                    )
                }
                
                if (currentNote != null) {
                    noteRepository.updateNote(note)
                } else {
                    noteRepository.insertNote(note)
                }
                
                _uiState.value = _uiState.value.copy(note = note, isSaved = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Not kaydedilirken hata oluştu",
                    isSaved = false
                )
            }
        }
    }
    
    fun generateSummary(contentText: String) {
        viewModelScope.launch {
            if (contentText.isBlank()) return@launch
            _uiState.value = _uiState.value.copy(isGeneratingSummary = true)
            
            try {
                val result = aiRepository.summarizeText(contentText)
                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        summary = result.getOrNull(),
                        isGeneratingSummary = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = result.exceptionOrNull()?.message,
                        isGeneratingSummary = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message,
                    isGeneratingSummary = false
                )
            }
        }
    }
    
}

data class NoteDetailUiState(
    val note: Note? = null,
    val isLoading: Boolean = true,
    val isSaved: Boolean = false,
    val summary: String? = null,
    val isGeneratingSummary: Boolean = false,
    val error: String? = null
)

