package com.example.carrental;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.carrental.database.DatabaseHelper;
import com.example.carrental.model.Car;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OrderConfirmationActivity extends AppCompatActivity {
    private TextView tvOrderCar, tvPickupDate, tvReturnDate;
    private TextView tvPickupLocation, tvReturnLocation, tvRentalDays, tvTotalPrice;
    private RadioGroup rgPayment;
    private Button btnBackToHome, btnPayNow;

    private DatabaseHelper databaseHelper;
    private Car orderedCar;
    private SimpleDateFormat dateFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmation);

        initViews();
        databaseHelper = new DatabaseHelper(this);
        dateFormatter = new SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault());

        // 获取传递的订单信息
        Intent intent = getIntent();
        if (!intent.hasExtra("order_car_id")) {
            Toast.makeText(this, "订单信息错误", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadOrderDetails(intent);
        setupClickListeners();
    }

    private void initViews() {
        tvOrderCar = findViewById(R.id.tvOrderCar);
        tvPickupDate = findViewById(R.id.tvPickupDate);
        tvReturnDate = findViewById(R.id.tvReturnDate);
        tvPickupLocation = findViewById(R.id.tvPickupLocation);
        tvReturnLocation = findViewById(R.id.tvReturnLocation);
        tvRentalDays = findViewById(R.id.tvRentalDays);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        rgPayment = findViewById(R.id.rgPayment);
        btnBackToHome = findViewById(R.id.btnBackToHome);
        btnPayNow = findViewById(R.id.btnPayNow);

        // 设置返回按钮
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("订单确认");
        }
    }

    private void loadOrderDetails(Intent intent) {
        int carId = intent.getIntExtra("order_car_id", -1);
        int days = intent.getIntExtra("order_days", 1);
        double totalPrice = intent.getDoubleExtra("order_total_price", 0.0);
        long pickupDateMillis = intent.getLongExtra("order_pickup_date", System.currentTimeMillis());
        long returnDateMillis = intent.getLongExtra("order_return_date", System.currentTimeMillis());
        String pickupLocation = intent.getStringExtra("order_pickup_location");
        String returnLocation = intent.getStringExtra("order_return_location");

        // 加载汽车信息
        new Thread(() -> {
            orderedCar = databaseHelper.getCarById(carId);
            runOnUiThread(() -> {
                if (orderedCar != null) {
                    displayOrderDetails(orderedCar, days, totalPrice,
                            pickupDateMillis, returnDateMillis,
                            pickupLocation, returnLocation);
                } else {
                    Toast.makeText(this, "加载订单信息失败", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void displayOrderDetails(Car car, int days, double totalPrice,
                                     long pickupDateMillis, long returnDateMillis,
                                     String pickupLocation, String returnLocation) {
        // 设置车辆信息
        String carInfo = car.getBrand() + " " + car.getModel();
        tvOrderCar.setText(carInfo);

        // 设置日期
        tvPickupDate.setText(dateFormatter.format(new Date(pickupDateMillis)));
        tvReturnDate.setText(dateFormatter.format(new Date(returnDateMillis)));

        // 设置地点
        tvPickupLocation.setText(pickupLocation);
        tvReturnLocation.setText(returnLocation);

        // 设置天数和价格
        tvRentalDays.setText(days + "天");
        tvTotalPrice.setText(String.format("¥%.2f", totalPrice));
    }

    private void setupClickListeners() {
        btnBackToHome.setOnClickListener(v -> {
            // 返回首页
            Intent intent = new Intent(OrderConfirmationActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        btnPayNow.setOnClickListener(v -> {
            processPayment();
        });
    }

    private void processPayment() {
        int selectedPaymentId = rgPayment.getCheckedRadioButtonId();
        String paymentMethod = "";

        if (selectedPaymentId == R.id.rbOnline) {
            paymentMethod = "在线支付";
        } else if (selectedPaymentId == R.id.rbCash) {
            paymentMethod = "到店支付";
        } else if (selectedPaymentId == R.id.rbCard) {
            paymentMethod = "信用卡支付";
        }

        // 模拟支付处理
        Toast.makeText(this, "使用" + paymentMethod + "完成支付", Toast.LENGTH_SHORT).show();

        // 支付成功后跳转到首页
        new android.os.Handler().postDelayed(() -> {
            Intent intent = new Intent(OrderConfirmationActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("show_success", true);
            startActivity(intent);
            finish();
        }, 1500);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}