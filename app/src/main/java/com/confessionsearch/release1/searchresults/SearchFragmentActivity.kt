package com.confessionsearch.release1.searchresults

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.confessionsearch.release1.data.documents.DocumentList


class SearchFragmentActivity  //public ViewPager2 vp2;
    : AppCompatActivity() {
    var shareList: String? = null
    var documentList = DocumentList()
    var files: String? = null
    var header = ""
    var index: Int? = null

    fun DisplayResults(sourceList: DocumentList, vp: ViewPager, search: SearchAdapter, searchTerm: String?, count: Int) {
        documentList = sourceList
        search.getItem(count)
        search.saveState()

        Log.d("Handler", "Fragment Activity is in charge")
        vp.adapter = search
    }
}

class SearchAdapter(fm: FragmentManager?, documents: DocumentList, searchTerm: String) : FragmentStatePagerAdapter(fm!!) {
    var dList1 = DocumentList()
    var documentList1 = DocumentList()
    private var docPosition = 0
    private var term = ""

    //public FragmentManager news;
    var news: FragmentManager? = null
    override fun getCount(): Int {
        return documentList1.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        if (term === "") {
            term = documentList1[position + 1].documentName!!
        }
        return String.format("Result %s of %s for %s", position + 1, documentList1.size, term)
    }

    override fun getItem(position: Int): Fragment {
        var title = ""
        val frg: Fragment
        val document = documentList1[position]
        var docTitle: String? = ""
        title = document.documentName!!
        docTitle = if (title === "Results" || title === "") document.documentName else document.documentName
        docPosition++
        frg = SearchResultFragment.NewResult(document.documentText, document.proofs, document.documentName,
                document.chNumber, documentList1.title, document.matches, document.chName, document.tags)
        return frg
    }

    init {
        documentList1 = documents


        term = searchTerm
    }
}