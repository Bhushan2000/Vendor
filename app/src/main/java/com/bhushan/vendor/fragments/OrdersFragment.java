package com.bhushan.vendor.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.bhushan.vendor.activities.LogInActivity;
import com.bhushan.vendor.adapter.OrderListAdapter;
import com.bhushan.vendor.R;
import com.bhushan.vendor.model.OrderListModel;
import com.bhushan.vendor.sharediPreference.SharedPrefManager;
import com.bhushan.vendor.url.Links;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrdersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrdersFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OrdersFragment() {
        // Required empty public constructor
    }

    public static OrdersFragment newInstance(String param1, String param2) {
        OrdersFragment fragment = new OrdersFragment();
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

    private RecyclerView OrderRecyclerView;
    private List<OrderListModel> orderListModelList;
    public static OrderListAdapter orderListAdapter;
    private RelativeLayout relativeLayout;
    private ImageView noInternet;

    private ProgressDialog progressDialog;
    private Button retry;
    private TextView tvNoDataFound;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_orders, container, false);



        if (!SharedPrefManager.getInstance(getContext()).isLoggedIn()) {
            getActivity().finish();
            startActivity(new Intent(getContext(), LogInActivity.class));
        }






        OrderRecyclerView = view.findViewById(R.id.OrderList);
        OrderRecyclerView.setHasFixedSize(true);
        OrderRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //initializing the productlist
        orderListModelList = new ArrayList<>();
        relativeLayout = view.findViewById(R.id.relativeLayout);
        noInternet = view.findViewById(R.id.noInternet);
        retry = view.findViewById(R.id.retry);

        progressDialog = new ProgressDialog(getContext());

        tvNoDataFound = view.findViewById(R.id.tvNoDataFound);


        loadList();


        return view;

    }

    public boolean isNetWorkAvailable() {
        // Internet Connectivity
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();


        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            return true;

        } else {
            return false;
        }

    }


    private void loadList() {

        String user_id =   SharedPrefManager.getInstance(getContext()).getUserID();
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
                                String pTotalPrice = product.getString("pOriginalPrice");
                                String date = product.getString("date");
                                String status = product.getString("status");


                                //adding the product to product list
                                orderListModelList.add(new OrderListModel(
                                        id, pName, image, pTotalPrice, date, status
                                ));
                            }
                            //creating adapter object and setting it to recyclerview
                            orderListAdapter = new OrderListAdapter(getContext(), orderListModelList);
                            OrderRecyclerView.setAdapter(orderListAdapter);
                            orderListAdapter.notifyDataSetChanged();


                        } catch (JSONException e) {
                            relativeLayout.setVisibility(View.GONE);
//
//                            e.printStackTrace();
                            tvNoDataFound.setVisibility(View.VISIBLE);
                            Toast.makeText(getContext(), e.toString() , Toast.LENGTH_SHORT).show();
                        }


                    }

                    @Override
                    public void onError(ANError anError) {
                        relativeLayout.setVisibility(View.GONE);
                        Toast.makeText(getContext(), anError.toString(), Toast.LENGTH_SHORT).show();
                    }
                });


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
        menu.findItem(R.id.action_sort).setVisible(false);


        SearchView searchView = (SearchView) MenuItemCompat.getActionView(items);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                orderListAdapter.getFilter().filter(newText);
                return false;
            }
        });


        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case (R.id.action_search):
                //search
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}
