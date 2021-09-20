package com.confessionsearch.release1.data.bible

class BibleContentsList : ArrayList<BibleContents>() {
    var title: String? = null

    init {
        this.title = ""
    }

}