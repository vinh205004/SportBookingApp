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

    // 1. Lấy thông tin User theo ID
    public User getUserById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        User user = null;

        // Truy vấn lấy user theo ID
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_USER +
                        " WHERE " + DatabaseHelper.COL_USER_ID + " = ?",
                new String[]{String.valueOf(id)});

        if (cursor != null && cursor.moveToFirst()) {
            // Lấy index của các cột để tránh lỗi nếu thứ tự cột thay đổi
            int idIndex = cursor.getColumnIndex(DatabaseHelper.COL_USER_ID);

            int nameIndex = cursor.getColumnIndex(DatabaseHelper.COL_USER_NAME);

            int phoneIndex = cursor.getColumnIndex(DatabaseHelper.COL_USER_PHONE);
            int emailIndex = cursor.getColumnIndex(DatabaseHelper.COL_USER_EMAIL); // <-- Quan trọng: Phải lấy cột Email

            // Kiểm tra xem cột có tồn tại không
            if (idIndex != -1 && nameIndex != -1) {
                int _id = cursor.getInt(idIndex);
                String name = cursor.getString(nameIndex);
                String phone = (phoneIndex != -1) ? cursor.getString(phoneIndex) : "";

                String email = (emailIndex != -1) ? cursor.getString(emailIndex) : "";

                // Tạo object User
                user = new User(_id, name, phone, email);
            }
            cursor.close();
        }
        return user;
    }

    // 2. Cập nhật thông tin
    public boolean updateUser(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Kiểm tra tên cột bên DatabaseHelper cho khớp nhé
        values.put(DatabaseHelper.COL_USER_NAME, user.getFullName());
        values.put(DatabaseHelper.COL_USER_PHONE, user.getPhone());
        values.put(DatabaseHelper.COL_USER_EMAIL, user.getEmail());

        int rows = db.update(DatabaseHelper.TABLE_USER, values,
                DatabaseHelper.COL_USER_ID + " = ?",
                new String[]{String.valueOf(user.getId())});

        return rows > 0;
    }

    // 3. Tạo user mặc định
    public void createDefaultUserIfNotExist() {
        if (getUserById(1) == null) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COL_USER_ID, 1);
            values.put(DatabaseHelper.COL_USER_NAME, "Người dùng mới");
            values.put(DatabaseHelper.COL_USER_PHONE, "0000000000");
            values.put(DatabaseHelper.COL_USER_EMAIL, "email@example.com");

            db.insert(DatabaseHelper.TABLE_USER, null, values);
        }
    }
}