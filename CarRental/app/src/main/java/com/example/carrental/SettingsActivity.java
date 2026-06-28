package com.example.carrental;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public class SettingsActivity extends AppCompatActivity {
    private SwitchCompat switchNotifications, switchOrderAlerts, switchPromotions;
    private TextView tvLanguage, tvTheme, tvCacheSize, tvVersion;
    private Button btnSaveSettings;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initViews();
        sharedPreferences = getSharedPreferences("app_settings", MODE_PRIVATE);

        loadSettings();
        setupClickListeners();
    }

    private void initViews() {
        switchNotifications = findViewById(R.id.switchNotifications);
        switchOrderAlerts = findViewById(R.id.switchOrderAlerts);
        switchPromotions = findViewById(R.id.switchPromotions);
        tvLanguage = findViewById(R.id.tvLanguage);
        tvTheme = findViewById(R.id.tvTheme);
        tvCacheSize = findViewById(R.id.tvCacheSize);
        tvVersion = findViewById(R.id.tvVersion);
        btnSaveSettings = findViewById(R.id.btnSaveSettings);

        // 设置返回按钮
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("设置");
        }

        // 设置版本信息
        tvVersion.setText("v1.0.0");
        tvCacheSize.setText("0.0 MB");
    }

    private void loadSettings() {
        // 加载保存的设置
        boolean notifications = sharedPreferences.getBoolean("notifications", true);
        boolean orderAlerts = sharedPreferences.getBoolean("order_alerts", true);
        boolean promotions = sharedPreferences.getBoolean("promotions", false);
        String language = sharedPreferences.getString("language", "简体中文");
        String theme = sharedPreferences.getString("theme", "跟随系统");

        switchNotifications.setChecked(notifications);
        switchOrderAlerts.setChecked(orderAlerts);
        switchPromotions.setChecked(promotions);
        tvLanguage.setText(language);
        tvTheme.setText(theme);
    }

    private void setupClickListeners() {
        btnSaveSettings.setOnClickListener(v -> saveSettings());

        // 语言设置点击
        findViewById(R.id.layoutLanguage).setOnClickListener(v -> {
            Toast.makeText(this, "语言设置功能开发中", Toast.LENGTH_SHORT).show();
        });

        // 主题设置点击
        findViewById(R.id.layoutTheme).setOnClickListener(v -> {
            Toast.makeText(this, "主题设置功能开发中", Toast.LENGTH_SHORT).show();
        });

        // 清除缓存点击
        findViewById(R.id.layoutClearCache).setOnClickListener(v -> {
            clearCache();
        });

        // 隐私政策点击
        findViewById(R.id.layoutPrivacy).setOnClickListener(v -> {
            Toast.makeText(this, "隐私政策页面开发中", Toast.LENGTH_SHORT).show();
        });

        // 用户协议点击
        findViewById(R.id.layoutTerms).setOnClickListener(v -> {
            Toast.makeText(this, "用户协议页面开发中", Toast.LENGTH_SHORT).show();
        });
    }

    private void saveSettings() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("notifications", switchNotifications.isChecked());
        editor.putBoolean("order_alerts", switchOrderAlerts.isChecked());
        editor.putBoolean("promotions", switchPromotions.isChecked());
        editor.apply();

        Toast.makeText(this, "设置已保存", Toast.LENGTH_SHORT).show();
    }

    private void clearCache() {
        // 模拟清除缓存
        tvCacheSize.setText("0.0 MB");
        Toast.makeText(this, "缓存已清除", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}