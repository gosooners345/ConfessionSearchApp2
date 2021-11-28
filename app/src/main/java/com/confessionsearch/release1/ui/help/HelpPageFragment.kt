package com.confessionsearch.release1.ui.help

import android.os.Bundle
import android.text.Html
import android.text.Html.FROM_HTML_MODE_LEGACY
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.confessionsearch.release1.MainActivity
import com.confessionsearch.release1.R
import mehdi.sakout.aboutpage.AboutPage
import mehdi.sakout.aboutpage.Element

class HelpPageFragment : Fragment() {

    val newLine = "\r\n"
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        var searchTitle = (getString(R.string.search_tab) + newLine +
                getString(R.string.help_searchTab_pgh1))


        var spannedTitle = Html.fromHtml(searchTitle, FROM_HTML_MODE_LEGACY).toString()
        var sourceElement = Element()
        sourceElement.title =
            getString(R.string.sources_tab) + newLine + Html.fromHtml(
                getString(R.string.copyright_disclaimer),
                FROM_HTML_MODE_LEGACY
            ).toString()
        var versionElement = Element()
        versionElement.title = "Version #: ${MainActivity.versionName}"
        val searchTabElement = Element()
        searchTabElement.title = spannedTitle
        searchTabElement.value = getString(R.string.help_searchTab_pgh1)
        val notesSectionElement = Element()
        notesSectionElement.title =
            Html.fromHtml(getString(R.string.notes_tab_help), FROM_HTML_MODE_LEGACY)
                .toString() + Html.fromHtml(
                getString(R.string.notes_tab_helpPgh),
                FROM_HTML_MODE_LEGACY
            ).toString()
        val bibleReaderElement = Element()
        bibleReaderElement.title =
            Html.fromHtml(getString(R.string.bible_tab_HelpLabel), FROM_HTML_MODE_LEGACY)
                .toString() + Html.fromHtml(
                getString(R.string.bible_tab_helpPgh),
                FROM_HTML_MODE_LEGACY
            ).toString()
        Log.i("HelpFragment", "I am loading...")
        val helpPage = AboutPage(requireContext())
            .setDescription(getString(R.string.app_description))
            .addItem(searchTabElement)
            .addItem(notesSectionElement)
            .addItem(bibleReaderElement)
            .addItem(sourceElement)
            .addPlayStore("com.confessionsearch.release1")
            .addEmail("BoomerSooner12345@gmail.com", "Email Developer")

            .addItem(versionElement)

            .create()



        return helpPage
        /*val view = inflater.inflate(R.layout.fragment_help_page, container, false)
      var searchTabTV = view.findViewById<TextView>(R.id.searchTabLabel)
      var searchTabPgh1 = view.findViewById<TextView>(R.id.searchTabHelpText1)

      searchTabPgh1.movementMethod = ScrollingMovementMethod()
      return view*///super.onCreateView(inflater, container, savedInstanceState)
    }


}