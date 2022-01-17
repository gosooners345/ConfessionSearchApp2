package com.confessionsearch.release1.ui.help

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Html
import android.text.Html.FROM_HTML_MODE_COMPACT
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.confessionsearch.release1.R

class HelpPageFragment : AppCompatActivity() {

    val newLine = "<br>"

    @SuppressLint("SetTextI18n")
    override fun onCreate(

        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_help_page)
        //val view = inflater.inflate(R.layout.fragment_help_page, container, false)
        var searchTabTV = findViewById<TextView>(R.id.searchTabLabel)
        searchTabTV.text = Html.fromHtml(
            (getString(R.string.search_tab) + newLine + getString(R.string.help_searchTab_pgh1)),
            FROM_HTML_MODE_COMPACT
        )
        var bibleTabTV = findViewById<TextView>(R.id.bibleTabLabel)
        bibleTabTV.text = Html.fromHtml(
            (getString(R.string.bible_tab_HelpLabel) + newLine + getString(R.string.bible_tab_helpPgh)),
            FROM_HTML_MODE_COMPACT
        )
        var notesTabTV = findViewById<TextView>(R.id.notesTabLabel)
        notesTabTV.text = Html.fromHtml(
            (getString(R.string.notes_tab_helpLabel) + newLine + getString(R.string.notes_tab_helpPgh)),
            FROM_HTML_MODE_COMPACT
        )
        var sourcesLabelTV = findViewById<TextView>(R.id.sourcesTabLabel)

        sourcesLabelTV.text =
            Html.fromHtml(
                (getString(R.string.sources_tab) + newLine + getString(R.string.copyright_disclaimer)),
                FROM_HTML_MODE_COMPACT
            )


        //return view

    }

    override fun onBackPressed() {
        super.onBackPressed()
        this.finish()
    }
}