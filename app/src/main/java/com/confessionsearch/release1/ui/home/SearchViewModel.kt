package com.confessionsearch.release1.ui.home

import androidx.lifecycle.ViewModel
import com.confessionsearch.release1.R
import com.confessionsearch.release1.data.documents.DocumentTitle
import com.confessionsearch.release1.data.documents.DocumentType

//Handles importing the Database for the search engine
class SearchViewModel : ViewModel() {

    //Document List variables for loading on main search form
    private var documentTypes: ArrayList<String?> = ArrayList()
    private var documentTitleList: ArrayList<String?> = ArrayList()

    //Fetch both Document types and titles
    fun getTypes(): ArrayList<String?> {
        return documentTypes
    }

    fun loadTypes(list: ArrayList<DocumentType>) {
        documentTypes.add("All")
        for (type in list)
            documentTypes.add(type.documentTypeName)

    }

    fun getTitles(): ArrayList<String?> {
        return documentTitleList
    }

    fun loadTitles(list: ArrayList<DocumentTitle>) {
        for (docTitle in list)
            documentTitleList.add(docTitle.documentName)

    }

    var query: String = ""
    var buttonText: String = ""
    var buttonPic: Int = R.drawable.search_light_drawable

}