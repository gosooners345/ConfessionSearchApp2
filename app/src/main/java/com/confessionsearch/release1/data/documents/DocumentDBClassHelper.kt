package com.confessionsearch.release1.data.documents

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteQueryBuilder
import android.util.Log
import com.confessionsearch.release1.data.bible.BibleBooks
import com.confessionsearch.release1.data.bible.BibleContents
import com.confessionsearch.release1.data.bible.BibleContentsList
import com.confessionsearch.release1.data.bible.BibleTranslation
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper

class DocumentDBClassHelper : SQLiteAssetHelper {
    var context: Context? = null

    constructor(context: Context?) : super(context, DATABASE_NAME, null, DATABASE_VERSION) {
        this.context = context
        setForcedUpgrade()
    }

    override fun onOpen(db: SQLiteDatabase) {
        super.onOpen(db)
    }

    @Synchronized
    override fun getReadableDatabase(): SQLiteDatabase {
        return super.getReadableDatabase()
    }

    // Cursors
    //Document types
    val documentTypes: Cursor
        get() {
            val db = readableDatabase
            Log.d("CHECKDB", readableDatabase.path)
            val docTypes = SQLiteQueryBuilder()
            val docTypeSQLQuery = arrayOf(KEY_DOCUMENT_TYPE_ID, KEY_DOCUMENT_TYPE_NAME)
            val tables = TABLE_DOCUMENTTYPE
            docTypes.tables = tables
            val c = docTypes.query(db, docTypeSQLQuery, null, null, null, null, null)
            c.moveToFirst()
            return c
        }

    //Bible Translations
    val bibleTranslations: Cursor
        get() {
            val db = readableDatabase
            Log.d("BIBLECHK", readableDatabase.path)
            val bibleTranslations = SQLiteQueryBuilder()
            val bibleTranslationSQLQuery = arrayOf(
                KEY_BIBLE_TRANSLATION_ID,
                KEY_BIBLE_TRANSLATION_TITLE,
                KEY_BIBLE_TRANSLATION_ABBREV
            )
            val tables = TABLE_BIBLETRANSLATION
            bibleTranslations.tables = tables
            val c =
                bibleTranslations.query(db, bibleTranslationSQLQuery, null, null, null, null, null)
            c.moveToFirst()
            return c
        }

    // Needs to be fixed to allow for smooth upgrades
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion != newVersion) {
//context.deleteDatabase(DATABASE_NAME)
            //new DocumentDBClassHelper(context)

            db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOCUMENTTITLE)
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOCUMENT)
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOCUMENTTYPE)
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_BIBLECONTENTS)
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_BIBLE_BOOKS)
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_BIBLETRANSLATION)
            db.execSQL("CREATE TABLE " + TABLE_DOCUMENTTITLE)
            db.execSQL("CREATE TABLE " + TABLE_DOCUMENT)
            db.execSQL("CREATE TABLE " + TABLE_DOCUMENTTYPE)
            db.execSQL("CREATE TABLE " + TABLE_BIBLECONTENTS)
            db.execSQL("CREATE TABLE " + TABLE_BIBLETRANSLATION)
            db.execSQL("CREATE TABLE " + TABLE_BIBLE_BOOKS)
            onCreate(db)
        }
    }

    //Get Bible translations
    fun getAllBibleTranslations(dbBibles: SQLiteDatabase?): ArrayList<BibleTranslation> {
        val translations = ArrayList<BibleTranslation>()
        val commandText = "SELECT * FROM BibleTranslations"
        val cursor = bibleTranslations
        try {
            if (cursor.moveToFirst()) {
                var i = 0
                while (i < cursor.count) {
                    val newTranslation = BibleTranslation()
                    newTranslation.bibleTranslationID = cursor.getInt(
                        cursor.getColumnIndex(
                            KEY_BIBLE_TRANSLATION_ID
                        )
                    )
                    newTranslation.bibleTranslationName = cursor.getString(
                        cursor.getColumnIndex(
                            KEY_BIBLE_TRANSLATION_TITLE
                        )
                    )
                    newTranslation.bibleTranslationAbbrev = cursor.getString(
                        cursor.getColumnIndex(
                            KEY_BIBLE_TRANSLATION_ABBREV
                        )
                    )
                    translations.add(newTranslation)
                    i++
                    cursor.moveToNext()
                }
            }
        } catch (e: Exception) {
            Log.e("Error", "Error Retrieving Entries from DB")
        } finally {
            if (!cursor.isClosed) cursor.close()
        }
        return translations
    }

    //Document Search Related DB Method
    //QUERY SQL STATEMENTS FOR SEARCH
    fun getAllDocTypes(dbTypes: SQLiteDatabase?): ArrayList<DocumentType> {
        val types = ArrayList<DocumentType>()
        val commandText = "SELECT * FROM DocumentType"
        val cursor = documentTypes
        try {
            if (cursor.moveToFirst()) {
                var i = 0
                while (i < cursor.count) {
                    val newType = DocumentType()
                    newType.documentTypeID = cursor.getInt(
                        cursor.getColumnIndex(
                            KEY_DOCUMENT_TYPE_ID
                        )
                    )
                    newType.documentTypeName = cursor.getString(
                        cursor.getColumnIndex(
                            KEY_DOCUMENT_TYPE_NAME
                        )
                    )
                    types.add(newType)
                    i++
                    cursor.moveToNext()
                }
            }
        } catch (e: Exception) {
            Log.d("ERROR", "Error retrieving entries from DB")
        } finally {
            if (!cursor.isClosed) cursor.close()
            run { cursor.close() } //
        }
        return types
    }

    //Bible Reader Related
