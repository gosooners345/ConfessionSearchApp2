package com.confessionsearch.release1.searchresults

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.confessionsearch.release1.R
import com.confessionsearch.release1.data.bible.BibleContentsList
import com.confessionsearch.release1.data.documents.DocumentDBClassHelper
import com.confessionsearch.release1.ui.bible.BibleViewerFragment
import com.confessionsearch.release1.ui.notesActivity.NotesComposeActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import www.sanju.motiontoast.MotionToast


class BibleReaderSearchResults : AppCompatActivity() {

    var shareList: String? = ""
    var shareNote: String? = ""
    lateinit var vp2: ViewPager2
    lateinit var adapter: BibleReaderAdapter
    var bibleVerseList = BibleContentsList()
    var docDBhelper: DocumentDBClassHelper? = null
    var documentDB: SQLiteDatabase? = null
    var header = ""

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
        //Attempt DB Stuff here
        try {
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

            val bibleAdapter = BibleReaderAdapter(
                supportFragmentManager,
                bibleVerseList,
                bibleVerseList.title!!, lifecycle
            )

            if (bibleVerseList.size > 1) {
                setContentView(R.layout.index_pager)
                val adapter = BibleReaderAdapter(
                    supportFragmentManager,
                    bibleVerseList,
                    bibleVerseList.title!!, lifecycle
                )
                vp2 = findViewById<ViewPager2>(R.id.resultPager2)
                adapter.createFragment(0)
                vp2.adapter = adapter
                val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
                TabLayoutMediator(tabLayout, vp2) { tab, position ->
                    tab.text = String.format(
                        "Chapter %s of %s in %s",
                        position + 1,
                        bibleVerseList.size,
                        bibleBook
                    )
                }.attach()
                adapter.saveState()


            } else {
                setContentView(R.layout.fragment_bible_view_results)
                val bibleContents = bibleVerseList[0]
                val chTextBox = findViewById<TextView>(R.id.chapterText)
                val chHeader = findViewById<TextView>(R.id.chapterHeader)
                //Set the header and verse text fields
                chHeader.text =
                    String.format("${bibleContents.BookName} ${bibleContents.ChapterNum}")
                chTextBox.text = bibleContents.VerseText
                val fab: Button = findViewById(R.id.shareActionButton)
                val saveFab: Button = findViewById(R.id.saveNote)
                fab.setOnClickListener(shareContent)
                shareNote = ""
                shareNote =
                    chHeader.text.toString() + ":" + chTextBox.text.toString()
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

class BibleReaderAdapter(
    fm: FragmentManager?,
    verseList: BibleContentsList,
    titleString: String,
    lifeCycle: Lifecycle
) :
    FragmentStateAdapter(fm!!, lifeCycle) {
    var dList1 = BibleContentsList()
    var bibleList = verseList
    private var bibleBookPosition = 0
    private var term = ""
    private val header = ""

    //public FragmentManager news;
    var news: FragmentManager? = null


    init {
        term = titleString
    }

    companion object {
        private const val ACTIVITY_ID = 66
    }

    override fun getItemCount(): Int {
        return bibleList.size
    }

    override fun createFragment(position: Int): Fragment {
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
}
