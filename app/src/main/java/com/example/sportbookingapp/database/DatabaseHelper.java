package com.example.sportbookingapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Tên DB
    private static final String DATABASE_NAME = "SportBooking.db";
    private static final int DATABASE_VERSION = 1;

    // Bảng USER
    public static final String TABLE_USER = "users";
    public static final String COL_USER_ID = "id";
    public static final String COL_USER_NAME = "fullname";
    public static final String COL_USER_PHONE = "phone";

    // Bảng COURT (Sân)
    public static final String TABLE_COURT = "courts";
    public static final String COL_COURT_ID = "id";
    public static final String COL_COURT_NAME = "name";
    public static final String COL_COURT_ADDRESS = "address";
    public static final String COL_COURT_TYPE = "type";
    public static final String COL_COURT_PRICE = "price";
    public static final String COL_COURT_IMAGE = "image_name";
    public static final String COL_COURT_RATING = "rating";
    public static final String COL_COURT_FACILITIES = "facilities";
    public static final String COL_COURT_LAT = "latitude";
    public static final String COL_COURT_LNG = "longitude";

    // Bảng BOOKING (Lịch đặt)
    public static final String TABLE_BOOKING = "bookings";
    public static final String COL_BOOKING_ID = "id";
    public static final String COL_BOOKING_USER_ID = "user_id";
    public static final String COL_BOOKING_COURT_ID = "court_id";
    public static final String COL_BOOKING_DATE = "date";
    public static final String COL_BOOKING_START = "start_time";
    public static final String COL_BOOKING_END = "end_time";
    public static final String COL_BOOKING_TOTAL = "total_price";
    public static final String COL_BOOKING_STATUS = "status";
    public static final String COL_BOOKING_PAYMENT = "payment_method";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 1. Tạo bảng User
        String createUser = "CREATE TABLE " + TABLE_USER + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USER_NAME + " TEXT, " +
                COL_USER_PHONE + " TEXT)";
        db.execSQL(createUser);

        // 2. Tạo bảng Court
        String createCourt = "CREATE TABLE " + TABLE_COURT + " (" +
                COL_COURT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_COURT_NAME + " TEXT, " +
                COL_COURT_ADDRESS + " TEXT, " +
                COL_COURT_TYPE + " TEXT, " +
                COL_COURT_PRICE + " REAL, " +
                COL_COURT_IMAGE + " TEXT, " +
                COL_COURT_RATING + " REAL, " +
                COL_COURT_FACILITIES + " TEXT, " +
                COL_COURT_LAT + " REAL, " +
                COL_COURT_LNG + " REAL)";
        db.execSQL(createCourt);

        // 3. Tạo bảng Booking
        String createBooking = "CREATE TABLE " + TABLE_BOOKING + " (" +
                COL_BOOKING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_BOOKING_USER_ID + " INTEGER, " +
                COL_BOOKING_COURT_ID + " INTEGER, " +
                "court_name TEXT, " +
                "court_image TEXT, " +
                COL_BOOKING_DATE + " TEXT, " +
                COL_BOOKING_START + " TEXT, " +
                COL_BOOKING_END + " TEXT, " +
                COL_BOOKING_TOTAL + " REAL, " +
                COL_BOOKING_STATUS + " TEXT, " +
                COL_BOOKING_PAYMENT + " TEXT)";
        db.execSQL(createBooking);

        // Tạo sẵn user mặc định
        db.execSQL("INSERT INTO " + TABLE_USER + " (fullname, phone) VALUES ('Nguyen Van A', '0987654321')");

        db.execSQL("INSERT INTO " + TABLE_COURT + " VALUES (null, 'Sân bóng Thanh Xuân', '123 Thanh Xuân, Hà Nội', 'BongDa', 150000, 'san_bong_1', 4.5, 'Wifi,Parking,WC', 21.0000, 105.8000)");
        db.execSQL("INSERT INTO " + TABLE_COURT + " VALUES (null, 'Sân Tennis Cầu Giấy', 'Cầu Giấy, Hà Nội', 'Tennis', 200000, 'san_tennis_1', 4.8, 'Parking,Store', 21.0300, 105.7800)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKING);
        onCreate(db);
    }
}