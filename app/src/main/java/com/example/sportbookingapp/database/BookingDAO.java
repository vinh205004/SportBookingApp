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

    public long addBooking(Booking booking) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

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
}