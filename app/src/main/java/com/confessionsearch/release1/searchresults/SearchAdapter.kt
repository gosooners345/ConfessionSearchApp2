package com.confessionsearch.release1.searchresults

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.confessionsearch.release1.data.documents.DocumentList

class SearchAdapter(
    fm: FragmentManager?,
    documents: DocumentList,
    searchTerm: String,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fm!!, lifecycle) {
    var dList1 = DocumentList()
    var documentList1 = DocumentList()
    private var docPosition = 0
    private var term = ""

    init {
        documentList1 = documents
        term = searchTerm
    }

    override fun getItemCount(): Int {
        return documentList1.size
    }

    override fun createFragment(position: Int): Fragment {
        var title = ""
        val frg: Fragment
        val document = documentList1[position]
        var docTitle: String? = ""
        title = document.documentName!!
        docTitle =
            if (title === "Results" || title === "") document.documentName else document.documentName
        docPosition++
        frg = SearchResultFragment.NewResult(
            document.documentText, document.proofs, document.documentName,
            document.chNumber, documentList1.title, document.matches, document.chName, document.tags
        )
        return frg
    }
}