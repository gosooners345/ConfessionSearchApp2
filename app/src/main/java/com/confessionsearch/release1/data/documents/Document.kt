package com.confessionsearch.release1.data.documents

import android.provider.BaseColumns

//Document Data class for confession Search app.

class Document : BaseColumns, Comparable<Document> {
    var chNumber: Int? = null
    var docDetailID: Int? = null
    var documentID: Int? = null
    var matches: Int? = null
    var documentName: String? = null
    var documentText: String? = null
    var chName: String? = null
    var tags: String? = null
    var proofs: String? = null

    constructor()

    //initializing constructor
    constructor(
        documentID: Int?,
        docDetailID: Int?,
        documentName: String?,
        chNumber: Int?,
        chName: String?,
        documentText: String?,
        proofs: String?,
        tags: String?
    ) {
        this.chName = chName
        this.documentText = documentText
        this.chNumber = chNumber
        this.documentID = documentID
        this.docDetailID = docDetailID
        this.documentName = documentName
        this.tags = tags
        matches = 0
        this.proofs = proofs
    }

    override fun compareTo(compareDocument: Document): Int {
        return chNumber!!.compareTo(compareDocument.chNumber!!)
    }


    companion object {
        var compareMatches = java.util.Comparator<Document> { document1, document2 ->
            val string1: String
            val string2: String
            if (document1.matches!! > document2.matches!!) -1
            else if (document1.matches!! < document2.matches!!) 1
            else {
                string1 = document1.chNumber.toString() + document1.docDetailID
                string2 = document2.chNumber.toString() + document2.docDetailID
                string1.compareTo(string2)
            }
        }
        var compareMatchesAndChapters = java.util.Comparator<Document> { document1, document2 ->

            if (document1.chNumber!! < document2.chNumber!!) -1
            else if (document1.chNumber == document2.chNumber) {
                if (document1.matches!! == document2.matches!!)
                    document1.documentID!!.compareTo(document2.documentID!!)
                else
                    document1.matches!!.compareTo(document2.matches!!)
            } else
                1


        }
    }

    override fun toString(): String {
        return String.format(
            "${documentName}\r\n Chapter $chNumber : $chName \r\n" +
                    "$documentText \r\n Proofs: \r\n" +
                    "$proofs"
        )
    }
}

