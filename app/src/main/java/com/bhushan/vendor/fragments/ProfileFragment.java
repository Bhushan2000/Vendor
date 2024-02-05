package com.bhushan.vendor.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.bhushan.vendor.database.DatabaseOperations;
import com.bhushan.vendor.R;
import com.bhushan.vendor.activities.LogInActivity;
import com.bhushan.vendor.sharediPreference.SharedPrefManager;
import com.bhushan.vendor.url.Links;
import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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

    private FloatingActionButton floatingActionButton;


    private TextView name, email, mobileNo, address, pincode, created_at, password;
    public static final int RequestPermissionCode = 1;

    // PHOTO UPLOAD

    private Bitmap bitmap;
    private File file;
    private Uri file_uri;
    private Button btnUpdate;
    private ProgressDialog progressDialog;
    private String desc = "describe";
    private NetworkImageView networkImageView;

    String error;
    String message;
    String image;

    private FloatingActionButton fabPick;
    private Button btnConfirm, btnUpdateProfile;
    private CircleImageView circularProfile;
    String descGet;
    String pathGet;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        if (!SharedPrefManager.getInstance(getContext()).isLoggedIn()) {
            getActivity().finish();
            startActivity(new Intent(getContext(), LogInActivity.class));
        }


        created_at = view.findViewById(R.id.created_at);


        // String  strtext = getArguments().getString("image");
        //  Glide.with(getContext()).load(strtext).into(circularProfile);

        floatingActionButton = view.findViewById(R.id.fab);


        name = view.findViewById(R.id.name);
        email = view.findViewById(R.id.email);
        mobileNo = view.findViewById(R.id.mobileNo);
        address = view.findViewById(R.id.address);
        pincode = view.findViewById(R.id.pincode);
      //  password = view.findViewById(R.id.password);
        btnUpdate = view.findViewById(R.id.btnUpdate);

        name.setText(SharedPrefManager.getInstance(getContext()).getUsername());
        email.setText(SharedPrefManager.getInstance(getContext()).getUserEmail());
        mobileNo.setText(SharedPrefManager.getInstance(getContext()).getUserMobileNo());
        address.setText(SharedPrefManager.getInstance(getContext()).getUserAddress());
        pincode.setText(SharedPrefManager.getInstance(getContext()).getUserPincode());

        created_at.setText(SharedPrefManager.getInstance(getContext()).getUserCreatedAt());


        fabPick = view.findViewById(R.id.fabPickImage);
        btnConfirm = view.findViewById(R.id.btnConfirm);
        circularProfile = view.findViewById(R.id.circular_profile);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Uploading Image Please Wait");
        progressDialog.setMax(100);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

//


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // showEditProfileDialog();
            }
        });
        floatingActionButton.setVisibility(View.INVISIBLE);


        fabPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Dexter.withContext(getContext())
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                                EditPicture();
                            }

                            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                                if (permissionDeniedResponse.isPermanentlyDenied()) {
                                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());


                                    builder.setTitle("Permission Required")
                                            .setBackground(getResources().getDrawable(R.drawable.slider_background, null))
                                            .setMessage("Permission Required to access the device storage go to settings and grant the permission")
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Intent intent = new Intent();
                                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                    intent.setData(Uri.fromParts("package", getContext().getPackageName(), null));
                                                    startActivityForResult(intent, 51);

                                                }
                                            }).setNegativeButton("Cancel", null)
                                            .create().show();
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                                permissionToken.continuePermissionRequest();
                            }
                        })
                        .check();
            }
        });


        DatabaseOperations.loadProfile(getContext(),progressDialog,circularProfile);



        return view;


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Uri ImageUri = data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(getContext(), this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                final Uri resultUri = result.getUri();
                Glide.with(this).load(resultUri).into(circularProfile);


                btnConfirm.setVisibility(View.VISIBLE);
                btnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        File imageFile = new File(resultUri.getPath());

                        progressDialog.show();
                        String user_id =   SharedPrefManager.getInstance(getContext()).getUserID();

                        AndroidNetworking.upload(Links.upload)
                                .addMultipartFile("image", imageFile)
                                .addMultipartParameter("desc", desc)
                                .addMultipartParameter("vid",user_id)
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
                                            JSONObject jsonObject = new JSONObject(response);
                                            error = jsonObject.getString("error");
                                            message = jsonObject.getString("message");
                                            image = jsonObject.getString("image");


                                            Toast.makeText(getContext(),
                                                    message, Toast.LENGTH_LONG).show();

                                            btnConfirm.setVisibility(View.GONE);

                                        } catch (JSONException e) {
                                            btnConfirm.setVisibility(View.GONE);

                                            e.printStackTrace();
                                            Toast.makeText(getContext(), "Parsing Error", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onError(ANError anError) {
                                        progressDialog.dismiss();
                                        btnConfirm.setVisibility(View.GONE);

                                        anError.printStackTrace();
                                        Toast.makeText(getContext(), "Error While Uploading Image", Toast.LENGTH_SHORT).show();
                                    }
                                });


                    }
                });


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();

                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void EditPicture() {
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
                    startActivityForResult(Intent.createChooser(gallery, "Select Picture"), 1);
                }
            }
        });

        builder.create().show();
    }

}