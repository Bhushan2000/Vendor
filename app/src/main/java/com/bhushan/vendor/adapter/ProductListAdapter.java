package com.bhushan.vendor.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bhushan.vendor.R;
import com.bhushan.vendor.activities.ViewProductActivity;
import com.bhushan.vendor.model.ProductsListModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ProductListViewHolder> implements Filterable {
    boolean check = false;
    Context context;
    private List<ProductsListModel> data;

    private List<ProductsListModel> updatedList;
    private int lastPosition = -1;

    public ProductListAdapter(Context context, List<ProductsListModel> data) {
        this.context = context;
        this.data = data;
        this.updatedList = new ArrayList<>(data);

    }

    @NonNull
    @Override
    public ProductListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.row_products, parent, false);
        return new ProductListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProductListViewHolder holder, int position) {

        final ProductsListModel productsListModel = data.get(position);


        holder.ProductName.setText(productsListModel.getProductName());
        holder.ProductPriceOriginal.setText(Integer.toString(productsListModel.getProductPriceOriginal()));


        holder.discountOff.setText(Double.toString(productsListModel.getDiscountPercentage()));


        holder.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!check) {
                    holder.fab.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#E91E63")));
                        check = true;
                }else{
                    holder.fab.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#d2d2d2")));
                     check = false;


                }


            }
        });


        Picasso.with(context).load(productsListModel.getProductImage()).placeholder(R.drawable.progress_animation).into(holder.ProductImage);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ViewProductActivity.class);
                intent.putExtra("id", productsListModel.getProductID());
                context.startActivity(intent);
            }
        });

        holder.ProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LayoutInflater layoutInflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                ImageView container = (ImageView) layoutInflater.inflate(R.layout.pop, null);

                final PopupWindow popUpWindow = new PopupWindow(container, 500, 500, true);
                popUpWindow.showAtLocation(view, Gravity.CENTER, 0, 0);


                Picasso.with(context).load(productsListModel.getProductImage())
                        .placeholder(R.drawable.progress_animation)
                        .into(container);

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
        return data.size();
    }

    @Override
    public Filter getFilter() {

        return filter;
    }

    Filter filter = new Filter() {


        // run on background thread
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ProductsListModel> productsListModelList = new ArrayList<>();
            if (constraint.toString().isEmpty()) {
                productsListModelList.addAll(updatedList);

            } else {
                for (ProductsListModel product : updatedList) {
                    if (product.getProductName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        productsListModelList.add(product);

                    }
                }

            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = productsListModelList;
            return filterResults;
        }

        // run on ui thread

        @Override
        protected void publishResults(CharSequence constraint, FilterResults filterResults) {
            data.clear();
            data.addAll((Collection<? extends ProductsListModel>) filterResults.values);
            notifyDataSetChanged();


        }
    };


    public class ProductListViewHolder extends RecyclerView.ViewHolder {
        CircleImageView  ProductImage;
        TextView ProductName, ProductPriceOriginal, discountOff;
        FloatingActionButton fab;

        public ProductListViewHolder(@NonNull View itemView) {
            super(itemView);

            ProductImage = itemView.findViewById(R.id.ivProductImage);
            ProductName = itemView.findViewById(R.id.tvProductName);
            ProductPriceOriginal = itemView.findViewById(R.id.tvOriginalPrice);
            discountOff = itemView.findViewById(R.id.tvOff);
            fab = itemView.findViewById(R.id.fab);


        }
    }


}
