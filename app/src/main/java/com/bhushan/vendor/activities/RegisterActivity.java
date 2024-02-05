package com.bhushan.vendor.activities;

import androidx.annotation.NonNull;
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
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.bhushan.vendor.AndroidNetworking.MyApp;
import com.bhushan.vendor.R;
import com.bhushan.vendor.database.SQLiteHandler;
 import com.bhushan.vendor.url.EndPoints;
import com.bhushan.vendor.url.Links;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdReceiver;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;


public class RegisterActivity extends AppCompatActivity {
    private EditText etName;
    private EditText etEmail;
    private EditText etAddress;
    private EditText etMobileNo;
    private EditText etPinCode;
    private EditText etPassword;
    private Button btnRegister;
    private TextView tvProcessedToLogin;
    private ProgressDialog progressDialog;

    private TextInputLayout tilName;
    private TextInputLayout tilEmail;
    private TextInputLayout tilAddress;
    private TextInputLayout tilMobileMo;
    private TextInputLayout tilPincode;
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

    private SQLiteHandler db;

    private String sToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);






        tilName = findViewById(R.id.tilName);
        tilEmail = findViewById(R.id.tilEmail);
        tilAddress = findViewById(R.id.tilAddress);
        tilMobileMo = findViewById(R.id.tilMobileNumber);
        tilPincode = findViewById(R.id.tilPincode);
        tilPassword = findViewById(R.id.tilPassword);


        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etAddress = findViewById(R.id.etAddress);
        etMobileNo = findViewById(R.id.etMobileNo);
        etPinCode = findViewById(R.id.etPincode);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvProcessedToLogin = findViewById(R.id.tvProcessed_to_login);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering user ...");



//        FirebaseMessaging.getInstance().subscribeToTopic("test");
//
//        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
//            @Override
//            public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<InstanceIdResult> task) {
//                if (task.isSuccessful()) {
//                    sToken = task.getResult().getToken();
//
//                    //   sendTokenToServer();
//                } else {
//                    Toast.makeText(RegisterActivity.this, "Token:" + task.getException().toString(), Toast.LENGTH_LONG).show();
//                }
//
//            }
//        });
//



        tvProcessedToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(RegisterActivity.this, LogInActivity.class));

            }
        });


        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {


                if (!validateName() | !validateEmail() | !validateMobileNo() | !validateAddress() | !validatePincode() | !validatePassword()) {
                    return;

                }

                setRegister();



            }
        });


    }

    public boolean isNetWorkAvailable() {
        // Internet Connectivity
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();


        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            return true;

        } else {
            return false;
        }

    }

    private boolean validateName() {
        String inputName = tilName.getEditText().getText().toString().trim();

        if (inputName.isEmpty()) {
            tilName.setError("Name field can't be empty");
            return false;
        } else {
            tilName.setError(null);
            return true;

        }
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

    private boolean validateMobileNo() {
        String inputMobileNo = tilMobileMo.getEditText().getText().toString().trim();

        if (inputMobileNo.isEmpty()) {
            tilMobileMo.setError("Mobile No field can't be empty");
            return false;
        } else {
            tilMobileMo.setError(null);
            return true;

        }
    }

    private boolean validateAddress() {
        String inputAddress = tilAddress.getEditText().getText().toString().trim();

        if (inputAddress.isEmpty()) {
            tilAddress.setError("Address field can't be empty");
            return false;
        } else {
            tilAddress.setError(null);
            return true;

        }
    }

    private boolean validatePincode() {
        String inputPincode = tilPincode.getEditText().getText().toString().trim();

        if (inputPincode.isEmpty()) {
            tilPincode.setError("Pincode field can't be empty");
            return false;
        } else {
            tilPincode.setError(null);
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


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setRegister() {
        final String name = etName.getText().toString();
        final String address = etAddress.getText().toString();
        final String pincode = etPinCode.getText().toString();
        final String email = etEmail.getText().toString();
        final String password = etPassword.getText().toString();
        final String mobileNo = etMobileNo.getText().toString();
        if (isNetWorkAvailable()) {
            progressDialog.show();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Links.URL_REGISTER, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progressDialog.dismiss();

                    try {
                        JSONObject jObj = new JSONObject(response);
                        boolean error = jObj.getBoolean("error");
                        if (!error) {
                            // User successfully stored in MySQL
                            // Now store the user in sqlite
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


                            Snackbar.make(btnRegister, "User successfully registered. Try login now!", Snackbar.LENGTH_SHORT).show();


                            AndroidNetworking.upload(EndPoints.URL_REGISTER_DEVICE)
                                    .addMultipartParameter("email", email)
                                    .addMultipartParameter("token", sToken)
                                    .setPriority(Priority.HIGH)
                                    .setTag("TEST")
                                    .build()
                                    .getAsString(new StringRequestListener() {
                                        @Override
                                        public void onResponse(String response) {
                                            progressDialog.dismiss();
                                            try {
                                                JSONObject obj = new JSONObject(response);
                                                // Launch login activity
                                                Intent intent = new Intent(
                                                        RegisterActivity.this,
                                                        LogInActivity.class);

                                                startActivity(intent);
                                                finish();

                                             } catch (JSONException e) {
                                                e.printStackTrace();
                                             }
                                        }

                                        @Override
                                        public void onError(ANError anError) {
                                            progressDialog.dismiss();
                                            Toast.makeText(RegisterActivity.this, anError.toString(), Toast.LENGTH_LONG).show();
                                        }
                                    });



                        } else {

                            // Error occurred in registration. Get the error
                            // message
                            String errorMsg = jObj.getString("error_msg");
                            Snackbar.make(btnRegister, errorMsg, Snackbar.LENGTH_SHORT).show();

                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    //Toast.makeText(RegisterActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    Snackbar.make(btnRegister, error.getMessage(), Snackbar.LENGTH_SHORT).show();


                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("name", name);
                    params.put("email", email);
                    params.put("mobileNo", mobileNo);
                    params.put("address", address);
                    params.put("pincode", pincode);
                    params.put("password", password);
                    return params;

                }
            };

            RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
        } else {

            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
            builder.setTitle("Error");
            builder.setIcon(R.drawable.ic_baseline_signal_cellular_off_24);
            builder.setMessage("Internet Connection is not found ");
            builder.setBackground(getResources().getDrawable(R.drawable.slider_background, null));

            builder.setPositiveButton("open settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                    startActivity(intent);


                }
            }).setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    finish();


                }
            });

            builder.create().show();
        }


    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

    }
}