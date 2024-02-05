package com.bhushan.vendor.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bhushan.vendor.database.DatabaseOperations;
import com.bhushan.vendor.R;
import com.bhushan.vendor.fragments.AadharFragment;
import com.bhushan.vendor.fragments.AboutUsFragment;

import com.bhushan.vendor.fragments.AddProductFragment;
import com.bhushan.vendor.fragments.HomeFragment;
import com.bhushan.vendor.fragments.OrdersFragment;
import com.bhushan.vendor.fragments.PanFragment;
import com.bhushan.vendor.fragments.PaymentsFragment;
import com.bhushan.vendor.fragments.ProfileFragment;
import com.bhushan.vendor.fragments.ViewProductFragment;
import com.bhushan.vendor.sharediPreference.SharedPrefManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;

import de.hdodenhof.circleimageview.CircleImageView;

public class NavigationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private FrameLayout frameLayout;
    public static Toolbar toolbar;
    private Spinner spinner;
    private String[] duration = {"Last week", "Last month", "Last year"};
    public static String currentDateString;
    private TextView tvUsername;
    private TextView tvEmail;
    private CircleImageView profileImage;
    public static NavigationView navigationView;
    private int id;
    private MenuItem previousMenuItem;
    private Window window;
    private HomeFragment homeFragment;
    private Dialog loadingDialogue;
    String descGet;
    String pathGet;
    private ProgressDialog progressDialog;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_navigation);

        //    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, LogInActivity.class));
        }


        ///////////////////////////////////loading dialogue/////////////////////////////////////////////
        loadingDialogue = new Dialog(NavigationActivity.this);
        loadingDialogue.setContentView(R.layout.loadingprogressdialogue);
        loadingDialogue.setCancelable(false);
        loadingDialogue.getWindow().setBackgroundDrawable(this.getDrawable(R.drawable.slider_background));
        loadingDialogue.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        /////////////////////////////////////loading dialogue/////////////////////////////////////////////

        progressDialog = new ProgressDialog(NavigationActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("loading");
        progressDialog.setMax(100);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Home");


        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);


        View view = navigationView.getHeaderView(0);
        tvUsername = view.findViewById(R.id.userName);
        tvEmail = view.findViewById(R.id.userEmail);
        profileImage = view.findViewById(R.id.profileImage);


        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolbar.setTitle(getString(R.string.profile));
                getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.fragment_container,
                        new ProfileFragment()).commit();
                drawer.closeDrawer(GravityCompat.START);

            }
        });
        tvUsername.setText(SharedPrefManager.getInstance(this).getUsername());
        tvEmail.setText(SharedPrefManager.getInstance(this).getUserEmail());


        //   BackgroundColorScan


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar
                , R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        frameLayout = findViewById(R.id.fragment_container);


        if (savedInstanceState == null) {


            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.home);


        }

//        spinner=findViewById(R.id.spinner);

//        spinner.setOnItemSelectedListener(this);
//        ArrayAdapter ad = new ArrayAdapter(this, android.R.layout.simple_spinner_item, duration);
//
//        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//        spinner.setAdapter(ad);

        DatabaseOperations.loadProfile(this, progressDialog, profileImage);


    }


    //    @Override
//    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//        if(duration[i].equalsIgnoreCase("Last year"))
//        {
//            showLastYearGraph();
//        }
//        if(duration[i].equalsIgnoreCase("Last month"))
//        {
//            showLastMonthGraph();
//        }
//        if(duration[i].equalsIgnoreCase("Last week"))
//        {
//            showLastWeekGraph();
//        }
//    }
//    private void showLastYearGraph() {
//
//        BarChart barChart=findViewById(R.id.barChart);
//        ArrayList<BarEntry> sales= new ArrayList<>();
//        sales.add(new BarEntry(1, 280));
//        sales.add(new BarEntry(2, 430));
//        sales.add(new BarEntry(3, 500));
//        sales.add(new BarEntry(4, 780));
//        sales.add(new BarEntry(5, 400));
//        sales.add(new BarEntry(6, 517));
//        sales.add(new BarEntry(7, 213));
//        sales.add(new BarEntry(8, 216));
//        sales.add(new BarEntry(9, 113));
//        sales.add(new BarEntry(10, 214));
//        sales.add(new BarEntry(11, 425));
//        sales.add(new BarEntry(12, 383));
//
//
//        BarDataSet barDataSet=new BarDataSet(sales,"Items sold");
//        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
//        barDataSet.setValueTextColor(Color.BLACK);
//        barDataSet.setValueTextSize(16f);
//
//        BarData barData=new BarData(barDataSet);
//        barChart.setFitBars(true);
//        barChart.setData(barData);
//        barChart.getDescription().setText("Sales in the months of the previous year");
//        barChart.animateY(5000);
//    }
//
//    private void showLastMonthGraph() {
//
//        BarChart barChart=findViewById(R.id.barChart);
//        ArrayList<BarEntry> sales= new ArrayList<>();
//        sales.add(new BarEntry(1, 90));
//        sales.add(new BarEntry(2, 76));
//        sales.add(new BarEntry(3, 98));
//        sales.add(new BarEntry(4, 112));
//
//        BarDataSet barDataSet=new BarDataSet(sales,"Items sold");
//        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
//        barDataSet.setValueTextColor(Color.BLACK);
//        barDataSet.setValueTextSize(16f);
//
//        BarData barData=new BarData(barDataSet);
//        barChart.setFitBars(true);
//        barChart.setData(barData);
//        barChart.getDescription().setText("Sales in the weeks of the previous month");
//        barChart.animateY(3000);
//    }

