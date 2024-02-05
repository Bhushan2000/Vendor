package com.bhushan.vendor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.bhushan.vendor.R;
import com.bhushan.vendor.model.NotificationModel;
import com.bhushan.vendor.notification.NotificationActivity;
import com.bhushan.vendor.url.Links;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> implements Filterable {

    Context context;
    private int lastPosition = -1;


    private List<NotificationModel> notificationModelList;
    private List<NotificationModel> updatedList;

    public NotificationAdapter(Context context, List<NotificationModel> notificationModelList) {
        this.context = context;
        this.notificationModelList = notificationModelList;
        this.updatedList = new ArrayList<>(notificationModelList);
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.row_notification, parent, false);

        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, final int position) {
        //setTexts
        final NotificationModel notificationModel = notificationModelList.get(position);
        int id = notificationModel.getId();
        String time = notificationModel.getNotificationDateTime();
        String title = notificationModel.getNotificationTitle();
        String message = notificationModel.getNotificationMessage();
        String image = notificationModel.getNotificationImage();
        int readed = notificationModel.getReaded();

        holder.setData(id,title, message, time, image, readed);


        holder.DeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AndroidNetworking.upload(Links.deleteNoti)
                        .addMultipartParameter("id",String.valueOf(notificationModel.getId()))
                        .setTag("test")
                        .build()
                        .getAsString(new StringRequestListener() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                                    NotificationActivity.notificationAdapter.notifyDataSetChanged();



                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onError(ANError anError) {
                                Toast.makeText(context, anError.toString(), Toast.LENGTH_SHORT).show();

                            }


                        });
            }
        });


        Picasso.with(context).load(notificationModel.getNotificationImage())
                .placeholder(R.drawable.progress_animation)
                .into(holder.notiImage);

        if (lastPosition < position) {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), android.R.anim.fade_in);
            holder.itemView.setAnimation(animation);
            lastPosition = position;

        }

    }

    @Override
    public int getItemCount() {
        return notificationModelList.size();
    }

    @Override
    public Filter getFilter() {

        return filter;
    }

    Filter filter = new Filter() {
        // run on background thread
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<NotificationModel> notificationModels = new ArrayList<>();
            if (constraint.toString().isEmpty()) {
                notificationModels.addAll(updatedList);

            } else {
                for (NotificationModel notificationModel : updatedList) {
                    if (notificationModel.getNotificationMessage().toLowerCase().contains(constraint.toString().toLowerCase()
                    )) {
                        notificationModels.add(notificationModel);

                    }
                }

            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = notificationModels;
            return filterResults;
        }

        // run on ui thread

        @Override
        protected void publishResults(CharSequence constraint, FilterResults filterResults) {
            notificationModelList.clear();
            notificationModelList.addAll((Collection<? extends NotificationModel>) filterResults.values);
            notifyDataSetChanged();


        }
    };


    public static class NotificationViewHolder extends RecyclerView.ViewHolder {

        CircleImageView notiImage;
        TextView NotificationTitle, NotificationTime, notificationMessage;
        ImageButton DeleteButton;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);

            notiImage = itemView.findViewById(R.id.notiImage);
            NotificationTitle = itemView.findViewById(R.id.notificationTitle);
            NotificationTime = itemView.findViewById(R.id.notificationTime);
            DeleteButton = itemView.findViewById(R.id.deleteNotification);
            notificationMessage = itemView.findViewById(R.id.notificationMessage);

        }

        public void setData(int id,String title, String message, String time, String image, int readed) {


            if (readed == 1) {

                NotificationTime.setAlpha(0.5f);
                NotificationTitle.setAlpha(0.5f);
                notificationMessage.setAlpha(0.5f);

            } else {

                NotificationTime.setAlpha(1f);
                NotificationTitle.setAlpha(1f);
                notificationMessage.setAlpha(1f);
            }

            NotificationTime.setText(time);
            NotificationTitle.setText(title);
            notificationMessage.setText(message);

            Picasso.with(itemView.getContext()).load(image)
                    .placeholder(R.drawable.progress_animation)
                    .into(notiImage);

        }
    }
}
