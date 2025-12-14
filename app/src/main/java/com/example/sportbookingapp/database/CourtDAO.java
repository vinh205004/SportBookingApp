package com.example.sportbookingapp.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.sportbookingapp.model.Court;
import java.util.ArrayList;
import java.util.List;

public class CourtDAO {
    private DatabaseHelper dbHelper;

    public CourtDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // 1. Lấy danh sách toàn bộ sân
    public List<Court> getAllCourts() {
        List<Court> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String sql = "SELECT * FROM " + DatabaseHelper.TABLE_COURT +
                " ORDER BY " + DatabaseHelper.COL_COURT_RATING + " DESC";

        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                list.add(cursorToCourt(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    // 2. Tìm kiếm sân (Tìm cả Tên, Địa chỉ, Loại sân)
    public List<Court> searchCourts(String keyword) {
        List<Court> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String sql = "SELECT * FROM " + DatabaseHelper.TABLE_COURT +
                " WHERE " + DatabaseHelper.COL_COURT_NAME + " LIKE ?" +
                " OR " + DatabaseHelper.COL_COURT_ADDRESS + " LIKE ?" +
                " OR " + DatabaseHelper.COL_COURT_TYPE + " LIKE ?";

        String key = "%" + keyword + "%";
        Cursor cursor = db.rawQuery(sql, new String[]{key, key, key});

        if (cursor.moveToFirst()) {
            do {
                list.add(cursorToCourt(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    // 3. Lấy chi tiết 1 sân theo ID
    public Court getCourtById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT * FROM " + DatabaseHelper.TABLE_COURT +
                " WHERE " + DatabaseHelper.COL_COURT_ID + " = ?";

        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(id)});

        Court court = null;
        if (cursor.moveToFirst()) {
            court = cursorToCourt(cursor);
        }
        cursor.close();
        return court;
    }

    private Court cursorToCourt(Cursor cursor) {
        return new Court(
                cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_COURT_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_COURT_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_COURT_ADDRESS)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_COURT_TYPE)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_COURT_PRICE)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_COURT_IMAGE)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_COURT_RATING)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_COURT_FACILITIES)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_COURT_LAT)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_COURT_LNG)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_COURT_DESC)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_COURT_OPEN)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_COURT_CLOSE))
        );
    }
}