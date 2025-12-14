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

        // --- MOCK DATA ---

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
                "'bd1', " +
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
                "'tn1', " +
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
                "'bd2', " +
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
                "'cl1', " +
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
                "'tn2', " +
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
                "'bd3', " +
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
                "'bd4', " +
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
                "'bd5', " +
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
                "'cl2', " +
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
                "'bd4', " +
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
                "'bd5', " +
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
                "'bd2', " +
                "4.1, " +
                "'Parking,Wifi', " +
                "21.0100, 105.7600, " +
                "'" + descTM + "', " +
                "'06:00', " +
                "'22:30')";
        db.execSQL(insertSan12);
        // --- SÂN BÓNG RỔ 1: SƯ PHẠM (Cầu Giấy) ---
        String descBR1 = "Sân ngoài trời, mặt sân tiêu chuẩn thi đấu, đèn chiếu sáng tốt.";
        String insertBR1 = "INSERT INTO " + TABLE_COURT + " VALUES (" +
                "null, " +
                "'Sân bóng rổ ĐH Sư Phạm', " +
                "'136 Xuân Thủy, Cầu Giấy, Hà Nội', " +
                "'BongRo', " +
                "100000, " +
                "'br1', " +
                "4.5, " +
                "'Parking,Wifi,Water', " +
                "21.0365, 105.7834, " +
                "'" + descBR1 + "', " +
                "'05:30', " +
                "'22:00')";
        db.execSQL(insertBR1);

// --- SÂN BÓNG RỔ 2: TUỔI TRẺ (Hai Bà Trưng) ---
        String descBR2 = "Sân trong nhà (Indoor), sàn gỗ cao cấp, có điều hòa và khán đài.";
        String insertBR2 = "INSERT INTO " + TABLE_COURT + " VALUES (" +
                "null, " +
                "'Sân bóng rổ Tuổi Trẻ', " +
                "'46 Thanh Nhàn, Hai Bà Trưng, Hà Nội', " +
                "'BongRo', " +
                "250000, " +
                "'br5', " +
                "4.8, " +
                "'Parking,Shower,AirConditioner', " +
                "21.0035, 105.8561, " +
                "'" + descBR2 + "', " +
                "'07:00', " +
                "'21:00')";
        db.execSQL(insertBR2);

// --- SÂN BÓNG RỔ 3: Y MEDICAL (Đống Đa) ---
        String descBR3 = "Sân đẹp, thoáng mát, nhiều câu lạc bộ sinh viên sinh hoạt.";
        String insertBR3 = "INSERT INTO " + TABLE_COURT + " VALUES (" +
                "null, " +
                "'Sân bóng rổ ĐH Y Hà Nội', " +
                "'1 Tôn Thất Tùng, Đống Đa, Hà Nội', " +
                "'BongRo', " +
                "150000, " +
                "'br3', " +
                "4.3, " +
                "'Parking,Canteen', " +
                "21.0028, 105.8295, " +
                "'" + descBR3 + "', " +
                "'06:00', " +
                "'23:00')";
        db.execSQL(insertBR3);

// --- SÂN BÓNG RỔ 4: ROYAL CITY (Thanh Xuân) ---
        String descBR4 = "Sân hiện đại nằm trong khu đô thị, an ninh tốt, mặt sân cao su EPDM.";
        String insertBR4 = "INSERT INTO " + TABLE_COURT + " VALUES (" +
                "null, " +
                "'Sân bóng rổ Royal City', " +
                "'72A Nguyễn Trãi, Thanh Xuân, Hà Nội', " +
                "'BongRo', " +
                "200000, " +
                "'br4', " +
                "4.7, " +
                "'Parking,Wifi,Locker', " +
                "21.0005, 105.8153, " +
                "'" + descBR4 + "', " +
                "'08:00', " +
                "'22:00')";
        db.execSQL(insertBR4);
        // --- BỔ SUNG 2 SÂN CẦU LÔNG ---

// 1. Sân Cầu Lông Đền Lừ (Hoàng Mai)
        String descCL3 = "Sân sàn gỗ, trần cao thoáng, không bị chói mắt khi thi đấu.";
        String insertCL3 = "INSERT INTO " + TABLE_COURT + " VALUES (" +
                "null, " +
                "'Sân Cầu Lông Đền Lừ', " +
                "'KĐT Đền Lừ, Hoàng Mai, Hà Nội', " +
                "'CauLong', " +
                "80000, " +
                "'cl3', " +
                "4.2, " +
                "'Parking,Canteen', " +
                "20.9850, 105.8560, " +
                "'" + descCL3 + "', " +
                "'06:00', " +
                "'22:00')";
        db.execSQL(insertCL3);

// 2. Sân Cầu Lông NTĐ Hà Đông (Hà Đông)
        String descCL4 = "Sân thảm tiêu chuẩn thi đấu quốc tế, không gian rộng rãi.";
        String insertCL4 = "INSERT INTO " + TABLE_COURT + " VALUES (" +
                "null, " +
                "'Sân Cầu Lông Hà Đông', " +
                "'Hà Đông, Hà Nội', " +
                "'CauLong', " +
                "95000, " +
                "'cl5', " +
                "4.7, " +
                "'Parking,Shower,Wifi', " +
                "20.9680, 105.7750, " +
                "'" + descCL4 + "', " +
                "'05:30', " +
                "'23:00')";
        db.execSQL(insertCL4);


// --- BỔ SUNG 2 SÂN TENNIS ---

// 1. Sân Tennis Khúc Hạo (Ba Đình)
        String descKH = "Sân đất nện, nằm trong khu thể thao yên tĩnh, dịch vụ tốt.";
        String insertTN1 = "INSERT INTO " + TABLE_COURT + " VALUES (" +
                "null, " +
                "'Sân Tennis Khúc Hạo', " +
                "'1B Lê Hồng Phong, Ba Đình, Hà Nội', " +
                "'Tennis', " +
                "200000, " +
                "'tn4', " +
                "4.5, " +
                "'Parking,Canteen,Locker', " +
                "21.0320, 105.8380, " +
                "'" + descKH + "', " +
                "'06:00', " +
                "'21:00')";
        db.execSQL(insertTN1);

// 2. Sân Tennis Quan Hoa (Cầu Giấy)
        String descQH = "Sân cứng (Hard court), hệ thống đèn Led sáng, mặt sân mới sơn lại.";
        String insertTN2 = "INSERT INTO " + TABLE_COURT + " VALUES (" +
                "null, " +
                "'Sân Tennis Quan Hoa', " +
                "'20 Ngõ 165 Cầu Giấy, Hà Nội', " +
                "'Tennis', " +
                "180000, " +
                "'tn3', " +
                "4.0, " +
                "'Parking,Water', " +
                "21.0330, 105.7990, " +
                "'" + descQH + "', " +
                "'05:00', " +
                "'22:00')";
        db.execSQL(insertTN2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKING);
        onCreate(db);
    }
}