package com.confessionsearch.release1

import android.provider.BaseColumns

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
    constructor(documentID: Int?, docDetailID: Int?, documentName: String?, chNumber: Int?, chName: String?, documentText: String?, proofs: String?, tags: String?) {
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
            if (document1.matches!! > document2.matches!!) 1 else if (document1.matches!! < document2.matches!!) -1 else {
                string1 = document1.chNumber.toString() + document1.docDetailID
                string2 = document2.chNumber.toString() + document2.docDetailID
                string1.compareTo(string2)
            }
        }
    }
}