package com.confessionsearch.release1.data.bible

import android.provider.BaseColumns


//BibleContents Data Class for ConfessionSearchApp
class BibleContents : BaseColumns {
    var EntryID: Int? = null
    var TranslationID: Int? = null
    var BookNum: Int? = null
    var ChapterNum: Int? = null
    var VerseNumber: Int? = null
    var VerseText: String? = null
    var BookName: String? = null


}
