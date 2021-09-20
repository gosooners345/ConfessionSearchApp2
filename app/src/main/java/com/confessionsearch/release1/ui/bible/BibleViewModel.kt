package com.confessionsearch.release1.ui.bible

import androidx.lifecycle.ViewModel
import com.confessionsearch.release1.data.bible.BibleBooks
import com.confessionsearch.release1.data.bible.BibleContentsList
import com.confessionsearch.release1.data.bible.BibleTranslation

class BibleViewModel : ViewModel() {
    private var bibleBookList: ArrayList<String?> = ArrayList()
    private var bibleTranslationList: ArrayList<String?> = ArrayList()

    //private var bibleContentList = ArrayList<BibleContents> = ArrayList()
    private var bibleContentList = BibleContentsList()
    private var bibleChapterList: ArrayList<Int?> = ArrayList()
    private var bibleVerseNumList: ArrayList<Int?> = ArrayList()
    private var bibleVerseList: ArrayList<String?> = ArrayList()

    fun getBooks(): ArrayList<String?> {
        return bibleBookList
    }

    fun loadBooks(list: ArrayList<BibleBooks>) {
        for (book in list) {
            bibleBookList.add(book.BookName)
        }
    }

    fun getChapters(): ArrayList<String?> {
        val chapterStringList: ArrayList<String?> = ArrayList()
        chapterStringList.add("All")
        for (i in bibleChapterList)
            chapterStringList.add(i.toString())
        return chapterStringList
    }

    fun loadChapters(list: ArrayList<Int?>) {
        // bibleChapterList.add(0)
        bibleChapterList.addAll(list)
    }

    fun getVerseNumbers(): ArrayList<String?> {
        val verseStringList: ArrayList<String?> = ArrayList()
        // forEach(verseNum i bibleVerseNumList)
        verseStringList.add("All")
        for (i in bibleVerseNumList) {
            verseStringList.add(i.toString())
        }
        //bibleVerseNumList
        return verseStringList
    }

    fun loadVerseNumbers(list: ArrayList<Int?>) {
        // bibleVerseNumList.add(0)
        bibleVerseNumList.addAll(list)

    }

    fun getVerses(): ArrayList<String?> {
        return bibleVerseList
    }

    fun loadVerses(list: ArrayList<String?>) {
        for (verse in list)
            bibleVerseList.add(verse)
    }

    fun getTranslations(): ArrayList<String?> {

        return bibleTranslationList
    }

    fun loadTranslations(list: ArrayList<BibleTranslation>) {
        for (translation in list)
            bibleTranslationList.add(translation.bibleTranslationName)
    }

    fun loadBibleList(list: BibleContentsList) {
        bibleContentList = list
    }

    fun getBibleList(): BibleContentsList {
        return bibleContentList
    }

}
