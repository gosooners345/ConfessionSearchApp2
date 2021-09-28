package com.confessionsearch.release1.ui.help

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.confessionsearch.release1.R

class HelpPageFragment : Fragment() {

  //  val newLine = "\r\n"
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i("HelpFragment", "I am loading...")
        val view = inflater.inflate(R.layout.fragment_help_page, container, false)
      var searchTabTV = view.findViewById<TextView>(R.id.searchTabLabel)
      var searchTabPgh1 = view.findViewById<TextView>(R.id.searchTabHelpText1)
      //var searchTabPgh2 = view.findViewById<TextView>(R.id.searchTabHelpText2)

      /* searchTabPgh1.text = String.format(
            getString(R.string.help_searchTab_pgh1) + "\r\n" + "Available Options:" + "\r\n" +
                    getString(R.string.search_tab_pgh2) + newLine + getString(R.string.notes_tab_help) + newLine
                    + getString(R.string.notes_tab_helpPgh) + newLine + getString(R.string.bible_tab_HelpLabel) + newLine +
                    getString(R.string.bible_tab_helpPgh) + newLine + getString(R.string.other_stuff_tab) + newLine +
                    getString(R.string.share_entry) + newLine
        )*///+getString(R.string.sources_tab)+newLine+getString(R.string.copyright_disclaimer))
      searchTabPgh1.movementMethod = ScrollingMovementMethod()
      return view//super.onCreateView(inflater, container, savedInstanceState)
  }


}