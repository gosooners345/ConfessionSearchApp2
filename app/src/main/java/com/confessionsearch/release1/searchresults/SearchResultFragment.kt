package com.confessionsearch.release1.searchresults

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.confessionsearch.release1.R
import com.confessionsearch.release1.ui.notesActivity.NotesComposeActivity

class SearchResultFragment : Fragment() {
    var shareNote: String? = null
    var shareList = ""

    @SuppressLint("NewApi")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        bundle: Bundle?
    ): View? {
        var header = ""
        var titleHeader = ""
        var tagLine = ""
        var matchLine = ""
        val resultChapter = requireArguments().getString(CHAPTER, "")
        val chTitle = requireArguments().getString(CHAPTITLE, "")
        val resultTags = requireArguments().getString(TAGS, "")
        val resultProofs = requireArguments().getString(PROOFS, "")
        val listTitles = requireArguments().getString(DOCTITLE, "")
        val resultTitle = requireArguments().getString(TITLE, "")
        val resultMatch = requireArguments().getInt(matchNumb, -1)
        val resultID = requireArguments().getInt(number, 0)
        val view = inflater.inflate(R.layout.search_results, container, false)
        val chapterBox = view.findViewById<TextView>(R.id.chapterText)
        val chNumbBox = view.findViewById<TextView>(R.id.confessionChLabel)
        val docTitleBox = view.findViewById<TextView>(R.id.documentTitleLabel)
        val fab = view.findViewById<Button>(R.id.shareActionButton)
        val saveFab = view.findViewById<Button>(R.id.saveNote)
        if (resultChapter.contains("Question")) {
            header = "Question "
            titleHeader = String.format("%s %s : %s", header, resultID, chTitle)
        } else if (resultChapter.contains("I. ")) {
            header = "Chapter"
            titleHeader = String.format("%s %s: %s", header, resultID, chTitle)
        } else {
            titleHeader = String.format("%s ", chTitle)
        }
        shareNote = ""
        tagLine = String.format("Tags: %s", resultTags)
        matchLine = String.format("Matches: %s", resultMatch)
        docTitleBox.text = resultTitle
        chNumbBox.text = titleHeader

        chapterBox.text = Html.fromHtml(
            lineBreak + resultChapter + lineBreak + "Proofs:" + lineBreak + resultProofs
                    + lineBreak + matchLine + lineBreak + tagLine
        )
        shareList = chapterBox.text.toString()
        shareNote = chapterBox.text.toString()
        fab.setOnClickListener(shareContent)
        saveFab.setOnClickListener(saveNewNote)
        return view
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
        intent.putExtra("activity_ID", ACTIVITY_ID)
        Log.i(TAG, "Opening new note to save entry")
        startActivity(intent)
    }

    companion object {
        const val ACTIVITY_ID = 30
        const val TAG = "SearchResultFragment"
        private const val PROOFS = "proofs"
        private const val CHAPTER = "chapter"
        private const val CHAPTITLE = "chapterTitle"
        private const val QUESTION = "question"
        private const val TYPE = "type"
        private const val TITLE = "title"
        private const val ANSWER = "answer"
        private const val DOCTITLE = "titles"
        val lineBreak = "<br><br>"
        val singleBreak = "<br>"
        private const val matchNumb = "matches"
        private const val TAGS = "tags"
        private const val newLine = "\r\n"
        private val number: String? = null
        private val match: String? = null
        var HEADER = "header"
        fun NewResult(
            Chapter: String?,
            Proofs: String?,
            Title: String?,
            ID: Int?,
            ListTitle: String?,
            MatchNum: Int?,
            Chaptitle: String?,
            docTags: String?
        ): SearchResultFragment {
            val fragment = SearchResultFragment()
            val spaces = Bundle()
            spaces.putString(CHAPTITLE, Chaptitle)
            spaces.putInt(number, ID!!)
            spaces.putInt(matchNumb, MatchNum!!)
            spaces.putString(CHAPTER, Chapter)
            spaces.putString(PROOFS, Proofs)
            spaces.putString(TITLE, Title)
            spaces.putString(DOCTITLE, ListTitle)
            spaces.putString(TAGS, docTags)
            fragment.arguments = spaces
            return fragment
        }
    }
}
