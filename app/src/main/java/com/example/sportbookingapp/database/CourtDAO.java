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

    // 1. Lấy danh sách toàn bộ sân (Cho Map và List)
    public List<Court> getAllCourts() {
        List<Court> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Query tất cả
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_COURT, null);

        if (cursor.moveToFirst()) {
            do {
                list.add(cursorToCourt(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    // 2. Tìm kiếm sân theo tên (Cho thanh search)
    public List<Court> searchCourts(String keyword) {
        List<Court> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String sql = "SELECT * FROM " + DatabaseHelper.TABLE_COURT + " WHERE " + DatabaseHelper.COL_COURT_NAME + " LIKE ?";
        Cursor cursor = db.rawQuery(sql, new String[]{"%" + keyword + "%"});

        if (cursor.moveToFirst()) {
            do {
                list.add(cursorToCourt(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    // 3. Lấy chi tiết 1 sân theo ID (Cho màn hình Detail)
    public Court getCourtById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_COURT + " WHERE id = ?", new String[]{String.valueOf(id)});

        Court court = null;
        if (cursor.moveToFirst()) {
            court = cursorToCourt(cursor);
        }
        cursor.close();
        return court;
    }

    // Hàm phụ: Chuyển dòng dữ liệu thành Object Court
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
                cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_COURT_LNG))
        );
    }
}
