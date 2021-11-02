package com.confessionsearch.release1.data.documents

import java.util.*

//DocumentList Class for Confessionsearchapp
class DocumentList : ArrayList<Document>() {
    var title: String? = null

    init {
        this.title = ""
    }
}
