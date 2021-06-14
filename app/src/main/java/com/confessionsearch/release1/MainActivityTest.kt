package com.confessionsearch.release1

import android.os.Bundle
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.tabs.TabLayout

class MainActivityTest : AppCompatActivity() {
    protected var documentTypeSpinner: Spinner? = null
    protected var documentNameSpinner: Spinner? = null
    var helpButton: ExtendedFloatingActionButton? = null
    var searchButton: ExtendedFloatingActionButton? = null
    var notesButton: ExtendedFloatingActionButton? = null
    var header = ""
    protected var textSearch: Boolean? = null
    protected var questionSearch: Boolean? = null
    protected var readerSearch: Boolean? = null
    var query: String? = null
    var dbName = "confessionSearchDB.sqlite3"
    var documentDBHelper: documentDBClassHelper? = null
    var type = ""
    var shareList = ""

    var fileName: String? = null
    protected var allOpen: Boolean? = null
    protected var confessionOpen: Boolean? = null
    protected var catechismOpen: Boolean? = null
    protected var creedOpen: Boolean? = null
    protected var helpOpen: Boolean? = null
    protected var proofs = true
    protected var answers = true
    protected var searchAll = false
    var searchUnit: Search? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.experimental_activitymain)
        val homeScreenPagerAdapter = HomeScreenPagerAdapter(this, supportFragmentManager)
        val viewPagerHome: ViewPager = findViewById(R.id.view_pager2)
        viewPagerHome.adapter = homeScreenPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPagerHome)


    }

}