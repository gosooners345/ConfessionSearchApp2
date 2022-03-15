package com.confessionsearch.release1

/* Author: Brandon Guerin
*
*  Language: Kotlin
*
*  Application : The Reformed Collective
*  Class: MainActivity.kt
*  Purpose: This is the main entry point to the application
*/

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.confessionsearch.release1.databinding.ActivityMainBinding
import com.confessionsearch.release1.ui.bible.BibleFragment
import com.confessionsearch.release1.ui.home.SearchFragment
import com.confessionsearch.release1.ui.notes.NotesFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.ramotion.paperonboarding.PaperOnboardingFragment
import com.ramotion.paperonboarding.PaperOnboardingPage
import com.vdx.designertoast.DesignerToast
import www.sanju.motiontoast.MotionToast


class MainActivity : AppCompatActivity() {

    val context: Context = this
    lateinit var fragmentManager: FragmentManager
    private var mainFab: ExtendedFloatingActionButton? = null
    lateinit var button: Button
    private lateinit var navView: BottomNavigationView
    private lateinit var navController: NavController
    var firstTime = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {

            val prefs = getSharedPreferences(prefsName, MODE_PRIVATE)

            firstTime = prefs.getBoolean("firstTime", true)
            if (firstTime)
            {
    
                firstTime = false
                setContentView(R.layout.first_use_layout)
                button = findViewById(R.id.skipButton)
                button.setOnClickListener(skipButtonClickListener)
                loadOnBoardingScreen()
                prefs.edit().putBoolean(
                    resources.getString(R.string.firstTime), firstTime
                ).apply()
            }
            else
            {
                loadApplication()
            }


        } catch (ex: Exception) {
            ex.printStackTrace()
            DesignerToast.Error(this, ex.message, Gravity.BOTTOM, Toast.LENGTH_LONG)
        }

    }


    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        recreate()
    }
    
    private fun loadOnBoardingScreen()
    {
        fragmentManager = supportFragmentManager
        val paperOnboardingFragment =
            PaperOnboardingFragment.newInstance(onBoardingScreens())
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.frameLayout, paperOnboardingFragment)
        fragmentTransaction.commit()
        
        
    }
    
    private fun onBoardingScreens(): ArrayList<PaperOnboardingPage>
    {
        val introList = ArrayList<PaperOnboardingPage>()
        val firstPage = PaperOnboardingPage(
            resources.getString(R.string.intro_message),
            resources.getString(
                R.string.app_description
            ) + "\r\nThe skip button below will take you past the tutorial if you've used this app before.",
            getColor(R.color.white),
            R.drawable.ic_home_black_24dp,
            R.drawable.next_arrow
        )
        val secondPage = PaperOnboardingPage(
            "Search",
            "When you load the app, you'll be greeted with the search page. This is the main function of the application and can help you find topics you're looking for." +
                    "\r\n Hit the Topic button at the top to search by topic. \r\n Hit the Question/Chapter button to search for a particular chapter number. \r\n " +
                    "Hit the read document button to simply read through the confession, creed, or catechism.",
            getColor(R.color.white),
            R.drawable.search_light_drawable,
            R.drawable.next_arrow
        )
        val thirdPage = PaperOnboardingPage(
            "Search Results",
            "When you hit the search button at the bottom of the search page, you'll be greeted with a list of results. \r\n" +
                    "You can swipe left or right to navigate through the results. " +
                    "\r\n You can sort by match count or by index number by hitting the sort icon in the upper right corner of the screen." +
                    "\r\n Each result will have the following: Document title, chapter name and number, chapter text, proofs, match count and tags. It also has a pair of buttons for sharing and saving the result as a note." +
                    "\r\nIf you want to store the result as a note, navigate to the bottom of the result and hit save note." +
                    "\r\n You can also share the result with people by hitting the share button at the bottom of the page as well.",
            getColor(R.color.white),
            R.drawable.search_light_drawable,
            R.drawable.next_arrow
        )
        val fourthPage = PaperOnboardingPage(
            resources.getString(R.string.notes_section_header),
            "You can view or edit notes you've saved in the applicatioh here. " +
                    "Simply click on the note you want to view and it will open a screen so you can view or edit it.\r\n" +
                    "You can save by backing out or hitting the save button at the bottom of the screen. You can disable editing by hitting the edit button. \r\n " +
                    "You can add new notes to the app by hitting the Add note button at the bottom of the screen." +
                    "\r\n You can sort notes by recently updated or by  title name by hitting the sort icon at the top of the screen. " +
                    "\r\nThere's also a search function built in for looking for a particular note you're interested in.",
            getColor(R.color.white),
            R.drawable.ic_note_navigation,
            R.drawable.next_arrow
        )
        val fifthPage = PaperOnboardingPage(
            resources.getString(R.string.bible_reader),
            "You can read the Scriptures from here. Simply select what book, chapter,and/or verse you'd like to visit. After that, hit read. The reader will have buttons at the bottom of the page for" +
                    " saving a note and sharing like the search results do.",
            getColor(R.color.white),
            R.drawable.ic_nav_bible,
            R.drawable.next_arrow
        )
        val sixthPage = PaperOnboardingPage(
            "A few more things...",
            "At the top of each page you'll see a help icon in the upper right corner or a menu icon. Those will take you to the help page with the same details this tutorial described.\r\n" +
                    "The settings page will allow you to customize how you want to see your notes laid out. There are options for grid, linear, and staggered grid. Along with it is a slider to set how many columns you want to see at once." +
                    "\r\n You can rate the app and email me by simply touching the options on screen.\r\n" +
                    "When emailing me, I recommend including what version of the app you're using to inform me what version of the app you're using. It helps me a lot with development and more.",
            getColor(R.color.white),
            R.drawable.settings_icon,
            R.drawable.ic_home_black_24dp
        )
        introList.addAll(
            arrayOf(
                firstPage,
                secondPage,
                thirdPage,
                fourthPage,
                fifthPage,
                sixthPage
            )
        )

        return introList
    }

    //Load Main Application if onboarding is completed
    fun loadApplication() {
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navView = binding.navView

        navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_notes,
                R.id.navigation_bible,
                R.id.navigation_settings

            )
        )
        mainFab = findViewById(R.id.mainFAB)
        mainFab!!.setOnClickListener(mainFabOnClickListener)
        navController.addOnDestinationChangedListener(navControllerEvent)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        appcontext = this
    }
    // This changes the Floating Action Button Depending on what is displayed on screen
    @SuppressLint("UseCompatLoadingForDrawables")
    private var navControllerEvent: NavController.OnDestinationChangedListener =
        NavController.OnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_bible -> {
                    mainFab!!.visibility = View.VISIBLE
                    mainFab!!.text = BibleFragment.buttonText
                    mainFab!!.icon = resources.getDrawable(BibleFragment.buttonPic)
                }
                R.id.navigation_home -> {
                    mainFab!!.visibility = View.VISIBLE
                    mainFab!!.text = SearchFragment.searchViewModel.buttonText
                    mainFab!!.icon = resources.getDrawable(SearchFragment.searchViewModel.buttonPic)
                }
                R.id.navigation_notes -> {
                    mainFab!!.visibility = View.VISIBLE
                    mainFab!!.text = NotesFragment.buttonText
                    mainFab!!.icon = resources.getDrawable(NotesFragment.buttonPic)
                }
                else -> {
                    mainFab!!.visibility = View.INVISIBLE
                }
            }
        }

    //Floating Action Button Event Handler
    private var mainFabOnClickListener = View.OnClickListener {
        when (navView.selectedItemId) {
            R.id.navigation_notes -> {
                mainFab!!.visibility = View.VISIBLE
                NotesFragment.newNote(context)
            }
            R.id.navigation_home -> {
                mainFab!!.visibility = View.VISIBLE
                if (SearchFragment.searchViewModel.query.isBlank() && SearchFragment.readerSearch != true) {

                    when (context.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
                        Configuration.UI_MODE_NIGHT_YES -> {
                            MotionToast.darkToast(
                                this, getString(R.string.error_query_is_empty),
                                "Enter A topic in the search field!",
                                MotionToast.TOAST_ERROR,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.SHORT_DURATION,
                                ResourcesCompat.getFont(
                                    this,
                                    R.font.helvetica_regular
                                )
                            )
                        }
                        Configuration.UI_MODE_NIGHT_NO -> {
                            MotionToast.createToast(
                                this, getString(R.string.error_query_is_empty),
                                "Enter a topic in the search field!",
                                MotionToast.TOAST_ERROR,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.SHORT_DURATION,
                                ResourcesCompat.getFont(
                                    this,
                                    R.font.helvetica_regular
                                )
                            )

                        }
                    }
                } else
                    SearchFragment.Search(SearchFragment.searchViewModel.query, this)
            }
            R.id.navigation_bible -> {
                mainFab!!.visibility = View.VISIBLE
                BibleFragment.Submit(context)
            }
            R.id.navigation_settings -> {
                mainFab!!.visibility = View.INVISIBLE
            }
        }

    }

    //Pass any static variables along here
    companion object {
        const val versionName = BuildConfig.VERSION_NAME
        const val appName = BuildConfig.APPLICATION_ID
        const val prefsName = appName + "_preferences"
        const val buildType = BuildConfig.BUILD_TYPE
        var appcontext: Context? = null
    }

    var skipButtonClickListener = View.OnClickListener {
        loadApplication()
    }

}

