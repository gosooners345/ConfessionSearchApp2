package com.confessionsearch.release1.ui.help

import android.os.Bundle
import android.text.Html
import android.text.Html.FROM_HTML_MODE_COMPACT
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.confessionsearch.release1.R

class HelpPageFragment : Fragment() {

    val newLine = "<br>"
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_help_page, container, false)
        var searchTabTV = view.findViewById<TextView>(R.id.searchTabLabel)
        searchTabTV.text = Html.fromHtml(
            (getString(R.string.search_tab) + newLine + getString(R.string.help_searchTab_pgh1)),
            FROM_HTML_MODE_COMPACT
        )
        var bibleTabTV = view.findViewById<TextView>(R.id.bibleTabLabel)
        bibleTabTV.text = Html.fromHtml(
            (getString(R.string.bible_tab_HelpLabel) + newLine + getString(R.string.bible_tab_helpPgh)),
            FROM_HTML_MODE_COMPACT
        )
        var notesTabTV = view.findViewById<TextView>(R.id.notesTabLabel)
        notesTabTV.text = Html.fromHtml(
            (getString(R.string.notes_tab_helpLabel) + newLine + getString(R.string.notes_tab_helpPgh)),
            FROM_HTML_MODE_COMPACT
        )
        var sourcesLabelTV = view.findViewById<TextView>(R.id.sourcesTabLabel)
        sourcesLabelTV.text =
            Html.fromHtml(
                (getString(R.string.sources_tab) + newLine + getString(R.string.copyright_disclaimer)),
                Html.FROM_HTML_MODE_COMPACT
            )

        return view

    }


}