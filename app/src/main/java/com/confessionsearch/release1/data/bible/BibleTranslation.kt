package com.confessionsearch.release1.data.bible

import android.provider.BaseColumns

//Bible Translations Data Class for ConfessionSearchApp
class BibleTranslation : BaseColumns {
    var bibleTranslationID: Int? = null
    var bibleTranslationName: String? = null
    var bibleTranslationAbbrev: String? = null
}
