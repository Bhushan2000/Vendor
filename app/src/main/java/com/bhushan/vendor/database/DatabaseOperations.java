package com.bhushan.vendor.database;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;

import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;

import com.bhushan.vendor.R;
import com.bhushan.vendor.model.NotificationModel;
import com.bhushan.vendor.notification.MyVolley;
import com.bhushan.vendor.sharediPreference.SharedPrefManager;
import com.bhushan.vendor.url.EndPoints;
import com.bhushan.vendor.url.Links;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseOperations {


    // This Database Operation class is useful for reducing redundant code in different fragment
    // because of this class contain all static & public method we can access it when ever it is required



    public static List<NotificationModel> notificationModelList = new ArrayList<>();
    public static int mCartItemCount;
    public static int unreadedCount;

    public static String pathGet;
    public static int readed;


    // for fetching readed notifications
    public static void fetchData(final Context context, final Dialog loadingDialogue) {
        notificationModelList.clear();

        String user_id = SharedPrefManager.getInstance(context).getUserID();

        AndroidNetworking.upload(Links.fetchMarkAsReaded)
                .addMultipartParameter("vid", user_id)
                .setTag("Test")
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        loadingDialogue.dismiss();
                        try {
                            JSONArray jsonArray = new JSONArray(response);

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                int id = jsonObject.getInt("id");
                                String title = jsonObject.getString("title");
                                String img = jsonObject.getString("img");
                                String msg = jsonObject.getString("msg");
                                String date = jsonObject.getString("date");
                                readed = jsonObject.getInt("readed");


                                notificationModelList.add(new NotificationModel(id, title, img, msg, date, readed));


                            }
                            mCartItemCount = notificationModelList.size();


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();

                        }


                    }

                    @Override
                    public void onError(ANError anError) {

                        loadingDialogue.dismiss();
                        Toast.makeText(context, anError.toString(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    // for notification badge count i.e., unreded notification
    public static void countUnreadNoti(final Context context) {
        String user_id = SharedPrefManager.getInstance(context).getUserID();

        AndroidNetworking.upload(Links.unreadedCount)
                .addMultipartParameter("vid", user_id)
                .setTag("Test")
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            boolean status = jsonObject.getBoolean("status");
                            unreadedCount = jsonObject.getInt("unreadedCount");

                        } catch (Exception e) {
                            e.printStackTrace();

                        }


                    }

                    @Override
                    public void onError(ANError anError) {

                        Toast.makeText(context, anError.toString(), Toast.LENGTH_SHORT).show();

                    }
                });

    }

    // for loading profile when user open the app
    public static void loadProfile(final Context context, final ProgressDialog progressDialog, final ImageView profileImage) {

        String user_id = SharedPrefManager.getInstance(context).getUserID();

        AndroidNetworking.upload(Links.single)
                .addMultipartParameter("vid", user_id)
                .setPriority(Priority.HIGH)
                .build()
                .setUploadProgressListener(new UploadProgressListener() {
                    @Override
                    public void onProgress(long bytesUploaded, long totalBytes) {

                        float progress = (float) bytesUploaded / totalBytes * 100;
                        progressDialog.setProgress((int) progress);
                    }
                })
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            progressDialog.dismiss();

                            // converting the string to json array object
                            JSONArray array = new JSONArray(response);


                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {

                                //getting product object from json array
                                JSONObject product = array.getJSONObject(i);

//                                descGet = product.getString("desc");
                                pathGet = product.getString("path");
                            }


                            Glide.with(context.getApplicationContext()).load(pathGet)
                                    .placeholder(R.drawable.ic_baseline_account_circle_24)
                                    .into(profileImage); //pass imageView reference to appear the image.

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Parsing Error", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        progressDialog.dismiss();
                        anError.printStackTrace();
                        Toast.makeText(context, "Slow or no internet connection", Toast.LENGTH_SHORT).show();
                    }
                });


    }

    // for sending Notification
    public static void sendSinglePush(final Context context,
                                      final String title,
                                      final String message,
                                      final String image,
                                      final Dialog loadingDialogue) {


        final String email = SharedPrefManager.getInstance(context).getUserEmail();

        loadingDialogue.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.URL_SEND_SINGLE_PUSH,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loadingDialogue.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("title", title);
                params.put("message", message);
                if (!TextUtils.isEmpty(image))
                    params.put("image", image);

                params.put("email", email);
                return params;
            }
        };

        MyVolley.getInstance(context).addToRequestQueue(stringRequest);
    }

    public static void insertNoti(final Context context, String title, String image, String message, final Dialog loadingDialogue) {

        String user_id = SharedPrefManager.getInstance(context).getUserID();

        AndroidNetworking.upload(Links.insertNoti)
                .addMultipartParameter("title", title)
                .addMultipartParameter("image", image)
                .addMultipartParameter("message", message)
                .addMultipartParameter("readed", String.valueOf(0))
                .addMultipartParameter("vid", user_id)
                .setTag("Test")
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        loadingDialogue.dismiss();
                    }

                    @Override
                    public void onError(ANError anError) {
                        loadingDialogue.dismiss();
                        Toast.makeText(context, anError.toString(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    public static void loadHomeData(final Context context,
                                    final Dialog loadingDialogue,
                                    final TextView tvOrdersToday,
                                    final TextView tvOrdersThisMonth,
                                    final TextView tvOrdersThisYear,
                                    final TextView tvOrdersTotal,
                                    final TextView tvOrdersNew,
                                    final TextView tvOrdersConfirmed,
                                    final TextView tvOrdersOnHold,
                                    final TextView tvOrdersInProcess,
                                    final TextView tvOrdersCompleted,
                                    final TextView tvOrdersCancelled,
                                    final TextView orders,
                                    final TextView viewOrders
    ) {


        loadingDialogue.show();
        String user_id = SharedPrefManager.getInstance(context).getUserID();

        AndroidNetworking.upload(Links.getdata)
                .addMultipartParameter("vid",user_id)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {

                        loadingDialogue.dismiss();

                        try {
                            JSONObject job = new JSONObject(response);
                            boolean error = job.getBoolean("error");
                            if (!error) {
                                String message = job.getString("message");
                                String today = job.getString("today");
                                String month = job.getString("month");
                                String year = job.getString("year");
                                String total = job.getString("total");
                                String New = job.getString("new");
                                String confirmed = job.getString("confirmed");
                                String onhold = job.getString("onhold");
                                String inprocess = job.getString("inprocess");
                                String completed = job.getString("completed");
                                String cancelled = job.getString("cancelled");


                                tvOrdersToday.setText(today);
                                tvOrdersThisMonth.setText(month);
                                tvOrdersThisYear.setText(year);
                                tvOrdersTotal.setText(total);
                                tvOrdersNew.setText(New);
                                tvOrdersConfirmed.setText(confirmed);
                                tvOrdersOnHold.setText(onhold);
                                tvOrdersInProcess.setText(inprocess);
                                tvOrdersCompleted.setText(completed);
                                tvOrdersCancelled.setText(cancelled);

                                // badges
                                orders.setText(total);
                                viewOrders.setText(total);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        loadingDialogue.dismiss();
                        anError.printStackTrace();
                        Toast.makeText(context, anError.toString(), Toast.LENGTH_SHORT).show();

                    }
                });

    }


    public static void markAsReaded(final Context context) {
        String user_id = SharedPrefManager.getInstance(context).getUserID();

        AndroidNetworking.upload(Links.markAsReaded)
                .addMultipartParameter("vid", user_id)
                .setTag("test")
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                        Toast.makeText(context, anError.toString(), Toast.LENGTH_SHORT).show();

                    }
                });


    }
}
