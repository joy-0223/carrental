package com.example.carrental;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.carrental.database.DatabaseHelper;
import com.example.carrental.model.Car;

public class CarDetailActivity extends AppCompatActivity {
    private ImageView ivCarImage;
    private TextView tvCarTitle, tvCarPrice, tvStatus, tvCarDescription;
    private TextView tvBrand, tvModel, tvPriceDetail;
    private Button btnReserve;

    private DatabaseHelper databaseHelper;
    private Car currentCar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_detail);

        initViews();
        databaseHelper = new DatabaseHelper(this);

        // 获取传递的汽车ID
        int carId = getIntent().getIntExtra("car_id", -1);
        if (carId == -1) {
            Toast.makeText(this, "汽车信息错误", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadCarDetails(carId);
        setupClickListeners();
    }

    private void initViews() {
        ivCarImage = findViewById(R.id.ivCarImage);
        tvCarTitle = findViewById(R.id.tvCarTitle);
        tvCarPrice = findViewById(R.id.tvCarPrice);
        tvStatus = findViewById(R.id.tvStatus);
        tvCarDescription = findViewById(R.id.tvCarDescription);
        tvBrand = findViewById(R.id.tvBrand);
        tvModel = findViewById(R.id.tvModel);
        tvPriceDetail = findViewById(R.id.tvPriceDetail);
        btnReserve = findViewById(R.id.btnReserve);

        // 设置返回按钮
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("车辆详情");
        }
    }

    private void loadCarDetails(int carId) {
        new Thread(() -> {
            currentCar = databaseHelper.getCarById(carId);
            runOnUiThread(() -> {
                if (currentCar != null) {
                    displayCarDetails();
                } else {
                    Toast.makeText(this, "加载汽车信息失败", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }).start();
    }

    private void displayCarDetails() {
        if (currentCar == null) return;

        // 设置汽车标题
        tvCarTitle.setText(currentCar.getBrand() + " " + currentCar.getModel());

        // 设置价格
        String priceText = String.format("¥%.2f/天", currentCar.getPricePerDay());
        tvCarPrice.setText(priceText);
        tvPriceDetail.setText(priceText);

        // 设置状态
        if (currentCar.isAvailable()) {
            tvStatus.setText("可用");
            tvStatus.setBackgroundResource(R.drawable.status_available_bg);
            btnReserve.setEnabled(true);
            btnReserve.setAlpha(1.0f);
        } else {
            tvStatus.setText("已租出");
            tvStatus.setBackgroundResource(R.drawable.status_unavailable_bg);
            btnReserve.setEnabled(false);
            btnReserve.setAlpha(0.5f);
            btnReserve.setText("已租出");
        }

        // 设置描述
        if (currentCar.getDescription() != null && !currentCar.getDescription().isEmpty()) {
            tvCarDescription.setText(currentCar.getDescription());
        } else {
            tvCarDescription.setText("暂无详细描述");
        }

        // 设置品牌和型号
        tvBrand.setText(currentCar.getBrand());
        tvModel.setText(currentCar.getModel());

        // 设置图片 - 根据图片URL加载对应的drawable资源
        int imageResId = getImageResourceId(currentCar.getImageUrl());
        ivCarImage.setImageResource(imageResId);
    }

    // 根据图片URL获取对应的drawable资源ID
    private int getImageResourceId(String imageUrl) {
        switch (imageUrl) {
            case "toyota_camry":
                return R.drawable.toyota_camry;
            case "honda_civic":
                return R.drawable.honda_civic;
            case "bmw_x5":
                return R.drawable.bmw_x5;
            case "mercedes_c_class":
                return R.drawable.mercedes_c_class;
            case "byd_han":
                return R.drawable.byd_han;
            case "tesla_model3":
                return R.drawable.tesla_model3;
            default:
                return R.drawable.car_placeholder; // 默认占位图
        }
    }

    private void setupClickListeners() {
        btnReserve.setOnClickListener(v -> {
            if (currentCar != null && currentCar.isAvailable()) {
                // 跳转到预订页面
                Intent intent = new Intent(CarDetailActivity.this, ReservationActivity.class);
                intent.putExtra("car_id", currentCar.getId());
                startActivity(intent);
            } else {
                Toast.makeText(this, "该车辆暂不可用", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 重新加载汽车信息，更新可用状态
        if (currentCar != null) {
            loadCarDetails(currentCar.getId());
        }
    }
}