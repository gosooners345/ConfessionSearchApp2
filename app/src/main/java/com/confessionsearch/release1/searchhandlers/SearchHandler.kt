package com.confessionsearch.release1.searchhandlers

/* Author: Brandon Guerin
*
*  Language: Kotlin
*
*  Application : The Reformed Collective
*  Class: SearchHandler.kt
*  Purpose: This class handles all search related functions and delivers search results to the
*  screen when completed
*/

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Html.FROM_HTML_MODE_COMPACT
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.viewpager2.widget.ViewPager2
import com.codeboy.pager2_transformers.Pager2_ClockSpinTransformer
import com.confessionsearch.release1.R
import com.confessionsearch.release1.data.documents.Document
import com.confessionsearch.release1.data.documents.DocumentDBClassHelper
import com.confessionsearch.release1.data.documents.DocumentList
import com.confessionsearch.release1.searchresults.SearchAdapter
import com.confessionsearch.release1.searchresults.SearchResultFragment
import com.confessionsearch.release1.ui.notes.NotesComposeActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import www.sanju.motiontoast.MotionToast
import java.util.*
import java.util.regex.Pattern


//Handle Search algorithm and Sort by types here

class SearchHandler : AppCompatActivity() {

    var masterList = DocumentList()
    var documentDB: SQLiteDatabase? = null
    var docDBhelper: DocumentDBClassHelper? = null
    var shareList = ""
    var shareNote = ""
    var docType = ""
    var refreshQuery = ""
    var sortType = ""
    lateinit var adapter: SearchAdapter
    lateinit var vp2: ViewPager2

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val allDocsBool = intent.getBooleanExtra("AllDocs", false)
        val answers = intent.getBooleanExtra("Answers", false)
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


    // This refreshes the list and fragments baseed on sort order,etc.
    private fun refreshFragmentsOnScreen(query: String?) {
        setContentView(R.layout.index_pager)
        adapter = SearchAdapter(supportFragmentManager, masterList, query!!, lifecycle)
        vp2 = findViewById<ViewPager2>(R.id.resultPager2)
        val animTransformer = Pager2_ClockSpinTransformer()

        vp2.setPageTransformer(animTransformer)
        adapter.createFragment(0)
        vp2.adapter = adapter
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        TabLayoutMediator(tabLayout, vp2) { tab, position ->
            vp2.setCurrentItem(tab.position, true)
            tab.text = String.format("Result %s of %s for %s", position + 1, masterList.size, query)
        }
            .attach()
        adapter.saveState()
    }

