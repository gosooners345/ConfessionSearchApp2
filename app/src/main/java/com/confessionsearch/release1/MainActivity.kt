package com.confessionsearch.release1

/* Author: Brandon Guerin
*
*  Language: Kotlin
*
*  Application : The Reformed Collective
*  Class: MainActivity.kt
*  Purpose: This is the main entry point to the application
*/

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
import androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.confessionsearch.release1.data.notes.Notes
import com.confessionsearch.release1.databinding.ActivityMainBinding
import com.confessionsearch.release1.ui.bible.BibleFragment
import com.confessionsearch.release1.ui.home.SearchFragment
import com.confessionsearch.release1.ui.notesActivity.NotesFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.vdx.designertoast.DesignerToast
import www.sanju.motiontoast.MotionToast


class MainActivity : AppCompatActivity() {

    val context: Context = this
    var mainFab: ExtendedFloatingActionButton? = null
    lateinit var navView: BottomNavigationView
    lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM)
            var binding = ActivityMainBinding.inflate(layoutInflater)
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
                    R.id.navigation_help

                )
            )
            mainFab = findViewById(R.id.mainFAB)
            mainFab!!.setOnClickListener(mainFabOnClickListener)
            navController.addOnDestinationChangedListener(navControllerEvent)
            setupActionBarWithNavController(navController, appBarConfiguration)
            navView.setupWithNavController(navController)

        } catch (ex: Exception) {
            ex.printStackTrace()
            DesignerToast.Error(this, ex.message, Gravity.BOTTOM, Toast.LENGTH_LONG)
        }

    }


    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        recreate()
    }

    // This changes the Floating Action Button Depending on what is displayed on screen
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
    var mainFabOnClickListener = View.OnClickListener {
        when (navView.selectedItemId) {
            R.id.navigation_notes -> {
                mainFab!!.visibility = View.VISIBLE
                NotesFragment.NewNote(context)
            }
            R.id.navigation_home -> {
                mainFab!!.visibility = View.VISIBLE
                if (SearchFragment.searchViewModel.query.isBlank() && SearchFragment.readerSearch != true) {

                    when (context.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
                        Configuration.UI_MODE_NIGHT_YES -> {
                            MotionToast.darkToast(
                                this, getString(R.string.query_error),
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
                                this, getString(R.string.query_error),
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
            R.id.navigation_help -> {
                mainFab!!.visibility = View.INVISIBLE
            }
        }

    }

    //Pass any static variables along here
    companion object {

        var notesArrayList = ArrayList<Notes>()
        const val versionName = BuildConfig.VERSION_NAME
        const val appName = BuildConfig.APPLICATION_ID
        const val buildType = BuildConfig.BUILD_TYPE

    }


}

