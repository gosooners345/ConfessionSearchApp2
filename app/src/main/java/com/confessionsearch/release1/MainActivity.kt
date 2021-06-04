package com.confessionsearch.release1

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.InputType
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ShareActionProvider
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabItem
import com.google.android.material.tabs.TabLayout
import java.util.*
import java.util.regex.Pattern

open class MainActivity : AppCompatActivity() {
    var shareProvider: ShareActionProvider? = null
    var bottomNav: BottomNavigationView? = null
    private var documentTypeSpinner: Spinner? = null
    private var documentNameSpinner: Spinner? = null
    var helpButton: ExtendedFloatingActionButton? = null
    var searchButton: ExtendedFloatingActionButton? = null
    var notesButton: ExtendedFloatingActionButton? = null
    var header = ""
    protected var textSearch: Boolean? = null
    protected var questionSearch: Boolean? = null
    protected var readerSearch: Boolean? = null
    var query: String? = null
    var dbName = "confessionSearchDB.sqlite3"
    var documentDBHelper: documentDBClassHelper? = null
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

    // Placeholders For New Tab UI Layout
    var tabLayout: TabLayout? = null
    var searchTab: TabItem? = null
    var notesTab: TabItem? = null
    var viewPager2: ViewPager2? = null

    //Testing
    var answerChip: Chip? = null
    var proofChip: Chip? = null
    var searchAllChip: Chip? = null
    var optionGroup: ChipGroup? = null

    //Testing
    var topicChip: Chip? = null
    var questionChip: Chip? = null
    var readDocsChip: Chip? = null

    var docTypes: ArrayList<String>? = null
    var docTitles: ArrayList<String>? = null
    var docTypeSpinnerAdapter: ArrayAdapter<String>? = null
    var docTitleSpinnerAdapter: ArrayAdapter<String>? = null
    var searchBox: SearchView? = null
    var documentDB: SQLiteDatabase? = null
    var themeName: Boolean? = false
    var chipGroup: ChipGroup? = null
    var masterList = DocumentList()
    var shareNote: String? = null
    var searchFragment: SearchFragmentActivity? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        val pref: SharedPreferences? = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this)

        this.themeName = pref?.getBoolean("darkMode", true)
        if (!themeName!!) {
            themeID = R.style.LightMode
            setTheme(themeID)
        } else if (themeName as Boolean) {
            themeID = R.style.DarkMode
            setTheme(themeID)
        }
        super.onCreate(savedInstanceState)
        //Set the show for the search app
        setTitle(R.string.app_name)
        refreshLayout(R.layout.test_activity_main)
        bottomNavEnabler()
        //New Layout for tab screen

    }
    //New Tab Layout 06.03.2021


    // Getting replaced
    protected fun bottomNavEnabler() {
        this.bottomNav = findViewById(R.id.bottom_navigation)
        bottomNav!!.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.search_page -> {
                    refreshLayout(R.layout.activity_main)
                    Log.d("HOME PAGE", "Home Page Launched")
                    bottomNavEnabler()
                    Log.d("BottomNav", "Home Page Bottom Nav Enabled")
                    //updateNavigationBarState(R.id.search_page);
                    Log.d("HomePage", "Menu item selected lit up")
                }
                R.id.notes_page -> {
                    val noteIntent = Intent(applicationContext, NotesActivity::class.java)
                    startActivity(noteIntent)
                }
                R.id.settings_page -> {
                    startActivityForResult(Intent(this@MainActivity, ThemePreferenceActivity::class.java), SETTINGS_ACTION)
                    bottomNavEnabler()
                }
                else -> false
            }
            item.isChecked = true
            true
        }

    }


