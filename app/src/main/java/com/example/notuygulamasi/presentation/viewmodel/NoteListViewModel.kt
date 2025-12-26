package com.example.notuygulamasi.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notuygulamasi.domain.model.Note
import com.example.notuygulamasi.domain.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Not Listesi ViewModel
 */
@HiltViewModel
class NoteListViewModel @Inject constructor(
    private val noteRepository: NoteRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(NoteListUiState())
    val uiState: StateFlow<NoteListUiState> = _uiState.asStateFlow()
    
    init {
        loadNotes()
    }
    
    fun loadNotes() {
        viewModelScope.launch {
            noteRepository.getAllNotes().collect { notes ->
                _uiState.value = _uiState.value.copy(
                    notes = notes,
                    isLoading = false
                )
            }
        }
    }
    
    fun searchNotes(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                loadNotes()
            } else {
                noteRepository.searchNotes(query).collect { notes ->
                    _uiState.value = _uiState.value.copy(
                        notes = notes,
                        isLoading = false
                    )
                }
            }
        }
    }
    
    fun filterByTag(tag: String) {
        viewModelScope.launch {
            noteRepository.filterNotesByTag(tag).collect { notes ->
                _uiState.value = _uiState.value.copy(
                    notes = notes,
                    isLoading = false
                )
            }
        }
    }
    
    fun deleteNote(id: String) {
        viewModelScope.launch {
            noteRepository.deleteNote(id)
        }
    }
    
    fun syncNotes() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSyncing = true)
            noteRepository.syncNotes()
            _uiState.value = _uiState.value.copy(isSyncing = false)
        }
    }
}

data class NoteListUiState(
    val notes: List<Note> = emptyList(),
    val isLoading: Boolean = true,
    val isSyncing: Boolean = false,
    val error: String? = null
)

