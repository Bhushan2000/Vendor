package com.bhushan.vendor.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;

import com.bhushan.vendor.database.DatabaseOperations;
import com.bhushan.vendor.notification.NotificationActivity;
import com.bhushan.vendor.R;
import com.bhushan.vendor.activities.NavigationActivity;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {
    private TextView tvOrdersToday;
    private TextView tvOrdersThisMonth;
    private TextView tvOrdersThisYear;
    private TextView tvOrdersTotal;
    private TextView tvOrdersNew;
    private TextView tvOrdersConfirmed;
    private TextView tvOrdersOnHold;
    private TextView tvOrdersInProcess;
    private TextView tvOrdersCompleted;
    private TextView tvOrdersCancelled;

    private TextView tvCompanyName;
    private TextView tvSellerCode;
    private ImageView ivCompanyLogo;

    private ProgressDialog progressDialog;

    TextView textCartItemCount;


    TextView orders, viewOrders;
    private Dialog loadingDialogue;

    CircleImageView civProfileImage;




    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ///////////////////////////////////loading dialogue/////////////////////////////////////////////

        loadingDialogue = new Dialog(getContext());
        loadingDialogue.setContentView(R.layout.loadingprogressdialogue);
        loadingDialogue.setCancelable(false);
        loadingDialogue.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.slider_background));
        loadingDialogue.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        /////////////////////////////////////loading dialogue/////////////////////////////////////////////



        tvOrdersToday = view.findViewById(R.id.tvOrdersToday);
        tvOrdersThisMonth = view.findViewById(R.id.tvOrdersThisMonth);
        tvOrdersThisYear = view.findViewById(R.id.tvOrdersThisYear);
        tvOrdersTotal = view.findViewById(R.id.tvOrdersTotal);
        tvOrdersNew = view.findViewById(R.id.tvOrdersNew);
        tvOrdersConfirmed = view.findViewById(R.id.tvOrdersConfirmed);
        tvOrdersOnHold = view.findViewById(R.id.tvOrdersOnHold);
        tvOrdersInProcess = view.findViewById(R.id.tvOrdersInProcess);
        tvOrdersCompleted = view.findViewById(R.id.tvOrdersCompleted);
        tvOrdersCancelled = view.findViewById(R.id.tvOrdersCancelled);

        tvCompanyName = view.findViewById(R.id.tvCompanyName);
        tvSellerCode = view.findViewById(R.id.tvSellerCode);
        ivCompanyLogo = view.findViewById(R.id.ivCompanyLogo);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");


        //These lines should be added in the OnCreate() of your main activity
        orders = (TextView) MenuItemCompat.getActionView(NavigationActivity.navigationView.getMenu().findItem(R.id.orders));
        viewOrders = (TextView) MenuItemCompat.getActionView(NavigationActivity.navigationView.getMenu().findItem(R.id.viewProducts));



        //This method will initialize the count value

        initializeCountDrawer();
        DatabaseOperations.loadHomeData(getContext(), loadingDialogue, tvOrdersToday,
                tvOrdersThisMonth,
                tvOrdersThisYear,
                tvOrdersTotal,
                tvOrdersNew,
                tvOrdersConfirmed,
                tvOrdersOnHold,
                tvOrdersInProcess,
                tvOrdersCompleted,
                tvOrdersCancelled,
                orders,
                viewOrders);


        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);


        final MenuItem menuItem = menu.findItem(R.id.notification);
        View actionView = menuItem.getActionView();
        textCartItemCount = (TextView) actionView.findViewById(R.id.cart_badge);

        MenuItem menuProfile = menu.findItem(R.id.profileImage);
        View profile = menuProfile.getActionView();;
        civProfileImage = (CircleImageView) profile.findViewById(R.id.civProfileImage);

        DatabaseOperations.loadProfile(getContext(),progressDialog,civProfileImage);

        //Glide.with(getContext()).load().placeholder(R.drawable.progress_animation).into(civProfileImage);


        civProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationActivity.toolbar.setTitle(R.string.profile);
                getFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.fragment_container,
                        new ProfileFragment()).commit();
            }
        });

        setupBadge();

        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });


        super.onCreateOptionsMenu(menu, inflater);
    }


    private void initializeCountDrawer() {
        //Gravity property aligns the text
        orders.setGravity(Gravity.CENTER);
        orders.setTypeface(null, Typeface.BOLD);


        viewOrders.setGravity(Gravity.CENTER);
        viewOrders.setTypeface(null, Typeface.BOLD);


    }


    private void setupBadge() {



        if (DatabaseOperations.unreadedCount != 0) {
            textCartItemCount.setVisibility(View.VISIBLE);
        } else {
            textCartItemCount.setVisibility(View.INVISIBLE);
        }

        if (DatabaseOperations.unreadedCount < 99) {
            textCartItemCount.setText(String.valueOf(DatabaseOperations.unreadedCount));
        } else {
            textCartItemCount.setText("99+");
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.profileImage) {

            NavigationActivity.toolbar.setTitle(R.string.profile);
            getFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.fragment_container,
                    new ProfileFragment()).commit();

            return true;

        } else if (id == R.id.notification) {


            //     NavigationActivity.toolbar.setTitle(R.string.notification);

            if (DatabaseOperations.unreadedCount > 0) {
                DatabaseOperations.markAsReaded(getContext());
            }
            startActivity(new Intent(getContext(), NotificationActivity.class));



            return true;

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onStart() {
        super.onStart();
        DatabaseOperations.fetchData(getContext(), loadingDialogue);
        DatabaseOperations.countUnreadNoti(getContext());
    }

}
