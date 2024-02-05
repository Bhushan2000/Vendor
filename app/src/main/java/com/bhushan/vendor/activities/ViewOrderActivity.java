package com.bhushan.vendor.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.bhushan.vendor.R;
import com.bhushan.vendor.url.Links;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

public class ViewOrderActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private Dialog loadingDialogue;
    private String idSend;
    String id = "", pName = "", pCategory = "", pSubCategory = "", pOriginalPrice = "",  gstPercent = "",
    gstRupees = "", discountPercent = "", discountRupees = "", pTotalPrice = "", noOfPices = "", pDescription = "", date = "", status = "", image = "";

    private TextView tvOrderId;
    private TextView tvDeliveryStatus;
    private TextView tvOrderPlacedOn;
    private ImageView ivProductImage;
    private TextView tvProductName;
    private TextView tvFinalPrice;
    private Button btnMarkAsFulfiled;
    private Spinner sShippingStatus;
    private TextView tvProductPrice;
    private TextView tvDiscounntPrice;
    private TextView tvTaxPrice;
    private TextView tvTotalPrice;
    private TextView btnRefund;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("View Order");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        idSend = getIntent().getStringExtra("id");

        ///////////////////////////////////loading dialogue/////////////////////////////////////////////

        loadingDialogue = new Dialog(this);
        loadingDialogue.setContentView(R.layout.loadingprogressdialogue);
        loadingDialogue.setCancelable(false);
        loadingDialogue.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loadingDialogue.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        // loadingDialogue.show();

        /////////////////////////////////////loading dialogue/////////////////////////////////////////////


        tvOrderId = findViewById(R.id.tvOrderId);
        tvDeliveryStatus = findViewById(R.id.tvDeliveryStatus);
        tvOrderPlacedOn = findViewById(R.id.tvOrderPlacedOn);
        ivProductImage = findViewById(R.id.ivProductImage);
        tvProductName = findViewById(R.id.tvProductName);
        tvFinalPrice = findViewById(R.id.tvFinalPrice);
        btnMarkAsFulfiled = findViewById(R.id.btnMarkAsFulfiled);
        sShippingStatus = findViewById(R.id.sShippingStatus);
        tvProductPrice = findViewById(R.id.tvProductPrice);
        tvDiscounntPrice = findViewById(R.id.tvDiscountPercentage);
        tvTaxPrice = findViewById(R.id.tvTaxPrice);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnRefund = findViewById(R.id.btnRefund);




        // update the orders status(pending)
        sShippingStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        getData();


    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {

            finish();


        }

        return true;

    }




    private void getData() {

        loadingDialogue.show();

        AndroidNetworking.upload(Links.productDataGet)

                .addMultipartParameter("id", idSend)
                .setTag("MYSQL_UPLOAD")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        loadingDialogue.dismiss();

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
                                    pCategory = product.getString("pCategory");
                                    pSubCategory = product.getString("pSubCategory");
                                    image = product.getString("image");
                                    pOriginalPrice = product.getString("pOriginalPrice");
                                    discountPercent = product.getString("discountPercent");
                                    discountRupees = product.getString("discountRupees");

                                    noOfPices = product.getString("noOfPices");
                                    gstPercent = product.getString("gstPercent");
                                    gstRupees = product.getString("gstRupees");

                                    pTotalPrice = product.getString("total");
                                    pDescription = product.getString("pDescription");

                                    date = product.getString("date");
                                    status = product.getString("status");


                                }

                                tvOrderId.setText(id);
                                if (status.equalsIgnoreCase("1")){

                                    tvDeliveryStatus.setText("pending");
                                }else{
                                    tvDeliveryStatus.setText("completed");

                                }
                                tvFinalPrice.setText(pTotalPrice);
                                tvProductName.setText(pName);
                                tvDiscounntPrice.setText(discountPercent);
                                tvTotalPrice.setText( pTotalPrice);
                                tvOrderPlacedOn.setText(date);
                                tvProductPrice.setText(pOriginalPrice);
                                tvTaxPrice.setText(gstRupees);



                                Glide.with(ViewOrderActivity.this).load(image).placeholder(R.drawable.progress_animation)
                                        .into(ivProductImage);


                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(ViewOrderActivity.this, "JSONException " + e.getMessage(), Toast.LENGTH_LONG).show();

                            }


                        } else {
                            Toast.makeText(ViewOrderActivity.this, "NULL RESPONSE. ", Toast.LENGTH_LONG).show();

                        }

                    }


                    @Override
                    public void onError(ANError anError) {
                        loadingDialogue.dismiss();
                        anError.printStackTrace();
                        Toast.makeText(ViewOrderActivity.this, "UNSUCCESSFUL :  ERROR IS : n" + anError.getMessage(), Toast.LENGTH_LONG).show();

                    }
                });

    }

}