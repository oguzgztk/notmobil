package com.example.notuygulamasi.presentation.screen.note

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.notuygulamasi.presentation.viewmodel.NoteDetailViewModel

/**
 * Not detay/düzenleme ekranı
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    noteId: String,
    onBack: () -> Unit,
    viewModel: NoteDetailViewModel = hiltViewModel()
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf<List<String>>(emptyList()) }
    
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(noteId) {
        try {
            if (noteId.isNotEmpty()) {
                viewModel.loadNote(noteId)
            } else {
                // Yeni not oluşturuluyor, loading'i false yap
                viewModel.initNewNote()
            }
        } catch (e: Exception) {
            // Hata durumunda loading'i false yap
            viewModel.initNewNote()
        }
    }
    
    LaunchedEffect(uiState.note) {
        uiState.note?.let { note ->
            title = note.title
            content = note.content
            tags = note.tags
        }
    }
    
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onBack()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (noteId.isEmpty()) "Yeni Not" else "Notu Düzenle") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.saveNote(title, content, tags)
                        }
                    ) {
                        Icon(Icons.Default.Done, contentDescription = "Kaydet")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Başlık") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("İçerik") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    minLines = 10
                )
                
                Button(
                    onClick = { viewModel.generateSummary(content) },
                    enabled = !uiState.isGeneratingSummary && content.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    if (uiState.isGeneratingSummary) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Özet Oluşturuluyor...")
                    } else {
                        Text("Özet Oluştur")
                    }
                }
                
                if (uiState.summary != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Özet",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = uiState.summary!!)
                        }
                    }
                }
                
                // Konum bilgisi göster
                val currentNote = uiState.note
                val location = currentNote?.location
                if (location != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "📍 Konum Bilgisi",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Enlem: ${location.latitude}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Boylam: ${location.longitude}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            location.address?.let { address ->
                                Text(
                                    text = "Adres: $address",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
                
                if (uiState.error != null) {
                    Text(
                        text = uiState.error!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

