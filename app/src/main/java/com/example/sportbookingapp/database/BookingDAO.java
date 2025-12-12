package com.example.sportbookingapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.sportbookingapp.model.Booking;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {
    private DatabaseHelper dbHelper;

    public BookingDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // Logic kiểm tra trùng lịch
    public boolean isTimeSlotBooked(int courtId, String date, String newStart, String newEnd) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT * FROM " + DatabaseHelper.TABLE_BOOKING +
                " WHERE " + DatabaseHelper.COL_BOOKING_COURT_ID + " = ?" +
                " AND " + DatabaseHelper.COL_BOOKING_DATE + " = ?" +
                " AND " + DatabaseHelper.COL_BOOKING_START + " < ?" +
                " AND " + DatabaseHelper.COL_BOOKING_END + " > ?" +
                " AND " + DatabaseHelper.COL_BOOKING_STATUS + " != 'Cancelled'";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(courtId), date, newEnd, newStart});
        boolean isBooked = cursor.getCount() > 0;
        cursor.close();
        return isBooked;
    }

    // Thêm booking mới
    public long addBooking(Booking booking) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_BOOKING_IS_DISCOUNTED, booking.getIsDiscounted());
        values.put(DatabaseHelper.COL_BOOKING_USER_ID, booking.getUserId());
        values.put(DatabaseHelper.COL_BOOKING_COURT_ID, booking.getCourtId());
        values.put("court_name", booking.getCourtName());
        values.put("court_image", booking.getCourtImage());
        values.put(DatabaseHelper.COL_BOOKING_DATE, booking.getDate());
        values.put(DatabaseHelper.COL_BOOKING_START, booking.getStartTime());
        values.put(DatabaseHelper.COL_BOOKING_END, booking.getEndTime());
        values.put(DatabaseHelper.COL_BOOKING_TOTAL, booking.getTotalPrice());
        values.put(DatabaseHelper.COL_BOOKING_STATUS, booking.getStatus());
        values.put(DatabaseHelper.COL_BOOKING_PAYMENT, booking.getPaymentMethod());

        return db.insert(DatabaseHelper.TABLE_BOOKING, null, values);
    }

    // Lấy danh sách booking theo user
    public List<Booking> getBookingsByUser(int userId) {
        List<Booking> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT * FROM " + DatabaseHelper.TABLE_BOOKING +
                " WHERE " + DatabaseHelper.COL_BOOKING_USER_ID + " = ?" +
                " ORDER BY " + DatabaseHelper.COL_BOOKING_ID + " DESC";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                list.add(new Booking(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BOOKING_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BOOKING_USER_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BOOKING_COURT_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow("court_name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("court_image")),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BOOKING_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BOOKING_START)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BOOKING_END)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BOOKING_TOTAL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BOOKING_STATUS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BOOKING_PAYMENT))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    // --- Thêm phương thức cập nhật trạng thái booking ---
    public int updateBookingStatus(int bookingId, String newStatus) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_BOOKING_STATUS, newStatus);
        return db.update(DatabaseHelper.TABLE_BOOKING, values,
                DatabaseHelper.COL_BOOKING_ID + " = ?",
                new String[]{String.valueOf(bookingId)});
    }
    // Hàm đếm số lượng đơn đã đặt (Chỉ tính CONFIRMED, bỏ qua CANCELLED)
    public int getBookingCount(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int count = 0;

        // Đếm tất cả đơn của user này mà trạng thái KHÁC 'CANCELLED'
        String sql = "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_BOOKING +
                " WHERE " + DatabaseHelper.COL_BOOKING_USER_ID + " = ?" +
                " AND " + DatabaseHelper.COL_BOOKING_STATUS + " != 'CANCELLED'";

        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }
    // 3. Hàm đếm số đơn ĐÃ ĐƯỢC GIẢM GIÁ (Active)
    public int countDiscountedBookings(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_BOOKING +
                " WHERE " + DatabaseHelper.COL_BOOKING_USER_ID + " = ?" +
                " AND " + DatabaseHelper.COL_BOOKING_STATUS + " != 'CANCELLED'" +
                " AND " + DatabaseHelper.COL_BOOKING_IS_DISCOUNTED + " = 1"; // Chỉ đếm đơn có cờ = 1
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(userId)});
        int count = 0;
        if (cursor.moveToFirst()) count = cursor.getInt(0);
        cursor.close();
        return count;
    }
}
