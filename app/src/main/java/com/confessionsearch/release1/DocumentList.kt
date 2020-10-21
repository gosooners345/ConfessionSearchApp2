package com.confessionsearch.release1

import java.util.*

class DocumentList : ArrayList<Document>() {
    var title: String? = null

    init {
        this.title = ""
    }
}