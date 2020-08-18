package com.confessionsearch.release1;

import java.util.ArrayList;

public class DocumentList extends ArrayList<Document> {
   String title = "";
   public DocumentList(){
       this.title = "";
   }
   public DocumentList(String titleValue)
   {
       this.title=titleValue;
   }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


}