//    private void showLastWeekGraph() {
//
//        BarChart barChart=findViewById(R.id.barChart);
//        ArrayList<BarEntry> sales= new ArrayList<>();
//        sales.add(new BarEntry(1, 28));
//        sales.add(new BarEntry(2, 43));
//        sales.add(new BarEntry(3, 50));
//        sales.add(new BarEntry(4, 78));
//        sales.add(new BarEntry(5, 40));
//        sales.add(new BarEntry(6, 57));
//        sales.add(new BarEntry(7, 23));
//
//        BarDataSet barDataSet=new BarDataSet(sales,"Items sold");
//        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
//        barDataSet.setValueTextColor(Color.BLACK);
//        barDataSet.setValueTextSize(16f);
//
//        BarData barData=new BarData(barDataSet);
//        barChart.setFitBars(true);
//        barChart.setData(barData);
//        barChart.getDescription().setText("Sales in the previous weekdays");
//        barChart.animateY(5000);
//    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        id = item.getItemId();

        if (previousMenuItem != null) {
            item.setChecked(false);
        }

        item.isCheckable();
        item.isChecked();
        previousMenuItem = item;

        //closing drawer on item click
        drawer.closeDrawers();


        switch (id) {

            case R.id.home:
                toolbar.setTitle(getString(R.string.home));

                getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.fragment_container,
                        new HomeFragment()).commit();
                break;


            case R.id.profile:
                toolbar.setTitle(getString(R.string.profile));
                getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.fragment_container,
                        new ProfileFragment()).commit();
                break;
            case R.id.orders:

                toolbar.setTitle(getString(R.string.orders));
                getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.fragment_container,
                        new OrdersFragment()).commit();
                break;
            case R.id.payment:


                toolbar.setTitle(getString(R.string.payment_setting));
                getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.fragment_container,
                        new PaymentsFragment()).commit();
                break;
            case R.id.addProduct:


                toolbar.setTitle(getString(R.string.add_product));
                getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.fragment_container,
                        new AddProductFragment()).commit();

                break;
            case R.id.viewProducts:

                toolbar.setTitle(getString(R.string.view_product));
                getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.fragment_container,
                        new ViewProductFragment()).commit();
                break;
            case R.id.aadhar:

                toolbar.setTitle(getString(R.string.aadhar_verification));
                getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.fragment_container,
                        new AadharFragment()).commit();
                break;
            case R.id.PAN:

                toolbar.setTitle(getString(R.string.pan_verification));
                getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.fragment_container,
                        new PanFragment()).commit();

                break;
            case R.id.aboutUs:
                toolbar.setTitle(getString(R.string.about));

                getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.fragment_container,
                        new AboutUsFragment()).commit();
                break;
            case R.id.logOut:

                SharedPrefManager.getInstance(NavigationActivity.this).logout();
                finish();
                startActivity(new Intent(NavigationActivity.this, LogInActivity.class));
                break;
        }


        drawer.closeDrawer(GravityCompat.START);
        return true;
    }




    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {

            drawer.closeDrawer(GravityCompat.START);

        } else {

            if (id == R.id.home) {


                new AlertDialog.Builder(this)
                        .setMessage("Are you sure do you want to exit?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                NavigationActivity.super.onBackPressed();
                            }
                        })

                        .setNegativeButton("No", null)
                        .create()
                        .show();


            } else {


                toolbar.setTitle(getString(R.string.home));
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new HomeFragment()).commit();
                navigationView.getMenu().getItem(0).setChecked(true);

                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(NavigationActivity.this);
                builder.setMessage("Are you sure do you want to exit?");
                builder.setTitle("Exit From App!");

                builder.setIcon(R.drawable.ic_baseline_exit_to_app_24);
                builder.setBackground(getResources().getDrawable(R.drawable.slider_background, null));

                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NavigationActivity.super.onBackPressed();
                    }
                })
                        .setNegativeButton("No", null)
                        .create()
                        .show();

            }
        }


    }


}