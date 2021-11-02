package com.confessionsearch.release1.data.bible

//List Class for BibleContents Class for adding extra properties to class
class BibleContentsList : ArrayList<BibleContents>() {
    var title: String? = null

    init {
        this.title = ""
    }

}
