package com.confessionsearch.release1.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.widget.ShareActionProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.confessionsearch.release1.R
import com.confessionsearch.release1.data.MyAdapter
import com.confessionsearch.release1.data.documents.DocumentDBClassHelper
import com.confessionsearch.release1.data.documents.DocumentList
import com.confessionsearch.release1.databinding.FragmentHomeBinding
import com.confessionsearch.release1.searchhandlers.SearchHandler
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.*
import kotlin.collections.ArrayList

class SearchFragment : Fragment() {


    private var _binding: FragmentHomeBinding? = null
    var documentDB: SQLiteDatabase? = null
    var docDBhelper: DocumentDBClassHelper? = null
    var shareProvider: ShareActionProvider? = null
    private var documentTypeSpinner: Spinner? = null
    private var documentNameSpinner: Spinner? = null

    // var docTypeATV : TextInputLayout? = null
    var header = ""
    var searchBoxContainer: TextInputLayout? = null

    var dbName = "confessionSearchDB.sqlite3"
    var type = ""


    //Testing
    var answerChip: Chip? = null
    var proofChip: Chip? = null
    var searchAllChip: Chip? = null
    var optionGroup: ChipGroup? = null
    var topicChip: Chip? = null
    var questionChip: Chip? = null
    var readDocsChip: Chip? = null
    var sortChapterChip: Chip? = null
    var docTypeSpinnerAdapter: MyAdapter<String>? = null
    var docTitleSpinnerAdapter: ArrayAdapter<String>? = null
    var docTitleList: ArrayList<String?> = ArrayList()
    var docTypes: ArrayList<String?> = ArrayList()
    var chipGroup: ChipGroup? = null
    var masterList = DocumentList()
    var shareNote: String? = null
    // var docType = ""

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
        val root: View = binding.root
        //Chip Group Initialization
        chipGroup = root.findViewById(R.id.chip_group)
        optionGroup = root.findViewById(R.id.option_group)

        //Search Box Initialization

        searchBoxContainer = root.findViewById(R.id.searchContainer)

        searchBoxContainer!!.editText!!.setOnKeyListener(submissionKey)


        searchBoxContainer!!.editText!!.addTextChangedListener(searchBoxEditTextChangedWatcher)

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
        //docTypeATV = root.findViewById(R.id.docTypeComboBox)
        documentTypeSpinner = root.findViewById(R.id.documentTypeSpinner)
        documentNameSpinner = root.findViewById(R.id.documentNameSpinner)
        //Adapter and Spinner Assignments
        docTypes = searchViewModel.getTypes()
        docTypeSpinnerAdapter = MyAdapter(
            requireContext(),
            R.layout.support_simple_spinner_dropdown_item,//spinner_item_layout,
            docTypes
        )


        documentTypeSpinner!!.adapter = (docTypeSpinnerAdapter)

        documentTypeSpinner!!.onItemSelectedListener = docTypeSpinnerListener

        // (docTypeATV!!.editText!! as AutoCompleteTextView)!!.showDropdown(docTypeSpinnerAdapter!!)
        type = ""
        //Load Document Titles into Doc Title list for preparation
        searchViewModel.loadTitles(docDBhelper!!.getAllDocTitles(type, documentDB!!))
        docTitleList = searchViewModel.getTitles()
        docTitleSpinnerAdapter = ArrayAdapter(
            requireContext(),
            R.layout.support_simple_spinner_dropdown_item,
            docTitleList
        )
        documentNameSpinner!!.onItemSelectedListener = docTitleSpinnerListener
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
        if (checkedId == (R.id.topicChip)) {
            searchBoxContainer!!.isEnabled = true
            searchBoxContainer!!.editText!!.imeOptions = EditorInfo.IME_ACTION_SEARCH
            searchBoxContainer!!.editText!!.setOnKeyListener(submissionKey)
            searchBoxContainer!!.editText!!.inputType = InputType.TYPE_CLASS_TEXT
            textSearch = true
            questionSearch = false
            readerSearch = false
            searchViewModel.buttonText = resources.getString(R.string.Search)


        } else if (checkedId == R.id.questionChip) {
            searchBoxContainer!!.isEnabled = true
            searchBoxContainer!!.editText!!.imeOptions = EditorInfo.IME_ACTION_SEARCH
            searchBoxContainer!!.editText!!.inputType = InputType.TYPE_CLASS_NUMBER
            searchBoxContainer!!.editText!!.setOnKeyListener(submissionKey)
            textSearch = false
            readerSearch = false
            questionSearch = true
            searchViewModel.buttonText = resources.getString(R.string.Search)

        } else if (checkedId == R.id.readDocsChip) {
            searchViewModel.buttonText = resources.getString(R.string.read_button_text)
            searchBoxContainer!!.isEnabled = false
            textSearch = false
            questionSearch = false
            readerSearch = true
        }

    }

    //Enter Key Event Handler for Search EditText
    var submissionKey = View.OnKeyListener { v, _, event ->
        val searchBox = v as TextInputEditText
        if (event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
            searchViewModel.query = searchBox.text.toString()
            Log.d("ENTERKEY", "THE ENTER KEY WAS PRESSED TO EXECUTE THIS")
            Log.d("View", String.format("%s", event.displayLabel))
            if (!searchViewModel.query.isEmpty() and !readerSearch!!) Search(
                searchViewModel.query,
                requireContext()
            ) else Toast.makeText(
                super.getContext(),
                R.string.query_error,
                Toast.LENGTH_LONG
            ).show()

            true
        } else {
            false
        }
    }

    // Needed to change the
    var searchBoxEditTextChangedWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            searchViewModel.query = s.toString()
            //Log.d("QUERYVAL","Query is ${searchViewModel.query}")
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            searchViewModel.query = s.toString()
            //  Log.d("QUERYVAL","Query is ${searchViewModel.query}")
        }

        override fun afterTextChanged(s: Editable?) {
            searchViewModel.query = s!!.toString()
            //    Log.d("QUERYVAL","Query is ${searchViewModel.query}")
        }

    }

    //Spinner Listeners

    var docTypeSpinnerListener: AdapterView.OnItemSelectedListener = object :
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
                documentNameSpinner!!.onItemSelectedListener = docTitleSpinnerListener
                when (type.uppercase(Locale.ROOT)) {
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

    var docTitleSpinnerListener: AdapterView.OnItemSelectedListener = object :
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


    companion object {
        const val ACTIVITY_ID = 32

        var fileName: String? = ""
        var allOpen: Boolean? = null
        var proofs = true
        var answers = true
        var searchAll: Boolean? = null
        var sortByChapterBool = false
        var sortType = ""
        var docType = ""
        var textSearch: Boolean? = null
        var questionSearch: Boolean? = null
        var readerSearch: Boolean? = null
        lateinit var searchViewModel: SearchViewModel

        fun Search(query: String?, context: Context?) {
            //This will force the application to halt before continuing
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

            context!!.startActivity(searchIntent)

        }
    }


}

