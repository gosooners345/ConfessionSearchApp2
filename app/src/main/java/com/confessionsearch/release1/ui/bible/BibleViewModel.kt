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
//This fetches the list for the bible fragment
    fun getChapters(): ArrayList<String?> {
    var chapterStringList: ArrayList<String?> = ArrayList()
        chapterStringList.add("All")
        for (i in bibleChapterList)
            chapterStringList.add(i.toString())
        return chapterStringList
    }

    //This method loads chapter numbers for the bible fragment
    fun loadChapters(list: ArrayList<Int?>) {
        bibleChapterList.clear()
        bibleChapterList.addAll(list)
    }

    //This fetches the verse list for the bible fragment
    fun getVerseNumbers(): ArrayList<String?> {
        var verseStringList: ArrayList<String?> = ArrayList()
        // forEach(verseNum i bibleVerseNumList)
        verseStringList.add("All")
        for (i in bibleVerseNumList) {
            verseStringList.add(i.toString())
        }
        //bibleVerseNumList
        return verseStringList
    }

    /*This method loads verse numbers into the application*/
    fun loadVerseNumbers(list: ArrayList<Int?>) {
        //
        bibleVerseNumList.clear()
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
