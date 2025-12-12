package com.example.sportbookingapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Tên DB
    private static final String DATABASE_NAME = "SportBooking.db";
    private static final int DATABASE_VERSION = 2;

    // Bảng USER
    public static final String TABLE_USER = "users";
    public static final String COL_USER_ID = "id";
    public static final String COL_USER_NAME = "fullname";
    public static final String COL_USER_PHONE = "phone";
    public static final String COL_USER_EMAIL = "email";

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
    // Thêm 3 cột mới cho đúng thiết kế
    public static final String COL_COURT_DESC = "description";
    public static final String COL_COURT_OPEN = "open_time";
    public static final String COL_COURT_CLOSE = "close_time";

    // Bảng BOOKING (Lịch đặt)
    public static final String TABLE_BOOKING = "bookings";
    public static final String COL_BOOKING_ID = "id";
    public static final String COL_BOOKING_USER_ID = "user_id";
    public static final String COL_BOOKING_COURT_ID = "court_id";
    public static final String COL_BOOKING_COURT_NAME_SNAPSHOT = "court_name";
    public static final String COL_BOOKING_COURT_IMAGE_SNAPSHOT = "court_image";
    public static final String COL_BOOKING_DATE = "date";
    public static final String COL_BOOKING_START = "start_time";
    public static final String COL_BOOKING_END = "end_time";
    public static final String COL_BOOKING_TOTAL = "total_price";
    public static final String COL_BOOKING_STATUS = "status";
    public static final String COL_BOOKING_PAYMENT = "payment_method";
    public static final String COL_BOOKING_IS_DISCOUNTED = "is_discounted";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 1. Tạo bảng User
        String createUser = "CREATE TABLE " + TABLE_USER + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USER_NAME + " TEXT, " +
                COL_USER_PHONE + " TEXT, " +
                COL_USER_EMAIL + " TEXT)";
        db.execSQL(createUser);

        // 2. Tạo bảng Court (Đã thêm cột mới)
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
                COL_COURT_LNG + " REAL, " +
                COL_COURT_DESC + " TEXT, " +
                COL_COURT_OPEN + " TEXT, " +
                COL_COURT_CLOSE + " TEXT)";
        db.execSQL(createCourt);

        // 3. Tạo bảng Booking
        String createBooking = "CREATE TABLE " + TABLE_BOOKING + " (" +
                COL_BOOKING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_BOOKING_USER_ID + " INTEGER, " +
                COL_BOOKING_COURT_ID + " INTEGER, " +
                COL_BOOKING_COURT_NAME_SNAPSHOT + " TEXT, " +
                COL_BOOKING_COURT_IMAGE_SNAPSHOT + " TEXT, " +
                COL_BOOKING_DATE + " TEXT, " +
                COL_BOOKING_START + " TEXT, " +
                COL_BOOKING_END + " TEXT, " +
                COL_BOOKING_TOTAL + " REAL, " +
                COL_BOOKING_STATUS + " TEXT, " +
                COL_BOOKING_PAYMENT + " TEXT," +
                COL_BOOKING_IS_DISCOUNTED + " INTEGER DEFAULT 0)";
        db.execSQL(createBooking);

        // --- MOCK DATA (12 Sân) ---

        // User mặc định
        db.execSQL("INSERT INTO " + TABLE_USER + " ("+COL_USER_NAME+", "+COL_USER_PHONE+", "+COL_USER_EMAIL+") " +
                "VALUES ('Nguyen Van A', '0987654321', 'nguyenvana@gmail.com')");

        // Sân 1: Bóng đá Thanh Xuân
        String descTX = "Sân bóng đá cỏ nhân tạo chất lượng cao, nằm ngay trung tâm quận Thanh Xuân. Hệ thống chiếu sáng hiện đại, có chỗ để xe rộng rãi.";
        String insertSan1 = "INSERT INTO " + TABLE_COURT + " VALUES (" +
                "null, " +
                "'Sân bóng Thanh Xuân', " +
                "'123 Thanh Xuân, Hà Nội', " +
                "'BongDa', " +
                "150000, " +
                "'san_bong_1', " +
                "4.5, " +
                "'Wifi,Canteen,Parking,Shower', " +
                "21.0000, 105.8000, " +
                "'" + descTX + "', " +
                "'06:00', " +
                "'23:00')";
        db.execSQL(insertSan1);

        // Sân 2: Tennis Cầu Giấy
        String descCG = "Sân Tennis đạt chuẩn quốc tế, mặt sân cứng, độ nảy bóng tốt.";
        String insertSan2 = "INSERT INTO " + TABLE_COURT + " VALUES (" +
                "null, " +
                "'Sân Tennis Cầu Giấy', " +
                "'Cầu Giấy, Hà Nội', " +
                "'Tennis', " +
                "200000, " +
                "'san_tennis_1', " +
                "4.8, " +
                "'Parking,Store,Wifi', " +
                "21.0300, 105.7800, " +
                "'" + descCG + "', " +
                "'05:00', " +
                "'22:00')";
        db.execSQL(insertSan2);

        // Sân 3: Bắc Từ Liêm
        String descNTL = "Sân bóng đá 7 người tiêu chuẩn, mặt cỏ nhân tạo, hệ thống đèn LED tối tân.";
        String insertSan3 = "INSERT INTO " + TABLE_COURT + " VALUES (" +
                "null, " +
                "'Sân bóng Bắc Từ Liêm', " +
                "'136 Hồ Tùng Mậu, Bắc Từ Liêm, Hà Nội', " +
                "'BongDa', " +
                "160000, " +
                "'san_bong_ntl_1', " +
                "4.6, " +
                "'Parking,Wifi,Shower', " +
                "21.0450, 105.7700, " +
                "'" + descNTL + "', " +
                "'06:00', " +
                "'23:00')";
        db.execSQL(insertSan3);

        // Sân 4: Cầu Lông Hoài Đức
        String descHD = "Sân cầu lông mái che, sàn gỗ tiêu chuẩn thi đấu.";
        String insertSan4 = "INSERT INTO " + TABLE_COURT + " VALUES (" +
                "null, " +
                "'Sân Cầu Lông Hoài Đức', " +
                "'Hoài Đức, Hà Nội', " +
                "'CauLong', " +
                "70000, " +
                "'san_caulong_hd_1', " +
                "4.4, " +
                "'Parking,Store', " +
                "21.0500, 105.6800, " +
                "'" + descHD + "', " +
                "'07:00', " +
                "'22:00')";
        db.execSQL(insertSan4);

        // Sân 5: Tennis Tây Hồ
        String descTH = "Cụm sân tennis tiêu chuẩn ITF, mặt sân acrylic.";
        String insertSan5 = "INSERT INTO " + TABLE_COURT + " VALUES (" +
                "null, " +
                "'Sân Tennis Tây Hồ', " +
                "'Tây Hồ, Hà Nội', " +
                "'Tennis', " +
                "250000, " +
                "'san_tennis_th_1', " +
                "4.9, " +
                "'Parking,Store,Wifi', " +
                "21.0700, 105.8200, " +
                "'" + descTH + "', " +
                "'05:30', " +
                "'22:00')";
        db.execSQL(insertSan5);

        // Sân 6: Bóng Hà Đông
        String descHD2 = "Sân bóng đá mini 7 người, cỏ nhân tạo chất lượng.";
        String insertSan6 = "INSERT INTO " + TABLE_COURT + " VALUES (" +
                "null, " +
                "'Sân bóng Hà Đông', " +
                "'Hà Đông, Hà Nội', " +
                "'BongDa', " +
                "140000, " +
                "'san_bong_hd_2', " +
                "4.2, " +
                "'Parking,Wifi', " +
                "20.9800, 105.7800, " +
                "'" + descHD2 + "', " +
                "'06:00', " +
                "'23:00')";
        db.execSQL(insertSan6);

        // Sân 7: Mỹ Đình
        String descMD = "Mặt sân phẳng, bóng nảy chuẩn, phù hợp tổ chức giải.";
        String insertSan7 = "INSERT INTO " + TABLE_COURT + " VALUES (" +
                "null, " +
                "'Sân bóng Mỹ Đình', " +
                "'Sân vận động Mỹ Đình, Nam Từ Liêm, Hà Nội', " +
                "'BongDa', " +
                "180000, " +
                "'san_bong_md_1', " +
                "4.8, " +
                "'Parking,Wifi,Canteen,Shower', " +
                "21.0300, 105.7600, " +
                "'" + descMD + "', " +
                "'06:00', " +
                "'23:30')";
        db.execSQL(insertSan7);

        // Sân 8: Đông La
        String descDL = "Sân bóng cộng đồng, giá rẻ phù hợp sinh viên.";
        String insertSan8 = "INSERT INTO " + TABLE_COURT + " VALUES (" +
                "null, " +
                "'Sân bóng Đông La', " +
                "'Đông La, Hoài Đức, Hà Nội', " +
                "'BongDa', " +
                "90000, " +
                "'san_bong_dl_1', " +
                "4.0, " +
                "'Parking', " +
                "21.0455, 105.7120, " +
                "'" + descDL + "', " +
                "'06:00', " +
                "'22:00')";
        db.execSQL(insertSan8);

        // Sân 9: Cầu Lông Cổ Nhuế
        String descCN = "Sân cầu lông trong nhà, ánh sáng tiêu chuẩn.";
        String insertSan9 = "INSERT INTO " + TABLE_COURT + " VALUES (" +
                "null, " +
                "'Sân Cầu Lông Cổ Nhuế', " +
                "'Cổ Nhuế, Bắc Từ Liêm, Hà Nội', " +
                "'CauLong', " +
                "75000, " +
                "'san_caulong_cn_1', " +
                "4.5, " +
                "'Parking,Wifi', " +
                "21.0600, 105.7900, " +
                "'" + descCN + "', " +
                "'07:00', " +
                "'22:00')";
        db.execSQL(insertSan9);

        // Sân 10: Xuân Phương
        String descXP = "Sân rộng, phù hợp tổ chức giải phong trào.";
        String insertSan10 = "INSERT INTO " + TABLE_COURT + " VALUES (" +
                "null, " +
                "'Sân bóng Xuân Phương', " +
                "'Xuân Phương, Nam Từ Liêm, Hà Nội', " +
                "'BongDa', " +
                "150000, " +
                "'san_bong_xp_1', " +
                "4.3, " +
                "'Parking,Wifi', " +
                "21.0400, 105.7500, " +
                "'" + descXP + "', " +
                "'06:00', " +
                "'22:30')";
        db.execSQL(insertSan10);

        // Sân 11: Phúc Diễn
        String descPD = "Sân bóng mini, mặt sân mới làm lại.";
        String insertSan11 = "INSERT INTO " + TABLE_COURT + " VALUES (" +
                "null, " +
                "'Sân bóng Phúc Diễn', " +
                "'Phúc Diễn, Bắc Từ Liêm, Hà Nội', " +
                "'BongDa', " +
                "130000, " +
                "'san_bong_pd_1', " +
                "4.2, " +
                "'Parking', " +
                "21.0450, 105.7550, " +
                "'" + descPD + "', " +
                "'06:00', " +
                "'22:00')";
        db.execSQL(insertSan11);

        // Sân 12: Tây Mỗ
        String descTM = "Sân 7 người, có mái che 1 phần khán đài.";
        String insertSan12 = "INSERT INTO " + TABLE_COURT + " VALUES (" +
                "null, " +
                "'Sân bóng Tây Mỗ', " +
                "'Tây Mỗ, Nam Từ Liêm, Hà Nội', " +
                "'BongDa', " +
                "120000, " +
                "'san_bong_tm_1', " +
                "4.1, " +
                "'Parking,Wifi', " +
                "21.0100, 105.7600, " +
                "'" + descTM + "', " +
                "'06:00', " +
                "'22:30')";
        db.execSQL(insertSan12);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Xóa bảng cũ tạo lại bảng mới khi tăng version
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKING);
        onCreate(db);
    }
}