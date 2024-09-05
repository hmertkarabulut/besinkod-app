package com.mertkar.besinkod;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class ECodeDataSource {

    private SQLiteDatabase database;
    private final DatabaseHelper dbHelper;

    public ECodeDataSource(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public String getDetailsForCodeOrName(String input) {
        String details = null;
        String lowerInput = input.toLowerCase(); // Kullanıcı girişini küçük harfe dönüştür

        Cursor cursor = database.rawQuery(
                "SELECT " +
                        DatabaseHelper.COLUMN_EKOD + ", " +
                        DatabaseHelper.COLUMN_KIMYASAL_ADI + ", " +
                        DatabaseHelper.COLUMN_ACIKLAMA + ", " +
                        DatabaseHelper.COLUMN_HELAL_HARAM + " " +
                        "FROM " + DatabaseHelper.TABLE_NAME + " " +
                        "WHERE LOWER(" + DatabaseHelper.COLUMN_EKOD + ") = ? OR LOWER(" + DatabaseHelper.COLUMN_KIMYASAL_ADI + ") = ?",
                new String[]{lowerInput, lowerInput});

        if (cursor != null && cursor.moveToFirst()) {
            String ekod = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EKOD));
            String kimyasalAdi = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_KIMYASAL_ADI));
            String aciklama = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACIKLAMA));
            String helalHaram = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HELAL_HARAM));
            details = "E Kodu: " + ekod + "\nKimyasal Adı: " + kimyasalAdi + "\nAçıklama: " + aciklama + "\nHelal/Haram: " + helalHaram;
            cursor.close();
        }

        return details;
    }
    public List<String> getAllCodesAndNames() {
        List<String> codesAndNames = new ArrayList<>();
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME,
                new String[]{DatabaseHelper.COLUMN_KIMYASAL_ADI},
                null, null, null, null, null);

        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_KIMYASAL_ADI));
            codesAndNames.add(name);
        }
        cursor.close();
        return codesAndNames;
    }
}

