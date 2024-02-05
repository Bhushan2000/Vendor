package com.bhushan.vendor.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
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

import android.os.Environment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bhushan.vendor.R;
import com.bhushan.vendor.activities.RequestHandler;
import com.bhushan.vendor.datePicker.DatePickerFragmentPan;
import com.bhushan.vendor.url.Links;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PanFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PanFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PanFragment() {
        // Required empty public constructor
    }


    public static PanFragment newInstance(String param1, String param2) {
        PanFragment fragment = new PanFragment();
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

    Button clear, save;
    SignaturePad signaturePad;
    ImageView Photo;
    Button addPhoto;
    public static TextView etDOB;
    private Button pickDate;
    private static final String TAG = "PanFragment";
    private Button conti;

    private EditText etName, etFatherName, etPanCardNumber;
    // insert data
    private String Name;
    private String FatherName;
    private String PanCardNumber;
    private String DOB;
    private Dialog loadingDialogue;
    private String signature;
    public static String signaturePath;
    public static final int STORAGE_PERMISSION_CODE = 1001;

    private Bitmap bitmap;
    private Uri filePath;
    private String encodedImageString;
    private ProgressDialog progressDialog;
    Uri resultUri;

    private TextInputLayout tilName;
    private TextInputLayout tilFatherName;
    private TextInputLayout tilPANNumber;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pan, container, false);

        // TextILayout
        tilName = view.findViewById(R.id.tilName);
        tilFatherName = view.findViewById(R.id.tilFatherName);
        tilPANNumber = view.findViewById(R.id.tilPanNumber);

        signaturePad = (SignaturePad) view.findViewById(R.id.signature_pad);
        clear = (Button) view.findViewById(R.id.clear);
        save = (Button) view.findViewById(R.id.save);
        addPhoto = view.findViewById(R.id.addPhoto);
        Photo = view.findViewById(R.id.pic);
        pickDate = view.findViewById(R.id.btnpickdate);
        conti = view.findViewById(R.id.conti);


        etName = view.findViewById(R.id.etName);
        etFatherName = view.findViewById(R.id.etFatherName);
        etPanCardNumber = view.findViewById(R.id.etPanNumber);
        etDOB = (TextView) view.findViewById(R.id.btnDate);

        ///////////////////////////////////loading dialogue/////////////////////////////////////////////

        loadingDialogue = new Dialog(getContext());
        loadingDialogue.setContentView(R.layout.loadingprogressdialogue);
        loadingDialogue.setCancelable(false);
        loadingDialogue.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.slider_background));
        loadingDialogue.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        /////////////////////////////////////loading dialogue/////////////////////////////////////////////

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("loading...");
        progressDialog.setMax(100);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);


        // At the starting disable the buttons
        enableDisableButtons(false);
        pickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_PickerDate();
            }
        });
        save.setEnabled(false);
        clear.setEnabled(false);
        signaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {

            }

            @Override
            public void onSigned() {
                save.setEnabled(true);
                clear.setEnabled(true);
            }

            @Override
            public void onClear() {
                save.setEnabled(false);
                clear.setEnabled(false);
            }
        });

        // signature
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signaturePad.clear();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //signature is saved
                //write code for saving the signature as image
                Bitmap bitmapSignature = signaturePad.getSignatureBitmap();
                // Create image from bitmap and store it in memory
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmapSignature.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
                Random rand = new Random();
                int randomValue = rand.nextInt(9999);
                File file = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() +
                        "/" + String.valueOf(randomValue) + "capturedsignature.jpg");
                try {
                    if (file.createNewFile()) {
                        file.createNewFile();
                    }
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    fileOutputStream.write(byteArrayOutputStream.toByteArray());
                    fileOutputStream.close();

                    signaturePath = file.getAbsolutePath();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                Toast.makeText(getContext(), "Signature Saved", Toast.LENGTH_SHORT).show();

            }

        });


        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent gallery = new Intent();
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                gallery.setType("image/*");
                startActivityForResult(Intent.createChooser(gallery, "Select Picture"), 51);
            }
        });


        conti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Name = etName.getText().toString();
                FatherName = etFatherName.getText().toString();
                PanCardNumber = etPanCardNumber.getText().toString();
                DOB = etDOB.getText().toString();

                // Pan Number Validation
                Pattern pattern = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]{1}");
                Matcher matcher = pattern.matcher(PanCardNumber);

                if (!validateName() | !validateFatherName() | !validateFatherPANNumber()) {

                    return;

                } else if (matcher.matches()) {

                    getVerifyPan();


                } else {
                    Toast.makeText(getContext(), PanCardNumber + " is Not Matching",
                            Toast.LENGTH_LONG).show();
                }


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

    private boolean validateFatherName() {
        String inputFatherName = tilFatherName.getEditText().getText().toString().trim();

        if (inputFatherName.isEmpty()) {
            tilFatherName.setError("Name field can't be empty");
            return false;
        } else {
            tilFatherName.setError(null);
            return true;

        }
    }

    private boolean validateFatherPANNumber() {
        String inputPANNumber = tilPANNumber.getEditText().getText().toString().trim();

        if (inputPANNumber.isEmpty()) {
            tilPANNumber.setError("Name field can't be empty");
            return false;
        } else {
            tilPANNumber.setError(null);
            return true;

        }
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


    private void enableDisableButtons(boolean enableButton) {
        save.setEnabled(enableButton);
        clear.setEnabled(enableButton);
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

    public void encodeBitmapImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        encodedImageString = android.util.Base64.encodeToString(imageBytes, Base64.DEFAULT);

    }


    private void getVerifyPan() {

        Name = etName.getText().toString();
        FatherName = etFatherName.getText().toString();
        PanCardNumber = etPanCardNumber.getText().toString();
        DOB = etDOB.getText().toString();


        loadingDialogue.show();


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Links.uploadPan, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loadingDialogue.dismiss();
                etName.setText("");
                etFatherName.setText("");
                etPanCardNumber.setText("");
                etDOB.setText("");
                signaturePad.clear();

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    jsonObject.getBoolean("error");
                    String message = jsonObject.getString("message");
                    Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingDialogue.dismiss();
                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("nameOnPan", Name);
                params.put("fatherName", FatherName);
                params.put("panNumber", PanCardNumber);
                params.put("dob", DOB);
                params.put("signature", signature);
                params.put("panPhoto", encodedImageString);

                return params;
            }
        };

        RequestHandler.getInstance(getContext()).addToRequestQueue(stringRequest);

    }


    public void btn_PickerDate() {
        DialogFragment fragment = new DatePickerFragmentPan();
        fragment.show(getFragmentManager(), "date picker");

    }

    public static void SetDateText(int year, int month, int day) {
        // etDOB.setText(day + "/" + month + "/" + year);

        etDOB.setText(year + "-" + month + "-" + day);
    }


