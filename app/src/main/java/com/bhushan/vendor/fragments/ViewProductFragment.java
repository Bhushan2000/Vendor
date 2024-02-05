package com.bhushan.vendor.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SearchView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.bhushan.vendor.R;
import com.bhushan.vendor.adapter.OrderListAdapter;
import com.bhushan.vendor.adapter.ProductListAdapter;
import com.bhushan.vendor.model.OrderListModel;
import com.bhushan.vendor.model.ProductsListModel;
import com.bhushan.vendor.sharediPreference.SharedPrefManager;
import com.bhushan.vendor.url.Links;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ViewProductFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewProductFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ViewProductFragment() {
        // Required empty public constructor
    }

    public static ViewProductFragment newInstance(String param1, String param2) {
        ViewProductFragment fragment = new ViewProductFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    RecyclerView ProductRecyclerView;
    List<ProductsListModel> s1;
   public static ProductListAdapter productListAdapter;
    private RelativeLayout relativeLayout;
    private ImageView noInternet;

    private ProgressDialog progressDialog;
    private Button retry;
    private TextView tvNoDataFound;
    String user_id;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_product, container, false);
        user_id =   SharedPrefManager.getInstance(getContext()).getUserID();

        s1 = new ArrayList<>();

        ProductRecyclerView = view.findViewById(R.id.productList);
        ProductRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        tvNoDataFound = view.findViewById(R.id.tvNoDataFound);

        relativeLayout = view.findViewById(R.id.relativeLayout);
        noInternet = view.findViewById(R.id.noInternet);
        retry = view.findViewById(R.id.retry);

        progressDialog = new ProgressDialog(getContext());



        loadProductList();


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search_sort, menu);

        MenuItem items = menu.findItem(R.id.action_search);


        SearchView searchView = (SearchView) MenuItemCompat.getActionView(items);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                productListAdapter.getFilter().filter(newText);
                return false;
            }
        });


        super.onCreateOptionsMenu(menu, inflater);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case (R.id.action_search):
                //search
                break;
            case (R.id.action_sort):
                //sort\
                String options[] = {"Price(high to low)", "Price(low to high)", "Newest first", "Name"};
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
                builder.setTitle("Sort by");
                builder.setBackground(getResources().getDrawable(R.drawable.slider_background, null));
                // single Choice
                builder.setSingleChoiceItems(options, 2, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (which == 0) {
                            //sort by price(high to low)
                            Snackbar.make(getView(), "sort by price(high to low)", Snackbar.LENGTH_LONG).show();
                            dialog.dismiss();
                            s1.clear();
                            getDataFromServerSortPriceDescending();

                        } else if (which == 1) {
                            //sort by price(low to high)
                            Snackbar.make(getView(), "sort by price(low to high)", Snackbar.LENGTH_LONG).show();
                            dialog.dismiss();
                            s1.clear();
                            getDataFromServerSortPriceAscending();
                        } else if (which == 2) {
                            //sort by newest first
                            Snackbar.make(getView(), "sort by newest first", Snackbar.LENGTH_LONG).show();
                            dialog.dismiss();
                            s1.clear();
                            getDataFromServerSortByDate();

                        } else if (which == 3) {
                            //sort by name
                            Snackbar.make(getView(), "sort by name", Snackbar.LENGTH_LONG).show();
                            dialog.dismiss();
                            s1.clear();
                            getDataFromServerSortByName();

                        }
                    }
                });


                builder.create().show();

                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void getDataFromServerSortPriceAscending() {

        relativeLayout.setVisibility(View.VISIBLE);
        AndroidNetworking.upload(Links.productFetch)
                .addMultipartParameter("vid",user_id)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        relativeLayout.setVisibility(View.GONE);
                        try {
                            // converting the string to json array object
                            JSONArray array = new JSONArray(response);


                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {

                                //getting product object from json array
                                JSONObject product = array.getJSONObject(i);

                                String id = product.getString("id");
                                String pName = product.getString("pName");
                                String image = product.getString("image");

                                int pOriginalPrice = product.getInt("pOriginalPrice");
                                double discountPercent = product.getDouble("discountPercent");


                                //adding the product to product list
                                s1.add(new ProductsListModel(id, image, pName, pOriginalPrice, discountPercent));
                            }
                            //creating adapter object and setting it to recyclerview
                            productListAdapter = new ProductListAdapter(getContext(), s1);
                            ProductRecyclerView.setAdapter(productListAdapter);
                            productListAdapter.notifyDataSetChanged();


                            //   sort

                            Collections.sort(s1, new Comparator<ProductsListModel>() {
                                @Override
                                public int compare(ProductsListModel o1, ProductsListModel o2) {
                                    if (o1.getProductPriceOriginal() > o2.getProductPriceOriginal()) {
                                        return 1;

                                    } else {
                                        return -1;

                                    }
                                }
                            });


                        } catch (JSONException e) {
                            relativeLayout.setVisibility(View.GONE);

                            e.printStackTrace();
                            Snackbar.make(retry, "check your internet connection", Snackbar.LENGTH_SHORT).show();

                        }


                    }

                    @Override
                    public void onError(ANError anError) {
                        relativeLayout.setVisibility(View.GONE);
                        Toast.makeText(getContext(), anError.toString(), Toast.LENGTH_SHORT).show();
                    }
                });


    }

    private void getDataFromServerSortPriceDescending() {


        relativeLayout.setVisibility(View.VISIBLE);
        AndroidNetworking.upload(Links.productFetch)
                .addMultipartParameter("vid",user_id)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        relativeLayout.setVisibility(View.GONE);
                        try {
                            // converting the string to json array object
                            JSONArray array = new JSONArray(response);


                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {

                                //getting product object from json array
                                JSONObject product = array.getJSONObject(i);

                                String id = product.getString("id");
                                String pName = product.getString("pName");
                                String image = product.getString("image");

                                int pOriginalPrice = product.getInt("pOriginalPrice");
                                double discountPercent = product.getDouble("discountPercent");


                                //adding the product to product list
                                s1.add(new ProductsListModel(id, image, pName, pOriginalPrice, discountPercent));
                            }
                            //creating adapter object and setting it to recyclerview
                            productListAdapter = new ProductListAdapter(getContext(), s1);
                            ProductRecyclerView.setAdapter(productListAdapter);
                            productListAdapter.notifyDataSetChanged();


                            //   sort descending

                            Collections.sort(s1, new Comparator<ProductsListModel>() {
                                @Override
                                public int compare(ProductsListModel o1, ProductsListModel o2) {
                                    if (o1.getProductPriceOriginal() > o2.getProductPriceOriginal()) {
                                        return -1;

                                    } else {
                                        return 1;

                                    }
                                }
                            });


                        } catch (JSONException e) {
                            relativeLayout.setVisibility(View.GONE);

                            e.printStackTrace();
                            Toast.makeText(getContext(), "No data Found", Toast.LENGTH_SHORT).show();

                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        relativeLayout.setVisibility(View.GONE);
                        Toast.makeText(getContext(), anError.toString(), Toast.LENGTH_SHORT).show();
                    }
                });


    }

    private void loadProductList() {


        relativeLayout.setVisibility(View.VISIBLE);
        AndroidNetworking.upload(Links.productFetch)
                .addMultipartParameter("vid",user_id)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        relativeLayout.setVisibility(View.GONE);


                        try {
                            // converting the string to json array object
                            JSONArray array = new JSONArray(response);


                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {

                                //getting product object from json array
                                JSONObject product = array.getJSONObject(i);

                                String id = product.getString("id");
                                String pName = product.getString("pName");
                                String image = product.getString("image");
                                String pCategory = product.getString("pCategory");
                                String pSubCategory = product.getString("pSubCategory");

                                String date = product.getString("date");


                                int pOriginalPrice = product.getInt("pOriginalPrice");
                                double discountPercent = product.getDouble("discountPercent");


                                //adding the product to product list
                                s1.add(new ProductsListModel(id, image, pName, pOriginalPrice,

                                        discountPercent));
                            }
                            //creating adapter object and setting it to recyclerview
                            productListAdapter = new ProductListAdapter(getContext(), s1);
                            ProductRecyclerView.setAdapter(productListAdapter);
                            productListAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            relativeLayout.setVisibility(View.GONE);

                            e.printStackTrace();
                            tvNoDataFound.setVisibility(View.VISIBLE);
                            Toast.makeText(getContext(), "No Data Found", Toast.LENGTH_SHORT).show();

                        }
                    }


                    @Override
                    public void onError(ANError anError) {
                        relativeLayout.setVisibility(View.GONE);
                        Toast.makeText(getContext(), anError.toString(), Toast.LENGTH_SHORT).show();
                    }
                });


    }

    private void getDataFromServerSortByName() {


        relativeLayout.setVisibility(View.VISIBLE);
        AndroidNetworking.upload(Links.productSortByName)

                .addMultipartParameter("vid",user_id)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        relativeLayout.setVisibility(View.GONE);
                        try {
                            // converting the string to json array object
                            JSONArray array = new JSONArray(response);


                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {

                                //getting product object from json array
                                JSONObject product = array.getJSONObject(i);

                                String id = product.getString("id");
                                String pName = product.getString("pName");
                                String image = product.getString("image");

                                int pOriginalPrice = product.getInt("pOriginalPrice");
                                double discountPercent = product.getDouble("discountPercent");


                                //adding the product to product list
                                s1.add(new ProductsListModel(id, image, pName, pOriginalPrice, discountPercent));
                            }
                            //creating adapter object and setting it to recyclerview
                            productListAdapter = new ProductListAdapter(getContext(), s1);
                            ProductRecyclerView.setAdapter(productListAdapter);
                            productListAdapter.notifyDataSetChanged();


                        } catch (JSONException e) {
                            relativeLayout.setVisibility(View.GONE);

                            e.printStackTrace();
                            Toast.makeText(getContext(), "No Data Found", Toast.LENGTH_SHORT).show();

                        }


                    }

                    @Override
                    public void onError(ANError anError) {
                        relativeLayout.setVisibility(View.GONE);
                        Toast.makeText(getContext(), anError.toString(), Toast.LENGTH_SHORT).show();
                    }
                });


    }

    private void getDataFromServerSortByDate() {

        relativeLayout.setVisibility(View.VISIBLE);
        AndroidNetworking.upload(Links.productSortByDate)
                .addMultipartParameter("vid",user_id)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        relativeLayout.setVisibility(View.GONE);
                        try {
                            // converting the string to json array object
                            JSONArray array = new JSONArray(response);


                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {

                                //getting product object from json array
                                JSONObject product = array.getJSONObject(i);

                                String id = product.getString("id");
                                String pName = product.getString("pName");
                                String image = product.getString("image");

                                int pOriginalPrice = product.getInt("pOriginalPrice");
                                double discountPercent = product.getDouble("discountPercent");


                                //adding the product to product list
                                s1.add(new ProductsListModel(id, image, pName, pOriginalPrice, discountPercent));
                            }
                            //creating adapter object and setting it to recyclerview
                            productListAdapter = new ProductListAdapter(getContext(), s1);
                            ProductRecyclerView.setAdapter(productListAdapter);
                            productListAdapter.notifyDataSetChanged();


                        } catch (JSONException e) {
                            relativeLayout.setVisibility(View.GONE);

                            e.printStackTrace();
                            Toast.makeText(getContext(), "No Data Found", Toast.LENGTH_SHORT).show();

                        }


                    }

                    @Override
                    public void onError(ANError anError) {
                        relativeLayout.setVisibility(View.GONE);
                        Toast.makeText(getContext(), anError.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

    }
}