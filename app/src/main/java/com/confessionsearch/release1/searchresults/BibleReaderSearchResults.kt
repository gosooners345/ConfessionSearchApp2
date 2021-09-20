package com.confessionsearch.release1.searchresults

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.confessionsearch.release1.R
import com.confessionsearch.release1.data.bible.BibleContentsList
import com.confessionsearch.release1.data.documents.DocumentDBClassHelper
import com.confessionsearch.release1.ui.bible.BibleViewerFragment
import com.confessionsearch.release1.ui.notesActivity.NotesComposeActivity
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import www.sanju.motiontoast.MotionToast


class BibleReaderSearchResults : AppCompatActivity() {

    var shareList: String? = ""
    var shareNote: String? = ""

    var bibleVerseList = BibleContentsList()
    var docDBhelper: DocumentDBClassHelper? = null
    var documentDB: SQLiteDatabase? = null
    var header = ""
    var bibleReaderHandler: BibleReaderHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bibleBookName = intent.getStringExtra("BookName")
        val bibleChapterNum = intent.getIntExtra("Chapter", 0)
        val bibleVerseNum = intent.getIntExtra("VerseNum", 0)
        val translationName = intent.getStringExtra("Translation")

        bibleReader(bibleBookName, bibleChapterNum, bibleVerseNum, translationName)
    }

    // Where
    fun bibleReader(
        bibleBook: String?,
        bibleCh: Int?,
        bibleVerseNum: Int?,
        bibleTranslation: String?
    ) {
        Log.d("BibleReader", "Debut")
        docDBhelper = DocumentDBClassHelper(this)
        documentDB = docDBhelper!!.readableDatabase
        try {
            bibleReaderHandler = BibleReaderHandler()
            bibleVerseList = docDBhelper!!.getChaptersandVerses(
                documentDB!!,
                bibleTranslation,
                bibleBook,
                bibleCh,
                bibleVerseNum
            )


            if (bibleCh != 0) {
                var verse = ""
                var verseNum = 0
                if (bibleVerseNum != 0) {
                    verse = bibleVerseList[0].VerseText!!

                } else {
                    for (verses in bibleVerseList)
                        verse += verses.VerseText!!
                }
            }
            bibleVerseList.title = bibleBook
            //Under further review for bugfixes
            //setContentView(R.layout.index_pager)
            val bibleAdapter = BibleReaderAdapter(
                supportFragmentManager,
                bibleVerseList,
                bibleVerseList.title!!
            )

            if (bibleVerseList.size > 1) {
                setContentView(R.layout.index_pager)
                val adapter = BibleReaderAdapter(
                    supportFragmentManager,
                    bibleVerseList,
                    bibleVerseList.title!!
                )
                val vp = findViewById<ViewPager>(R.id.resultPager)
                bibleReaderHandler!!.displayResults(bibleVerseList, vp, adapter, 0)

            } else {
                setContentView(R.layout.fragment_bible_view_results)
                val bibleContents = bibleVerseList[0]
                val chTextBox = findViewById<TextView>(R.id.chapterText)
                val chHeader = findViewById<TextView>(R.id.chapterHeader)
                //Set the header and verse text fields
                chHeader.text =
                    String.format("${bibleContents.BookName} ${bibleContents.ChapterNum}")
                chTextBox.text = bibleContents.VerseText
                val fab: ExtendedFloatingActionButton = findViewById(R.id.shareActionButton)
                val saveFab: ExtendedFloatingActionButton = findViewById(R.id.saveNote)
                fab.setOnClickListener(shareContent)
                shareNote = ""
                shareNote =
                    chHeader.text.toString() + ":" + chTextBox.text.toString()//+" " + BibleViewerFragment.newLine
                saveFab.setOnClickListener(saveNewNote)
            }
            Log.i("VerseCatcher", "Results found " + bibleVerseList.count())
        } catch (ex: Exception) {
            ex.printStackTrace()
            MotionToast.createToast(
                this,
                "Error",
                ex.stackTraceToString(),
                MotionToast.TOAST_ERROR,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                ResourcesCompat.getFont(applicationContext, R.font.helvetica_regular)
            )
        }
    }

    var shareContent = View.OnClickListener {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        val INTENTNAME = "SHARE"
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareList)
        sendIntent.type = "text/plain"
        startActivity(Intent.createChooser(sendIntent, INTENTNAME))
    }
    var saveNewNote = View.OnClickListener {
        val intent = Intent(applicationContext, NotesComposeActivity::class.java)
        intent.putExtra("search_result_save", shareNote)
        intent.putExtra("activity_ID", BibleReaderSearchResults.ACTIVITY_ID)
        Log.i(SearchResultFragment.TAG, "Opening new note to save entry")
        startActivity(intent)
    }

    companion object {
        private const val ACTIVITY_ID = 65
    }
}

class BibleReaderAdapter(fm: FragmentManager?, verseList: BibleContentsList, titleString: String) :
    FragmentStatePagerAdapter(fm!!) {
    var dList1 = BibleContentsList()
    var bibleList = verseList
    private var bibleBookPosition = 0
    private var term = ""
    private val header = ""

    //public FragmentManager news;
    var news: FragmentManager? = null
    override fun getCount(): Int {
        return bibleList.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        var titleString =
            String.format("Chapter %s of %s in %s", position + 1, bibleList.size, term)
        return titleString
    }

    override fun getItem(position: Int): Fragment {
        var title = ""
        val frg: Fragment
        val bibleSection = bibleList[position]
        bibleBookPosition++
        if (bibleSection.VerseNumber!! > 0)
            frg = BibleViewerFragment.NewVerse(
                bibleSection.ChapterNum!!,
                bibleSection.VerseText!!,
                bibleSection.VerseNumber!!,
                bibleSection.BookName!!
            )
        else
            frg = BibleViewerFragment.NewVerse(
                bibleSection.ChapterNum!!,
                bibleSection.VerseText!!,
                0,
                bibleSection.BookName!!
            )
        return frg
    }

    init {
        term = titleString
    }

    companion object {
        private const val ACTIVITY_ID = 66
    }
}
