package com.confessionsearch.release1.ui.bible

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.confessionsearch.release1.R
import com.confessionsearch.release1.data.documents.DocumentDBClassHelper
import com.confessionsearch.release1.databinding.FragmentBibleFormBinding
import com.confessionsearch.release1.searchresults.BibleReaderSearchResults
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.vdx.designertoast.DesignerToast

@Suppress("MemberVisibilityCanBePrivate")
class BibleFragment : Fragment() {

    private var _binding: FragmentBibleFormBinding? = null
    private lateinit var bibleViewModel: BibleViewModel
    var documentDB: SQLiteDatabase? = null
    var docDBhelper: DocumentDBClassHelper? = null
    var bibleTransList: ArrayList<String?> = ArrayList()

    var submitButton: ExtendedFloatingActionButton? = null

    var bibleBooksList: ArrayList<String?> = ArrayList()
    var bibleChapterList: ArrayList<String?> = ArrayList()
    var bibleVerseNumList: ArrayList<String?> = ArrayList()


    var bibleBookSelectorComboBox: Spinner? = null
    var bibleBookAdapter: ArrayAdapter<String>? = null
    var bibleChNumAdapter: ArrayAdapter<String>? = null
    var bibleVerseNumAdapter: ArrayAdapter<String>? = null

    var bibleSelectorAdapter: ArrayAdapter<String>? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentBibleFormBinding.inflate(inflater, container, false)
        bibleViewModel = ViewModelProvider(this).get(BibleViewModel::class.java)
        docDBhelper = DocumentDBClassHelper(super.getContext())

        documentDB = docDBhelper!!.readableDatabase
//Translations
        bibleViewModel.loadTranslations(docDBhelper!!.getAllBibleTranslations(documentDB!!))
        bibleTransList = bibleViewModel.getTranslations()
        val root = binding.root
        // bibleSelectorSpinner = root.findViewById(R.id.bibleTranslationSelector)
        bibleSelectorAdapter = ArrayAdapter(
            requireContext(),
            R.layout.support_simple_spinner_dropdown_item, bibleTransList
        )
        binding.bibleTranslationCB.item = bibleTransList as List<Any>?
        binding.bibleTranslationCB.onItemSelectedListener = bibleSelectorSpinnerListener

        //bibleSelectorSpinner!!.adapter = bibleSelectorAdapter
        //bibleSelectorSpinner!!.onItemSelectedListener = bibleSelectorSpinnerListener
        //bibleBookSelectorComboBox = root.findViewById(R.id.bibleBookSelector)
        /*bibleChapterSpinner = root.findViewById(R.id.bibleChapterSpinner)
        bibleVerseSelector = root.findViewById(R.id.verseSpinner)*/
        binding.bibleTranslationCB.setSelection(0)



        return root

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    var bibleSelectorSpinnerListener: AdapterView.OnItemSelectedListener = object :
        AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            bibleTranslation = String.format("%s", parent!!.selectedItem.toString())
            bibleViewModel.loadBooks(docDBhelper!!.getAllBooks(documentDB!!))
            bibleBooksList = bibleViewModel.getBooks()
            bibleBookAdapter = ArrayAdapter(
                requireContext(),
                R.layout.support_simple_spinner_dropdown_item,
                bibleBooksList
            )
            binding.bibleBookCB.item = bibleBooksList as List<Any>?
            binding.bibleBookCB.onItemSelectedListener = bibleBookSelectorListener
            binding.bibleBookCB.setSelection(0)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            bibleTranslation = String.format("%s", parent!!.selectedItem.toString())
        }
    }

    var bibleBookSelectorListener: AdapterView.OnItemSelectedListener = object :
        AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            bibleBook = String.format("%s", parent!!.selectedItem.toString())
                bibleChapterList.clear()
            bibleViewModel.loadChapters(
                docDBhelper!!.getAllChapters(
                    documentDB,
                    bibleTranslation,
                    bibleBook
                )
            )
            bibleChapterList = bibleViewModel.getChapters()
            bibleChNumAdapter = ArrayAdapter(
                requireContext(),
                R.layout.support_simple_spinner_dropdown_item,
                bibleChapterList
            )

            binding.bibleChapterSpinner.item = bibleChapterList as List<Any>?
            binding.bibleChapterSpinner.onItemSelectedListener = bibleChSelectorListener
            binding.bibleChapterSpinner.setSelection(0)


        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            bibleBook = String.format("%s", parent!!.selectedItem.toString())
                bibleChapterList.clear()
            bibleViewModel.loadChapters(
                docDBhelper!!.getAllChapters(
                    documentDB,
                    bibleTranslation,
                    bibleBook
                )
            )
            bibleChapterList = bibleViewModel.getChapters()
            bibleChNumAdapter = ArrayAdapter(
                requireContext(),
                R.layout.support_simple_spinner_dropdown_item,
                bibleChapterList
            )
            binding.bibleChapterSpinner.item = bibleChapterList as List<Any>?
            binding.bibleChapterSpinner.onItemSelectedListener = bibleChSelectorListener
            binding.bibleChapterSpinner.setSelection(0)
        }
    }


    var bibleChSelectorListener: AdapterView.OnItemSelectedListener =
        object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                bibleCh = if (parent!!.selectedItem.toString() == "All")
                    0
                else
                    parent.selectedItem.toString().toInt()

                bibleVerseNumList.clear()

                bibleViewModel.loadVerseNumbers(
                    docDBhelper!!.getAllVerseNumbers(
                        documentDB!!,
                        bibleTranslation,
                        bibleBook,
                        bibleCh
                    )
                )
                bibleVerseNumList = bibleViewModel.getVerseNumbers()
                bibleVerseNumAdapter = ArrayAdapter(
                    requireContext(),
                    R.layout.support_simple_spinner_dropdown_item,
                    bibleVerseNumList
                )
                binding.verseSpinner.item = bibleVerseNumList as List<Any>?
                //bibleVerseSelector!!.adapter = bibleVerseNumAdapter
                binding.verseSpinner.onItemSelectedListener = bibleVerseSelectorListener
                binding.verseSpinner.setSelection(0)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                bibleCh = if (parent!!.selectedItem.toString() == "All")
                    0
                else
                    parent.selectedItem.toString().toInt()
            }
        }
    var bibleVerseSelectorListener: AdapterView.OnItemSelectedListener =
        object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                bibleVerseNum = if (parent!!.selectedItem.toString() == "All")
                    0
                else
                    parent.selectedItem.toString().toInt()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                bibleVerseNum = if (parent!!.selectedItem.toString() == "All")
                    0
                else
                    parent.selectedItem.toString().toInt()
            }
        }

    var submitFabClicker = View.OnClickListener {
        Submit(requireContext())


    }

    companion object {
        const val ACTIVITY_ID = 37
        var bibleTranslation = ""
        var bibleCh = 0
        var bibleVerseNum = 0
        var bibleBook = ""
        fun Submit(context: Context?) {
            try {
                val bibleIntent = Intent(context, BibleReaderSearchResults::class.java)
                bibleIntent.putExtra("Translation", bibleTranslation)
                bibleIntent.putExtra("BookName", bibleBook)
                bibleIntent.putExtra("Chapter", bibleCh)
                bibleIntent.putExtra("VerseNum", bibleVerseNum)
                context!!.startActivity(bibleIntent)
            } catch (ex: Exception) {
                DesignerToast.Error(
                    context,
                    String.format(ex.message!!.toString()),
                    Gravity.CENTER,
                    Toast.LENGTH_LONG
                )
            }
        }

        const val buttonText = "Read"
        const val buttonPic = R.drawable.ic_nav_bible
    }
}
