package com.bhushan.vendor.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.bhushan.vendor.R;
import com.bhushan.vendor.adapter.ViewPagerAdapter;
import com.bhushan.vendor.fragments.ViewProductFragment;
import com.bhushan.vendor.model.SliderModel;
import com.bhushan.vendor.url.Links;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ViewProductActivity extends AppCompatActivity {
    ////////// New ViewProduct
    TextView productName, productCategory, productSubCategory, productDiscountPercent, productDealPrice, productInStock, productDetails, PostDate;


    RelativeLayout relativeLayout;
    String idSend;
    private ImageView noInternet;

    private Button retry;
    LinearLayout linearLayout;
    private Toolbar toolbar;
    private Button btnShare, btnDelete;


    String id = "", pName = "", pCategory = "", pSubCategory = "", discountPercent = "", pTotalPrice = "", noOfPices = "", pDescription = "", date = "", status = "", image = "";

    private List<SliderModel> sliderModelList;


    ViewPager viewPager;
    LinearLayout sliderDotspanel;
    private int dotscount;
    private ImageView[] dots;
    ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_product);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("View");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        productName = findViewById(R.id.ProductName);
        productCategory = findViewById(R.id.productCategory);
        productSubCategory = findViewById(R.id.productSubCategory);
        productDiscountPercent = findViewById(R.id.discountPercent);
        productDealPrice = findViewById(R.id.productPrice);
        productInStock = findViewById(R.id.inStock);
        productDetails = findViewById(R.id.Details);
        PostDate = findViewById(R.id.PostDate);
        relativeLayout = findViewById(R.id.relativeLayout);
        noInternet = findViewById(R.id.noInternet);
        retry = findViewById(R.id.retry);
        linearLayout = findViewById(R.id.l);
        sliderModelList = new ArrayList<>();


        idSend = getIntent().getStringExtra("id");

        viewPager = (ViewPager) findViewById(R.id.viewPager);

        sliderDotspanel = (LinearLayout) findViewById(R.id.SliderDots);


        btnShare = findViewById(R.id.btnShare);
        btnDelete = findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {


                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(ViewProductActivity.this);
                builder.setTitle("Delete");
                builder.setMessage("Ae you sure do you want to delete");
                builder.setIcon(R.drawable.ic_delete);
                builder.setBackground(getResources().getDrawable(R.drawable.slider_background, null));
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        deleteProduct();


                    }
                }).setNegativeButton("cancel", null);

                builder.create().show();


            }
        });


        shareProduct();

        getData();
    }

    private void deleteProduct() {


        AndroidNetworking.upload(Links.productDelete)
                .addMultipartParameter("id", idSend)
                .addMultipartParameter("aid", idSend)
                .setTag("MYSQL_UPLOAD")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject job = new JSONObject(response);
                            String status = job.getString("message");
                            Snackbar.make(btnDelete, status, Snackbar.LENGTH_SHORT).show();
                            ViewProductFragment.productListAdapter.notifyDataSetChanged();


                        } catch (Exception e) {
                            e.printStackTrace();
                            Snackbar.make(btnDelete, e.getMessage(), Snackbar.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                        anError.printStackTrace();
                        Toast.makeText(ViewProductActivity.this, "JSONException " + anError.getMessage(), Toast.LENGTH_LONG).show();

                    }
                });


    }

    private void shareProduct() {
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String shareBody = "Your body is here";
                String shareSub = "Your subject is here";
                intent.putExtra(Intent.EXTRA_SUBJECT, shareSub);
                intent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(intent, "Share Using"));
            }
        });

    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();


        }

        return super.onOptionsItemSelected(item);
    }


    private void getData() {

        relativeLayout.setVisibility(View.VISIBLE);


        AndroidNetworking.upload(Links.productDataGet)

                .addMultipartParameter("id", idSend)
                .setTag("MYSQL_UPLOAD")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        relativeLayout.setVisibility(View.GONE);

                        if (response != null) {
                            try {
                                //SHOW RESPONSE FROM SERVER
                                JSONArray jObj = new JSONArray(response);


                                //traversing through all the object
                                for (int i = 0; i < jObj.length(); i++) {
                                    //getting product object from json array
                                    JSONObject product = jObj.getJSONObject(i);


                                    id = product.getString("id");
                                    pName = product.getString("pName");


                                    discountPercent = product.getString("discountPercent");
                                    noOfPices = product.getString("noOfPices");

                                    pCategory = product.getString("pCategory");
                                    pSubCategory = product.getString("pSubCategory");


                                    image = product.getString("image");
                                    pTotalPrice = product.getString("pOriginalPrice");
                                    pDescription = product.getString("pDescription");

                                    date = product.getString("date");
                                    status = product.getString("status");


                                }

                                productName.setText(pName);

                                productCategory.setText(pCategory);
                                productSubCategory.setText(pSubCategory);
                                productDiscountPercent.setText(discountPercent);
                                productDealPrice.setText("₹" + pTotalPrice);
                                productInStock.setText(noOfPices);
                                productDetails.setText(pDescription);
                                PostDate.setText(date);
                                // Glide.with(ViewProductActivity.this).load(image).into(imgProduct);

                                sliderModelList.add(new SliderModel(id, image, image));
                                viewPagerAdapter = new ViewPagerAdapter(getApplicationContext(), sliderModelList);
                                viewPager.setAdapter(viewPagerAdapter);


                                dotscount = viewPagerAdapter.getCount();
                                dots = new ImageView[dotscount];

                                for (int i = 0; i < dotscount; i++) {

                                    dots[i] = new ImageView(getApplicationContext());
                                    dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.indicator_inactive));

                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                                    params.setMargins(8, 0, 8, 0);

                                    sliderDotspanel.addView(dots[i], params);

                                }

                                dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.indicator_active));

                                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                                    @Override
                                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                                    }

                                    @Override
                                    public void onPageSelected(int position) {

                                        for (int i = 0; i < dotscount; i++) {
                                            dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.indicator_inactive));
                                        }

                                        dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.indicator_active));

                                    }

                                    @Override
                                    public void onPageScrollStateChanged(int state) {

                                    }
                                });


                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(ViewProductActivity.this, "JSONException " + e.getMessage(), Toast.LENGTH_LONG).show();

                            }


                        } else {
                            Toast.makeText(ViewProductActivity.this, "NULL RESPONSE. ", Toast.LENGTH_LONG).show();

                        }

                    }


                    @Override
                    public void onError(ANError anError) {
                        relativeLayout.setVisibility(View.GONE);
                        anError.printStackTrace();
                        Toast.makeText(ViewProductActivity.this, "UNSUCCESSFUL :  ERROR IS : n" + anError.getMessage(), Toast.LENGTH_LONG).show();

                    }
                });

        }


}

