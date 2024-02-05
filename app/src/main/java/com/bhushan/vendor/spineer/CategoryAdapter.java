package com.bhushan.vendor.spineer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bhushan.vendor.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class CategoryAdapter extends ArrayAdapter<CategoryItem> {

    public CategoryAdapter(Context context, ArrayList<CategoryItem> categoryItemArrayList){

        super(context,0, categoryItemArrayList);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return  initView(position,convertView,parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return  initView(position,convertView,parent);
    }

    private View initView(int position,View convertView,ViewGroup parent){
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.country_spineer_row,parent,false);

        }

        ImageView imageViewFlag = convertView.findViewById(R.id.ivFlag);
        TextView countryName = convertView.findViewById(R.id.tvCountryName);

        CategoryItem currentItem = getItem(position);

        if (currentItem !=null){


            Glide.with(getContext()).load(currentItem.getCategoryImage()).into(imageViewFlag);
            countryName.setText(currentItem.getCategoryName());
            currentItem.getCategoryId();

        }
        return convertView;


    }
}
