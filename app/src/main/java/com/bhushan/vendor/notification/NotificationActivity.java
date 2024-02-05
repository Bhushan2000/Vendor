package com.bhushan.vendor.notification;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.bhushan.vendor.database.DatabaseOperations;
import com.bhushan.vendor.R;
import com.bhushan.vendor.adapter.NotificationAdapter;
import com.bhushan.vendor.fragments.HomeFragment;

public class NotificationActivity extends AppCompatActivity {
    private Toolbar toolbar;

    public static  RecyclerView NotificationRecyclerView;
    //  List<NotificationModel> notificationModelList;
    public static NotificationAdapter notificationAdapter;
    public static int notificationModelListInt;
    private Dialog loadingDialogue;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Notification");


        ///////////////////////////////////loading dialogue/////////////////////////////////////////////

        loadingDialogue = new Dialog(this);
        loadingDialogue.setContentView(R.layout.loadingprogressdialogue);
        loadingDialogue.setCancelable(false);
        loadingDialogue.getWindow().setBackgroundDrawable( getDrawable(R.drawable.slider_background));
        loadingDialogue.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialogue.show();

        /////////////////////////////////////loading dialogue/////////////////////////////////////////////


        //   notificationModelList = new ArrayList<>();

        NotificationRecyclerView =  findViewById(R.id.NotificationList);

        NotificationRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        notificationAdapter = new NotificationAdapter(this, DatabaseOperations.notificationModelList);
        NotificationRecyclerView.setAdapter(notificationAdapter);

        if( DatabaseOperations.notificationModelList.size() ==0){
            DatabaseOperations.fetchData(this,loadingDialogue);
        }else{
            loadingDialogue.dismiss();
        }


        notificationAdapter.notifyDataSetChanged();







    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {

            finish();


        }


        switch (id) {
            case (R.id.action_search):
                //search
                break;

        }

        return true;
    }


//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        setHasOptionsMenu(true);
//        super.onActivityCreated(savedInstanceState);
//    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_sort, menu);
        menu.findItem(R.id.action_sort).setVisible(false);

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



                notificationAdapter.getFilter().filter(newText);



                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void finish() {
        super.finish();
        Fragment fragment = new HomeFragment();
        getSupportFragmentManager().beginTransaction().detach(fragment).attach(fragment).addToBackStack(null).commit();
    }
}