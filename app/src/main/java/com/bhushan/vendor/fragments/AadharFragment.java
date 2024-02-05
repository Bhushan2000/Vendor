package com.bhushan.vendor.fragments;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.bhushan.vendor.datePicker.DatePickerFragmentAadhar;
import com.bhushan.vendor.R;
import com.bhushan.vendor.url.Links;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.app.Activity.RESULT_OK;


public class AadharFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AadharFragment() {
        // Required empty public constructor
    }


    public static AadharFragment newInstance(String param1, String param2) {
        AadharFragment fragment = new AadharFragment();
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

    private ImageView Photo;
    private Button addPhoto, AadharVerifyBtn;
    private RadioGroup radioGender;
    private RadioButton radioGenderButton;
    private Dialog loadingDialogue;
    private Button pickDate;
    private EditText etName, etVIDNumber, etAadharCardNumber;
    public static TextView etDOB;
    private String Name;
    private String VIDNumber;
    private String AadharCardNumber;
    private String DOB;

    public String gender;
    private AwesomeValidation awesomeValidation;
    private Bitmap bitmap;

    private static final int STORAGE_PERMISSION_CODE = 4655;

    private Uri filePath;
    private String encodedImageString;
    private ProgressDialog progressDialog;

    private TextInputLayout tilName;
    private TextInputLayout tilVIDNo;
    private TextInputLayout tilAadharNo;

    private Button btnPanVerification;

    Matcher m;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_aadhar, container, false);




        // TextILayout
        tilName = view.findViewById(R.id.tilName);
        tilVIDNo = view.findViewById(R.id.tilVIDNo);
        tilAadharNo = view.findViewById(R.id.tilAadharNo);


        ///////////////////////////////////loading dialogue/////////////////////////////////////////////

        loadingDialogue = new Dialog(getContext());
        loadingDialogue.setContentView(R.layout.loadingprogressdialogue);
        loadingDialogue.setCancelable(false);
        loadingDialogue.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.slider_background));
        loadingDialogue.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);


