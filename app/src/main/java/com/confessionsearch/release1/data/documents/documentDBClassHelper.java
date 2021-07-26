package com.confessionsearch.release1.data.documents;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.confessionsearch.release1.databaseshelpers.DatabaseContext;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.io.File;
import java.util.ArrayList;

public class documentDBClassHelper extends SQLiteAssetHelper {
    //DATABASE INFORMATION
    private static final String DATABASE_NAME = "confessionSearchDB.sqlite3";
    private static final Integer DATABASE_VERSION = 1;
    private static final String DATABASE_PATH = Environment.DIRECTORY_DOWNLOADS.concat("/"+DATABASE_NAME);

    //TABLE INFO
    private static final String TABLE_DOCUMENT = "Document";
    private static final String TABLE_DOCUMENTTYPE = "DocumentType";
    private static final String TABLE_DOCUMENTTITLE = "DocumentTitle";

static SQLiteDatabase db;
public Context context;
    //DOCUMENT TABLE COLUMNS
    private static final String KEY_DOCDETAILID_ID = "DocDetailID";
    private static final String KEY_DOCUMENT_ID_FK = "DocumentID";
    private static final String KEY_DOC_INDEX_NUM = "DocIndexNum";
    private static final String KEY_CHAPTER_NAME = "ChName";
    private static final String KEY_CHAPTER_TEXT = "ChText";
    private static final String KEY_CHAPTER_PROOFS = "ChProofs";
    private static final String KEY_DOCUMENT_TAGS = "ChTags";
    private static final String KEY_MATCHES = "ChMatches";

    //DOCUMENT TITLE TABLE
    private static final String KEY_DOCUMENTTITLE_ID = "DocumentID";
    private static final String KEY_DOCUMENTTITLE_NAME = "DocumentName";
    public static final String KEY_DOCUMENT_TITLE_TYPE_ID_FK = "DocumentTypeID";

    //DOCUMENT TYPE TABLE
    private static final String KEY_DOCUMENT_TYPE_ID = "DocumentTypeID";
    private static final String KEY_DOCUMENT_TYPE_NAME = "DocumentTypeName";

    public documentDBClassHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
this.context = context;
        this.setForcedUpgrade();
    }

