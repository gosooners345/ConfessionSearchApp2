package com.confessionsearch.release1;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class SearchFragmentActivity extends AppCompatActivity {


    public SearchAdapter adapter;
    public String shareList;
    public DocumentList documentList = new DocumentList();
    String files;
    String header = "";
    public Integer index;
    //public ViewPager2 vp2;

    public SearchFragmentActivity(){}

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    public void DisplayResults(DocumentList sourceList, ViewPager vp, SearchAdapter search, String searchTerm, Integer count, Boolean truncate ){

        documentList = sourceList;
        search.getItem(count);
    search.saveState();

vp.setAdapter(search);


    }



}
class SearchAdapter extends FragmentStatePagerAdapter {

    public DocumentList dList1 = new DocumentList(), documentList1 = new DocumentList();
    private  Integer docPosition = 0;
    private  String term ="",header = "";
    private Boolean truncate = false;
    //public FragmentManager news;
FragmentManager news;
    public SearchAdapter(FragmentManager fm, DocumentList documents, String searchTerm, Boolean truncate)
    { super(fm);

        documentList1 = documents;
this.truncate = truncate;
//news = fm;
term = searchTerm;
    }




@Override
    public int getCount(){return documentList1.size(); }


   @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if(term=="") {
            term = documentList1.get(position+1).getDocumentName();
        }
return  String.format("Result %s of %s for %s",position+1,documentList1.size(),term);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        String title = "";
        Fragment frg;
        Document document = documentList1.get(position);
        String docTitle = "";
        title = document.getDocumentName();
        if(title == "Results"||title=="")
            docTitle = document.getDocumentName();
        else
            docTitle = document.getDocumentName();
        docPosition++;
        frg =SearchResultFragment.NewResult(document.getDocumentText(),document.getProofs(),document.getDocumentName(),
                document.getChNumber(),documentList1.getTitle(),truncate,document.getMatches(),document.getChName(),document.getTags());
        return frg;
    }


}

