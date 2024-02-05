package com.bhushan.vendor.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bhushan.vendor.R;
import com.bhushan.vendor.activities.ViewOrderActivity;
import com.bhushan.vendor.model.OrderListModel;
import com.bhushan.vendor.sharediPreference.SharedPrefManager;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.OrderListViewHolder> implements Filterable {

    private Context context;
    private List<OrderListModel> orderListModelList;
    private List<OrderListModel> updatedList;

    private int lastPosition = -1;

    public OrderListAdapter(Context context, List<OrderListModel> orderListModelList) {
        this.context = context;
        this.orderListModelList = orderListModelList;
        this.updatedList = new ArrayList<>(orderListModelList);
    }

    @NonNull
    @Override
    public OrderListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.row_orders, parent, false);
        return new OrderListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final OrderListViewHolder holder, int position) {
        final OrderListModel orderListModel = orderListModelList.get(position);


        holder.orderId.setText(orderListModel.getOrderId());
        holder.ProductName.setText(orderListModel.getProductName());
        holder.Price.setText(String.valueOf(orderListModel.getPrice()));
        holder.userName.setText(SharedPrefManager.getInstance(context).getUsername());
        holder.userEmail.setText(SharedPrefManager.getInstance(context).getUserEmail());
        holder.OrderDate.setText(orderListModel.getDate());
        holder.OrderStatus.setText(orderListModel.getStatus());


        if (orderListModel.getProductImage() != null && orderListModel.getProductImage().length() > 0) {
            Picasso.with(context).load(orderListModel.getProductImage()) .placeholder(R.drawable.progress_animation).into(holder.productImage);
        } else {
            Toast.makeText(context, "Empty Image URL", Toast.LENGTH_LONG).show();
            Picasso.with(context).load(R.drawable.ic_baseline_image_24) .placeholder(R.drawable.progress_animation).into(holder.productImage);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ViewOrderActivity.class);
                intent.putExtra("id", orderListModel.getOrderId());
                context.startActivity(intent);
            }
        });
        holder.productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LayoutInflater layoutInflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                ImageView container = (ImageView) layoutInflater.inflate(R.layout.pop, null);

                final PopupWindow popUpWindow = new PopupWindow(container, 500, 500, true);
                popUpWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                Glide.with(context).load(orderListModel.getProductImage()).placeholder(R.drawable.progress_animation).into(container);

                container.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        popUpWindow.dismiss();
                        return true;
                    }
                });


            }
        });

        if (lastPosition < position) {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), android.R.anim.fade_in);
            holder.itemView.setAnimation(animation);
            lastPosition = position;

        }

    }

    @Override
    public int getItemCount() {
        return orderListModelList.size();
    }


    @Override
    public Filter getFilter() {

        return filter;
    }

    Filter filter = new Filter() {


        // run on background thread
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<OrderListModel> orderListModels = new ArrayList<>();
            if (constraint.toString().isEmpty()) {
                orderListModels.addAll(updatedList);

            } else {
                for (OrderListModel orderListModel : updatedList) {
                    if (orderListModel.getProductName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        orderListModels.add(orderListModel);

                    }
                }

            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = orderListModels;
            return filterResults;
        }

        // run on ui thread

        @Override
        protected void publishResults(CharSequence constraint, FilterResults filterResults) {
            orderListModelList.clear();
            orderListModelList.addAll((Collection<? extends OrderListModel>) filterResults.values);
            notifyDataSetChanged();


        }
    };


    public class OrderListViewHolder extends RecyclerView.ViewHolder {

        CircleImageView productImage;
        TextView orderId, ProductName, userName, userEmail, OrderDate, Price, OrderStatus;

        public OrderListViewHolder(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.ivProductImage);
            orderId = itemView.findViewById(R.id.order_id);
            ProductName = itemView.findViewById(R.id.productName);
            userName = itemView.findViewById(R.id.name);
            userEmail = itemView.findViewById(R.id.email);
            OrderDate = itemView.findViewById(R.id.date);
            Price = itemView.findViewById(R.id.Price);
            OrderStatus = itemView.findViewById(R.id.OrderStatus);


        }

    }

}