    //Menu Functions
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.numerical_Ascending -> {
                Log.d("MENU", "Chapter Order Ascending Selected")
                sortOptions(CHAPTER_ASC, masterList)
                refreshFragmentsOnScreen(refreshQuery)
            }
            R.id.numerical_Descending -> {
                Log.d("MENU", "Chapter Order Descending Selected")
                sortOptions(CHAPTER_DSC, masterList)
                refreshFragmentsOnScreen(refreshQuery)
            }
            R.id.match_Order_Ascending -> {
                Log.d("MENU", "Match Order Ascending Selected")
                sortOptions(MATCH_ASC, masterList)
                refreshFragmentsOnScreen(refreshQuery)
            }
            R.id.match_Order_Descending -> {
                Log.d("MENU", "Match Order Descending Selected")
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


    //Sorts documents based on order given
    private fun sortOrder(docList: DocumentList) {
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

    // This will be replaced with an AI function that will probably improve performance and such. It'd be cool if the functionality allowed for
    // stuff like auto quote and grammar correction in the DB or in results
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
        //Filters for how searches are executed by document type and name

        when (docType) {
            "All" -> { // #1
                docID = 0
                fileString = if (!searchAll!!)
                    String.format( // #1a.
                        "Select * From DocumentTitle where DocumentTitle.DocumentName = '%s'",
                        fileName
                    ) else "SELECT * FROM DocumentTitle" // #1b
                accessString =
                    if (searchAll) String.format("Select * from Document") else "s" //1c
            }
            "Catechism" -> { //#2
                docID = 3
                fileString = if (!searchAll!!) {
                    String.format( //#2a.
                        " documentTitle.DocumentTypeID = 3 AND DocumentName = '%s' ",
                        fileName
                    )
                } else "documentTitle.DocumentTypeID=3" //#2b
                accessString =
                    if (!searchAll) "s" else "" //2c
            }
            "Creed" -> { //#3
                docID = 1
                fileString = if (!searchAll!!) {
                    String.format( //#3a
                        " documentTitle.DocumentTypeID = 1 AND DocumentName = '%s'",
                        fileName
                    ) //#3b
                } else "documentTitle.DocumentTypeID=1"
                accessString =
                    if (searchAll) "" else "s" //3c
            }
            "Confession" -> { //#4
                docID = 2
                fileString = if (!searchAll!!) {
                    String.format( // #4a
                        " documentTitle.DocumentTypeID = 2 AND DocumentName = '%s'",
                        fileName
                    )
                } else "documentTitle.DocumentTypeID=2" // 4b
                accessString =
                    if (searchAll) "" else "s" //4c

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
                if (masterList.size > 1) {
                    refreshFragmentsOnScreen(query)

                }
            } else {
                recreate()
            }
        } else if (readerSearch and !questionSearch and !textSearch) {
            query = if (!searchAll!!) {
                fileName
            } else "View All"
            refreshQuery = query!!
            if (masterList.size > 1) {
                refreshFragmentsOnScreen(query)

            }
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
                val lineBreak = "<br><br>"
                val singleBreak = "<br>"
                var titleHeader = ""
                var tagLine = ""
                var matchLine = ""
                val newLine = "\r\n"
                val saveFab = findViewById<Button>(R.id.saveNote)
                val fab = findViewById<Button>(R.id.shareActionButton)
                val chapterBox = findViewById<TextView>(R.id.chapterText)
                val chNumbBox = findViewById<TextView>(R.id.confessionChLabel)
                val docTitleBox = findViewById<TextView>(R.id.documentTitleLabel)
                if (document.documentText!!.contains("Question")) {
                    header = "Question "
                    titleHeader =
                        String.format("%s %s: %s", header, document.chNumber, document.chName)
                } else if (document.documentText!!.contains("I. ")) {
                    header = "Chapter"
                    titleHeader =
                        String.format("%s %s: %s", header, document.chNumber, document.chName)
                } else titleHeader = String.format("%s", document.documentName)
                shareNote = ""
                tagLine = String.format("Tags: %s", document.tags)
                matchLine = String.format("Matches: %s", document.matches)
                docTitleBox.text = document.documentName
                chNumbBox.text = titleHeader
                chapterBox.text = Html.fromHtml(
                    lineBreak + document.documentText + lineBreak + "Proofs:" + lineBreak + document.proofs +
                            lineBreak + tagLine + lineBreak + matchLine, FROM_HTML_MODE_COMPACT
                )
                shareList = docTitleBox.text.toString() + newLine + chapterBox.text.toString()
                shareNote = shareList
                fab.setOnClickListener(shareContent)
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
                val errorMessage = String.format(
                    """
    No results were found for %s 
    
    Go back to home page to search for another topic
    """.trimIndent(), query
                )
                errorMsg.text = errorMessage

                MaterialAlertDialogBuilder(this)
                    .setTitle("No Results Found!")
                    .setMessage(errorMessage)
                    .setPositiveButton("Yes") { _, _ ->
                        onBackPressed()
                    }
                    .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                    .show()
            }
        }
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
                            word.uppercase(Locale.getDefault())
                                .indexOf(query!!.uppercase(Locale.getDefault()), matchIndex)
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

    // View On Click Event Handlers for saving notes and sharing content
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


    companion object {
        const val CHAPTER_ASC = "Chapter_ASC"
        const val CHAPTER_DSC = "Chapter_DSC"
        const val MATCH_ASC = "Matches_ASC"
        const val MATCH_DSC = "Matches_DSC"
        const val TAG = "SearchHandler"
    }

}



