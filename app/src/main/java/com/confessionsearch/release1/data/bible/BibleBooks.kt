package com.confessionsearch.release1.data.bible

import android.provider.BaseColumns

//Bible Books Data Class for ConfessionSearchApp2
class BibleBooks : BaseColumns {
    var BookID: Int? = null
    var BookName: String? = null
    var TotalChapters: Int? = null
}