/*    fun SearchType(view: View) {
        val enter = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER)
        val radio = view as RadioButton
        val searchFab = findViewById<ExtendedFloatingActionButton>(R.id.searchFAB)
        if (radio === findViewById<View>(R.id.topicRadio)) {
            if (radio.isChecked) {
                searchBox!!.isEnabled = true
                searchBox!!.imeOptions = EditorInfo.IME_ACTION_SEARCH
                searchBox!!.isSubmitButtonEnabled = true
                searchBox!!.setOnKeyListener(submissionKey)
                searchBox!!.setOnQueryTextListener(searchQueryListener)
                searchBox!!.inputType = InputType.TYPE_CLASS_TEXT
                textSearch = true
                questionSearch = false
                readerSearch = false
                searchFab.text = resources.getString(R.string.Search)
            }
        } else if (radio === findViewById<View>(R.id.chapterRadio)) if (radio.isChecked) {
            searchBox!!.isEnabled = true
            searchBox!!.imeOptions = EditorInfo.IME_ACTION_SEARCH
            searchBox!!.inputType = InputType.TYPE_CLASS_NUMBER
            searchBox!!.setOnQueryTextListener(searchQueryListener)
            searchBox!!.setOnKeyListener(submissionKey)
            textSearch = false
            readerSearch = false
            questionSearch = true
            searchFab.text = resources.getString(R.string.Search)
        } else if (radio === findViewById<View>(R.id.viewAllRadio)) if (radio.isChecked) {
            searchFab.text = resources.getString(R.string.read_button_text)
            textSearch = false
            questionSearch = false
            readerSearch = true
        }
    }*/

    // Update: Testing out chip check settings
    var checkBox = CompoundButton.OnCheckedChangeListener { compoundButton, _ ->
        when (compoundButton.id) {

            // These Work Well
            R.id.proofChip -> proofs = !proofChip!!.isChecked
            R.id.answerChip -> answers = !answerChip!!.isChecked
            R.id.searchAllChip -> searchAll = searchAllChip!!.isChecked
        }
    }
    var optionListener = ChipGroup.OnCheckedChangeListener { group, checkedId ->
        val enter = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER)
        val searchFab = findViewById<ExtendedFloatingActionButton>(R.id.searchFAB)
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
            searchFab.text = resources.getString(R.string.Search)

        } else if (checkedId == R.id.questionChip) {
            searchBox!!.isEnabled = true
            searchBox!!.imeOptions = EditorInfo.IME_ACTION_SEARCH
            searchBox!!.inputType = InputType.TYPE_CLASS_NUMBER
            searchBox!!.setOnQueryTextListener(searchQueryListener)
            searchBox!!.setOnKeyListener(submissionKey)
            textSearch = false
            readerSearch = false
            questionSearch = true
            searchFab.text = resources.getString(R.string.Search)

        } else if (checkedId == R.id.readDocsChip) {
            searchFab.text = resources.getString(R.string.read_button_text)
            textSearch = false
            questionSearch = false
            readerSearch = true
        }

    }


    //Searches the Database for the topic and returns the results in a list
    @RequiresApi(Build.VERSION_CODES.N)
    fun Search(query: String?) {
        var query = query
        var docID = 0
        var accessString = ""
        var fileString = ""
        //Boolean  proofs = true, answers = true, searchAll = false, viewDocs = false;

        Log.d("Search()", getString(R.string.search_execution_begins))
        searchFragment = SearchFragmentActivity()

        //Filters for how searches are executed by document type and name
        if (!searchAll) {
            accessString = String.format(" and documenttitle.documentName = '%s' ", fileName)
        }
        if (allOpen!!) {
            docID = 0
            fileString = if (searchAll) "SELECT * FROM DocumentTitle" else String.format("Select * From DocumentTitle where DocumentTitle.DocumentName = '%s'", fileName)
        }

        if (catechismOpen!!) {
            docID = 3
            fileString = if (!searchAll) {
                String.format(" documentTitle.DocumentTypeID = 3 AND DocumentName = '%s'", fileName)
            } else " documentTitle.DocumentTypeID = 3"
        } else if (confessionOpen!!) {
            docID = 2
            fileString = if (!searchAll) {
                String.format(" documentTitle.DocumentTypeID = 2 AND DocumentName = '%s'", fileName)
            } else {
                " documentTitle.DocumentTypeID = 2"
            }
        } else if (creedOpen!!) {
            docID = 1
            fileString = if (!searchAll) {
                String.format(" documentTitle.DocumentTypeID = 1 AND DocumentName = '%s'", fileName)
            } else {
                String.format(" documentTitle.DocumentTypeID = 1")
            }
        }
        //This fills the list with entries for filtering and sorting
        masterList = documentDBHelper!!.getAllDocuments(fileString, fileName, docID, allOpen, documentDB, accessString, masterList, this)
        for (d in masterList) {
            if (d.documentText!!.contains("|") or d.proofs!!.contains("|")) {
                d.proofs = Formatter(d.proofs!!)
                d.documentText = Formatter(d.documentText!!)
            }
        }
        //Search topics and filter them
        if (!readerSearch!! and textSearch!! and !questionSearch!!) {
            if (!query!!.isEmpty()) {
                this.FilterResults(masterList, answers, proofs, query)
                //Collections.reverse(masterList)
            } else {
                if (masterList.size > 1) {
                    query = fileName
                    setContentView(R.layout.index_pager)
                    val adapter = SearchAdapter(supportFragmentManager, masterList, query!!)
                    val vp2 = findViewById<ViewPager>(R.id.resultPager)
                    searchFragment!!.DisplayResults(masterList, vp2, adapter, query, 0)
                }
            }
        } else if (questionSearch!! and (query !== "") and !readerSearch!! and !textSearch!!) {
            if (query !== "") {
                val searchInt = query!!.toInt()
                FilterResults(masterList, answers, proofs, searchInt)
            } else {
                recreate()
            }
        } else if (readerSearch!! and !questionSearch!! and !textSearch!!) {
            query = if (!searchAll) {
                "Results for All"
            } else "View All"
        }


        //Displays the list of results
        if (masterList.size > 1) {
            setContentView(R.layout.index_pager)
            val adapter = SearchAdapter(supportFragmentManager, masterList, query!!)
            val vp2 = findViewById<ViewPager>(R.id.resultPager)
            searchFragment!!.DisplayResults(masterList, vp2, adapter, query, 0)
        } else {
            //Returns an error if there are no results in the list
            if (masterList.size == 0) {
                Log.i("Error", "No results found for Topic")
                Toast.makeText(this, String.format("No Results were found for %s", query), Toast.LENGTH_LONG).show()
                setContentView(R.layout.error_page)
                val errorMsg = findViewById<TextView>(R.id.errorTV)
                errorMsg.text = String.format("""
    No results were found for %s 
    
    Go back to home page to search for another topic
    """.trimIndent(), query)
                val alert = AlertDialog.Builder(this)
                alert.setTitle("No Results found!")
                alert.setMessage(String.format("""
    No Results were found for %s.
    
    Do you want to go back and search for another topic?
    """.trimIndent(), query))
                alert.setPositiveButton("Yes") { dialog, which ->
                    intent = Intent(this@MainActivity, this@MainActivity.javaClass)
                    searchFragment = null
                    onStop()
                    finish()
                    startActivity(intent)
                }
                alert.setNegativeButton("No") { dialog, which -> dialog.dismiss() }
                val dialog: Dialog = alert.create()
                if (!isFinishing) dialog.show()
            } else {
                val document = masterList[masterList.size - 1]
                setContentView(R.layout.search_results)
                val saveFab = findViewById<ExtendedFloatingActionButton>(R.id.saveNote)
                val fab = findViewById<ExtendedFloatingActionButton>(R.id.shareActionButton)
                val chapterBox = findViewById<TextView>(R.id.chapterText)
                val proofBox = findViewById<TextView>(R.id.proofText)
                val chNumbBox = findViewById<TextView>(R.id.confessionChLabel)
                val docTitleBox = findViewById<TextView>(R.id.documentTitleLabel)
                val tagBox = findViewById<TextView>(R.id.tagView)
                proofBox.text = Html.fromHtml(document.proofs)
                docTitleBox.text = document.documentName
                docTitleBox.text = document.documentName
                chapterBox.text = Html.fromHtml(document.documentText)
                tagBox.text = String.format("Tags: %s", document.tags)
                if (chapterBox.text.toString().contains("Question")) {
                    header = "Question "
                    chNumbBox.text = String.format("%s %s: %s", header, document.chNumber, document.chName)
                } else if (chapterBox.text.toString().contains("I. ")) {
                    header = "Chapter"
                    chNumbBox.text = String.format("%s %s: %s", header, document.chNumber, document.chName)
                } else chNumbBox.text = String.format("%s", document.documentName)
                val newLine = "\r\n"
                shareList = (docTitleBox.text.toString() + newLine + chNumbBox.text + newLine
                        + newLine + chapterBox.text + newLine + "Proofs" + newLine + proofBox.text)
                fab.setOnClickListener(shareContent)
                fab.setBackgroundColor(Color.BLACK)
                shareNote = ""
                shareNote = (docTitleBox.text.toString() + "<br>" + "<br>" + chNumbBox.text + "<br>"
                        + "<br>" + document.documentText + "<br>" + "Proofs" + "<br>" + document.proofs)
                saveFab.setOnClickListener(saveNewNote)
            }
        }
    }

    //Enables Note Saving from results screen
    var saveNewNote = View.OnClickListener {
        val intent = Intent(applicationContext, NotesComposeActivity::class.java)
        intent.putExtra("activity_ID", ACTIVITY_ID)
        intent.putExtra("search_result_save", shareNote)
        startActivity(intent)
    }

    //Enables Share function
    var shareContent = View.OnClickListener {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        val INTENTNAME = "SHARE"
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareList)
        sendIntent.type = "text/plain"
        startActivity(Intent.createChooser(sendIntent, INTENTNAME))
    }

    //Formats the text to be reader friendly
    fun Formatter(formatString: String): String {
        var formatString = formatString
        formatString = formatString.replace("|", "<br><br>")
        return formatString
    }

    //Filter Search Results
    @RequiresApi(Build.VERSION_CODES.N)
    fun FilterResults(documentList: DocumentList, answers: Boolean?, proofs: Boolean, query: String?) {
        val resultList = DocumentList()

        //Break document up into pieces to be searched for topic
        for (document in documentList) {
            val searchEntries = ArrayList<String>()
            searchEntries.add(document.chName!!)
            searchEntries.add(document.documentText!!)
            if (proofs) searchEntries.add(document.proofs!!)
            searchEntries.add(document.tags!!)
            for (word in searchEntries) {
                run {
                    var matchIndex = 0
                    //Tally up all matching sections
                    while (true) {
                        val wordIndex = word.toUpperCase().indexOf(query!!.toUpperCase(), matchIndex)
                        if (wordIndex < 0) break
                        matchIndex = wordIndex + 1
                        document.matches = document.matches!! + 1
                    }
                }
            }
            //If the entry has a match to the query, it'll show up in the results
            if (document.matches!! > 0) {
                // No answers
                if (!answers!!) {
                    if (document.documentText!!.contains("Question")) {
                        val closeIndex = document.documentText!!.indexOf("Answer")
                        document.documentText = document.documentText!!.substring(0, closeIndex - 1)
                    }
                }
                //No proofs
                if (!proofs) document.proofs = "No Proofs available!"
                resultList.add(document)
            }
        }
        //Sort the Results by highest matching tally
        Collections.sort(resultList, Document.compareMatches.reversed())
        for (d in resultList) {
            d.proofs = HighlightText(d.proofs!!, query)
            d.documentText = HighlightText(d.documentText, query)
        }
        masterList = resultList
    }

    //Look for the matching chapter/question index
    fun FilterResults(documentList: DocumentList, answers: Boolean?, proofs: Boolean?, indexNum: Int) {
        val resultList = DocumentList()
        for (document in documentList) {
            if (document.chNumber!! == indexNum) {
                if (!answers!!) {
                    if (document.documentText!!.contains("Question")) {
                        val closeIndex = document.documentText!!.indexOf("Answer")
                        document.documentText = document.documentText!!.substring(0, closeIndex - 1)
                    }
                } else if (!proofs!!) {
                    document.proofs = "No Proofs Available"
                }
                resultList.add(document)
            } else continue
        }

        Collections.sort(resultList, Document.compareMatches)
        masterList = resultList
    }

    //Highlights topic entries in search results
    fun HighlightText(sourceStr: String?, query: String?): String {
        val replaceQuery = "<b>$query</b>"
        var resultString = ""
        val replaceString = Pattern.compile(query, Pattern.CASE_INSENSITIVE)
        val m = replaceString.matcher(sourceStr)
        resultString = m.replaceAll(replaceQuery)
        Log.d("Test", resultString)
        return resultString
    }

    //Executes on startup
    fun refreshLayout(viewID: Int) {
        setContentView(viewID)
        //New Chip Group addition for testing
        chipGroup = findViewById(R.id.chip_group)
        optionGroup = findViewById(R.id.option_group)
        //Search Button Initialization
        searchButton = findViewById(R.id.searchFAB)
        searchButton!!.setOnClickListener(searchButtonListener)
        //Search Box Initialization
        searchBox = findViewById(R.id.searchView1)
        searchBox!!.setOnQueryTextListener(searchQueryListener)
        searchBox!!.setOnKeyListener(submissionKey)

        //tab UI initialization 06/03/2021


        optionGroup!!.setOnCheckedChangeListener(optionListener)

        // Chip Initialization 06/01/2021 - Testing look and execution
        answerChip = findViewById(R.id.answerChip)
        proofChip = findViewById(R.id.proofChip)
        searchAllChip = findViewById(R.id.searchAllChip)

        //Implement check changed listeners
        answerChip!!.setOnCheckedChangeListener(checkBox)
        proofChip!!.setOnCheckedChangeListener(checkBox)
        searchAllChip!!.setOnCheckedChangeListener(checkBox)
        topicChip = findViewById(R.id.topicChip)
        questionChip = findViewById(R.id.questionChip)
        readDocsChip = findViewById(R.id.readDocsChip)

        documentTypeSpinner = findViewById(R.id.documentTypeSpinner)
        documentNameSpinner = findViewById(R.id.documentNameSpinner)
        //Database stuff
        documentDBHelper = documentDBClassHelper(this)
        documentDB = documentDBHelper!!.readableDatabase
        //Document selector Lists
        docTypes = ArrayList()
        docTitles = ArrayList()
        val typeList = documentDBHelper!!.getAllDocTypes(documentDB)
        val documentTitles = documentDBHelper!!.getAllDocTitles(type, documentDB)
        //Document Type Spinner Initialization
        docTypes!!.add("All")
        for (type in typeList) {
            docTypes!!.add(type.documentTypeName!!)
        }
        docTypeSpinnerAdapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, docTypes!!)
        documentTypeSpinner = findViewById(R.id.documentTypeSpinner)
        documentTypeSpinner!!.adapter = docTypeSpinnerAdapter
        documentTypeSpinner!!.onItemSelectedListener = spinnerItemSelectedListener

        //Document Titles Spinner Initialization
        for (docTitle in documentTitles) {
            docTitles!!.add(docTitle.documentName!!)
        }
        docTitleSpinnerAdapter = ArrayAdapter(applicationContext, R.layout.support_simple_spinner_dropdown_item, docTitles!!)
        documentNameSpinner = findViewById(R.id.documentNameSpinner)
        documentNameSpinner!!.onItemSelectedListener = docTitleSpinner
        searchBox!!.setOnKeyListener(submissionKey)
        topicChip!!.performClick()

    }

    //Select search type


    //Menu options for themes
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        when (item.itemId) {
            R.id.settings -> {
                HelpLauncher()
                bottomNavEnabler()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //For Activities that return a result like theme setting
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SETTINGS_ACTION) {
            if (resultCode == ThemePreferenceActivity.RESULT_CODE_THEME_UPDATED) {
                finish()
                startActivity(intent)
                return
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    //This listens for clicks on the spinner widgets on home screen
    var spinnerItemSelectedListener: OnItemSelectedListener = object : OnItemSelectedListener {
        @SuppressLint("ResourceAsColor")
        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            run {
                docTitles = ArrayList()
                type = parent.selectedItem.toString()
                //Gets all document titles and places them in a list
                for (docTitle in documentDBHelper!!.getAllDocTitles(type, documentDB)) {
                    docTitles!!.add(docTitle.documentName!!)
                }
                docTitleSpinnerAdapter = ArrayAdapter(applicationContext, R.layout.support_simple_spinner_dropdown_item, docTitles!!)
                docTitleSpinnerAdapter!!.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
                documentNameSpinner!!.adapter = docTitleSpinnerAdapter
                documentNameSpinner!!.onItemSelectedListener = docTitleSpinner
                when (type.toUpperCase()) {
                    "ALL" -> {
                        allOpen = true
                        confessionOpen = false
                        catechismOpen = false
                        creedOpen = false
                        helpOpen = false
                    }
                    "CONFESSION" -> {
                        allOpen = false
                        confessionOpen = true
                        catechismOpen = false
                        header = "Chapter "
                        creedOpen = false
                        helpOpen = false
                    }
                    "CATECHISM" -> {
                        allOpen = false
                        header = "Question "
                        confessionOpen = false
                        catechismOpen = true
                        creedOpen = false
                        helpOpen = false
                    }
                    "CREED" -> {
                        allOpen = false
                        creedOpen = true
                        catechismOpen = false
                        confessionOpen = false
                        helpOpen = false
                    }
                }
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>) {
            type = parent.selectedItem.toString()
        }
    }
    var docTitleSpinner: OnItemSelectedListener = object : OnItemSelectedListener {
        override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
            try {
                if (themeName!!) //if(themeName.contains("Dark"))
                    (adapterView.getChildAt(0) as TextView).setTextColor(Color.WHITE)
            } catch (ex: Exception) {
                documentNameSpinner!!.onItemSelectedListener = this
                documentNameSpinner!!.setSelection(0)
            }
            fileName = String.format("%s", adapterView.selectedItem.toString())
        }

        override fun onNothingSelected(adapterView: AdapterView<*>?) {}
    }

    @SuppressLint("NewApi")
    //This allows for Submission to take place
    var submissionKey = View.OnKeyListener { v, keyCode, event ->
        val searchBox = v as SearchView
        if (event.keyCode == KeyEvent.KEYCODE_ENTER) {
            query = searchBox.query.toString()
            Log.d("View", String.format("%s", event.displayLabel))
            if (!query!!.isEmpty() and !readerSearch!!) Search(query) else ErrorMessage(resources.getString(R.string.query_error))
            true
        } else {
            false
        }
    }

    // This assigns an action to the search button so it can execute the search
    @SuppressLint("NewApi")
    var searchButtonListener = View.OnClickListener {
        val query: String
        if (!readerSearch!!) {
            query = searchBox!!.query.toString()
            if (query.isEmpty()) ErrorMessage(resources.getString(R.string.query_error)) else Search(query)
        } else {
            query = ""
            Search(query)
        }
    }

    //Return to application's main starting screen
    fun Home() {
// Finish Results activity
        searchFragment!!.finish()
        val intent = Intent(this@MainActivity, MainActivity::class.java)
        Log.d("Application", "Rebooting application")
        searchFragment = null
        //Take user to main search screen
        super@MainActivity.onStop()
        super@MainActivity.finish()
        startActivity(intent)
    }

    //Prevents application from proceeding to execute if an error is found
    fun ErrorMessage(message: String?) {
        //Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        val errorBar = Snackbar.make(findViewById(R.id.layout_super), message!!, BaseTransientBottomBar.LENGTH_SHORT)
        errorBar.setAnchorView(R.id.bottom_navigation)
        errorBar.show()
    }

    //Back Key Behavior
    override fun onBackPressed() {
        super.onBackPressed()
        if (searchFragment != null) Home() else {
            Log.d("Exiting", "Elvis Has Left the building")
            super@MainActivity.finish()
        }
    }

    @SuppressLint("NewApi")
    //SearchView Listeners
    var searchQueryListener: SearchView.OnQueryTextListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(entry: String): Boolean {
            query = entry
            if (!readerSearch!!) {
                if (query!!.isEmpty()) ErrorMessage(resources.getString(R.string.query_error)) else Search(query)
            } else Search(query)
            return false
        }

        //nothing happens here
        override fun onQueryTextChange(newText: String): Boolean {
            return false
        }
    }

    //Enables the app to return to the main screen after home button pressed
    fun HelpLauncher() {
        setContentView(R.layout.help_page)
        bottomNavEnabler()
    }

    companion object {
        protected var SETTINGS_ACTION = 1
        const val ACTIVITY_ID = 31
        private const val THEME = "THEME"
        private const val theme = ""
        var themeID = 0
        var notesArrayList = ArrayList<Notes>()
    }
}