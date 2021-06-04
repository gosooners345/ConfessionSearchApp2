package com.confessionsearch.release1

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout

class MainScreenFragmentActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Create the BG framework
        super.onCreate(savedInstanceState)
        setContentView(R.layout.experimental_activitymain)
        var viewPager = findViewById<ViewPager>(R.id.tab_viewpager)
        var tab_Layout = findViewById<TabLayout>(R.id.tabLayout)
        //Setup The Fragments
        setupViewPager(viewPager)
        tab_Layout.setupWithViewPager(viewPager)

    }

    private fun setupViewPager(viewPager: ViewPager?) {
        var adapter: ViewPagerAdapter
    }

}

class ViewPagerAdapter : FragmentPagerAdapter {
    private var fragmentList1: ArrayList<Fragment> = ArrayList()
    private var fragmentList: ArrayList<String> = ArrayList()

    constructor(supportFragmentManager: FragmentManager)
            : super(supportFragmentManager)

    override fun getCount(): Int {
        return fragmentList1.size
        TODO("Not yet implemented")
    }

    override fun getItem(position: Int): Fragment {
        return fragmentList1.get(position)
        TODO("Not yet implemented")
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return fragmentList.get(position)
    }

    fun addFragment(fragment: Fragment, title: String) {
        fragmentList1.add(fragment)
        fragmentList.add(title)
    }


}

