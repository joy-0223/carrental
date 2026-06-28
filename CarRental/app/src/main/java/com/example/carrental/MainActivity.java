package com.example.carrental;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.carrental.adapter.CarAdapter;
import com.example.carrental.database.DatabaseHelper;
import com.example.carrental.model.Car;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView rvCars;
    private EditText etSearch;
    private CarAdapter carAdapter;
    private DatabaseHelper databaseHelper;
    private List<Car> allCars;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        databaseHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);

        // 检查登录状态
        if (!isUserLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setupRecyclerView();
        setupSearchFunction();
        setupBottomNavigation();
        loadCars();

        // 检查是否显示支付成功提示
        if (getIntent().getBooleanExtra("show_success", false)) {
            Toast.makeText(this, "支付成功！您可以在个人中心查看订单", Toast.LENGTH_LONG).show();
        }
    }

    private void initViews() {
        rvCars = findViewById(R.id.rvCars);
        etSearch = findViewById(R.id.etSearch);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("汽车租赁");
        }
    }

    private boolean isUserLoggedIn() {
        return sharedPreferences.getBoolean("is_logged_in", false);
    }

    private void setupRecyclerView() {
        rvCars.setLayoutManager(new LinearLayoutManager(this));
        carAdapter = new CarAdapter(new ArrayList<>(), this::onCarItemClick);
        rvCars.setAdapter(carAdapter);
    }

    private void setupSearchFunction() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCars(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                // 已经在首页，刷新数据
                loadCars();
                return true;
            } else if (itemId == R.id.navigation_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            } else if (itemId == R.id.navigation_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            }
            return false;
        });
    }

    private void loadCars() {
        new Thread(() -> {
            allCars = databaseHelper.getAllCars();
            runOnUiThread(() -> {
                carAdapter.updateData(allCars);
                if (allCars.isEmpty()) {
                    Toast.makeText(this, "暂无可用车辆", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void filterCars(String query) {
        if (allCars == null) return;

        List<Car> filteredCars = new ArrayList<>();
        if (query.isEmpty()) {
            filteredCars.addAll(allCars);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (Car car : allCars) {
                if (car.getBrand().toLowerCase().contains(lowerCaseQuery) ||
                        car.getModel().toLowerCase().contains(lowerCaseQuery)) {
                    filteredCars.add(car);
                }
            }
        }
        carAdapter.updateData(filteredCars); // 移除重复的调用
    }

    private void onCarItemClick(Car car) {
        Intent intent = new Intent(this, CarDetailActivity.class);
        intent.putExtra("car_id", car.getId());
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 刷新数据
        loadCars();
    }
}