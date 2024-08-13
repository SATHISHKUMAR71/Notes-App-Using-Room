package com.example.samplenotesapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.samplenotesapplication.model.Note
import com.example.samplenotesapplication.repository.NoteRepository
import kotlinx.coroutines.launch
import java.util.Collections.min

class NotesAppViewModel(private val application: Application,private val noteRepository: NoteRepository):AndroidViewModel(application) {


    private var queryNotesLiveDataCache = mutableMapOf<String,LiveData<MutableList<Note>>>()
    private var getAllNotesCache = mutableMapOf<String,LiveData<MutableList<Note>>>()
    var selectedNote = MutableLiveData<Note>()
    var query = ""
    var _searchQuery = MutableLiveData("")
    val searchQuery:MutableLiveData<String> get() = _searchQuery
    var selectCount = MutableLiveData(0)
    var deleteConfirmation = MutableLiveData(false)
    var searchedNotesList = searchQuery.switchMap {
        noteRepository.getNotesByQuery(it)
    }

    var isPinned = MutableLiveData(0)
    var onBackPressed = MutableLiveData(false)
    var selectAllItem = MutableLiveData(false)
    var pinItemsClicked = MutableLiveData(false)
    var deleteSelectedItems = MutableLiveData(false)
    fun setPinnedValues(list: List<Int>){
        isPinned.value =  min(list)
    }


    fun addNote(note: Note){
        viewModelScope.launch {
            noteRepository.addNote(note)
        }
    }

    fun updateNote(note:Note){
        viewModelScope.launch {
            noteRepository.updateNote(note)
        }
    }

    fun deleteNote(note:Note){
        viewModelScope.launch {
            noteRepository.deleteNote(note)
        }
    }

    fun getAllNotes(): LiveData<MutableList<Note>> {
        val i = getAllNotesCache.getOrPut("1"){
            noteRepository.getAllNotes()
        }
        return i
    }

    fun setSelectedNote(note:Note){
        selectedNote.value = note
    }
    fun getNotesByQuery(query:String):LiveData<MutableList<Note>>{
//        return noteRepository.getNotesByQuery(query)
        val i =queryNotesLiveDataCache.getOrPut(query){
            noteRepository.getNotesByQuery(query)
        }
        return i
    }
}