package com.bhushan.vendor.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bhushan.vendor.R;
import com.bhushan.vendor.database.SQLiteHandler;
import com.bhushan.vendor.sharediPreference.SharedPrefManager;
import com.bhushan.vendor.url.Links;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;


public class LogInActivity extends AppCompatActivity {

    public ConnectivityManager connectivityManager;
    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;
    private TextView tvProcessed_to_register;
    private ProgressDialog progressDialog;
    private static final String TAG = "LogInActivity";


    private SQLiteHandler db;

    private TextInputLayout tilEmail;
    private TextInputLayout tilPassword;
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +         //at least 1 digit
                    "(?=.*[a-z])" +         //at least 1 lower case letter
                    "(?=.*[A-Z])" +         //at least 1 upper case letter
                    //"(?=.*[a-zA-Z])" +      //any letter
                    "(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{4,}" +               //at least 4 characters
                    "$");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, NavigationActivity.class));
            return;
        }

        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);


        etEmail = findViewById(R.id.etEmail);

        etPassword = findViewById(R.id.etPassword);

        btnLogin = findViewById(R.id.btnLogin);

        tvProcessed_to_register = findViewById(R.id.tvProcessed_to_register);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in ...");

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());


        tvProcessed_to_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(LogInActivity.this, RegisterActivity.class));

            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {

                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();


                if (!validateEmail() | !validatePassword()) {
                    return;

                }
                setLogin(email, password);


            }
        });


    }

    private boolean validateEmail() {
        String inputEmail = tilEmail.getEditText().getText().toString().trim();

        if (inputEmail.isEmpty()) {
            tilEmail.setError("Email field can't be empty");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(inputEmail).matches()) {

            tilEmail.setError("Please enter a valid email address");
            return false;

        } else {
            tilEmail.setError(null);
            return true;

        }
    }

    private boolean validatePassword() {
        String inputPassword = tilPassword.getEditText().getText().toString().trim();

        if (inputPassword.isEmpty()) {
            tilPassword.setError("Password field can't be empty");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(inputPassword).matches()) {
            tilPassword.setError("Password Conditions: \n" +
                    "At least 1 digit\n" +
                    "At least 1 lower case letter\n" +
                    "At least 1 upper case letter\n" +
                    "At least 1 special character\n" +
                    "No white spaces\n" +
                    "At least 4 characters");
            return false;
        } else {
            tilPassword.setError(null);
            return true;

        }
    }


    public boolean isNetWorkAvailable() {
        // Internet Connectivity
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();


        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            return true;

        } else {
            return false;
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setLogin(final String email, final String password) {


            progressDialog.show();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Links.URL_LOGIN, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progressDialog.dismiss();
                    try {
                        JSONObject jObj = new JSONObject(response);
                        boolean error = jObj.getBoolean("error");
                        // Check for error node in json
                        if (!error) {
                            // user successfully logged in


                            // Now store the user in SQLite
                            String uid = jObj.getString("uid");


                            JSONObject user = jObj.getJSONObject("user");
                            String id = user.getString("id");
                            String name = user.getString("name");
                            String email = user.getString("email");
                            String mobileNo = user.getString("mobileNo");
                            String address = user.getString("address");
                            String pincode = user.getString("pincode");
                            String created_at = user.getString("created_at");


                            // Inserting row in users table
                            db.addUser(name, email, uid, created_at, mobileNo, address, pincode);

                            SharedPrefManager.getInstance(getApplicationContext()).userLogin(

                                    jObj.getString("uid"),
                                    user.getString("id"),
                                    user.getString("name"),
                                    user.getString("email"),
                                    user.getString("mobileNo"),
                                    user.getString("address"),
                                    user.getString("pincode"),
                                    user.getString("created_at")


                            );

                            // Launch main activity
                            Intent intent = new Intent(LogInActivity.this,
                                    NavigationActivity.class);

                             Snackbar.make(btnLogin,"Welcome User", Snackbar.LENGTH_LONG).show();

                             startActivity(intent);
                            finish();
                        } else {
                            // Error in login. Get the error message
                            String errorMsg = jObj.getString("error_msg");
                            Snackbar.make(btnLogin,errorMsg, Snackbar.LENGTH_LONG).show();

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.i(TAG, "onResponse:JSONException............................................................ ", e);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Log.i(TAG, "onErrorResponse:.............................. ,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,", error);
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("email", email);
                    params.put("password", password);


                    return params;

                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(6000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


            RequestHandler.getInstance(this).addToRequestQueue(stringRequest);



    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

    }
}