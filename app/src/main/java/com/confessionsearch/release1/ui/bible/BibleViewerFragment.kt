package com.confessionsearch.release1.ui.bible

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ShareActionProvider
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.confessionsearch.release1.R
import com.confessionsearch.release1.searchresults.SearchResultFragment
import com.confessionsearch.release1.ui.notesActivity.NotesComposeActivity
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class BibleViewerFragment : Fragment() {
    private val bibleReaderTitle: String? = null
    var action: ShareActionProvider? = null
    var shareNote: String? = null
    var shareList = ""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val verseText = requireArguments().getString(VERSE, "")
        val bookName = requireArguments().getString(BOOKNAME, "")
        val verseNum = requireArguments().getInt(verseNum, 0)
        val chapterNumber = requireArguments().getInt(CHAPTERNUM, 0)
        val view = inflater.inflate(R.layout.fragment_bible_view_results, container, false)
        val chTextBox = view.findViewById<TextView>(R.id.chapterText)
        val chHeader = view.findViewById<TextView>(R.id.chapterHeader)
        if (verseNum > 0)
            chHeader.text = bookName + " " + chapterNumber + ":" + verseNum
        else
            chHeader.text = bookName + " " + chapterNumber
        chTextBox.text = verseText
        val fab: ExtendedFloatingActionButton = view.findViewById(R.id.shareActionButton)
        val saveFab: ExtendedFloatingActionButton = view.findViewById(R.id.saveNote)
        fab.setOnClickListener(shareContent)
        shareNote = ""
        shareNote =
            String.format(chHeader.text.toString() + newLine + chTextBox.text.toString())//Html.fromHtml(chHeader.text.toString() +"<br><br>" + chTextBox.text.toString())
        //.toString())//+" " + BibleViewerFragment.newLine)

        saveFab.setOnClickListener(saveNewNote)

        return view
        //return super.onCreateView(inflater, container, savedInstanceState)
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
        val intent = Intent(context, NotesComposeActivity::class.java)
        intent.putExtra("search_result_save", shareNote)
        intent.putExtra("activity_ID", BibleViewerFragment.ACTIVITY_ID)
        Log.i(SearchResultFragment.TAG, "Opening new note to save entry")
        startActivity(intent)
    }

    companion object {
        private const val CHAPTERNUM = "chapter"
        private const val VERSE = "verse"
        private const val BOOKNAME = "book"
        private const val verseNum = "verseNum"
        private const val ACTIVITY_ID = 76
        val newLine = "/r/n"

        fun NewVerse(
            ChapterNum: Int?,
            Verse: String?,
            VerseNum: Int?,
            BookName: String?
        ): BibleViewerFragment {
            val fragment = BibleViewerFragment()
            val spaces = Bundle()
            spaces.putInt(CHAPTERNUM, ChapterNum!!)
            spaces.putInt(verseNum, VerseNum!!)
            spaces.putString(BOOKNAME, BookName!!)
            spaces.putString(VERSE, Verse!!)
            fragment.arguments = spaces
            return fragment
        }
    }

}