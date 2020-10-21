package com.confessionsearch.release1;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import java.io.File;


class DatabaseContext extends ContextWrapper {

    private static final String DEBUG_CONTEXT = "DatabaseContext";

    public DatabaseContext(Context base) {
        super(base);
    }

    @Override
    public File getDatabasePath(String name) {
        File sdcard = Environment.getExternalStorageDirectory();//getApplicationContext().getDatabasePath(name);// "assets/database/"+name;//Environment.getExternalStorageDirectory();
      Log.d("Fix",sdcard.getAbsolutePath());
       // Toast.makeText(this,Environment.getExternalStorageDirectory().getAbsolutePath(),Toast.LENGTH_LONG).show();
        String dbfile =sdcard.getAbsolutePath() +  File.separator +"Downloads/"+"Database/"+ name;// "assets/database/"+name;
        if (!dbfile.endsWith(".db")) {
            dbfile += ".db";
        }

        File result = new File(dbfile);

        if (!result.getParentFile().exists()) {
            result.getParentFile().mkdirs();
        }
else result.setReadable(true);

        if (Log.isLoggable(DEBUG_CONTEXT, Log.WARN)) {
            Log.w(DEBUG_CONTEXT, "getDatabasePath(" + name + ") = " + result.getAbsolutePath());
        }

        return result;
    }
    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
        return openOrCreateDatabase(name,mode, factory);
    }

}