/////////////////////////////////////loading dialogue/////////////////////////////////////////////
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Uploading data Please Wait");
        progressDialog.setMax(100);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        pickDate = view.findViewById(R.id.btnPickDate);
        addPhoto = view.findViewById(R.id.addPhoto);
        AadharVerifyBtn = view.findViewById(R.id.btnAadharVerifyBtn);
        Photo = view.findViewById(R.id.profilePhoto);
        radioGender = (RadioGroup) view.findViewById(R.id.radioGender);
        etName = view.findViewById(R.id.etName);
        etVIDNumber = view.findViewById(R.id.etVIDNumber);
        etAadharCardNumber = view.findViewById(R.id.etAadharNumber);
        etDOB = view.findViewById(R.id.tvDateOfBirth);




        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withContext(getContext())
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                addPhotoDialog();
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                                permissionToken.continuePermissionRequest();

                            }
                        }).check();


            }
        });
        pickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_PickerDate();
            }
        });
        AadharVerifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!validateName() | !validateVIDNo() | !validateAadharNumber()) {

                    return;

                }

                uploadData();


            }
        });


        return view;

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

    private boolean validateVIDNo() {
        String inputVIDNo = tilVIDNo.getEditText().getText().toString().trim();

        if (inputVIDNo.isEmpty()) {
            tilVIDNo.setError("Name field can't be empty");
            return false;
        } else {
            tilVIDNo.setError(null);
            return true;

        }
    }

    private boolean validateAadharNumber() {
        String inputAadharNumber = tilAadharNo.getEditText().getText().toString().trim();

        if (inputAadharNumber.isEmpty()) {
            tilAadharNo.setError("Name field can't be empty");
            return false;
        } else {
            tilAadharNo.setError(null);
            return true;

        }
    }


    private boolean validateAadharPhoto() {

        if (Photo.getDrawable() == null) {
            //Image doesn´t exist.
            Toast.makeText(getContext(), "Add Photo field can't be empty", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            //Image Exists!.
            return true;
        }


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


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void addPhotoDialog() {
        String options[] = {"Select a photo from gallery"};
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
        builder.setTitle("Choose Action");
        builder.setIcon(R.drawable.ic_baseline_image_24);
        builder.setBackground(getResources().getDrawable(R.drawable.slider_background, null));
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    Intent gallery = new Intent();
                    gallery.setAction(Intent.ACTION_GET_CONTENT);
                    gallery.setType("image/*");
                    startActivityForResult(Intent.createChooser(gallery, "Select Picture"), 51);
                }
            }
        });

        builder.create().show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 51 && resultCode == RESULT_OK && data != null) {
            Uri filepath = data.getData();
            try {

                InputStream inputStream = getContext().getContentResolver().openInputStream(filepath);

                bitmap = BitmapFactory.decodeStream(inputStream);
                Photo.setImageBitmap(bitmap);
                encodeBitmapImage(bitmap);

                // Toast.makeText(getApplicationContext(),getPath(filepath),Toast.LENGTH_LONG).show();
            } catch (Exception ex) {
                Toast.makeText(getContext(), ex.toString(), Toast.LENGTH_SHORT).show();
            }
        }


    }

    public void encodeBitmapImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        encodedImageString = android.util.Base64.encodeToString(imageBytes, Base64.DEFAULT);

    }


    public void btn_PickerDate() {
        DialogFragment fragment = new DatePickerFragmentAadhar();
        fragment.show(getFragmentManager(), "date picker");
    }

    public static void SetDateText1(int year, int month, int day) {
        //   etDOB.setText(day + "/" + month + "/" + year);
        etDOB.setText(year + "-" + month + "-" + day);
    }


    private void uploadData() {

        Name = etName.getText().toString().trim();
        VIDNumber = etVIDNumber.getText().toString().trim();
        AadharCardNumber = etAadharCardNumber.getText().toString().trim();
        DOB = etDOB.getText().toString().trim();


        // If the string is empty
        // return false
        // get selected radio button from radiogroup


        if (radioGender.getCheckedRadioButtonId() != -1) {


            int selectedId = radioGender.getCheckedRadioButtonId();
            //find radio button by returned id
            radioGenderButton = radioGender.findViewById(selectedId);
            int radioID = radioGender.indexOfChild(radioGenderButton);
            RadioButton btn = (RadioButton) radioGender.getChildAt(radioID);
            gender = (String) btn.getText();


            // Aadhar Number Validation

            // Regex to check valid Aadhar number.
            String regex = "^[2-9]{1}[0-9]{3}\\s[0-9]{4}\\s[0-9]{4}$";
            // Compile the ReGex
            Pattern p = Pattern.compile(regex);
            // Pattern class contains matcher() method
            // to find matching between given string
            // and regular expression.
            Matcher m = p.matcher(AadharCardNumber);

            m.matches();


            loadingDialogue.show();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Links.uploadAadhar, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    loadingDialogue.dismiss();
                    etName.setText("");
                    etVIDNumber.setText("");
                    etAadharCardNumber.setText("");
                    etDOB.setText("");
                    Photo.setImageResource(R.drawable.profile_icon);


                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        jsonObject.getBoolean("error");
                        String message = jsonObject.getString("message");
                        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    // AadharVerifyBtn.setText(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    loadingDialogue.dismiss();

                    Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
                    // AadharVerifyBtn.setText(error.toString());

                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("nameOnAadhar", Name);
                    params.put("aadharVIDNo", VIDNumber);
                    params.put("aadharNo", AadharCardNumber);
                    params.put("dob", DOB);
                    params.put("gender", gender);

                    if (!TextUtils.isEmpty(encodedImageString))
                    params.put("aadharPhoto", encodedImageString);

                    return params;
                }
            };

            RequestQueue queue = Volley.newRequestQueue(getContext());
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    6000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            );
            queue.add(stringRequest);


        } else {
            // no radio button selected
            Toast.makeText(getContext(), "Select Gender", Toast.LENGTH_SHORT).show();

        }

    }


}