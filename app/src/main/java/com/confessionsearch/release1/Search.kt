package com.confessionsearch.release1

import android.os.Bundle
import android.util.Log
import java.util.*
import java.util.regex.Pattern

// This class will handle all of the search functionality within the application
class Search {

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
    var masterList = DocumentList()

    constructor()

    constructor(bundle: Bundle?) {
        TODO("Pass variables into constructor and existing variables here")


    }

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

    }


}