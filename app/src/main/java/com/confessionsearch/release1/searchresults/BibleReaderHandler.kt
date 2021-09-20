package com.confessionsearch.release1.searchresults

import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.confessionsearch.release1.data.bible.BibleContentsList

class BibleReaderHandler : AppCompatActivity() {
    var adapter: BibleReaderAdapter? = null
    var bibleList = BibleContentsList()
    fun displayResults(
        verseList: BibleContentsList,
        vp: ViewPager,
        bibleAdapter: BibleReaderAdapter,
        count: Int
    ) {
        bibleList = verseList
        bibleAdapter.getItem(count)
        bibleAdapter.saveState()
        vp.adapter = bibleAdapter
    }

}