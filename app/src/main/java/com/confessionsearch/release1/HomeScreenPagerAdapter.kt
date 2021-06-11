package com.confessionsearch.release1

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class HomeScreenPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm) {

    private val TAB_TITLES = arrayOf(
        R.string.Search, R.string.notes_button_string
    )

    override fun getCount(): Int {
        return 2
    }

    //Retrieve Fragment based on position
    override fun getItem(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = SearchScreenFragment()
            1 -> fragment = NotesScreenFragment()

        }
        return fragment!!
    }

    // Label the tab above
    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }
}