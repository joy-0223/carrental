package com.example.carrental.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.carrental.R;
import com.example.carrental.model.Order;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private List<Order> orderList;
    private SimpleDateFormat dateFormatter;

    public OrderAdapter(List<Order> orderList) {
        this.orderList = orderList;
        this.dateFormatter = new SimpleDateFormat("MM月dd日", Locale.getDefault());
    }

    public void updateData(List<Order> newOrderList) {
        this.orderList = newOrderList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {
        private TextView tvOrderCar, tvOrderStatus, tvOrderDate, tvOrderLocation, tvOrderPrice;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderCar = itemView.findViewById(R.id.tvOrderCar);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvOrderLocation = itemView.findViewById(R.id.tvOrderLocation);
            tvOrderPrice = itemView.findViewById(R.id.tvOrderPrice);
        }

        // 在OrderAdapter的bind方法中更新汽车信息显示
        public void bind(Order order) {
            // 这里需要从数据库获取汽车详细信息，或者修改Order类包含汽车信息
            // 临时显示订单ID，实际应该显示汽车品牌型号
            tvOrderCar.setText("订单 #" + order.getId());

            // 设置状态
            tvOrderStatus.setText(getStatusText(order.getStatus()));

            // 设置日期范围
            String dateRange = dateFormatter.format(order.getStartDate()) + " - " +
                    dateFormatter.format(order.getEndDate());
            tvOrderDate.setText(dateRange);

            // 设置地点
            String location = order.getPickupLocation() + " → " + order.getReturnLocation();
            tvOrderLocation.setText(location);

            // 设置价格
            tvOrderPrice.setText(String.format("¥%.2f", order.getTotalPrice()));
        }

        private String getStatusText(String status) {
            switch (status) {
                case "pending": return "待确认";
                case "confirmed": return "已确认";
                case "completed": return "已完成";
                case "cancelled": return "已取消";
                default: return "未知";
            }
        }
    }
}