documentDBClassHelper(final  Context context, String databaseName){
             super(new DatabaseContext(context),databaseName,null,DATABASE_VERSION);
    try{
        String myPath =DATABASE_PATH; //"assets/databases/"+DATABASE_NAME;// DATABASE_PATH;
        File dbFile = new File(myPath);
        if(dbFile.exists()) {
            Toast.makeText(context, "DBExists", Toast.LENGTH_LONG).show();

        }
        else {
            Toast.makeText(context, "Db doesn't exist", Toast.LENGTH_LONG).show();

        }
    }
    catch (SQLiteException  e)
    {Log.d("Database doesn't exist",e.getMessage());}
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        return super.getWritableDatabase();

    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);

    }

    @Override
    public synchronized SQLiteDatabase getReadableDatabase() {
        return super.getReadableDatabase();
    }



    public Cursor getDocumentTypes(){
        SQLiteDatabase db = getReadableDatabase();
        Log.d("CHECKDB",getReadableDatabase().getPath());
        SQLiteQueryBuilder docTypes = new SQLiteQueryBuilder();
        String[] docTypeSQLQuery = {KEY_DOCUMENT_TYPE_ID,KEY_DOCUMENT_TYPE_NAME};
        String tables = TABLE_DOCUMENTTYPE;
docTypes.setTables(tables);
Cursor c =docTypes.query(db,docTypeSQLQuery,null,null,null,null,null);

c.moveToFirst();
        return c;
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOCUMENTTITLE);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOCUMENT);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOCUMENTTYPE);
            onCreate(db);
        }

    }

    private static documentDBClassHelper sInstance;

    public static synchronized documentDBClassHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new documentDBClassHelper(context.getApplicationContext());
        }
        return sInstance;
    }


    //QUERY SQL STATEMENTS FOR SEARCH
    public ArrayList<DocumentType> getAllDocTypes(SQLiteDatabase dbTypes) {
        ArrayList<DocumentType> types = new ArrayList<DocumentType>();
        String commandText = "SELECT * FROM DocumentType";


        Cursor cursor =getDocumentTypes();// getReadableDatabase().rawQuery(commandText, null);
        try {
            if(cursor.moveToFirst())
           for(int i = 0; i < cursor.getCount();i++,cursor.moveToNext())
           {

                    DocumentType newType = new DocumentType();
                    newType.setDocumentTypeID(cursor.getInt(cursor.getColumnIndex(KEY_DOCUMENT_TYPE_ID)));
                    newType.setDocumentTypeName(cursor.getString(cursor.getColumnIndex(KEY_DOCUMENT_TYPE_NAME)));
                    types.add(newType);
            }

        } catch (Exception e) {
            Log.d("ERROR", "Error retrieving entries from DB");
        } finally {
            if (cursor != null && !cursor.isClosed()) cursor.close();
            {
                cursor.close();
            }    //
        }
        return types;
    }

    public ArrayList<DocumentTitle> getAllDocTitles(String type,SQLiteDatabase dbType) {
        ArrayList<DocumentTitle> documentTitles = new ArrayList<DocumentTitle>();
       Integer typeID=0;
        String commandText;
        if (type == "")
            type ="ALL";
        switch (type.toUpperCase())
        {
            case "ALL":commandText = "SELECT * FROM DocumentTitle";break;
            //case"ALL": commandText="SELECT * FROM DocumentTitle"; break;
            default:commandText = LayoutString(type.toUpperCase());
        }

switch (type.toUpperCase())
{ case "CREED":typeID=1; break;
    case "CONFESSION":typeID=2;break;
    case  "CATECHISM":typeID=3;break;
    case "ALL":typeID=0;break;}


       // SQLiteDatabase db =dbType; //getReadableDatabase();
        Cursor cursor = dbType.rawQuery(commandText,null);
        try{
            if (cursor.moveToFirst()){
               for(int i = 0;i<cursor.getCount();i++,cursor.moveToNext())
                {
                    DocumentTitle newTitle = new DocumentTitle();
                    newTitle.setDocumentTypeID(cursor.getInt(cursor.getColumnIndex(KEY_DOCUMENT_TITLE_TYPE_ID_FK)));
                newTitle.setDocumentID(cursor.getInt(cursor.getColumnIndex(KEY_DOCUMENTTITLE_ID)));
                newTitle.setDocumentName(cursor.getString(cursor.getColumnIndex(KEY_DOCUMENTTITLE_NAME)));
               if (newTitle.getDocumentTypeID()==typeID || typeID ==0)
                                    documentTitles.add(newTitle);

               else
                    continue;
              // documentTitles.add(newTitle);
                }

            }
            cursor.close();
        }
        catch (Exception e)
        {
            Log.d("Error","Error Retrieving the entries from DocumentTitle Table");
        }
        finally{
            if (cursor != null && !cursor.isClosed()) cursor.close();
            {
                cursor.close();
            }
        }

return  documentTitles;
    }
