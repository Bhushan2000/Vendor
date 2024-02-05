package com.bhushan.vendor.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bhushan.vendor.R;

import java.util.Calendar;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AboutUsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AboutUsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AboutUsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AboutUsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AboutUsFragment newInstance(String param1, String param2) {
        AboutUsFragment fragment = new AboutUsFragment();
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
    private static final int CALL_REQUEST_CODE = 100;
    String callPermissions[];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_about_us, container, false);

        callPermissions = new String[]{Manifest.permission.CALL_PHONE};

        View aboutPage=new AboutPage(getContext()).isRTL(false)
                .setImage(R.drawable.ic_launcher_foreground)
                .setDescription("")
                .addItem(new Element().setTitle("Version 1.0.0").setGravity(Gravity.CENTER))
                .addGroup("Contact us at ")
                .addEmail("abcd@gmail.com","Email")
                .addItem(new Element().setTitle("+91 8474180670").setIconDrawable(R.drawable.ic_baseline_call_24).setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(View view) {
                        String phone="8474180670";
                        String s="tel:" + phone;
                        if(!checkCallPermission()){
                            requestCallPermission();
                        }
                        else
                        {
                            Intent intent= new Intent(Intent.ACTION_CALL);
                            intent.setData(Uri.parse(s));
                            startActivity(intent);
                        }

                    }
                }))
                .addItem(new Element().setTitle("AF-230, Sector-1, Saltlake, Kolkata-700084,West Bengal, India").setIconDrawable(R.drawable.ic_baseline_home_24))
                .addGroup("Connect with us")
                .addWebsite("Enter website")
                .addFacebook("Enter facebook link")
                .addTwitter("Enter twitter link")
                .addItem(createCopyright())
                .create();


        return aboutPage;

    }
    private Element createCopyright() {
        Element copyright = new Element();
        final String copyrightString = String.format("Copyright %d by Company", Calendar.getInstance().get(Calendar.YEAR));
        copyright.setTitle(copyrightString);
        copyright.setIconDrawable(R.drawable.ic_copy_right_foreground);
        copyright.setGravity(Gravity.CENTER);
        copyright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(),copyrightString,Toast.LENGTH_SHORT).show();
            }
        });
        return  copyright;
    }
    private boolean checkCallPermission() {
        boolean result = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) == (PackageManager.PERMISSION_GRANTED);
        return result;

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestCallPermission() {
        requestPermissions(callPermissions, CALL_REQUEST_CODE);
    }
}