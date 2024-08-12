package com.example.samplenotesapplication.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.samplenotesapplication.R
import com.example.samplenotesapplication.model.NotesDatabase
import com.example.samplenotesapplication.repository.NoteRepository
import com.example.samplenotesapplication.viewmodel.NotesAppViewModel
import com.example.samplenotesapplication.viewmodel.NotesViewModelFactory
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton


class AppbarFragment(private val fab:FloatingActionButton,private var viewModel:NotesAppViewModel) : Fragment() {
    private lateinit var search:SearchView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        println("12345 query value: ${viewModel.query.value} ${viewModel.query.value?.isEmpty()==false}")
        val view =  inflater.inflate(R.layout.fragment_appbar, container, false)
        search = view.findViewById(R.id.searchView)
        if(viewModel.query.value?.isEmpty()==false){
            println("12345 ON QUERY CHANGED isEmpty ${viewModel.query.value}")
            viewModel.query.value = viewModel.query.value
            search.setQuery(viewModel.query.value?:"",true)
        }
        search.isFocusable = false
        search.isFocusableInTouchMode = false

        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                println("ON QUERY CHANGED Submit")
//                viewModel.getNotesByQuery(query?:"")
                viewModel.query.value = query
                println("12345 submit value: $query")
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(search.windowToken, 0)
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                println("ON QUERY CHANGED Text Change")
//                viewModel.getNotesByQuery(newText?:"")
                viewModel.query.value = newText
                println("12345 change value: $newText")
                return true
            }
        })
        search.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus){
                fab.hide()
            }
            else{
//                fab.show()
            }
        }
        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }
    override fun onStop() {
        super.onStop()
        println("12345 On Stop")
    }
    override fun onStart() {
        super.onStart()
        println("12345 On Start")
    }

    override fun onResume() {
        super.onResume()
        println("12345 On Resume")
    }
    override fun onPause() {
        super.onPause()
        println("12345 On Pause")
    }
}