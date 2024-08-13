package com.example.samplenotesapplication.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.samplenotesapplication.R
import com.example.samplenotesapplication.model.Note
import com.example.samplenotesapplication.recyclerview.NotesAdapter
import com.example.samplenotesapplication.model.NotesDatabase
import com.example.samplenotesapplication.repository.NoteRepository
import com.example.samplenotesapplication.viewmodel.NotesAppViewModel

import com.example.samplenotesapplication.viewmodel.NotesViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeFragment : Fragment() {
    private lateinit var appbarFragment: AppbarFragment
    private var searchActionPerformed = false
    private lateinit var view:View
    private lateinit var fab:FloatingActionButton
    private var query = ""
    private lateinit var adapter: NotesAdapter
    private var count =0
    private lateinit var viewModel: NotesAppViewModel
    private var dummyList = MutableLiveData<MutableList<Note>>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false)
        val rv = view.findViewById<RecyclerView>(R.id.notesRecyclerView)
        val viewModelFactory = NotesViewModelFactory(requireActivity().application, NoteRepository(NotesDatabase.getNoteDatabase(requireContext())))
        viewModel = ViewModelProvider(this,viewModelFactory)[NotesAppViewModel::class.java]
        query = viewModel.query

        adapter = NotesAdapter(viewModel,this)
        adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

//        Floating Action Button On Click Listener
        fab = view.findViewById(R.id.addButton)

        if(searchActionPerformed){
            fab.hide()
        }

        fab.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .addToBackStack("Add Note")
                    .replace(R.id.fragmentContainerView,AddNote(viewModel))
                    .commit()
            }
        appbarFragment = AppbarFragment(fab,viewModel,this)
        if(parentFragmentManager.findFragmentByTag("longFragmentEnabled")?.isVisible != true){
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerMenu,appbarFragment,"appbarFragment")
                .commit()
        }
        (context as FragmentActivity).onBackPressedDispatcher.addCallback(viewLifecycleOwner,object:OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                handleBackPress()
            }
        })

//        SearchView Observer
        viewModel.searchedNotesList.observe(viewLifecycleOwner){
            searchActionPerformed = true
            adapter.setNotesQuery(it,viewModel.searchQuery.value?:"")
        }





//        DELETE CONFIRMATION DIALOG OBSERVER
        viewModel.deleteConfirmation.observe(viewLifecycleOwner, Observer {
            if(it){
                adapter.deleteSelectedItem()
                parentFragmentManager.popBackStack()
            }
        })


//        SelectedNotes Observer
        viewModel.selectedNote.observe(viewLifecycleOwner, Observer {
            adapter.selectedItem()
        })


//        Select All Items Observer
        viewModel.selectAllItem.observe(viewLifecycleOwner, Observer {
            if(it){
                adapter.selectAllItems()
            }
            else{
                adapter.unSelectAllItems()
            }
        })


//        On BackPressed Observer
        viewModel.onBackPressed.observe(viewLifecycleOwner, Observer {
            adapter.onBackPressed()
        })

        viewModel.getAllNotes().observe(viewLifecycleOwner){
            if(viewModel.searchQuery.value!!.isEmpty()){
                searchActionPerformed = false
                adapter.setNotes(it)
            }
            else{
                searchActionPerformed = true

            }
        }

//        Delete Selected Item Observer
        viewModel.deleteSelectedItems.observe(viewLifecycleOwner, Observer {
            if(it){
                adapter.deleteDialog(requireContext())
            }
        })

//        Pin Items Observer
        viewModel.pinItemsClicked.observe(viewLifecycleOwner, Observer {
            if(viewModel.isPinned.value== 0){
                adapter.pinSelectedItems()
            }
            else{
                adapter.unpinSelectedItems()
            }
        })


//        Adapter initialization
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(context)

        return view
    }



//    APPBAR CLEAR FOCUS WHEN BACK PRESSED
    private fun handleBackPress() {

        // Clear focus and hide keyboard if necessary
        val searchView = (appbarFragment.view?.findViewById<SearchView>(R.id.searchView))
        if(parentFragmentManager.findFragmentByTag("longFragmentEnabled")?.isVisible == true){
            parentFragmentManager.popBackStack()
        }

        else if ((searchView?.hasFocus() == true)||(searchActionPerformed)) {
            searchView?.setQuery("",false)
            viewModel.query = ""
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(searchView?.windowToken, 0)
            searchView?.clearFocus()
            searchActionPerformed = false
            fab.show()
        }
        else {
            requireActivity().finish()
        }
    }

}