package com.confessionsearch.release1;

import android.provider.BaseColumns;

import java.util.Comparator;

public class Document implements BaseColumns,Comparable<Document>{
   private Integer chNumber,docDetailID,documentID, matches;
    private String documentName,documentText,chName,tags, proofs;
    public Document(){}
    //initializing constructor
    public Document(Integer documentID,Integer docDetailID,String documentName,Integer chNumber,String chName,String documentText
            ,String proofs,String tags)
    {
        this.chName = chName; this.documentText = documentText; this.chNumber = chNumber;
        this.documentID =documentID; this.docDetailID = docDetailID;       this.documentName=documentName;
        this.tags = tags; this.matches = 0; this.proofs = proofs;
    }

    public Integer getChNumber()
    {return this.chNumber;}
    public  Integer getDocDetailID(){
        return  this.docDetailID;
    }
    public Integer getDocumentID(){
        return  this.documentID;
    }
    public String getDocumentName(){
        return this.documentName;
    }

    public String getDocumentText(){
        return  this.documentText;
    }
    public String getTags(){
        return  this.tags;
    }

    public String getChName() {
        return chName;
    }

    public Integer getMatches() {
        return matches;
    }

    public String getProofs() {
        return proofs;
    }

    public void setChName(String chName) {
        this.chName = chName;
    }

    public void setChNumber(Integer chNumber) {
        this.chNumber = chNumber;
    }

    public void setDocDetailID(Integer docDetailID) {
        this.docDetailID = docDetailID;
    }

    public void setDocumentID(Integer documentID) {
        this.documentID = documentID;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public void setDocumentText(String documentText) {
        this.documentText = documentText;
    }

    public void setMatches(Integer matches) {
        this.matches = matches;
    }

    public void setProofs(String proofs) {
        this.proofs = proofs;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public int compareTo(Document compareDocument)
    {
        return this.getChNumber().compareTo(compareDocument.getChNumber());
    }
    public static Comparator<Document> compareMatches = new Comparator<Document>() {
        @Override
        public int compare(Document document1, Document document2) {
            String string1, string2;
            if(document1.getMatches()>document2.getMatches())
                return 1;
            else if (document1.getMatches() <document2.getMatches())
                return  -1;
            else
            {
                string1 = document1.getChNumber().toString() + document1.getDocDetailID();
                string2 = document2.getChNumber().toString() + document2.getDocDetailID();
                return  string1.compareTo(string2);
            }
        }
    };



    }



