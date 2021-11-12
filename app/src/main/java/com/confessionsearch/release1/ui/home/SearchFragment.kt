package com.confessionsearch.release1.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.widget.ShareActionProvider
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.confessionsearch.release1.R
import com.confessionsearch.release1.data.documents.DocumentDBClassHelper
import com.confessionsearch.release1.data.documents.DocumentList
import com.confessionsearch.release1.databinding.FragmentHomeBinding
import com.confessionsearch.release1.searchhandlers.SearchHandler
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import www.sanju.motiontoast.MotionToast
import java.util.*
import kotlin.collections.ArrayList

class SearchFragment : Fragment() {

    private lateinit var searchViewModel: SearchViewModel
    private var _binding: FragmentHomeBinding? = null
    var documentDB: SQLiteDatabase? = null
    var docDBhelper: DocumentDBClassHelper? = null
    var shareProvider: ShareActionProvider? = null
    private var documentTypeSpinner: Spinner? = null
    private var documentNameSpinner: Spinner? = null
    var searchButton: ExtendedFloatingActionButton? = null
    var header = ""
    var recurCalls = 0
    var searchBoxContainer: TextInputLayout? = null
    var searchBoxTextBox: TextInputEditText? = null
    protected var textSearch: Boolean? = null
    protected var questionSearch: Boolean? = null
    protected var readerSearch: Boolean? = null
    var query: String? = null
    var dbName = "confessionSearchDB.sqlite3"
    var type = ""
    var shareList = ""
    var fileName: String? = null
    protected var allOpen: Boolean? = null
    protected var confessionOpen: Boolean? = null
    protected var catechismOpen: Boolean? = null
    protected var creedOpen: Boolean? = null
    protected var helpOpen: Boolean? = null
    protected var proofs = true
    protected var answers = true
    protected var searchAll = false
    protected var sortByChapterBool = false

    //Testing
    var answerChip: Chip? = null
    var proofChip: Chip? = null
    var searchAllChip: Chip? = null
    var optionGroup: ChipGroup? = null
    var sortType: String = ""
    var topicChip: Chip? = null
    var questionChip: Chip? = null
    var readDocsChip: Chip? = null
    var sortChapterChip: Chip? = null
    var docTypeSpinnerAdapter: ArrayAdapter<String>? = null
    var docTitleSpinnerAdapter: ArrayAdapter<String>? = null
    var docTitleList: ArrayList<String?> = ArrayList()
    var docTypes: ArrayList<String?> = ArrayList()
    var searchBox: SearchView? = null
    var chipGroup: ChipGroup? = null
    var masterList = DocumentList()
    var shareNote: String? = null
    var docType = ""

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    //Create Fragment
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        // Load all objects related to Search Screen Here
        searchViewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
        // Load database
        docDBhelper = DocumentDBClassHelper(super.getContext())
        documentDB = docDBhelper!!.readableDatabase
        //Load Types and Load Spinners
        searchViewModel.loadTypes(docDBhelper!!.getAllDocTypes(documentDB))
        val root: View = binding.root//View.inflate(context,R.layout.fragment_home,container)
        //Chip Group Initialization
        chipGroup = root.findViewById(R.id.chip_group)
        optionGroup = root.findViewById(R.id.option_group)
        //Search Button Initialization
        searchButton = root.findViewById(R.id.searchFAB)
        searchButton!!.setOnClickListener(searchButtonListener)
        //Search Box Initialization
        //Old
        //searchBox = root.findViewById(R.id.searchView1)
        //searchBox!!.setOnQueryTextListener(searchQueryListener)
        //searchBox!!.setOnKeyListener(submissionKey)
        //New
        searchBoxContainer = root.findViewById(R.id.searchContainer)
        //searchBoxTextBox=root.findViewById(R.id.searchBoxTextBox)
        searchBoxContainer!!.editText!!.setOnKeyListener(submissionKey)
        //searchBoxContainer.setOnQueryTextListener
        //More stuff
        optionGroup!!.setOnCheckedChangeListener(optionListener)

        // Chip Initialization 06/01/2021 - Testing look and execution
        answerChip = root.findViewById(R.id.answerChip)
        proofChip = root.findViewById(R.id.proofChip)
        searchAllChip = root.findViewById(R.id.searchAllChip)
        sortChapterChip = root.findViewById(R.id.sortByChapter)

