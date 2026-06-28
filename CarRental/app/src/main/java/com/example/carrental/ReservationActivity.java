package com.example.carrental;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.carrental.database.DatabaseHelper;
import com.example.carrental.model.Car;
import com.example.carrental.model.Order;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ReservationActivity extends AppCompatActivity {
    private TextView tvSelectedCar, tvCarPrice, tvRentalDays;
    private TextView tvDailyPrice, tvDaysCount, tvTotalPrice;
    private EditText etPickupDate, etReturnDate, etPickupLocation, etReturnLocation;
    private Button btnConfirmReservation;

    private DatabaseHelper databaseHelper;
    private Car selectedCar;
    private Calendar pickupCalendar, returnCalendar;
    private SimpleDateFormat dateFormatter;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        initViews();
        databaseHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);

        // 初始化日期格式化器
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        pickupCalendar = Calendar.getInstance();
        returnCalendar = Calendar.getInstance();
        returnCalendar.add(Calendar.DAY_OF_MONTH, 1); // 默认还车日期为明天

        // 获取传递的汽车ID
        int carId = getIntent().getIntExtra("car_id", -1);
        if (carId == -1) {
            Toast.makeText(this, "汽车信息错误", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadCarDetails(carId);
        setupDatePickers();
        setupClickListeners();
        updateDateDisplay();
        calculatePrice();
    }

    private void initViews() {
        tvSelectedCar = findViewById(R.id.tvSelectedCar);
        tvCarPrice = findViewById(R.id.tvCarPrice);
        tvRentalDays = findViewById(R.id.tvRentalDays);
        tvDailyPrice = findViewById(R.id.tvDailyPrice);
        tvDaysCount = findViewById(R.id.tvDaysCount);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);

        etPickupDate = findViewById(R.id.etPickupDate);
        etReturnDate = findViewById(R.id.etReturnDate);
        etPickupLocation = findViewById(R.id.etPickupLocation);
        etReturnLocation = findViewById(R.id.etReturnLocation);

        btnConfirmReservation = findViewById(R.id.btnConfirmReservation);

        // 设置返回按钮
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("车辆预订");
        }
    }

    private void loadCarDetails(int carId) {
        new Thread(() -> {
            selectedCar = databaseHelper.getCarById(carId);
            runOnUiThread(() -> {
                if (selectedCar != null) {
                    displayCarInfo();
                } else {
                    Toast.makeText(this, "加载汽车信息失败", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }).start();
    }

    private void displayCarInfo() {
        if (selectedCar == null) return;

        String carInfo = selectedCar.getBrand() + " " + selectedCar.getModel();
        tvSelectedCar.setText(carInfo);

        String priceInfo = String.format("日租金：¥%.2f/天", selectedCar.getPricePerDay());
        tvCarPrice.setText(priceInfo);
        tvDailyPrice.setText(String.format("¥%.2f", selectedCar.getPricePerDay()));

        // 设置默认地点
        etPickupLocation.setText("门店取车");
        etReturnLocation.setText("门店还车");
    }

    private void setupDatePickers() {
        // 取车日期选择器
        etPickupDate.setOnClickListener(v -> showDatePicker(true));

        // 还车日期选择器
        etReturnDate.setOnClickListener(v -> showDatePicker(false));
    }

    private void showDatePicker(boolean isPickupDate) {
        Calendar currentCalendar = isPickupDate ? pickupCalendar : returnCalendar;

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    currentCalendar.set(year, month, dayOfMonth);
                    updateDateDisplay();
                    calculatePrice();

                    // 如果选择了取车日期，自动设置还车日期为取车日期后一天
                    if (isPickupDate) {
                        returnCalendar.setTime(pickupCalendar.getTime());
                        returnCalendar.add(Calendar.DAY_OF_MONTH, 1);
                        updateDateDisplay();
                        calculatePrice();
                    }
                },
                currentCalendar.get(Calendar.YEAR),
                currentCalendar.get(Calendar.MONTH),
                currentCalendar.get(Calendar.DAY_OF_MONTH)
        );

        // 设置最小日期为今天
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        datePickerDialog.show();
    }

    private void updateDateDisplay() {
        etPickupDate.setText(dateFormatter.format(pickupCalendar.getTime()));
        etReturnDate.setText(dateFormatter.format(returnCalendar.getTime()));
    }

    private void calculatePrice() {
        if (selectedCar == null) return;

        long diff = returnCalendar.getTimeInMillis() - pickupCalendar.getTimeInMillis();
        int days = (int) (diff / (1000 * 60 * 60 * 24));

        if (days < 1) {
            days = 1; // 最少租1天
            returnCalendar.setTime(pickupCalendar.getTime());
            returnCalendar.add(Calendar.DAY_OF_MONTH, 1);
            updateDateDisplay();
        }

        double totalPrice = days * selectedCar.getPricePerDay();

        tvRentalDays.setText(String.format("租赁天数：%d天", days));
        tvDaysCount.setText(String.format("%d天", days));
        tvTotalPrice.setText(String.format("¥%.2f", totalPrice));
    }

    private void setupClickListeners() {
        btnConfirmReservation.setOnClickListener(v -> confirmReservation());
    }

    private void confirmReservation() {
        // 验证输入
        String pickupLocation = etPickupLocation.getText().toString().trim();
        String returnLocation = etReturnLocation.getText().toString().trim();

        if (pickupLocation.isEmpty() || returnLocation.isEmpty()) {
            Toast.makeText(this, "请填写取车和还车地点", Toast.LENGTH_SHORT).show();
            return;
        }

        // 验证日期
        if (pickupCalendar.after(returnCalendar)) {
            Toast.makeText(this, "还车日期不能早于取车日期", Toast.LENGTH_SHORT).show();
            return;
        }

        // 获取当前用户ID
        String username = sharedPreferences.getString("current_username", "");
        if (username.isEmpty()) {
            Toast.makeText(this, "用户信息错误，请重新登录", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // 计算总价
        long diff = returnCalendar.getTimeInMillis() - pickupCalendar.getTimeInMillis();
        int days = (int) (diff / (1000 * 60 * 60 * 24));
        double totalPrice = days * selectedCar.getPricePerDay();

        // 创建订单
        new Thread(() -> {
            // 获取用户ID
            com.example.carrental.model.User user = databaseHelper.getUser(username);
            if (user == null) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "用户信息错误", Toast.LENGTH_SHORT).show();
                });
                return;
            }

            Order order = new Order(
                    user.getId(),
                    selectedCar.getId(),
                    pickupCalendar.getTime(),
                    returnCalendar.getTime(),
                    pickupLocation,
                    returnLocation,
                    totalPrice
            );

            boolean success = databaseHelper.addOrder(order);
            runOnUiThread(() -> {
                if (success) {
                    Toast.makeText(this, "预订成功", Toast.LENGTH_SHORT).show();

                    // 跳转到订单确认页面
                    Intent intent = new Intent(ReservationActivity.this, OrderConfirmationActivity.class);
                    intent.putExtra("order_car_id", selectedCar.getId());
                    intent.putExtra("order_days", days);
                    intent.putExtra("order_total_price", totalPrice);
                    intent.putExtra("order_pickup_date", pickupCalendar.getTimeInMillis());
                    intent.putExtra("order_return_date", returnCalendar.getTimeInMillis());
                    intent.putExtra("order_pickup_location", pickupLocation);
                    intent.putExtra("order_return_location", returnLocation);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "预订失败，请重试", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}