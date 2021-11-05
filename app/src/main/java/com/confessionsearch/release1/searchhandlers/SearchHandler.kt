package com.confessionsearch.release1.searchhandlers

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.viewpager.widget.ViewPager
import com.confessionsearch.release1.R
import com.confessionsearch.release1.data.documents.Document
import com.confessionsearch.release1.data.documents.DocumentDBClassHelper
import com.confessionsearch.release1.data.documents.DocumentList
import com.confessionsearch.release1.searchresults.SearchAdapter
import com.confessionsearch.release1.searchresults.SearchFragmentActivity
import com.confessionsearch.release1.searchresults.SearchResultFragment
import com.confessionsearch.release1.ui.notesActivity.NotesComposeActivity
import com.example.awesomedialog.*
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import www.sanju.motiontoast.MotionToast
import java.util.*
import java.util.regex.Pattern

//Handle Search algorithm and Sort by types here

class SearchHandler : AppCompatActivity() {

    var masterList = DocumentList()
    var searchFragment: SearchFragmentActivity? = null
    var documentDB: SQLiteDatabase? = null
    var docDBhelper: DocumentDBClassHelper? = null
    var shareList = ""
    var shareNote = ""
    var docType = ""
    var refreshQuery = ""
    var sortType = ""
    lateinit var adapter: SearchAdapter
    lateinit var vp2: ViewPager

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {//, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState)
        val allDocsBool = intent.getBooleanExtra("AllDocs", false)
        val answers = intent.getBooleanExtra("Answers", false)
        val confession = intent.getBooleanExtra("Confession", false)
        val catechism = intent.getBooleanExtra("Catechism", false)
        val creed = intent.getBooleanExtra("Creed", false)
        docType = intent.getStringExtra("docType").toString()
        val searchAll = intent.getBooleanExtra("SearchAll", false)
        val readerSearch = intent.getBooleanExtra("Reader", false)
        val textSearch = intent.getBooleanExtra("Text", false)
        val questionSearch = intent.getBooleanExtra("Question", false)
        val proofs = intent.getBooleanExtra("Proofs", false)
        val query = intent.getStringExtra("Query")
        val fileName = intent.getStringExtra("FileName")
        sortType = intent.getStringExtra("SortType").toString()
        Log.d("Handle", "SearchHandler is in charge")

        search(
            query, allDocsBool, answers,
            docType,
            searchAll,
            proofs,
            readerSearch,
            textSearch,
            questionSearch,
            fileName
        )
    }
    //The main star of the show. This method is critical to the rest of the app. It handles the search function
    @RequiresApi(Build.VERSION_CODES.N)
    fun search(
        query: String?,
        allOpen: Boolean?,
        answers: Boolean?,
        docType: String?,
        searchAll: Boolean?,
        proofs: Boolean?,
        readerSearch: Boolean?,
        textSearch: Boolean?,
        questionSearch: Boolean?,
        fileName: String?
    ) {
        Log.d("SearchMethod", "SearchWorks")
        var query = query
        var docID = 0
        var accessString = ""
        var fileString = ""

        docDBhelper = DocumentDBClassHelper(this)
        documentDB = docDBhelper!!.readableDatabase

        Log.d("Search()", "Search Party Begins")
        searchFragment = SearchFragmentActivity()
        //Filters for how searches are executed by document type and name

        when (docType) {
            "All" -> {
                docID = 0
                fileString = if (!searchAll!!) String.format(
                    "Select * From DocumentTitle where DocumentTitle.DocumentName = '%s'",
                    fileName
                ) else "SELECT * FROM DocumentTitle"
                accessString =
                    if (searchAll) String.format("Select * from Document") else "s"
            }
            "Catechism" -> {
                docID = 3
                fileString = if (!searchAll!!) {
                    String.format(
                        " documentTitle.DocumentTypeID = 3 AND DocumentName = '%s' ",
                        fileName
                    )
                } else "documentTitle.DocumentTypeID=3"
                accessString =
                    if (!searchAll) "s" else ""//"AND DocumentTitle.DocumentTypeID=3"// else "s"//String.format(" AND DocumentTitle.DocumentName = '%s' ", fileName) //""// "and documenttitle.documentName = '%s'"
            }
            "Creed" -> {
                docID = 1
                fileString = if (!searchAll!!) {
                    String.format(
                        " documentTitle.DocumentTypeID = 1 AND DocumentName = '%s'",
                        fileName
                    )
                } else "documentTitle.DocumentTypeID=1"
                accessString =
                    if (searchAll) "" else "s"// AND documentTitle.DocumentTypeID=1
            }
            "Confession" -> {
                docID = 2
                fileString = if (!searchAll!!) {
                    String.format(
                        " documentTitle.DocumentTypeID = 2 AND DocumentName = '%s'",
                        fileName
                    )
                } else "documentTitle.DocumentTypeID=2"
                accessString =
                    if (searchAll) "" else "s"

            }
        }

        //This fills the list with entries for filtering and sorting
        masterList = docDBhelper!!.getAllDocuments(
            fileString,
            fileName,
            docID,
            allOpen,
            searchAll,
            documentDB!!,
            accessString,
            masterList,
            this
        )!!
        for (d in masterList) {
            if (d.documentText!!.contains("|") or d.proofs!!.contains("|")) {
                d.proofs = formatter(d.proofs!!)
                d.documentText = formatter(d.documentText!!)
            }
        }
        //Search topics and filter them
        if (!readerSearch!! and textSearch!! and !questionSearch!!) {
            this.FilterResults(
                masterList,
                allOpen,
                answers,
                proofs!!,
                query,
                sortType,
                fileName,
                docID
            )
            if (masterList.size > 1) {
                refreshQuery = query!!
                refreshFragmentsOnScreen(query)
            }

        } else if (questionSearch and (query !== "") and !readerSearch and !textSearch) {
            if (query !== "") {
                val searchInt = query!!.toInt()
                FilterResults(masterList, answers, proofs, searchInt)
                if (masterList.size > 1)
                    refreshFragmentsOnScreen(query)
            } else {
                recreate()
            }
        } else if (readerSearch and !questionSearch and !textSearch) {
            query = if (!searchAll!!) {
                fileName
            } else "View All"
            refreshQuery = query!!
            if (masterList.size > 1)
                refreshFragmentsOnScreen(query)
        }
        if (masterList.size < 2) {
            //Returns an error if there are no results in the list
            if (masterList.size == 1) {
                val document = masterList[masterList.size - 1]
                try {
                    setContentView(R.layout.search_results)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    setContentView(R.layout.error_page)
                }
                var header = ""
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
                    chNumbBox.text =
                        String.format("%s %s: %s", header, document.chNumber, document.chName)
                } else if (chapterBox.text.toString().contains("I. ")) {
                    header = "Chapter"
                    chNumbBox.text =
                        String.format("%s %s: %s", header, document.chNumber, document.chName)
                } else chNumbBox.text = String.format("%s", document.documentName)
                val newLine = "\r\n"
                shareList = (docTitleBox.text.toString() + newLine + chNumbBox.text + newLine
                        + newLine + chapterBox.text + newLine + "Proofs" + newLine + proofBox.text)
                fab.setOnClickListener(shareContent)
                // fab.setBackgroundColor(Color.BLACK)

                shareNote = (docTitleBox.text.toString() + "<br>" + "<br>" + chNumbBox.text + "<br>"
                        + "<br>" + document.documentText + "<br>" + "Proofs" + "<br>" + document.proofs)
                saveFab.setOnClickListener(saveNewNote)
            } else {
                Log.i("Error", "No results found for Topic")
                //Night Mode Code to allow for dark mode toasts
                when (applicationContext.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
                    Configuration.UI_MODE_NIGHT_YES -> {
                        MotionToast.darkToast(
                            this,
                            "No Results Found",
                            String.format("No results were found for %s", query),
                            MotionToast.TOAST_ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.SHORT_DURATION,
                            ResourcesCompat.getFont(this, R.font.helvetica_regular)
                        )
                    }
                    Configuration.UI_MODE_NIGHT_NO -> {
                        MotionToast.createToast(
                            this,
                            "No Results Found",
                            String.format("No results were found for %s", query),
                            MotionToast.TOAST_ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.SHORT_DURATION,
                            ResourcesCompat.getFont(this, R.font.helvetica_regular)
                        )

                    }
                }
                super.setContentView(R.layout.error_page)
                val errorMsg = findViewById<TextView>(R.id.errorTV)
                errorMsg.text = String.format(
                    """
    No results were found for %s 
    
    Go back to home page to search for another topic
    """.trimIndent(), query
                )
                val awesomeDialog = AwesomeDialog.build(this)
                    .title(
                        "No Results Found!",

                        )
                    .body(
                        "No results were found. Do you want to go back and search for another topic?",
                    )
                    .background(R.drawable.error_background)
                    .onPositive("Yes") {
                        this.onBackPressed()
                    }
                    .onNegative("No") {
                    }
                    .position(AwesomeDialog.POSITIONS.CENTER)

                if (!isFinishing) awesomeDialog.show()

            }
        }
    }

    private fun refreshFragmentsOnScreen(query: String?) {
        setContentView(R.layout.index_pager)
        adapter = SearchAdapter(supportFragmentManager, masterList, query!!)
        vp2 = findViewById<ViewPager>(R.id.resultPager)
        searchFragment!!.DisplayResults(masterList, vp2, adapter, query, 0)
    }


    //Filter out search results
    @RequiresApi(Build.VERSION_CODES.N)
    fun FilterResults(
        documentList: DocumentList, allOpen: Boolean?,
        answers: Boolean?,
        proofs: Boolean,
        query: String?, sortOrderString: String?, fileName: String?, docID: Int?
    ) {
        val resultList = DocumentList()
        Log.d("SORTORDER", sortOrderString!!)
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
                        val wordIndex =
                            word.toUpperCase()
                                .indexOf(query!!.toUpperCase(), matchIndex)
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
        for (d in resultList) {
            d.proofs = HighlightText(d.proofs!!, query)
            d.documentText = HighlightText(d.documentText, query)
        }
        sortOrder(resultList)
        masterList = resultList
    }

    //Menu Functions
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.numerical_Ascending -> {
                Log.d("MENU", "Chapter Order Ascending Selected")
                Toast.makeText(this, "Chapter Order Ascending Item Selected", Toast.LENGTH_LONG)
                    .show()
                sortOptions(CHAPTER_ASC, masterList)
                refreshFragmentsOnScreen(refreshQuery)
            }
            R.id.numerical_Descending -> {
                Log.d("MENU", "Chapter Order Descending Selected")
                Toast.makeText(this, "Chapter Order Descending Item Selected", Toast.LENGTH_LONG)
                    .show()
                sortOptions(CHAPTER_DSC, masterList)
                refreshFragmentsOnScreen(refreshQuery)
            }
            R.id.match_Order_Ascending -> {
                Log.d("MENU", "Match Order Ascending Selected")
                Toast.makeText(this, "Match Order Ascending Item Selected", Toast.LENGTH_LONG)
                    .show()
                sortOptions(MATCH_ASC, masterList)
                refreshFragmentsOnScreen(refreshQuery)
            }
            R.id.match_Order_Descending -> {
                Log.d("MENU", "Match Order Descending Selected")
                Toast.makeText(this, "Descending Order Descending Item Selected", Toast.LENGTH_LONG)
                    .show()
                sortOptions(MATCH_DSC, masterList)
                refreshFragmentsOnScreen(refreshQuery)
            }
        }
        return true
    }

    //Menu Creation
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.sort_menu, menu)
        return true
    }

    //Look for the matching chapter/question index
    fun FilterResults(
        documentList: DocumentList,
        answers: Boolean?,
        proofs: Boolean?,
        indexNum: Int
    ) {
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

    var shareContent = View.OnClickListener {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        val INTENTNAME = "SHARE"
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareList)
        sendIntent.type = "text/plain"
        Log.i(SearchResultFragment.TAG, "Sharing Content with provider")
        startActivity(Intent.createChooser(sendIntent, INTENTNAME))
    }
    var saveNewNote = View.OnClickListener {
        val intent = Intent(this, NotesComposeActivity::class.java)
        intent.putExtra("search_result_save", shareNote)
        intent.putExtra("activity_ID", SearchResultFragment.ACTIVITY_ID)
        Log.i(SearchResultFragment.TAG, "Opening new note to save entry")
        startActivity(intent)
    }

    //Highlights topic entries in search results
    fun HighlightText(sourceStr: String?, query: String?): String {
        val replaceQuery = "<b>$query</b>"
        var resultString = ""
        val replaceString = Pattern.compile(query!!, Pattern.CASE_INSENSITIVE)
        val m = replaceString.matcher(sourceStr!!)
        resultString = m.replaceAll(replaceQuery)
        return resultString
    }

    //Formats the code into a user friendly readable format
    private fun formatter(formatString: String): String {
        var formatString = formatString
        formatString = formatString.replace("|", "<br><br>")
        return formatString
    }

    //Back Button
    override fun onBackPressed() {
        this.finish()
        super.onBackPressed()

    }

    //Sorts documents based on order given
    fun sortOrder(docList: DocumentList) {
        //Prevents Creeds from crashing the app
        if (docType == "CREED")
            Collections.sort(docList, Document.compareMatches)
        else {
            if (sortType == "Chapter")
                Collections.sort(docList, Document.compareMatchesAndChapters)
            else
                Collections.sort(docList, Document.compareMatches)
        }
    }

    private fun sortOptions(SortOrder: String, docList: DocumentList) {
        when (SortOrder) {
            "Chapter_ASC" -> Collections.sort(docList, Document.compareMatchesAndChapters)
            "Chapter_DSC" -> {
                Collections.sort(docList, Document.compareMatchesAndChapters)
                Collections.reverse(docList)

            }
            "Matches_DSC" -> Collections.sort(docList, Document.compareMatches)
            "Matches_ASC" -> {
                Collections.sort(docList, Document.compareMatches)
                Collections.reverse(docList)
            }
        }

    }

    companion object {
        const val CHAPTER_ASC = "Chapter_ASC"
        const val CHAPTER_DSC = "Chapter_DSC"
        const val MATCH_ASC = "Matches_ASC"
        const val MATCH_DSC = "Matches_DSC"
        const val ACTIVITY_ID = 676
        const val TAG = "SearchHandler"
    }

}

