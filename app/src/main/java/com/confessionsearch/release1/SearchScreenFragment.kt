package com.confessionsearch.release1

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
import androidx.fragment.app.Fragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import java.util.*

class SearchScreenFragment : Fragment() {
    var bundle: Bundle? = null
    protected var documentTypeSpinner: Spinner? = null
    protected var documentNameSpinner: Spinner? = null
    var searchButton: ExtendedFloatingActionButton? = null
    var answerChip: Chip? = null
    var proofChip: Chip? = null
    var searchAllChip: Chip? = null
    var optionGroup: ChipGroup? = null
    var dbName = "confessionSearchDB.sqlite3"
    var documentDBHelper: documentDBClassHelper? = null
    var type = ""
    var fileName: String? = null
    var topicChip: Chip? = null
    var questionChip: Chip? = null
    var readDocsChip: Chip? = null
    protected var allOpen: Boolean? = null
    protected var confessionOpen: Boolean? = null
    protected var catechismOpen: Boolean? = null
    protected var creedOpen: Boolean? = null
    protected var helpOpen: Boolean? = null
    protected var proofs = true
    protected var answers = true
    protected var searchAll = false
    protected var textSearch: Boolean? = null
    protected var questionSearch: Boolean? = null
    protected var readerSearch: Boolean? = null

    var query: String? = null
    var docTypes: ArrayList<String>? = null
    var docTitles: ArrayList<String>? = null
    var docTypeSpinnerAdapter: ArrayAdapter<String>? = null
    var docTitleSpinnerAdapter: ArrayAdapter<String>? = null
    var searchBox: SearchView? = null
    var documentDB: SQLiteDatabase? = null
    var themeName: Boolean? = false
    var chipGroup: ChipGroup? = null
    var header = ""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.search_fragment, container, true)
        //Chip group initialization
        chipGroup = root.findViewById(R.id.chip_group)
        optionGroup = root.findViewById(R.id.option_group)
        //Search Field initialization
        searchButton = root.findViewById(R.id.searchFAB)
        searchButton!!.setOnClickListener(searchButtonClicker)
        //Search View
        searchBox = root.findViewById(R.id.searchView1)
        searchBox!!.setOnQueryTextListener(searchQueryListener)
        searchBox!!.setOnKeyListener(submissionKey)
//ChipGroup Listeners
        optionGroup!!.setOnCheckedChangeListener(optionListener)
        //individual chip listeners
        answerChip = root.findViewById(R.id.answerChip)
        proofChip = root.findViewById(R.id.proofChip)
        searchAllChip = root.findViewById(R.id.searchAllChip)
        answerChip!!.setOnCheckedChangeListener(checkBox)
        proofChip!!.setOnCheckedChangeListener(checkBox)
        searchAllChip!!.setOnCheckedChangeListener(checkBox)



        return root
    }

    //Search Button Click Event
    var searchButtonClicker = View.OnClickListener {
        val query: String
        if (!readerSearch!!) {
            query = searchBox!!.query.toString()
            if (query.isEmpty()) ErrorMessage(resources.getString(R.string.query_error)) else searchPassThru(
                query
            )
        } else {
            query = ""
            searchPassThru(query)
        }


    }
    //SearchView

    var searchQueryListener: SearchView.OnQueryTextListener =
        object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(entry: String): Boolean {
                query = entry
                if (!readerSearch!!) {
                    if (query!!.isEmpty()) ErrorMessage(resources.getString(R.string.query_error)) else searchPassThru(
                        query
                    )
                } else searchPassThru(query)
                return false
            }

            //nothing happens here
            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        }

    //Search Method to rule them all
    fun searchPassThru(stringQuery: String?) {
        query = stringQuery
        TODO(
            "Implement one single method for passing multiple lines into a bundle and also allow for integers to be " +
                    "passed by using a boolean and an int"
        )
    }

    //Enter Key
    private var submissionKey = View.OnKeyListener { v, keyCode, event ->
        val searchBox = v as SearchView
        if (event.keyCode == KeyEvent.KEYCODE_ENTER) {
            query = searchBox.query.toString()
            Log.d("View", String.format("%s", event.displayLabel))
            if (!query!!.isEmpty() and !readerSearch!!) searchPassThru(query) else ErrorMessage(
                resources.getString(R.string.query_error)
            )
            true
        } else {
            false
        }
    }


    // For the advanced search options
    private var checkBox = CompoundButton.OnCheckedChangeListener { compoundButton, _ ->
        when (compoundButton.id) {
            R.id.proofChip -> proofs = !proofChip!!.isChecked
            R.id.answerChip -> answers = !answerChip!!.isChecked
            R.id.searchAllChip -> searchAll = searchAllChip!!.isChecked
        }
    }

    fun ErrorMessage(message: String?) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT)
    }

    //For the option group chips
    private var optionListener = ChipGroup.OnCheckedChangeListener { group, checkedId ->
        val enter = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER)
        //val searchButton!! = <ExtendedFloatingActionButton>(R.id.searchButton!!)
        if (checkedId == (R.id.topicChip)) {
            searchBox!!.isEnabled = true
            searchBox!!.imeOptions = EditorInfo.IME_ACTION_SEARCH
            searchBox!!.isSubmitButtonEnabled = true
            searchBox!!.setOnKeyListener(submissionKey)
            searchBox!!.setOnQueryTextListener(searchQueryListener)
            searchBox!!.inputType = InputType.TYPE_CLASS_TEXT
            textSearch = true
            questionSearch = false
            readerSearch = false
            searchButton!!.text = resources.getString(R.string.Search)

        } else if (checkedId == R.id.questionChip) {
            searchBox!!.isEnabled = true
            searchBox!!.imeOptions = EditorInfo.IME_ACTION_SEARCH
            searchBox!!.inputType = InputType.TYPE_CLASS_NUMBER
            searchBox!!.setOnQueryTextListener(searchQueryListener)
            searchBox!!.setOnKeyListener(submissionKey)
            textSearch = false
            readerSearch = false
            questionSearch = true
            searchButton!!.text = resources.getString(R.string.Search)

        } else if (checkedId == R.id.readDocsChip) {
            searchButton!!.text = resources.getString(R.string.read_button_text)
            textSearch = false
            questionSearch = false
            readerSearch = true
        }

    }


    companion object {
        private const val ALLOPEN = "allopen"
        private const val PROOFS = "proofs"
        private const val TYPE = "type"
        private const val QUERY = "query"
        private const val CATECHISMOPEN = "catechismopen"
        private const val CREEDOPEN = "creedopen"
        private const val CONFESSIONOPEN = "confessionopen"
        private const val FILENAME = "filename"
        private const val READERSEARCH = "readersearch"
        private const val ANSWERS = "answers"
        private const val QUESTIONSEARCH = "questionsearch"
        private const val TEXTSEARCH = "textsearch"
        private const val HEADER = "header"
        private const val DOCTYPES = "doctypes"
        private const val DOCNAMES = "docnames"


        fun NewSearch(): SearchScreenFragment {
            val fragment = SearchScreenFragment()

            return fragment
        }
    }


}