package com.confessionsearch.release1.data.documents
//DocumentTitle Data Class For ConfessionSearchApp
class DocumentTitle {
    var documentID: Int? = null
    var documentTypeID: Int? = null
    var documentName: String? = null
    fun CompareIDs(id1: Int?): String? {
        return if (id1 == documentID!!) documentName else ""
    }
}
