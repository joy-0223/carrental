package com.example.carrental.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.carrental.model.User;
import com.example.carrental.model.Car;
import com.example.carrental.model.Order;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "CarRental.db";
    private static final int DATABASE_VERSION = 2; // 从1改为2

    // 用户表
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_FULL_NAME = "full_name";

    // 汽车表
    private static final String TABLE_CARS = "cars";
    private static final String COLUMN_CAR_ID = "car_id";
    private static final String COLUMN_BRAND = "brand";
    private static final String COLUMN_MODEL = "model";
    private static final String COLUMN_PRICE_PER_DAY = "price_per_day";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_IMAGE_URL = "image_url";
    private static final String COLUMN_IS_AVAILABLE = "is_available";

    // 订单表
    private static final String TABLE_ORDERS = "orders";
    private static final String COLUMN_ORDER_ID = "order_id";
    private static final String COLUMN_START_DATE = "start_date";
    private static final String COLUMN_END_DATE = "end_date";
    private static final String COLUMN_PICKUP_LOCATION = "pickup_location";
    private static final String COLUMN_RETURN_LOCATION = "return_location";
    private static final String COLUMN_TOTAL_PRICE = "total_price";
    private static final String COLUMN_STATUS = "status";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建用户表
        String createUserTable = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USERNAME + " TEXT UNIQUE NOT NULL,"
                + COLUMN_PASSWORD + " TEXT NOT NULL,"
                + COLUMN_EMAIL + " TEXT,"
                + COLUMN_PHONE + " TEXT,"
                + COLUMN_FULL_NAME + " TEXT"
                + ")";
        db.execSQL(createUserTable);

        // 创建汽车表
        String createCarTable = "CREATE TABLE " + TABLE_CARS + "("
                + COLUMN_CAR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_BRAND + " TEXT NOT NULL,"
                + COLUMN_MODEL + " TEXT NOT NULL,"
                + COLUMN_PRICE_PER_DAY + " REAL NOT NULL,"
                + COLUMN_DESCRIPTION + " TEXT,"
                + COLUMN_IMAGE_URL + " TEXT,"
                + COLUMN_IS_AVAILABLE + " INTEGER DEFAULT 1"
                + ")";
        db.execSQL(createCarTable);

        // 创建订单表
        String createOrderTable = "CREATE TABLE " + TABLE_ORDERS + "("
                + COLUMN_ORDER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USER_ID + " INTEGER NOT NULL,"
                + COLUMN_CAR_ID + " INTEGER NOT NULL,"
                + COLUMN_START_DATE + " INTEGER NOT NULL,"
                + COLUMN_END_DATE + " INTEGER NOT NULL,"
                + COLUMN_PICKUP_LOCATION + " TEXT NOT NULL,"
                + COLUMN_RETURN_LOCATION + " TEXT NOT NULL,"
                + COLUMN_TOTAL_PRICE + " REAL NOT NULL,"
                + COLUMN_STATUS + " TEXT DEFAULT 'pending',"
                + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "),"
                + "FOREIGN KEY(" + COLUMN_CAR_ID + ") REFERENCES " + TABLE_CARS + "(" + COLUMN_CAR_ID + ")"
                + ")";
        db.execSQL(createOrderTable);

        // 插入示例汽车数据
        insertSampleCars(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CARS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // 插入示例汽车数据
    private void insertSampleCars(SQLiteDatabase db) {
        Car[] sampleCars = {
                new Car("丰田", "凯美瑞", 280.0, "舒适型轿车，燃油经济性优秀，适合家庭使用和商务出行", "toyota_camry"),
                new Car("本田", "思域", 240.0, "经典紧凑型轿车，操控灵活，适合城市通勤", "honda_civic"),
                new Car("宝马", "X5", 680.0, "豪华SUV，配备先进科技和卓越驾驶体验", "bmw_x5"),
                new Car("奔驰", "C级", 520.0, "优雅豪华轿车，内饰精致，驾乘舒适", "mercedes_c_class"),
                new Car("比亚迪", "汉EV", 320.0, "纯电动轿车，续航里程长，智能化配置先进", "byd_han"),
                new Car("特斯拉", "Model 3", 380.0, "智能电动轿车，自动驾驶技术领先，加速性能出色", "tesla_model3")
        };

        for (Car car : sampleCars) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_BRAND, car.getBrand());
            values.put(COLUMN_MODEL, car.getModel());
            values.put(COLUMN_PRICE_PER_DAY, car.getPricePerDay());
            values.put(COLUMN_DESCRIPTION, car.getDescription());
            values.put(COLUMN_IMAGE_URL, car.getImageUrl());
            values.put(COLUMN_IS_AVAILABLE, car.isAvailable() ? 1 : 0);
            db.insert(TABLE_CARS, null, values);
        }
    }

    // 用户相关操作
    public boolean addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, user.getUsername());
        values.put(COLUMN_PASSWORD, user.getPassword());
        values.put(COLUMN_EMAIL, user.getEmail());
        values.put(COLUMN_PHONE, user.getPhone());
        values.put(COLUMN_FULL_NAME, user.getFullName());

        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_USER_ID};
        String selection = COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {username, password};

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    public User getUser(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_USERNAME + " = ?";
        String[] selectionArgs = {username};

        Cursor cursor = db.query(TABLE_USERS, null, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            User user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)));
            user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)));
            user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)));
            user.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)));
            user.setFullName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FULL_NAME)));
            cursor.close();
            return user;
        }
        return null;
    }

    // 汽车相关操作
    public List<Car> getAllCars() {
        List<Car> carList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_CARS + " WHERE " + COLUMN_IS_AVAILABLE + " = 1";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Car car = new Car();
                car.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CAR_ID)));
                car.setBrand(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BRAND)));
                car.setModel(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MODEL)));
                car.setPricePerDay(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE_PER_DAY)));
                car.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)));
                car.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URL)));
                car.setAvailable(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_AVAILABLE)) == 1);
                carList.add(car);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return carList;
    }

    // 确保DatabaseHelper中有这个方法（之前已经实现过）
    public Car getCarById(int carId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_CAR_ID + " = ?";
        String[] selectionArgs = {String.valueOf(carId)};

        Cursor cursor = db.query(TABLE_CARS, null, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Car car = new Car();
            car.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CAR_ID)));
            car.setBrand(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BRAND)));
            car.setModel(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MODEL)));
            car.setPricePerDay(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE_PER_DAY)));
            car.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)));
            car.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URL)));
            car.setAvailable(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_AVAILABLE)) == 1);
            cursor.close();
            return car;
        }
        if (cursor != null) {
            cursor.close();
        }
        return null;
    }

    // 订单相关操作
    public boolean addOrder(Order order) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, order.getUserId());
        values.put(COLUMN_CAR_ID, order.getCarId());
        values.put(COLUMN_START_DATE, order.getStartDate().getTime());
        values.put(COLUMN_END_DATE, order.getEndDate().getTime());
        values.put(COLUMN_PICKUP_LOCATION, order.getPickupLocation());
        values.put(COLUMN_RETURN_LOCATION, order.getReturnLocation());
        values.put(COLUMN_TOTAL_PRICE, order.getTotalPrice());
        values.put(COLUMN_STATUS, order.getStatus());

        long result = db.insert(TABLE_ORDERS, null, values);
        return result != -1;
    }

    public List<Order> getUserOrders(int userId) {
        List<Order> orderList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_ORDERS + " WHERE " + COLUMN_USER_ID + " = ? ORDER BY " + COLUMN_START_DATE + " DESC";
        String[] selectionArgs = {String.valueOf(userId)};

        Cursor cursor = db.rawQuery(query, selectionArgs);

        if (cursor.moveToFirst()) {
            do {
                Order order = new Order();
                order.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ORDER_ID)));
                order.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)));
                order.setCarId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CAR_ID)));
                order.setStartDate(new Date(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_START_DATE))));
                order.setEndDate(new Date(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_END_DATE))));
                order.setPickupLocation(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PICKUP_LOCATION)));
                order.setReturnLocation(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RETURN_LOCATION)));
                order.setTotalPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_TOTAL_PRICE)));
                order.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS)));
                orderList.add(order);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return orderList;
    }
    // 在DatabaseHelper类中添加搜索方法
    public List<Car> searchCars(String keyword) {
        List<Car> carList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_CARS + " WHERE " + COLUMN_IS_AVAILABLE + " = 1 " +
                "AND (" + COLUMN_BRAND + " LIKE ? OR " + COLUMN_MODEL + " LIKE ?)";
        String[] selectionArgs = {"%" + keyword + "%", "%" + keyword + "%"};

        Cursor cursor = db.rawQuery(query, selectionArgs);

        if (cursor.moveToFirst()) {
            do {
                Car car = new Car();
                car.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CAR_ID)));
                car.setBrand(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BRAND)));
                car.setModel(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MODEL)));
                car.setPricePerDay(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE_PER_DAY)));
                car.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)));
                car.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URL)));
                car.setAvailable(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_AVAILABLE)) == 1);
                carList.add(car);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return carList;
    }
    // 在DatabaseHelper类中添加以下方法

    /**
     * 更新用户信息
     */
    public boolean updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FULL_NAME, user.getFullName());
        values.put(COLUMN_EMAIL, user.getEmail());
        values.put(COLUMN_PHONE, user.getPhone());

        String whereClause = COLUMN_USER_ID + " = ?";
        String[] whereArgs = {String.valueOf(user.getId())};

        int result = db.update(TABLE_USERS, values, whereClause, whereArgs);
        return result > 0;
    }

    /**
     * 根据用户ID获取用户信息
     */
    public User getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_USER_ID + " = ?";
        String[] selectionArgs = {String.valueOf(userId)};

        Cursor cursor = db.query(TABLE_USERS, null, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            User user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)));
            user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)));
            user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)));
            user.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)));
            user.setFullName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FULL_NAME)));
            cursor.close();
            return user;
        }
        if (cursor != null) {
            cursor.close();
        }
        return null;
    }

    /**
     * 检查用户名是否已存在（用于注册时验证）
     */
    public boolean isUsernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_USER_ID};
        String selection = COLUMN_USERNAME + " = ?";
        String[] selectionArgs = {username};

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }
    // 在DatabaseHelper中添加获取订单及汽车信息的方法
    /**
     * 获取用户订单列表（包含汽车信息）
     */
    public List<Order> getUserOrdersWithCarInfo(int userId) {
        List<Order> orderList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT o.*, c." + COLUMN_BRAND + ", c." + COLUMN_MODEL +
                " FROM " + TABLE_ORDERS + " o " +
                " INNER JOIN " + TABLE_CARS + " c ON o." + COLUMN_CAR_ID + " = c." + COLUMN_CAR_ID +
                " WHERE o." + COLUMN_USER_ID + " = ? ORDER BY o." + COLUMN_START_DATE + " DESC";
        String[] selectionArgs = {String.valueOf(userId)};

        Cursor cursor = db.rawQuery(query, selectionArgs);

        if (cursor.moveToFirst()) {
            do {
                Order order = new Order();
                order.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ORDER_ID)));
                order.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)));
                order.setCarId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CAR_ID)));
                order.setStartDate(new Date(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_START_DATE))));
                order.setEndDate(new Date(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_END_DATE))));
                order.setPickupLocation(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PICKUP_LOCATION)));
                order.setReturnLocation(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RETURN_LOCATION)));
                order.setTotalPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_TOTAL_PRICE)));
                order.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS)));

                // 设置汽车信息（用于显示）
                String carBrand = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BRAND));
                String carModel = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MODEL));
                orderList.add(order);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return orderList;
    }
}