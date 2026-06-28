package com.example.carrental.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.carrental.R;
import com.example.carrental.model.Car;
import java.util.List;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.CarViewHolder> {
    private List<Car> carList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Car car);
    }

    public CarAdapter(List<Car> carList, OnItemClickListener listener) {
        this.carList = carList;
        this.listener = listener;
    }

    public void updateData(List<Car> newCarList) {
        this.carList = newCarList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_car, parent, false);
        return new CarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarViewHolder holder, int position) {
        Car car = carList.get(position);
        holder.bind(car, listener);
    }

    @Override
    public int getItemCount() {
        return carList.size();
    }

    static class CarViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivCarImage;
        private TextView tvCarBrand;
        private TextView tvCarModel;
        private TextView tvCarPrice;
        private TextView tvCarDescription;
        private TextView tvAvailable;

        public CarViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCarImage = itemView.findViewById(R.id.ivCarImage);
            tvCarBrand = itemView.findViewById(R.id.tvCarBrand);
            tvCarModel = itemView.findViewById(R.id.tvCarModel);
            tvCarPrice = itemView.findViewById(R.id.tvCarPrice);
            tvCarDescription = itemView.findViewById(R.id.tvCarDescription);
            tvAvailable = itemView.findViewById(R.id.tvAvailable);
        }

        public void bind(Car car, OnItemClickListener listener) {
            tvCarBrand.setText(car.getBrand());
            tvCarModel.setText(car.getModel());
            tvCarPrice.setText(String.format("¥%.2f/天", car.getPricePerDay()));
            tvCarDescription.setText(car.getDescription());

            // 设置可用状态
            if (car.isAvailable()) {
                tvAvailable.setText("可用");
                tvAvailable.setBackgroundResource(R.drawable.available_bg);
            } else {
                tvAvailable.setText("已租");
                tvAvailable.setBackgroundColor(itemView.getContext().getColor(android.R.color.darker_gray));
            }

            // 设置图片 - 根据图片URL加载对应的drawable资源
            int imageResId = getImageResourceId(car.getImageUrl());
            ivCarImage.setImageResource(imageResId);

            // 点击事件
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(car);
                }
            });
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
                    return R.drawable.car_placeholder;
            }
        }
    }
}