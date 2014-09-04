package com.souttab.kamus.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

public class CopyDatabase extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "KamusJaringan";
    private Context context;
    private static String DATABASE_PATH = null;
    private SQLiteDatabase sqLiteDatabase;

    public CopyDatabase(Context mContext) {
        super(mContext, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = mContext;
        DATABASE_PATH = "/data/data/"+context.getPackageName()+"/databases/";
    }

    void copyDatabase() throws IOException {
        // open the local db as the input stream
        InputStream inputStream = context.getAssets().open(DATABASE_NAME);
        // path to the just created empty db
        String pathOutputFile = DATABASE_PATH + DATABASE_NAME;
        // open the empty db as the output stream
        OutputStream outputStream = new FileOutputStream(pathOutputFile);
        // transfer byte from the input file to the output file
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) >0 ){
            outputStream.write(buffer, 0, length);
        }

        // close the streams
        outputStream.flush();
        outputStream.close();
        inputStream.close();
    }

    public void openDatabase() throws SQLException {
        String path = DATABASE_PATH + DATABASE_NAME;
        sqLiteDatabase = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
    }

    public synchronized void close() {
        if (sqLiteDatabase != null) sqLiteDatabase.close();

        super.close();
    }

    boolean checkDatabase() {
        SQLiteDatabase database = null;
        try {
            String path = DATABASE_PATH + DATABASE_NAME;
            database = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            // database does't exist yet
            e.getMessage();
        }

        if (database != null) {
            database.close();
        }

        return database != null ? true : false;
    }

    public void createdDatabase() {
        boolean exist = checkDatabase();
        if (exist) {
            // do nothing because database already exist
        } else {
            this.getReadableDatabase();
            try {
                copyDatabase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
