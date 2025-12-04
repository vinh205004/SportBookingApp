package com.example.sportbookingapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.sportbookingapp.model.User;

public class UserDAO {
    private DatabaseHelper dbHelper;

    public UserDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public User getUser() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        User user = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_USER + " WHERE id = 1", null);

        if (cursor.moveToFirst()) {
            user = new User(
                    cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_PHONE)),
                    "user@example.com"
            );
        }
        cursor.close();
        return user;
    }

    // 2. Cập nhật thông tin
    public boolean updateUser(String newName, String newPhone) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.COL_USER_NAME, newName);
        values.put(DatabaseHelper.COL_USER_PHONE, newPhone);

        int rows = db.update(DatabaseHelper.TABLE_USER, values, "id = ?", new String[]{"1"});
        return rows > 0;
    }
}