        //Implement check changed listeners
        answerChip!!.setOnCheckedChangeListener(checkBox)
        proofChip!!.setOnCheckedChangeListener(checkBox)
        searchAllChip!!.setOnCheckedChangeListener(checkBox)
        sortChapterChip!!.setOnCheckedChangeListener(checkBox)
        topicChip = root.findViewById(R.id.topicChip)
        questionChip = root.findViewById(R.id.questionChip)
        readDocsChip = root.findViewById(R.id.readDocsChip)
        //Spinner Initialization
        documentTypeSpinner = root.findViewById(R.id.documentTypeSpinner)
        documentNameSpinner = root.findViewById(R.id.documentNameSpinner)
        //Adapter and Spinner Assignments
        docTypes = searchViewModel.getTypes()
        docTypeSpinnerAdapter = ArrayAdapter(
            requireContext(),
            R.layout.support_simple_spinner_dropdown_item,
            docTypes
        )
        documentTypeSpinner!!.adapter = docTypeSpinnerAdapter
        documentTypeSpinner!!.onItemSelectedListener = spinnerItemSelectedListener
        type = ""
        //Load Document Titles into Doc Title list for preparation
        searchViewModel.loadTitles(docDBhelper!!.getAllDocTitles(type, documentDB!!))
        docTitleList = searchViewModel.getTitles()
        docTitleSpinnerAdapter = ArrayAdapter(
            requireContext(),
            R.layout.support_simple_spinner_dropdown_item,
            docTitleList
        )
        documentNameSpinner!!.onItemSelectedListener = docTitleSpinner
        //searchBoxContainer!!.setOnKeyListener(submissionKey)
        topicChip!!.performClick()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private var checkBox = CompoundButton.OnCheckedChangeListener { compoundButton, _ ->
        when (compoundButton.id) {
            R.id.proofChip -> proofs = !proofChip!!.isChecked
            R.id.answerChip -> answers = !answerChip!!.isChecked
            R.id.searchAllChip -> searchAll = searchAllChip!!.isChecked
            R.id.sortByChapter -> sortByChapterBool = sortChapterChip!!.isChecked
        }
    }


