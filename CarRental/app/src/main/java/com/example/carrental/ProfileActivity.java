package com.example.carrental;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.carrental.adapter.OrderAdapter;
import com.example.carrental.database.DatabaseHelper;
import com.example.carrental.model.Order;
import com.example.carrental.model.User;
import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    private ImageView ivUserAvatar;
    private TextView tvUsername, tvUserEmail;
    private EditText etFullName, etEmail, etPhone;
    private Button btnSaveProfile, btnLogout;
    private RecyclerView rvOrders;
    private TextView tvNoOrders;

    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;
    private OrderAdapter orderAdapter;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();
        databaseHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);

        // 检查登录状态
        if (!isUserLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        loadUserData();
        setupRecyclerView();
        setupClickListeners();
        loadUserOrders();
    }

    private void initViews() {
        ivUserAvatar = findViewById(R.id.ivUserAvatar);
        tvUsername = findViewById(R.id.tvUsername);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        btnLogout = findViewById(R.id.btnLogout);
        rvOrders = findViewById(R.id.rvOrders);
        tvNoOrders = findViewById(R.id.tvNoOrders);

        // 设置返回按钮
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("个人资料");
        }
    }

    private boolean isUserLoggedIn() {
        return sharedPreferences.getBoolean("is_logged_in", false);
    }

    private void loadUserData() {
        String username = sharedPreferences.getString("current_username", "");
        if (!username.isEmpty()) {
            new Thread(() -> {
                currentUser = databaseHelper.getUser(username);
                runOnUiThread(() -> {
                    if (currentUser != null) {
                        displayUserInfo();
                    }
                });
            }).start();
        }
    }

    private void displayUserInfo() {
        if (currentUser == null) return;

        tvUsername.setText(currentUser.getUsername());
        tvUserEmail.setText(currentUser.getEmail());

        etFullName.setText(currentUser.getFullName());
        etEmail.setText(currentUser.getEmail());
        etPhone.setText(currentUser.getPhone());
    }

    private void setupRecyclerView() {
        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        orderAdapter = new OrderAdapter(new ArrayList<>());
        rvOrders.setAdapter(orderAdapter);
    }

    private void loadUserOrders() {
        if (currentUser == null) return;

        new Thread(() -> {
            List<Order> orders = databaseHelper.getUserOrders(currentUser.getId());
            runOnUiThread(() -> {
                if (orders != null && !orders.isEmpty()) {
                    orderAdapter.updateData(orders);
                    tvNoOrders.setVisibility(View.GONE);
                    rvOrders.setVisibility(View.VISIBLE);
                } else {
                    tvNoOrders.setVisibility(View.VISIBLE);
                    rvOrders.setVisibility(View.GONE);
                }
            });
        }).start();
    }

    private void setupClickListeners() {
        btnSaveProfile.setOnClickListener(v -> saveProfile());
        btnLogout.setOnClickListener(v -> logout());
    }

    private void saveProfile() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (fullName.isEmpty()) {
            Toast.makeText(this, "请输入姓名", Toast.LENGTH_SHORT).show();
            return;
        }

        if (email.isEmpty()) {
            Toast.makeText(this, "请输入邮箱", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentUser != null) {
            currentUser.setFullName(fullName);
            currentUser.setEmail(email);
            currentUser.setPhone(phone);

            new Thread(() -> {
                boolean success = databaseHelper.updateUser(currentUser);
                runOnUiThread(() -> {
                    if (success) {
                        Toast.makeText(ProfileActivity.this, "个人信息已保存", Toast.LENGTH_SHORT).show();
                        // 更新显示
                        displayUserInfo();
                    } else {
                        Toast.makeText(ProfileActivity.this, "保存失败，请重试", Toast.LENGTH_SHORT).show();
                    }
                });
            }).start();
        }
    }

    private void logout() {
        // 清除登录状态
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("is_logged_in", false);
        editor.remove("current_username");
        editor.apply();

        Toast.makeText(this, "已退出登录", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, WelcomeActivity.class));
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 刷新数据
        loadUserData();
        loadUserOrders();
    }
}