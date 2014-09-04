package com.souttab.kamus.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.souttab.kamus.Kamus;

import java.util.ArrayList;
import java.util.List;

public class DatabaseUtil {

    private static final String _TABLE_KAMUS = "table_jaringan_komputer";

    private SQLiteDatabase database;
    private CopyDatabase copyDatabase;

    String istilah, pengertian;

    public DatabaseUtil(Context context) {
        copyDatabase = new CopyDatabase(context);
    }

    public void open() { // open database and allow to write data
        database = copyDatabase.getReadableDatabase();
    }

    public void close() { // close database connection
        if (database != null) {
            database.close();
        }
    }

    // Untuk ambil semua data yang ada di database
    // dan digunakan untuk autocomplete
    public List<Kamus> listKamus() {
        List<Kamus> listKamus = new ArrayList<Kamus>();
        String query = "SELECT * FROM " + _TABLE_KAMUS;

        open();

        Cursor cursor = database.rawQuery(query, null);

        try {
            if (cursor.moveToFirst()) {
                Kamus entity;
                do {
                    entity = new Kamus();
                    istilah = cursor.getString(cursor.getColumnIndex("istilah"));
                    pengertian = cursor.getString(cursor.getColumnIndex("pengertian"));

                    entity.setIstilah(istilah);
                    entity.setPenjelasan(pengertian);

                    listKamus.add(entity);
                } while (cursor.moveToNext());
            }
        } finally {
            close();
        }
        return listKamus;
    }

    /*
    * Untuk ambil data berdasarkan inputan user
    * jika ada tidak ditemukan makan
    * akan return value null
    *
    * return kamus jika ada data yang ditemukan
    * return null kalau tidak ada ditemukan data yang dimaksud
    */
    public Kamus getKamus(String istilah) {
        // buka koneksi ke database
        open();
        // set query ke database
        Cursor cursor = database.rawQuery("SELECT * FROM " + _TABLE_KAMUS + " WHERE istilah LIKE '" + istilah + "%'", null);

        Kamus kamus = null;

        try {
            // jika data tidak kosong maka ambil data dan masukkan ke dalan entitas
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    kamus = new Kamus();
                    kamus.setIstilah(cursor.getString(cursor.getColumnIndex("istilah")));
                    kamus.setPenjelasan(cursor.getString(cursor.getColumnIndex("pengertian")));
                } while (cursor.moveToNext());
            }
        } finally {
            // tutup koneksi ke database
            close();
        }
        return kamus;
    }
}
