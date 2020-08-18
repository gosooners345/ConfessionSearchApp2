package com.confessionsearch.release1;

public class DocumentTitle {
    private Integer documentID, documentTypeID;
    private  String documentName;

    public void setDocumentTypeID(Integer documentTypeID) {
        this.documentTypeID = documentTypeID;
    }

    public Integer getDocumentTypeID() {
        return documentTypeID;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public void setDocumentID(Integer documentID) {
        this.documentID = documentID;
    }

    public Integer getDocumentID() {
        return documentID;
    }

    public String getDocumentName() {
        return documentName;
    }
public String CompareIDs (Integer docID1)
{
    if(docID1 == getDocumentID())
        return  this.documentName;
    else
        return  "";
}

}