//    private void postData() {
//
//        Name = etName.getText().toString();
//        FatherName = etFatherName.getText().toString();
//        PanCardNumber = etPanCardNumber.getText().toString();
//        DOB = etDOB.getText().toString();
//        File imageFile = new File(resultUri.getPath());
//        progressDialog.show();
//        AndroidNetworking.upload(STRING_URL)
//                .addMultipartFile("image", imageFile)
//                .addMultipartParameter("nameOnPan", Name)
//                .addMultipartParameter("fatherName", FatherName)
//                .addMultipartParameter("panNumber", PanCardNumber)
//                .addMultipartParameter("dob", DOB)
//                .setPriority(Priority.HIGH)
//                .build()
//                .setUploadProgressListener(new UploadProgressListener() {
//                    @Override
//                    public void onProgress(long bytesUploaded, long totalBytes) {
//
//                        float progress = (float) bytesUploaded / totalBytes * 100;
//                        progressDialog.setProgress((int) progress);
//                    }
//                })
//                .getAsString(new StringRequestListener() {
//                    @Override
//                    public void onResponse(String response) {
//
//                        progressDialog.dismiss();
//
//
//                        etName.setText("");
//                        etFatherName.setText("");
//                        etPanCardNumber.setText("");
//                        etDOB.setText("");
//                        signaturePad.clear();
//                        Toast.makeText(getContext(), response.toString(), Toast.LENGTH_LONG).show();
//
//
//                    }
//
//                    @Override
//                    public void onError(ANError anError) {
//                        progressDialog.dismiss();
//
//                        anError.printStackTrace();
//                        Toast.makeText(getContext(), "Error While Uploading Image", Toast.LENGTH_SHORT).show();
//                    }
//                });
//
//    }
}