    var optionListener = ChipGroup.OnCheckedChangeListener { group, checkedId ->
        val enter = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER)
        if (checkedId == (R.id.topicChip)) {
            //searchBox!!.isEnabled = true
            searchBoxContainer!!.isEnabled = true
            searchBoxContainer!!.editText!!.imeOptions = EditorInfo.IME_ACTION_SEARCH
            searchBoxContainer!!.editText!!.setOnKeyListener(submissionKey)
            searchBoxContainer!!.editText!!.inputType = InputType.TYPE_CLASS_TEXT
            textSearch = true
            questionSearch = false
            readerSearch = false


        } else if (checkedId == R.id.questionChip) {
            searchBoxContainer!!.isEnabled = true
            searchBoxContainer!!.editText!!.imeOptions = EditorInfo.IME_ACTION_SEARCH
            searchBoxContainer!!.editText!!.inputType = InputType.TYPE_CLASS_NUMBER
            //searchBoxContainer!!.editText!!.setOnEditorActionListener(searchQueryListener)
            searchBoxContainer!!.editText!!.setOnKeyListener(submissionKey)
            textSearch = false
            readerSearch = false
            questionSearch = true
            //searchFAB!!.text = resources.getString(R.string.Search)

        } else if (checkedId == R.id.readDocsChip) {
            //   searchFAB!!.text = resources.getString(R.string.read_button_text)
            textSearch = false
            questionSearch = false
            readerSearch = true
        }

    }


    //Submission key
    var searchButtonListener = View.OnClickListener {
        val query: String
        Log.d("SEARCHBUTTON", "THE SEARCHBUTTON WAS PUSHED TO EXECUTE THIS")
        if (readerSearch!!) {
            query = ""
            Search(query)
        } else {

            query = searchBoxContainer!!.editText?.text.toString()
            //searchBox!!.query.toString()
            if (query.isEmpty()) {
                when (requireContext().resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
                    Configuration.UI_MODE_NIGHT_YES -> {
                        MotionToast.darkToast(
                            super.requireActivity(), getString(R.string.query_error),
                            "Enter A topic in the search field!",
                            MotionToast.TOAST_ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.SHORT_DURATION,
                            ResourcesCompat.getFont(
                                super.requireActivity(),
                                R.font.helvetica_regular
                            )
                        )
                    }
                    Configuration.UI_MODE_NIGHT_NO -> {
                        MotionToast.createToast(
                            super.requireActivity(), getString(R.string.query_error),
                            "Enter a topic in the search field!",
                            MotionToast.TOAST_ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.SHORT_DURATION,
                            ResourcesCompat.getFont(
                                super.requireActivity(),
                                R.font.helvetica_regular
                            )
                        )

                    }
                }
            } else Search(query)
        }
    }

    //Enter Key Submission
    var submissionKey = View.OnKeyListener { v, _, event ->
        val searchBox = v as TextInputEditText
        if (event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
            query = searchBox.text.toString()
            /* recurCalls++
             if(recurCalls==1)
             searchButton!!.performClick()*/


            Log.d("ENTERKEY", "THE ENTER KEY WAS PRESSED TO EXECUTE THIS")
            Log.d("View", String.format("%s", event.displayLabel))
            if (!query!!.isEmpty() and !readerSearch!!) Search(query) else Toast.makeText(
                super.getContext(),
                R.string.query_error,
                Toast.LENGTH_LONG
            ).show()

            true
        } else {
            false
        }
    }

    //Deprecated old code, No longer needed, but useful for learning
    /*var searchQueryListener: SearchView.OnQueryTextListener =
        object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(entry: String): Boolean {
                query = entry
                if (!readerSearch!!) {
                    if (query!!.isEmpty()) Toast.makeText(
                        context,
                        R.string.query_error,
                        Toast.LENGTH_LONG
                    ).show() else Search(query)
                } else Search(query)
                return false
            }

            //nothing happens here
            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        }
*/
    //Spinner Listeners
    var spinnerItemSelectedListener: AdapterView.OnItemSelectedListener = object :
        AdapterView.OnItemSelectedListener {
        @SuppressLint("ResourceAsColor")
        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            run {
                val docTitles: ArrayList<String?> = ArrayList()
                type = parent.selectedItem.toString()
                //Gets all document titles and places them in a list
                for (docTitle in docDBhelper!!.getAllDocTitles(type, documentDB!!)) {
                    docTitles.add(docTitle.documentName!!)
                }
                docTitleSpinnerAdapter = ArrayAdapter(
                    requireContext(),
                    R.layout.support_simple_spinner_dropdown_item,
                    docTitles
                )
                docTitleSpinnerAdapter!!.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
                documentNameSpinner!!.adapter = docTitleSpinnerAdapter
                documentNameSpinner!!.onItemSelectedListener = docTitleSpinner
                when (type.toUpperCase(Locale.ROOT)) {
                    "ALL" -> {
                        allOpen = true
                        docType = "All"
                    }
                    "CONFESSION" -> {
                        allOpen = false
                        header = "Chapter "
                        docType = "Confession"
                    }
                    "CATECHISM" -> {
                        allOpen = false
                        header = "Question "
                        docType = "Catechism"
                    }
                    "CREED" -> {
                        allOpen = false
                        docType = "Creed"
                    }
                }
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>) {
            type = parent.selectedItem.toString()
        }
    }
    var docTitleSpinner: AdapterView.OnItemSelectedListener = object :
        AdapterView.OnItemSelectedListener {
        override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
            fileName = String.format("%s", adapterView.selectedItem.toString())
        }

        override fun onNothingSelected(adapterView: AdapterView<*>?) {
            fileName = adapterView!!.selectedItem.toString()
        }
    }


    // 7-13-21 Take the data from the search form and package it in a format to put in the search handler
    @SuppressLint("NewApi")
    fun Search(query: String?) {
        Log.d("tagme", "THIS SHOULD ONLY APPEAR ONE TIME")
        Log.d("Handler", "HomeScreen is in charge")
        //Sort Type Setting
        if (sortByChapterBool)
            sortType = "Chapter"
        else
            sortType = "Matches"

        var searchIntent = Intent(context, SearchHandler::class.java)
        val stringQuery = query
        Log.d("Test", context.toString())
        //Document Type Filtering
        searchIntent.putExtra("AllDocs", allOpen)
        //All document search within type or all
        searchIntent.putExtra("SearchAll", searchAll)
        //Search Type
        searchIntent.putExtra("Question", questionSearch)
        searchIntent.putExtra("Text", textSearch)
        searchIntent.putExtra("Reader", readerSearch)
        searchIntent.putExtra("docType", docType)
        //Advanced Options
        searchIntent.putExtra("Answers", answers)
        searchIntent.putExtra("Proofs", proofs)
        //Query Holder
        searchIntent.putExtra("Query", stringQuery)
        //FileName
        searchIntent.putExtra("FileName", fileName)
        searchIntent.putExtra("ACTIVITY_ID", ACTIVITY_ID)
        //Sort Options
        searchIntent.putExtra("SortType", sortType)

        requireContext().startActivity(searchIntent)
        recurCalls = 0

    }

    companion object {
        const val ACTIVITY_ID = 32
    }

}

