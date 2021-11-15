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

      searchTabPgh1.movementMethod = ScrollingMovementMethod()
      return view
  }


}