//Fetch documents for processing
    public DocumentList getAllDocuments(String fileString,String fileName,Integer docID, Boolean allDocs,SQLiteDatabase dbList,String access,DocumentList docList,Context context) {
        Cursor cursor;
        DocumentList documentList = new DocumentList();
//SQLiteDatabase db =dbList;// getReadableDatabase();
        String commandText, docCommandText, accessString;
        int documentIndex = 0;
        accessString = DataTableAccess(access);
//SQL Query Execution
//Identify what needs selected
        if (docID != 0) {
            commandText = TableAccess(fileString);
        } else
            commandText = fileString;
        docCommandText = accessString;
//Add entries to Document List
        ArrayList<DocumentTitle> docTitle = new ArrayList<DocumentTitle>();
        ArrayList<Integer> docIds = new ArrayList<>();
        ArrayList<String> docTitleList = new ArrayList<>();
        cursor = dbList.rawQuery(commandText, null);
       //cursor1 =

        try {

            if (cursor.moveToFirst()) {
                for (int i = 0; i < cursor.getCount(); i++, cursor.moveToNext()) {
                    DocumentTitle addDoc = new DocumentTitle();
                    addDoc.setDocumentName(cursor.getString(cursor.getColumnIndex(KEY_DOCUMENTTITLE_NAME)));
                    addDoc.setDocumentTypeID(cursor.getInt(cursor.getColumnIndex(KEY_DOCUMENT_TITLE_TYPE_ID_FK)));
                    addDoc.setDocumentID(cursor.getInt(cursor.getColumnIndex(KEY_DOCUMENTTITLE_ID)));
                    docTitle.add(addDoc);

                }
            }
            cursor.close();
for(Integer y =0; y<docTitle.size();y++)
{
    docIds.add(docTitle.get(y).getDocumentID());
    docTitleList.add(docTitle.get(y).getDocumentName());
}
            Cursor cursor1;
            cursor1 = dbList.rawQuery(docCommandText, null);

            Log.d("Size of Query List", String.valueOf(((cursor1.getColumnIndexOrThrow(KEY_DOCDETAILID_ID)))));
            if (cursor1.moveToFirst()) {

                for (int i = 0; i < cursor1.getCount(); i++, cursor1.moveToNext()) {
                    Document doc = new Document();
                    doc.setChName(cursor1.getString(cursor1.getColumnIndex(KEY_CHAPTER_NAME)));
                    doc.setChNumber(cursor1.getInt(cursor1.getColumnIndex(KEY_DOC_INDEX_NUM)));
                    doc.setDocumentID(cursor1.getInt(cursor1.getColumnIndex(KEY_DOCUMENT_ID_FK)));


    {

            documentIndex =docIds.indexOf(doc.getDocumentID());
    Log.d("DocumentIndex","Document Index is "+documentIndex);
    if(documentIndex>-1)
        doc.setDocumentName(docTitle.get(documentIndex).getDocumentName());
    Log.d("DocumentTitle","Document Title: " + doc.getDocumentName());
    }

doc.setChName(cursor1.getString(cursor1.getColumnIndex(KEY_CHAPTER_NAME)));
                    doc.setProofs((cursor1.getString(cursor1.getColumnIndex(KEY_CHAPTER_PROOFS))));
                    doc.setDocDetailID(cursor1.getInt(cursor1.getColumnIndex(KEY_DOCDETAILID_ID)));
                    doc.setDocumentText((cursor1.getString(cursor1.getColumnIndex(KEY_CHAPTER_TEXT))));
                    doc.setTags(cursor1.getString(cursor1.getColumnIndex(KEY_DOCUMENT_TAGS)));
                    doc.setMatches(cursor1.getInt(cursor1.getColumnIndex(KEY_MATCHES)));
                    documentList.add(doc);
                }
            }
            cursor1.close();
            Log.d("Size of List", String.valueOf(documentList.size()));

            docList = documentList;
return docList;

        } catch (Exception e) {
            Log.d("ERROR", e.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) cursor.close();
            {
                cursor.close(); return docList;
            }
        }



    }
    public String Formatter(String formatString)
    {
        Integer x =0;
        String formatter = "";
        String[] words;
        words = formatString.split("|");
        for(Integer i = 0; i <= words.length - 1; i++)
        {
            formatter+=words[i];
            formatter+="\r\n\n";
        }
        formatString = formatter;
        return formatString;
    }
    public String LayoutString(String docType) {
        String docType1 = String.format("SELECT DocumentType.*, DocumentTitle.* FROM DocumentTitle NATURAL JOIN DocumentType WHERE DocumentTitle.DocumentTypeID = DocumentType.DocumentTypeId AND DocumentType.DocumentTypeName = '%s'", docType);
        return docType1;
    }

    public String DataTableAccess(String documentName) {
        String document = String.format("SELECT Documenttitle.documentName, " +
                "document.*, documenttitle.documentid FROM " +
                "documentTitle NATURAL JOIN document WHERE document.DocumentID = DocumentTitle.DocumentID %s", documentName);
        return document;
    }

    public String TableAccess(String tableName) {
        String table1;
        if (tableName != "")
            table1 = String.format("SELECT documenttitle.* FROM documenttitle WHERE %s", tableName);
        else
            table1 = "Select documenttype.*,documenttitle.* from documenttitle natural join documenttype where documenttitle.documenttypeid = documenttype.documenttypeid";
        return table1;
    }
}



