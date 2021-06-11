package com.confessionsearch.release1

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.text.Html
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import java.util.*
import java.util.regex.Pattern

// This class will handle all of the search functionality within the application
class Search : AppCompatActivity() {

    var query: String? = null
    var dbName = "confessionSearchDB.sqlite3"
    var documentDBHelper: documentDBClassHelper? = null
    var fileName: String? = null

    //Document Type
    protected var allOpen: Boolean? = null
    protected var confessionOpen: Boolean? = null
    protected var catechismOpen: Boolean? = null
    protected var creedOpen: Boolean? = null
    protected var helpOpen: Boolean? = null

    //Advanced Search Options
    protected var proofs = true
    protected var answers = true
    protected var searchAll = false


    //Search Type
    protected var textSearch: Boolean? = null
    protected var questionSearch: Boolean? = null
    protected var readerSearch: Boolean? = null
    var header = ""
    var searchFragment: SearchFragmentActivity? = null
    // var masterList = DocumentList()

    fun FilterResults(
        documentList: DocumentList,
        answers: Boolean?,
        proofs: Boolean,
        query: String?
    ) {
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
                        val wordIndex =
                            word.toUpperCase().indexOf(query!!.toUpperCase(), matchIndex)
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
        val replaceString = Pattern.compile(query, Pattern.CASE_INSENSITIVE)
        val m = replaceString.matcher(sourceStr)
        resultString = m.replaceAll(replaceQuery)
        Log.d("Test", resultString)
        return resultString
    }


    fun search(searchQuery: String?) {
        TODO("Pass in variables from ")
        query = searchQuery
        var docID = 0
        var accessString = ""
        var fileString = ""
        Log.d("Search()", getString(R.string.search_execution_begins))
        searchFragment = SearchFragmentActivity()

        //Filters for how searches are executed by document type and name
        if (!searchAll) {
            accessString = String.format(" and documenttitle.documentName = '%s' ", fileName)
        }
        if (allOpen!!) {
            docID = 0
            fileString = if (searchAll) "SELECT * FROM DocumentTitle" else String.format(
                "Select * From DocumentTitle where DocumentTitle.DocumentName = '%s'",
                fileName
            )
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
        masterList = documentDBHelper!!.getAllDocuments(
            fileString,
            fileName,
            docID,
            allOpen,
            documentDB,
            accessString,
            masterList,
            this
        )
        for (d in masterList) {
            if (d.documentText!!.contains("|") or d.proofs!!.contains("|")) {
                d.proofs = formatter(d.proofs!!)
                d.documentText = formatter(d.documentText!!)
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
                Toast.makeText(
                    this,
                    String.format("No Results were found for %s", query),
                    Toast.LENGTH_LONG
                ).show()
                setContentView(R.layout.error_page)
                val errorMsg = findViewById<TextView>(R.id.errorTV)
                errorMsg.text = String.format(
                    """
    No results were found for %s 
    
    Go back to home page to search for another topic
    """.trimIndent(), query
                )
                val alert = AlertDialog.Builder(this)
                alert.setTitle("No Results found!")
                alert.setMessage(
                    String.format(
                        """
    No Results were found for %s.
    
    Do you want to go back and search for another topic?
    """.trimIndent(), query
                    )
                )
                alert.setPositiveButton("Yes") { dialog, which ->
                    intent = Intent(this@Search, this@Search.javaClass)
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
                fab.setBackgroundColor(Color.BLACK)
                shareNote = ""
                shareNote = (docTitleBox.text.toString() + "<br>" + "<br>" + chNumbBox.text + "<br>"
                        + "<br>" + document.documentText + "<br>" + "Proofs" + "<br>" + document.proofs)
                saveFab.setOnClickListener(saveNewNote)
            }


        }
    }

    private fun formatter(formatString: String): String {
        var formatString = formatString
        formatString = formatString.replace("|", "<br><br>")
        return formatString
    }

    companion object {
        var masterList = DocumentList()
    }
}