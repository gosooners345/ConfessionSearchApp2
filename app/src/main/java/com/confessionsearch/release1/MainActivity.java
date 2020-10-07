package com.confessionsearch.release1;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity {

    ShareActionProvider shareProvider;
    BottomNavigationView bottomNav;
    private Spinner documentTypeSpinner, documentNameSpinner;
    protected static int SETTINGS_ACTION = 1;
    ExtendedFloatingActionButton helpButton;
    ExtendedFloatingActionButton searchButton;
    public static final int ACTIVITY_ID = 31;
    ExtendedFloatingActionButton notesButton;
    String header = "";
    private static String THEME = "THEME";
    private static String theme = "";
    protected Boolean textSearch, questionSearch, readerSearch;
    String query;
    public String dbName = "confessionSearchDB.sqlite3";
    documentDBClassHelper documentDBHelper;
    public String type = "";
    String shareList = "";
    public static int themeID;
    public String fileName;
    protected Boolean allOpen, confessionOpen, catechismOpen, creedOpen, helpOpen;
    protected Boolean proofs = true, answers = true, searchAll = false;
    RadioButton topicButton, questionButton, viewAllButton;
    Intent intent;
    CheckBox answerCheck, allDocCheck, proofCheck;
    ArrayList<String> docTypes, docTitles;
    ArrayAdapter<String> docTypeSpinnerAdapter;
    ArrayAdapter<String> docTitleSpinnerAdapter;
    SearchView searchBox;
    SQLiteDatabase documentDB;
    Boolean themeName;
    SharedPreferences pref;
    DocumentList masterList = new DocumentList();
    String shareNote;
    static ArrayList<Notes> notesArrayList = new ArrayList<>();
    SearchFragmentActivity searchFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        //int nightModeOn=32, nightModeOff=16,on=2,off=1;

//        themeName = pref.getString("theme","Dark");
        themeName = pref.getBoolean("darkMode", true);

        if (!themeName) {
            themeID = R.style.LightMode;
            setTheme(themeID);
        } else if (themeName) {
            themeID = R.style.DarkMode;
            setTheme(themeID);
        }
        super.onCreate(savedInstanceState);
        //Set the show for the search app
        setTitle(R.string.app_name);
        refreshLayout(R.layout.activity_main);
        bottomNavEnabler();

    }

    protected void bottomNavEnabler() {
        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(bottomNavListener);
        // bottomNav.setOnNavigationItemReselectedListener(reselectedListener);
    }


    CheckBox.OnCheckedChangeListener checkBox = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            switch (compoundButton.getId()) {
                case R.id.proofBox:
                    proofs = !proofCheck.isChecked();
                    break;
                case R.id.AnswerBox:
                    answers = !answerCheck.isChecked();
                    break;
                case R.id.searchAllCheckBox:
                    searchAll = allDocCheck.isChecked();
                    break;
            }
        }
    };

    //Searches the Database for the topic and returns the results in a list
    public void Search(String query) {


        Integer docID = 0;

        String accessString = "", fileString = "";
        //Boolean  proofs = true, answers = true, searchAll = false, viewDocs = false;
        Log.d("Search()", getString(R.string.search_execution_begins));
        searchFragment = new SearchFragmentActivity();
        RadioButton topicRadio = findViewById(R.id.topicRadio);
        RadioButton questionRadio = findViewById(R.id.chapterRadio);
        RadioButton readerRadio = findViewById(R.id.viewAllRadio);
        //Type of Search
        if (topicRadio.isChecked()) {
            readerSearch = false;
            textSearch = true;
            questionSearch = false;
        } else if (questionRadio.isChecked()) {
            readerSearch = false;
            textSearch = false;
            questionSearch = true;
        } else if (readerRadio.isChecked()) {
            readerSearch = true;
            textSearch = false;
            questionSearch = false;
        }


        //Filters for how searches are executed by document type and name
        if (!allDocCheck.isChecked()) {
            accessString = String.format(" and documenttitle.documentName = '%s' ", fileName);
        }
        if (allOpen) {
            docID = 0;
            if (searchAll)
                fileString = "SELECT * FROM DocumentTitle";
            else
                fileString = String.format("Select * From DocumentTitle where DocumentTitle.DocumentName = '%s'", fileName);
        }
        if (catechismOpen) {
            docID = 3;
            if (!searchAll) {
                fileString = (String.format(" documentTitle.DocumentTypeID = 3 AND DocumentName = '%s'", fileName));
            } else
                fileString = (" documentTitle.DocumentTypeID = 3");
        } else if (confessionOpen) {
            docID = 2;
            if (!searchAll) {
                fileString = (String.format(" documentTitle.DocumentTypeID = 2 AND DocumentName = '%s'", fileName));
            } else {
                fileString = (" documentTitle.DocumentTypeID = 2");

            }
        } else if (creedOpen) {
            docID = 1;
            if (!searchAll) {
                fileString = (String.format(" documentTitle.DocumentTypeID = 1 AND DocumentName = '%s'", fileName));
            } else {
                fileString = (String.format(" documentTitle.DocumentTypeID = 1"));
            }
        }
//This fills the list with entries for filtering and sorting
        masterList = documentDBHelper.getAllDocuments(fileString, fileName, docID, allOpen, documentDB, accessString, masterList, this);
        for (Document d : masterList) {
            if (d.getDocumentText().contains("|") | d.getProofs().contains("|")) {
                d.setProofs(Formatter(d.getProofs()));
                d.setDocumentText(Formatter(d.getDocumentText()));
            }
        }
        //Search topics and filter them
        if (!readerSearch & textSearch & !questionSearch) {
            if (!query.isEmpty()) {
                FilterResults(masterList, answers, proofs, query);
                Collections.reverse(masterList);
            }
            //Display reader
            else {
                if (masterList.size() > 1) {
                    query = fileName;
                    setContentView(R.layout.index_pager);
                    SearchAdapter adapter = new SearchAdapter(getSupportFragmentManager(), masterList, query);
                    ViewPager vp2 = findViewById(R.id.resultPager);
                    searchFragment.DisplayResults(masterList, vp2, adapter, query, 0);
                }
            }
        }
        //Searching chapters
        else if (questionSearch & query != "" & !readerSearch & !textSearch) {
            if (query != "") {
                Integer searchInt = Integer.parseInt(query);
                FilterResults(masterList, answers, proofs, searchInt);
            } else {
                recreate();
            }

        } else if (readerSearch & !questionSearch & !textSearch) {
            if (!searchAll) {
                query = "Results for All";

            } else
                query = "View All";
        }


//Displays the list of results
        if (masterList.size() > 1) {
            setContentView(R.layout.index_pager);
            SearchAdapter adapter = new SearchAdapter(getSupportFragmentManager(), masterList, query);
            ViewPager vp2 = findViewById(R.id.resultPager);

            searchFragment.DisplayResults(masterList, vp2, adapter, query, 0);

        } else {
            //Returns an error if there are no results in the list
            if (masterList.size() == 0) {
                Log.i("Error", "No results found for Topic");
                Toast.makeText(this, String.format("No Results were found for %s", query), Toast.LENGTH_LONG).show();
                setContentView(R.layout.error_page);
                TextView errorMsg = findViewById(R.id.errorTV);
                errorMsg.setText(String.format("No results were found for %s \r\n\r\n" +
                        "Go back to home page to search for another topic", query));
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("No Results found!");
                alert.setMessage(String.format("No Results were found for %s.\r\n\r\n" +
                        "Do you want to go back and search for another topic?", query));
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        intent = new Intent(MainActivity.this, MainActivity.this.getClass());
                        searchFragment = null;
                        onStop();
                        finish();
                        startActivity(intent);
                    }
                });
                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                Dialog dialog = alert.create();
                if (!isFinishing())
                    dialog.show();
            }
            // For Results with only 1 result
            else {
                Document document = masterList.get(masterList.size() - 1);
                setContentView(R.layout.search_results);
                ExtendedFloatingActionButton saveFab = findViewById(R.id.saveNote);
                ExtendedFloatingActionButton fab = findViewById(R.id.shareActionButton);
                TextView chapterBox = findViewById(R.id.chapterText);
                TextView proofBox = findViewById(R.id.proofText);
                TextView chNumbBox = findViewById(R.id.confessionChLabel);
                TextView docTitleBox = findViewById(R.id.documentTitleLabel);
                TextView tagBox = findViewById(R.id.tagView);
                proofBox.setText(Html.fromHtml(document.getProofs()));
                docTitleBox.setText(document.getDocumentName());
                docTitleBox.setText(document.getDocumentName());
                chapterBox.setText(Html.fromHtml(document.getDocumentText()));
                tagBox.setText(String.format("Tags: %s", document.getTags()));
                if (chapterBox.getText().toString().contains("Question")) {
                    header = "Question ";
                    chNumbBox.setText(String.format("%s %s: %s", header, document.getChNumber(), document.getChName()));

                } else if (chapterBox.getText().toString().contains("I. ")) {
                    header = "Chapter";
                    chNumbBox.setText(String.format("%s %s: %s", header, document.getChNumber(), document.getChName()));

                } else
                    chNumbBox.setText(String.format("%s", document.getDocumentName()));
                String newLine = "\r\n";
                shareList = docTitleBox.getText() + newLine + chNumbBox.getText() + newLine
                        + newLine + chapterBox.getText() + newLine + "Proofs" + newLine + proofBox.getText();
                fab.setOnClickListener(shareContent);
                fab.setBackgroundColor(Color.BLACK);
                shareNote = "";
                shareNote = (docTitleBox.getText() + "<br>" + "<br>" + chNumbBox.getText() + "<br>"
                        + "<br>" + document.getDocumentText() + "<br>" + "Proofs" + "<br>" + document.getProofs());
                //  shareNote = (docTitleBox.getText() +newLine +newLine +  chNumbBox.getText() + "<br>"
                //        + "<br>" + resultChapter + "<br>" + "Proofs" + "<br>" + resultProofs).toString();
                //  shareNote = (docTitleBox.getText() + newLine + newLine + chNumbBox.getText() + newLine
                //       + newLine + chapterBox.getText() + newLine + "Proofs" + newLine + proofBox.getText()) + newLine;
                saveFab.setOnClickListener(saveNewNote);

            }
        }
    }

    //Enables Note Saving from results screen
    ExtendedFloatingActionButton.OnClickListener saveNewNote = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), NotesComposeActivity.class);
            intent.putExtra("activity_ID", ACTIVITY_ID);
            intent.putExtra("search_result_save", shareNote);
            startActivity(intent);
        }
    };

    //Enables Share function
    ExtendedFloatingActionButton.OnClickListener shareContent = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            String INTENTNAME = "SHARE";
            sendIntent.putExtra(Intent.EXTRA_TEXT, shareList);
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, INTENTNAME));
        }
    };

    //Formats the text to be reader friendly
    public String Formatter(String formatString) {
        formatString = formatString.replace("|", "<br><br>");
        return formatString;
    }

    //Filter Search Results
    public void FilterResults(DocumentList documentList, Boolean answers, Boolean proofs, String query) {
        DocumentList resultList = new DocumentList();

        //Break document up into pieces to be searched for topic
        for (Document document : documentList) {

            ArrayList<String> searchEntries = new ArrayList<String>();

            searchEntries.add(document.getChName());
            searchEntries.add(document.getDocumentText());
            if (proofs) searchEntries.add((document.getProofs()));
            searchEntries.add(document.getTags());
            for (String word : searchEntries) {
                {
                    int matchIndex = 0;
//Tally up all matching sections
                    while (true) {
                        int wordIndex = word.toUpperCase().indexOf(query.toUpperCase(), matchIndex);
                        if (wordIndex < 0) break;
                        matchIndex = wordIndex + 1;
                        document.setMatches(document.getMatches() + 1);
                    }
                }
            }
//If the entry has a match to the query, it'll show up in the results
            if (document.getMatches() > 0) {
                // No answers
                if (!answers) {
                    if (document.getDocumentText().contains("Question")) {
                        int closeIndex = document.getDocumentText().indexOf("Answer");
                        document.setDocumentText(document.getDocumentText().substring(0, closeIndex - 1));
                    }
                }
                //No proofs
                if (!proofs)
                    document.setProofs("No Proofs available!");
                resultList.add(document);
            }
        }
        //Sort the Results by highest matching tally
        Collections.sort(resultList, Document.compareMatches);
        for (Document d : resultList) {
            d.setProofs(HighlightText(d.getProofs(), query));
            d.setDocumentText(HighlightText(d.getDocumentText(), query));
        }
        masterList = resultList;
    }

    //Look for the matching chapter/question index
    public void FilterResults(DocumentList documentList, Boolean answers, Boolean proofs, Integer indexNum) {
        DocumentList resultList = new DocumentList();
        for (Document document : documentList) {
            if (document.getChNumber() == indexNum) {
                if (!answers) {
                    if (document.getDocumentText().contains("Question")) {
                        int closeIndex = document.getDocumentText().indexOf("Answer");
                        document.setDocumentText(document.getDocumentText().substring(0, closeIndex - 1));
                    }
                } else if (!proofs) {
                    document.setProofs("No Proofs Available");
                }
                resultList.add(document);
            } else
                continue;

        }

        Collections.sort(resultList);
        masterList = resultList;
    }

    //Highlights topic entries in search results
    public String HighlightText(String sourceStr, String query) {
        String replaceQuery = "<b>" + query + "</b>";
        String resultString = "";
        Pattern replaceString = Pattern.compile(query, Pattern.CASE_INSENSITIVE);
        Matcher m = replaceString.matcher(sourceStr);
        resultString = m.replaceAll(replaceQuery);
        Log.d("Test", resultString);
        return resultString;
    }

    //Executes on startup
    public void refreshLayout(int viewID) {
        setContentView(viewID);
        topicButton = findViewById(R.id.topicRadio);
        questionButton = findViewById(R.id.chapterRadio);
        viewAllButton = findViewById(R.id.viewAllRadio);
        //Search Button Initialization
        searchButton = findViewById(R.id.searchFAB);
        searchButton.setOnClickListener(searchButtonListener);
        //Search Box Initialization
        searchBox = findViewById(R.id.searchView1);
        searchBox.setOnQueryTextListener(searchQueryListener);
        searchBox.setOnKeyListener(submissionKey);

        //CheckBox initialization
        proofCheck = findViewById(R.id.proofBox);
        allDocCheck = findViewById(R.id.searchAllCheckBox);
        answerCheck = findViewById(R.id.AnswerBox);
        proofCheck.setOnCheckedChangeListener(checkBox);
        allDocCheck.setOnCheckedChangeListener(checkBox);
        answerCheck.setOnCheckedChangeListener(checkBox);
        documentTypeSpinner = findViewById(R.id.documentTypeSpinner);
        documentNameSpinner = findViewById(R.id.documentNameSpinner);
        //Database stuff
        documentDBHelper = new documentDBClassHelper(this);
        documentDB = documentDBHelper.getReadableDatabase();
        //Document selector Lists
        docTypes = new ArrayList<>();
        docTitles = new ArrayList<>();
        ArrayList<DocumentType> typeList = documentDBHelper.getAllDocTypes(documentDB);
        ArrayList<DocumentTitle> documentTitles = documentDBHelper.getAllDocTitles(type, documentDB);
        //Document Type Spinner Initialization
        docTypes.add("All");
        for (DocumentType type : typeList) {
            docTypes.add(type.documentTypeName);
        }
        docTypeSpinnerAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, docTypes);
        documentTypeSpinner = findViewById(R.id.documentTypeSpinner);
        documentTypeSpinner.setAdapter(docTypeSpinnerAdapter);
        documentTypeSpinner.setOnItemSelectedListener(spinnerItemSelectedListener);

        //Document Titles Spinner Initialization
        for (DocumentTitle docTitle : documentTitles) {
            docTitles.add(docTitle.getDocumentName());
        }
        docTitleSpinnerAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, docTitles);

        documentNameSpinner = findViewById(R.id.documentNameSpinner);
        documentNameSpinner.setOnItemSelectedListener(docTitleSpinner);
        searchBox.setOnKeyListener(submissionKey);
        topicButton.performClick();


        // helpButton.setOnClickListener(helpButton_Click);
        //notesButton = findViewById(R.id.notesButton);
    }

    //Select search type
    public void SearchType(View view) {
        KeyEvent enter = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER);
        RadioButton radio = (RadioButton) view;
        ExtendedFloatingActionButton searchFab = findViewById(R.id.searchFAB);
        if (radio == findViewById(R.id.topicRadio)) {
            if (radio.isChecked()) {
                searchBox.setEnabled(true);
                searchBox.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
                searchBox.setSubmitButtonEnabled(true);
                searchBox.setOnKeyListener(submissionKey);
                searchBox.setOnQueryTextListener(searchQueryListener);
                searchBox.setInputType(InputType.TYPE_CLASS_TEXT);
                textSearch = true;
                questionSearch = false;
                readerSearch = false;
                searchFab.setText(getResources().getString(R.string.Search));
            }
        } else if (radio == findViewById(R.id.chapterRadio))
            if (radio.isChecked()) {
                searchBox.setEnabled(true);
                searchBox.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
                searchBox.setInputType(InputType.TYPE_CLASS_NUMBER);
                searchBox.setOnQueryTextListener(searchQueryListener);
                searchBox.setOnKeyListener(submissionKey);
                textSearch = false;
                readerSearch = false;
                questionSearch = true;
                searchFab.setText(getResources().getString(R.string.Search));


            } else if (radio == findViewById(R.id.viewAllRadio))
                if (radio.isChecked()) {
                    searchFab.setText(getResources().getString(R.string.read_button_text));

                    textSearch = false;
                    questionSearch = false;
                    readerSearch = true;

                }
    }

    //Menu options for themes
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.settings:
                HelpLauncher();
                bottomNavEnabler();
        }
        return super.onOptionsItemSelected(item);
    }

    //For Activities that return a result like theme setting
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SETTINGS_ACTION) {
            if (resultCode == ThemePreferenceActivity.RESULT_CODE_THEME_UPDATED) {
                finish();
                startActivity(getIntent());
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //This listens for clicks on the spinner widgets on home screen
    AdapterView.OnItemSelectedListener spinnerItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @SuppressLint("ResourceAsColor")
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            {

                    docTitles = new ArrayList<>();
                    type = parent.getSelectedItem().toString();
                    //Gets all document titles and places them in a list
                    for (DocumentTitle docTitle : documentDBHelper.getAllDocTitles(type, documentDB)
                    ) {
                        docTitles.add(docTitle.getDocumentName());
                    }
                    docTitleSpinnerAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, docTitles);
                    docTitleSpinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                documentNameSpinner.setAdapter(docTitleSpinnerAdapter);
                documentNameSpinner.setOnItemSelectedListener(docTitleSpinner);
                    switch (type.toUpperCase()) {
                        case "ALL":
                            allOpen = true;
                            confessionOpen = false;
                            catechismOpen = false;
                            creedOpen = false;
                            helpOpen = false;
                            break;
                        case "CONFESSION":
                            allOpen = false;
                            confessionOpen = true;
                            catechismOpen = false;
                            header = "Chapter ";
                            creedOpen = false;
                            helpOpen = false;
                            break;
                        case "CATECHISM":
                            allOpen = false;
                            header = "Question ";
                            confessionOpen = false;
                            catechismOpen = true;
                            creedOpen = false;
                            helpOpen = false;
                            break;
                        case "CREED":
                            allOpen = false;
                            creedOpen = true;
                            catechismOpen = false;
                            confessionOpen = false;
                            helpOpen = false;
                            break;
                    }
            }
        }


        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            type = parent.getSelectedItem().toString();
        }
    };
    AdapterView.OnItemSelectedListener docTitleSpinner = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            try {
                if (themeName)  //if(themeName.contains("Dark"))

                    ((TextView) adapterView.getChildAt(0)).setTextColor(Color.WHITE);

            } catch (Exception ex) {
                documentNameSpinner.setOnItemSelectedListener(docTitleSpinner);
                documentNameSpinner.setSelection(0);
            }

            fileName = String.format("%s", adapterView.getSelectedItem().toString());
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    //This allows for Submission to take place
    SearchView.OnKeyListener submissionKey = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            SearchView searchBox = (SearchView) v;

            if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                query = searchBox.getQuery().toString();
                Log.d("View", String.format("%s", event.getDisplayLabel()));
                if (!query.isEmpty() & !viewAllButton.isSelected())
                    Search(query);
                else
                    ErrorMessage(getResources().getString(R.string.query_error));
                return true;
            } else {
                return false;
            }
        }
    };

    // This assigns an action to the search button so it can execute the search
    public ExtendedFloatingActionButton.OnClickListener searchButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            String query;
            if (!viewAllButton.isChecked()) {
                query = searchBox.getQuery().toString();
                if (query.isEmpty())
                    ErrorMessage(getResources().getString(R.string.query_error));
                else
                    Search(query);
            } else {
                query = "";
                Search(query);
            }
        }
    };

    //Return to application's main starting screen
    public void Home() {
// Finish Results activity
        searchFragment.finish();
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        Log.d("Application", "Rebooting application");
        searchFragment = null;
//Take user to main search screen
        MainActivity.super.onStop();
        MainActivity.super.finish();
        startActivity(intent);
    }

    //Prevents application from proceeding to execute if an error is found
    public void ErrorMessage(String message) {
        //Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Snackbar errorBar = Snackbar.make(findViewById(R.id.layout_super), message, BaseTransientBottomBar.LENGTH_SHORT);
        errorBar.setAnchorView(R.id.bottom_navigation);
        errorBar.show();
    }

    //Back Key Behavior
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (searchFragment != null)
            Home();
        else {
            Log.d("Exiting", "Elvis Has Left the building");
            MainActivity.super.finish();
        }
    }

    //SearchView Listeners
    SearchView.OnQueryTextListener searchQueryListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String entry) {
            query = entry;
            if (!viewAllButton.isChecked()) {
                if (query.isEmpty())
                    ErrorMessage(getResources().getString(R.string.query_error));
                else
                    Search(query);
            } else
                Search(query);
            return false;
        }

        //nothing happens here
        @Override
        public boolean onQueryTextChange(String newText) {
            return false;
        }

    };


    //Enables the app to return to the main screen after home button pressed

    public void HelpLauncher() {
        setContentView(R.layout.help_page);
        bottomNavEnabler();

    }


    //Handles Orientation changes
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    BottomNavigationView.OnNavigationItemSelectedListener bottomNavListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            //Boolean itemState = navItemSelected(item);
            switch (item.getItemId()) {
                case R.id.search_page:
                    refreshLayout(R.layout.activity_main);
                    Log.d("HOME PAGE", "Home Page Launched");
                    bottomNavEnabler();
                    Log.d("BottomNav", "Home Page Bottom Nav Enabled");
                    //updateNavigationBarState(R.id.search_page);
                    Log.d("HomePage", "Menu item selected lit up");

                    break;
                case R.id.notes_page:

                    Intent noteIntent = new Intent(getApplicationContext(), NotesActivity.class);
                    startActivity(noteIntent);

                    break;
                case R.id.settings_page:
                    startActivityForResult(new Intent(MainActivity.this, ThemePreferenceActivity.class), SETTINGS_ACTION);
                    bottomNavEnabler();

                    break;
                default:
                    return false;
            }
            item.setChecked(true);
            return true;

        }
    };


}


