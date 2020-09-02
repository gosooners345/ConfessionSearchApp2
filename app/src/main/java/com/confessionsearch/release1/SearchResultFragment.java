package com.confessionsearch.release1;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public  class SearchResultFragment extends Fragment {
    private String documentTitle;
    private static String PROOFS = "proofs", CHAPTER = "chapter", CHAPTITLE="chapterTitle",
            QUESTION = "question", TYPE = "type",
            TITLE = "title", ANSWER = "answer", DOCTITLE = "titles", matchNumb = "matches",TAGS = "tags";
    private static final String newLine = "\r\n";
    private static String number, match;
    public static String HEADER = "header";
    ShareActionProvider action;
public String shareList = "";
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        String header = "";
        String resultChapter = getArguments().getString(CHAPTER, "");
        String chTitle = getArguments().getString(CHAPTITLE,"");
        String resultTags = getArguments().getString(TAGS,"");
        String resultProofs = getArguments().getString(PROOFS, "");
        String listTitles = getArguments().getString(DOCTITLE, "");
        String resultTitle = getArguments().getString(TITLE, "");
        int resultMatch = getArguments().getInt(matchNumb, -1);
        int resultID = getArguments().getInt(number, 0);
        View view = inflater.inflate(R.layout.search_results, container, false);
        TextView chapterBox = (TextView) view.findViewById(R.id.chapterText);
        //TextView chNameBox = (TextView)view.findViewById(R.id.)
        TextView proofBox = (TextView) view.findViewById(R.id.proofText);
        TextView chNumbBox = (TextView) view.findViewById(R.id.confessionChLabel);
        TextView docTitleBox = (TextView) view.findViewById(R.id.documentTitleLabel);
        TextView matchView = (TextView) view.findViewById(R.id.matchView);
        TextView proofView = (TextView) view.findViewById(R.id.proofLabel);
        TextView tagBox = (TextView)view.findViewById(R.id.tagView);
        chapterBox.setText(Html.fromHtml( resultChapter));
        proofBox.setText(Html.fromHtml(resultProofs));
        tagBox.setText(String.format("Tags: %s",resultTags));
        docTitleBox.setText(resultTitle);
        if (resultChapter.contains("Question")) {
            header = "Question ";
            chNumbBox.setText(String.format("%s %s : %s", header, resultID, chTitle));

        } else if (resultChapter.contains("I. ")) {
            header = "Chapter";
            chNumbBox.setText(String.format("%s %s: %s", header, resultID, chTitle));
        }
        else{
            chNumbBox.setText(String.format("%s ",chTitle));
        }
        matchView.setText(String.format("Matches: %s",resultMatch));
        shareList = docTitleBox.getText()+newLine+chNumbBox.getText()+newLine
                + newLine+chapterBox.getText()+newLine+"Proofs"+newLine+proofBox.getText();
        FloatingActionButton fab= view.findViewById(R.id.shareActionButton);
        fab.setOnClickListener(shareContent);
        fab.setBackgroundColor(Color.BLACK);
        return  view;
    }

    public void ChangeColor(TextView[] views, Boolean selectable, int color)
    {
        for(TextView view:views) {
            view.setTextColor(color);
            view.setTextIsSelectable(selectable);
        }
    }
    public static SearchResultFragment NewResult(String Chapter, String Proofs,String Title, Integer ID, String ListTitle, Integer MatchNum,String Chaptitle,String docTags )
    {
        SearchResultFragment fragment = new SearchResultFragment();
        Bundle spaces = new Bundle();
        spaces.putString(CHAPTITLE, Chaptitle);
        spaces.putInt(number, ID);
        spaces.putInt(matchNumb,MatchNum);
        spaces.putString(CHAPTER,Chapter);
        spaces.putString(PROOFS,Proofs);
        spaces.putString(TITLE,Title);
        spaces.putString(DOCTITLE,ListTitle);
        spaces.putString(TAGS,docTags);
        fragment.setArguments(spaces);
        return fragment;
    }
FloatingActionButton.OnClickListener shareContent = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        String INTENTNAME = "SHARE";
        sendIntent.putExtra(Intent.EXTRA_TEXT,shareList);
    sendIntent.setType("text/plain");
    startActivity(Intent.createChooser(sendIntent,INTENTNAME));

    }
};

}
