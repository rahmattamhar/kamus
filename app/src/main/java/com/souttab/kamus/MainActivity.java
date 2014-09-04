package com.souttab.kamus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.souttab.kamus.database.CopyDatabase;
import com.souttab.kamus.database.DatabaseUtil;

import java.sql.SQLException;
import java.util.ArrayList;


public class MainActivity extends Activity {

    private Button buttonCari;
    private AutoCompleteTextView autoCompleteTextView;
    private TextView textViewIstilah, textViewPengertian, textViewKosong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Copy Database yang ada di folder assets
        // ke dalam handphone yang menginstall
        CopyDatabase copyDatabase = new CopyDatabase(getApplicationContext());
        try {
            copyDatabase.createdDatabase();
            copyDatabase.openDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        final DatabaseUtil databaseUtil = new DatabaseUtil(getApplicationContext());

        // reference variable
        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        textViewIstilah = (TextView) findViewById(R.id.textViewIstilah);
        textViewPengertian = (TextView) findViewById(R.id.textViewPengertian);
        textViewKosong = (TextView) findViewById(R.id.textViewKosong);
        buttonCari = (Button) findViewById(R.id.buttonSearch);


        // untuk set autocomplete
        // buat arraylist
        ArrayList<String> dataList = new ArrayList<String>();
        // masukan data  yang ada di database ke arraylist
        for (Kamus kamus : databaseUtil.listKamus()) {
            dataList.add(kamus.getIstilah());
        }

        ArrayAdapter stringArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
        // set ke adapternya
        autoCompleteTextView.setAdapter(stringArrayAdapter);

        // button click cari
        buttonCari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // panggil method get kamus
                String toString = autoCompleteTextView.getText().toString();

                if (toString.length() == 1) {
                    textViewKosong.setText("huruf tidak diinjinkan, harus kata");
                } else if (!toString.isEmpty() && toString.length() >= 1) {
                    Kamus kamus = databaseUtil.getKamus(toString);
                    // check jika tidak null maka tampilkan hasilnya
                    if (kamus != null) {
                        textViewKosong.setVisibility(View.GONE);
                        textViewIstilah.setVisibility(View.VISIBLE);
                        textViewPengertian.setVisibility(View.VISIBLE);
                        // tampilkan istilah yang didapatkan
                        textViewIstilah.setText(kamus.getIstilah());
                        // tampilkan pengertian yang didapatkan
                        textViewPengertian.setText(Html.fromHtml(
                                "<p align=\"justify\"> "+ kamus.getPenjelasan() + "</p>"
                        ));
                    } else {
                        // jika tidak ada data maka
                        // kosongkan tampilan
                        textViewKosong.setVisibility(View.VISIBLE);
                        textViewKosong.setText("Kata " + toString + " tidak ditemukan");
                        // kosongkan text istilah
                        textViewIstilah.setVisibility(View.GONE);
                        // kosongkan text pengertian
                        textViewPengertian.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Tidak boleh kosong", Toast.LENGTH_SHORT).show();
                    textViewIstilah.setVisibility(View.GONE);
                    // kosongkan text pengertian
                    textViewPengertian.setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.about) {
            // panggil class about
            Intent layoutPanggil = new Intent(this, AboutAcvitity.class);
            // panggil sekarang
            startActivity(layoutPanggil);
        }
        return super.onOptionsItemSelected(item);
    }
}