//Book Specific Filtering, Safe, Gets Chapters for spinner
    fun getAllChapters(
        bibleList: SQLiteDatabase?,
        translationName: String?,
        bookName: String?
    ): ArrayList<Int?> {
        val bookList = BibleContentsList()
        var chInt = 0
        val accessString: String?
        val ChList = ArrayList<Int?>()
//SQL Statement to get Chapter numbers in code
        accessString = BookChapterNumberAccess(bookName)
        val cursor = bibleList!!.rawQuery(accessString, null)
        try {
            if (cursor.moveToFirst()) {
                var i = 0
                chInt = cursor.getInt(cursor.getColumnIndex(KEY_BIBLE_CONTENTS_CHAPTERNUMBER))
                ChList.add(chInt)
                while (i < cursor.count) {
                    val addBibleContent = BibleContents()

                    addBibleContent.ChapterNum = cursor.getInt(
                        cursor.getColumnIndex(
                            KEY_BIBLE_CONTENTS_CHAPTERNUMBER
                        )
                    )
                    if (chInt < addBibleContent.ChapterNum!!) {
                        chInt = addBibleContent.ChapterNum!!
                        ChList.add(addBibleContent.ChapterNum)
                    }
                    i++
                    cursor.moveToNext()
                }
            }
            cursor.close()
            return ChList
        } catch (exception: Exception) {
            exception.printStackTrace()
            cursor.close()
            return ChList
        }
    }


    //Bible Reader Related Method,Safe
    fun getAllVerseNumbers(
        bibleList: SQLiteDatabase?,
        translationName: String?,
        bookName: String?, chapNum: Int?
    ): ArrayList<Int?> {
        val bookList = BibleContentsList()
        var verseInt = 0
        var accessString: String?
        val verseList = ArrayList<Int?>()
//SQL Statement for Verse numbers
        accessString = BookChapterVerseAccess(chapNum, bookName)


        val cursor = bibleList!!.rawQuery(accessString, null)
        try {
            if (cursor.moveToFirst()) {
                var i = 0
                verseInt = cursor.getInt(cursor.getColumnIndex(KEY_BIBLE_CONTENTS_VERSENUMBER))
                verseList.add(verseInt)
                while (i < cursor.count) {
                    val addBibleContent = BibleContents()

                    addBibleContent.VerseNumber = cursor.getInt(
                        cursor.getColumnIndex(
                            KEY_BIBLE_CONTENTS_VERSENUMBER
                        )
                    )
                    // Filters out excessive verses so we don't duplicate entries
                    if (verseInt < addBibleContent.VerseNumber!!) {
                        verseInt = addBibleContent.VerseNumber!!
                        verseList.add(addBibleContent.VerseNumber)
                    }
                    i++
                    cursor.moveToNext()
                }
            }
            cursor.close()
            return verseList
        } catch (exception: Exception) {
            exception.printStackTrace()
            cursor.close()
            return verseList
        }
    }

    fun getAllVerses(
        bibleList: SQLiteDatabase?,
        translationName: String?,
        bookName: String?, chapNum: Int?, verseNum: Int?
    ): ArrayList<String?> {
        val bookList = BibleContentsList()
        var verseText = ""
        var accessString: String?
        val verseList = ArrayList<String?>()
//SQL Statement for Verse numbers
        accessString = VerseAccess(verseNum, chapNum, bookName)


        val cursor = bibleList!!.rawQuery(accessString, null)
        try {
            if (cursor.moveToFirst()) {
                var i = 0
                verseText = cursor.getString(cursor.getColumnIndex(KEY_BIBLE_CONTENTS_VERSETEXT))
                verseList.add(verseText)
                while (i < cursor.count) {
                    val addBibleContent = BibleContents()
                    addBibleContent.VerseText = cursor.getString(
                        cursor.getColumnIndex(
                            KEY_BIBLE_CONTENTS_VERSETEXT
                        )
                    )
                    i++
                    cursor.moveToNext()
                }
            }
            cursor.close()
            return verseList
        } catch (exception: Exception) {
            exception.printStackTrace()

            cursor.close()
            return verseList
        }
    }

    //Bible Contents List
    fun getChaptersandVerses(
        bibleList: SQLiteDatabase?,
        translationName: String?,
        bookName: String?, chapNum: Int?, verseNum: Int?
    ): BibleContentsList {
        var chapterNumb: Int? = 0
        var verseNumb: Int? = 0
        var verseOnly = false
        var chapterOnly = false
        val bookList = BibleContentsList()
        var verseText = ""
        var accessString: String?
        var prevChapter = 0
        //This allows the database to filter out requests without the need for multiple functions
        if (verseNum!! == 0 && chapNum!! > 0) {
            accessString = BookChapterVerseAccess(chapNum, bookName)
        } else if (chapNum!! == 0 && verseNum == 0) {
            accessString = BookChapterNumberAccess(bookName)
        } else if (chapNum != 0 && verseNum != 0)
            accessString = VerseAccess(verseNum, chapNum, bookName)
        else
            accessString = VerseAccess(verseNum, chapNum, bookName)
        if (verseNum > 0 && chapNum > 0)
            verseOnly = true
        if (chapNum > 0 && !verseOnly)
            chapterOnly = true
        //Populate the cursor with entries from db
        val cursor = bibleList!!.rawQuery(accessString, null)
        try {
            if (cursor.moveToFirst()) {


                var addBibleContent = BibleContents()
                chapterNumb = cursor.getInt(cursor.getColumnIndex(KEY_BIBLE_CONTENTS_CHAPTERNUMBER))
                verseNumb = verseNum
                var i = 0

                //set chapter number to what the db entry at current position value is

                outerloop@ while (!cursor.isAfterLast) {
                    addBibleContent = BibleContents()
                    verseNumb = 1
                    if (verseOnly) {
                        verseText = String.format(
                            verseNum.toString() + " " +
                                    cursor.getString(
                                        cursor.getColumnIndex(
                                            KEY_BIBLE_CONTENTS_VERSETEXT
                                        )
                                    )
                        )
                        //Deals with one chapter
                        addBibleContent.VerseText = verseText
                        addBibleContent.VerseNumber = verseNum
                        addBibleContent.ChapterNum = chapterNumb
                        addBibleContent.BookName = bookName
                        bookList.add(addBibleContent)
                        break@outerloop

                    } else { //gather verses
                        i++
                        verseNumb = i
                        // Look over this tomorrow 9-14-21
                        verseText += verseNumb.toString() + " " + cursor.getString(
                            cursor.getColumnIndex(
                                KEY_BIBLE_CONTENTS_VERSETEXT
                            )
                        )
                        // This breaks when the cursor has gone through the list of verses
                        //This works for chapter selection - 09-13-21
                        if (chapterOnly && cursor.isLast) {
                            addBibleContent.VerseText = verseText
                            addBibleContent.VerseNumber = 0
                            addBibleContent.ChapterNum = chapterNumb
                            addBibleContent.BookName = bookName
                            bookList.add(addBibleContent)
                            break@outerloop
                        }

                        // This will break the loop
                        else if (!chapterOnly && cursor.isLast) {
                            addBibleContent.VerseText = verseText
                            addBibleContent.VerseNumber = 0
                            addBibleContent.ChapterNum = chapterNumb
                            addBibleContent.BookName = bookName
                            bookList.add(addBibleContent)
                            break@outerloop
                        }
                        // This continues the loop
                        else {
                            prevChapter = cursor.getInt(
                                cursor.getColumnIndex(KEY_BIBLE_CONTENTS_CHAPTERNUMBER)
                            )
                            if (chapterNumb!! < prevChapter && !cursor.isLast) {
                                addBibleContent.VerseNumber = 0
                                addBibleContent.VerseText = verseText
                                addBibleContent.ChapterNum = chapterNumb
                                addBibleContent.BookName = bookName
                                chapterNumb = prevChapter
                                bookList.add(addBibleContent)
                                i = 0
                                verseText = ""

                            }
                            cursor.moveToNext()
                        }
                    }
                }

                cursor.close()
            }


            Log.i("COUNT", bookList.count().toString())
            return bookList
        } catch (exception: Exception) {
            exception.printStackTrace()
            cursor.close()
            return bookList
        }
    }


    //get bible books, Bible Reader Related
    fun getAllBooks(
        bibleList: SQLiteDatabase?
    ): ArrayList<BibleBooks> {
        val bookList = ArrayList<BibleBooks>()
        val cursor: Cursor?
        val accessString: String? = BibleBookAccess()
        cursor = bibleList!!.rawQuery(accessString, null)
        try {

            if (cursor.moveToFirst()) {
                var i = 0
                while (i < cursor.count) {
                    val bibleBook = BibleBooks()
                    bibleBook.BookID = cursor.getInt(
                        cursor.getColumnIndex(
                            KEY_BIBLE_BOOKS_ID
                        )
                    )
                    bibleBook.BookName = cursor.getString(
                        cursor.getColumnIndex(
                            KEY_BIBLE_BOOKS_BOOKNAME
                        )
                    )
                    i++
                    cursor.moveToNext()
                    bookList.add(bibleBook)
                }
            }
            cursor.close()
            return bookList
        } catch (exception: Exception) {
            exception.printStackTrace()
            cursor.close()
            return bookList
        }
    }

    // For another section of project
    fun getVersesForProofs() {
    }

    //Grab Document titles from DB, Document Search Related;
    fun getAllDocTitles(type: String, dbType: SQLiteDatabase): ArrayList<DocumentTitle> {
        var type = type
        val documentTitles = ArrayList<DocumentTitle>()
        var typeID = 0
        val commandText: String
        if (type === "") type = "ALL"
        commandText = when (type.toUpperCase()) {
            "ALL" -> "SELECT * FROM DocumentTitle"
            else -> LayoutString(type.toUpperCase())
        }
        when (type.toUpperCase()) {
            "CREED" -> typeID = 1
            "CONFESSION" -> typeID = 2
            "CATECHISM" -> typeID = 3
            "ALL" -> typeID = 0
        }
        val cursor = dbType.rawQuery(commandText, null)
        try {
            if (cursor!!.moveToFirst()) {
                var i = 0
                while (i < cursor.count) {
                    val newTitle = DocumentTitle()
                    newTitle.documentTypeID = cursor.getInt(
                        cursor.getColumnIndex(
                            KEY_DOCUMENT_TITLE_TYPE_ID_FK
                        )
                    )
                    newTitle.documentID = cursor.getInt(cursor.getColumnIndex(KEY_DOCUMENTTITLE_ID))
                    newTitle.documentName = cursor.getString(
                        cursor.getColumnIndex(
                            KEY_DOCUMENTTITLE_NAME
                        )
                    )
                    if (newTitle.documentTypeID === typeID || typeID == 0) documentTitles.add(
                        newTitle
                    ) else {
                        i++
                        cursor.moveToNext()
                        continue
                    }
                    i++
                    cursor.moveToNext()
                }
            }
            cursor.close()
        } catch (e: Exception) {
            Log.d("Error", "Error Retrieving the entries from DocumentTitle Table")
        } finally {
            if (cursor != null && !cursor.isClosed) cursor.close()
            run { cursor!!.close() }
        }
        return documentTitles
    }

    //Fetch documents for processing, Document Search Related
    fun getAllDocuments(
        fileString: String,
        fileName: String?,
        docID: Int,
        allDocs: Boolean?,
        dbList: SQLiteDatabase,
        access: String?,
        docList: DocumentList?,
        context: Context?
    ): DocumentList? {
        var docList = docList
        val cursor: Cursor?
        val documentList = DocumentList()
        val commandText: String
        val docCommandText: String
        val accessString: String
        var documentIndex = 0
        accessString = DataTableAccess(access)
        //SQL Query Execution
//Identify what needs selected
        commandText = if (docID != 0) {
            TableAccess(fileString)
        } else fileString
        docCommandText = accessString
        //Add entries to Document List
        val docTitle = ArrayList<DocumentTitle>()
        val docIds = ArrayList<Int?>()
        val docTitleList = ArrayList<String?>()
        //CommandText Uses Table Access For Document Titles, doc uses DataTableAccess
        cursor = dbList.rawQuery(commandText, null)
        //cursor1 =
        try {
            if (cursor.moveToFirst()) {
                var i = 0
                while (i < cursor.count) {
                    val addDoc = DocumentTitle()
                    addDoc.documentName = cursor.getString(
                        cursor.getColumnIndex(
                            KEY_DOCUMENTTITLE_NAME
                        )
                    )
                    addDoc.documentTypeID = cursor.getInt(
                        cursor.getColumnIndex(
                            KEY_DOCUMENT_TITLE_TYPE_ID_FK
                        )
                    )
                    addDoc.documentID = cursor.getInt(cursor.getColumnIndex(KEY_DOCUMENTTITLE_ID))
                    docTitle.add(addDoc)
                    i++
                    cursor.moveToNext()
                }
            }
            cursor.close()
            for (y in docTitle.indices) {
                docIds.add(docTitle[y].documentID)
                docTitleList.add(docTitle[y].documentName)
            }
            val cursor1: Cursor
            cursor1 = dbList.rawQuery(docCommandText, null)
            Log.d(
                "Size of Query List", cursor1.getColumnIndexOrThrow(KEY_DOCDETAILID_ID)
                    .toString()
            )
            if (cursor1.moveToFirst()) {
                var i = 0
                while (i < cursor1.count) {
                    val doc = Document()
                    doc.chName = cursor1.getString(cursor1.getColumnIndex(KEY_CHAPTER_NAME))
                    doc.chNumber = cursor1.getInt(cursor1.getColumnIndex(KEY_DOC_INDEX_NUM))
                    doc.documentID = cursor1.getInt(cursor1.getColumnIndex(KEY_DOCUMENT_ID_FK))
                    run {
                        documentIndex = docIds.indexOf(doc.documentID)
                        Log.d("DocumentIndex", "Document Index is $documentIndex")
                        if (documentIndex > -1) doc.documentName =
                            docTitle[documentIndex].documentName
                        Log.d("DocumentTitle", "Document Title: " + doc.documentName)
                    }
                    doc.chName = cursor1.getString(cursor1.getColumnIndex(KEY_CHAPTER_NAME))
                    doc.proofs = cursor1.getString(cursor1.getColumnIndex(KEY_CHAPTER_PROOFS))
                    doc.docDetailID = cursor1.getInt(cursor1.getColumnIndex(KEY_DOCDETAILID_ID))
                    doc.documentText = cursor1.getString(cursor1.getColumnIndex(KEY_CHAPTER_TEXT))
                    doc.tags = cursor1.getString(cursor1.getColumnIndex(KEY_DOCUMENT_TAGS))
                    doc.matches = cursor1.getInt(cursor1.getColumnIndex(KEY_MATCHES))
                    documentList.add(doc)
                    i++
                    cursor1.moveToNext()
                }
            }
            cursor1.close()
            Log.d("Size of List", documentList.size.toString())
            docList = documentList
            return docList
        } catch (e: Exception) {
            Log.d("ERROR", e.message!!)
        } finally {
            if (cursor != null && !cursor.isClosed) cursor.close()
            run {
                cursor.close()
                return docList
            }
        }
    }

    //Document search related
    fun LayoutString(docType: String?): String {
        return String.format(
            "SELECT DocumentType.*, DocumentTitle.* FROM DocumentTitle NATURAL JOIN DocumentType WHERE DocumentTitle.DocumentTypeID = DocumentType.DocumentTypeId AND DocumentType.DocumentTypeName = '%s'",
            docType
        )
    }

    //Document Search Related, Document search
    fun DataTableAccess(documentName: String?): String {
        return String.format(
            "SELECT Documenttitle.documentName, " +
                    "document.*, documenttitle.documentid FROM " +
                    "documentTitle NATURAL JOIN document WHERE document.DocumentID = DocumentTitle.DocumentID %s",
            documentName
        )
    }

    // Chapter Retrieval and filtering. Simply add this + extra filters on string to use query. Bible Reader Related
    fun BookChapterNumberAccess(bookName: String?): String {
        return String.format(
            "SELECT BibleTranslations.*, BibleBooks.*, BibleContents.* from BibleContents NATURAL JOIN BibleBooks Natural Join BibleTranslations " +
                    "Where BibleContents.BookNumber = BibleBooks.BookID And BibleContents.TranslationID = BibleTranslations.TranslationID And BibleBooks.BookName = '%s'",
            bookName
        )

    }

    //Verse Retrieval. Adds on to BCNA method above for verse retrieval
    fun BookChapterVerseAccess(chapNum: Int?, bookName: String?): String {
        return BookChapterNumberAccess(bookName) + " And BibleContents.ChapterNum = " + chapNum
    }

    fun VerseAccess(verseNum: Int?, chapterNum: Int?, bookName: String?): String {
        if (verseNum!! > 0)
            return BookChapterVerseAccess(
                chapterNum,
                bookName
            ) + "    And BibleContents.VerseNumber = " + verseNum
        else
            return BookChapterVerseAccess(chapterNum, bookName)
    }

    //Bible Book Retrieval , Bible Reader Related Method
    fun BibleBookAccess(): String {
        return "SELECT * FROM BibleBooks"
    }

    //Document Search Related, Document Title Related
    fun TableAccess(tableName: String): String {
        val table1: String
        table1 = if (tableName !== "") String.format(
            "SELECT documenttitle.* FROM documenttitle WHERE %s",
            tableName
        ) else "Select documenttype.*,documenttitle.* from documenttitle natural join documenttype where documenttitle.documenttypeid = documenttype.documenttypeid"
        return table1
    }

    companion object {
        //DATABASE INFORMATION
        private const val DATABASE_NAME = "confessionSearchDB.sqlite3"
        private const val DATABASE_VERSION = 4
        //  private val DATABASE_PATH = Environment.DIRECTORY_DOWNLOADS + "/" + DATABASE_NAME

        //TABLE INFO
        private const val TABLE_DOCUMENT = "Document"
        private const val TABLE_DOCUMENTTYPE = "DocumentType"
        private const val TABLE_DOCUMENTTITLE = "DocumentTitle"
        private const val TABLE_BIBLETRANSLATION = "BibleTranslations"
        private const val TABLE_BIBLECONTENTS = "BibleContents"
        private const val TABLE_BIBLE_BOOKS = "BibleBooks"
        var db: SQLiteDatabase? = null

        //DOCUMENT TABLE COLUMNS
        private const val KEY_DOCDETAILID_ID = "DocDetailID"
        private const val KEY_DOCUMENT_ID_FK = "DocumentID"
        private const val KEY_DOC_INDEX_NUM = "DocIndexNum"
        private const val KEY_CHAPTER_NAME = "ChName"
        private const val KEY_CHAPTER_TEXT = "ChText"
        private const val KEY_CHAPTER_PROOFS = "ChProofs"
        private const val KEY_DOCUMENT_TAGS = "ChTags"
        private const val KEY_MATCHES = "ChMatches"

        //DOCUMENT TITLE TABLE
        private const val KEY_DOCUMENTTITLE_ID = "DocumentID"
        private const val KEY_DOCUMENTTITLE_NAME = "DocumentName"
        const val KEY_DOCUMENT_TITLE_TYPE_ID_FK = "DocumentTypeID"

        //DOCUMENT TYPE TABLE
        private const val KEY_DOCUMENT_TYPE_ID = "DocumentTypeID"
        private const val KEY_DOCUMENT_TYPE_NAME = "DocumentTypeName"

        //BIBLE TRANSLATION TABLE
        private const val KEY_BIBLE_TRANSLATION_ID = "TranslationID"
        private const val KEY_BIBLE_TRANSLATION_TITLE = "TranslationTitle"
        private const val KEY_BIBLE_TRANSLATION_ABBREV = "TranslationAbbrev"

        //BibleBooksTable
        private const val KEY_BIBLE_BOOKS_ID = "BookID"
        private const val KEY_BIBLE_BOOKS_BOOKNAME = "BookName"

        //BIBLE CONTENTS TABLE
        private const val KEY_BIBLE_CONTENTS_ENTRY_ID = "EntryID"
        private const val KEY_BIBLE_CONTENTS_TRANSLATION_ID_FK = "TranslationID"
        private const val KEY_BIBLE_CONTENTS_BOOKNUM_FK = "BookNum"
        private const val KEY_BIBLE_CONTENTS_CHAPTERNUMBER = "ChapterNum"
        private const val KEY_BIBLE_CONTENTS_VERSENUMBER = "VerseNumber"
        private const val KEY_BIBLE_CONTENTS_VERSETEXT = "VerseText"
        private var sInstance: DocumentDBClassHelper? = null

        @Synchronized
        fun getInstance(context: Context): DocumentDBClassHelper? {
            if (sInstance == null) {
                sInstance = DocumentDBClassHelper(context.applicationContext)
            }
            return sInstance
        }
    }
}
