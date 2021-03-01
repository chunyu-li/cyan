package com.example.cyan.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.cyan.MyApplication;
import com.example.cyan.R;
import com.example.cyan.object.Chat;
import com.example.cyan.object.Friend;
import com.example.cyan.object.Moment;
import com.example.cyan.object.User;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * @author Chunyu Li
 * @File: MomentActivity.java
 * @Package com.example.cyan.activity
 * @date 12/12/20 8:13 PM
 * @Description: Get the moment objects from web server and decide which moments to add to the list
 */

public class MomentActivity extends AppCompatActivity {

    private static final String TAG = "MomentActivity";

    private ImageButton imageButtonMomentBack;
    private RecyclerView recyclerViewMoment;
    private ImageButton imageButtonCamera;
    private User me;
    private List<String> friends = new ArrayList<>();
    private List<Moment> moments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moment);
        initView();
        setEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateMoments();
    }

    private void initView() {
        me = MyApplication.getUser();
        imageButtonMomentBack = (ImageButton) findViewById(R.id.imageButtonMomentBack);
        recyclerViewMoment = (RecyclerView) findViewById(R.id.recyclerViewMoment);
        imageButtonCamera = (ImageButton) findViewById(R.id.imageButtonCamera);
        recyclerViewMoment.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMoment.setAdapter(new MomentAdapter());
    }

    private void setEvent() {
        imageButtonMomentBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        imageButtonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MomentActivity.this, EditActivity.class);
                startActivity(intent);
            }
        });
    }

    private void updateMoments() {
        BmobQuery<Friend> bmobQuery = new BmobQuery<>();
        bmobQuery.findObjects(new FindListener<Friend>() {
            @Override
            public void done(List<Friend> list, BmobException e) {
                friends.clear();
                if (list != null) {
                    List<String> ids = new ArrayList<>();
                    for (Friend friend : list) {
                        if (friend.getUser1().contentEquals(me.getObjectId())) {
                            ids.add(friend.getUser2());
                        } else if (friend.getUser2().contentEquals(me.getObjectId())) {
                            ids.add(friend.getUser1());
                        }
                    }
                    BmobQuery<User> bmobQuery1 = new BmobQuery<>();
                    bmobQuery1.findObjects(new FindListener<User>() {
                        @Override
                        public void done(List<User> list, BmobException e) {
                            if (list != null) {
                                for (User user : list) {
                                    if (ids.contains(user.getObjectId())) {
                                        friends.add(user.getUsername());
                                    }
                                }
                            }
                            BmobQuery<Moment> bmobQuery2 = new BmobQuery<>();
                            bmobQuery2.findObjects(new FindListener<Moment>() {
                                @Override
                                public void done(List<Moment> list, BmobException e) {
                                    moments.clear();
                                    if (list != null) {
                                        for (Moment moment : list) {
                                            if (friends.contains(moment.getUser()) || moment.getUser().contentEquals(me.getUsername())) {
                                                moments.add(0, moment);
                                            }
                                        }
                                    }
                                    recyclerViewMoment.setAdapter(new MomentAdapter());
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    class MomentAdapter extends RecyclerView.Adapter<MomentAdapter.ViewHolder> {

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imageViewMomentUser;
            TextView textViewMomentUser;
            TextView textViewMomentContent;
            ImageView imageViewMomentPhoto;
            TextView textViewMomentTime;
            ImageView imageViewLike;
            TextView textViewLike;

            public ViewHolder(View view) {
                super(view);
                imageViewMomentUser = (ImageView) view.findViewById(R.id.imageViewMomentUser);
                textViewMomentUser = (TextView) view.findViewById(R.id.textViewMomentUser);
                textViewMomentContent = (TextView) view.findViewById(R.id.textViewMomentContent);
                imageViewMomentPhoto = (ImageView) view.findViewById(R.id.imageViewMomentPhoto);
                textViewMomentTime = (TextView) view.findViewById(R.id.textViewMomentTime);
                imageViewLike = (ImageView) view.findViewById(R.id.imageViewLike);
                textViewLike = (TextView) view.findViewById(R.id.textViewLike);
            }
        }

        @NonNull
        @Override
        public MomentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.moment_item, parent, false);
            MomentAdapter.ViewHolder holder = new MomentAdapter.ViewHolder(view);
            holder.imageViewLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.textViewLike.getCurrentTextColor() != -1979711488)
                        return;
                    Moment moment = moments.get(holder.getAdapterPosition());
//                    holder.imageViewLike.setBackgroundResource(R.drawable.like2);
                    holder.imageViewLike.setImageResource(R.drawable.like2);
                    holder.textViewLike.setVisibility(View.VISIBLE);
                    holder.textViewLike.setTextColor(getResources().getColor(R.color.colorPink));
                    moment.increaseLike();
                    holder.textViewLike.setText(String.valueOf(moment.getLike()));
                    moments.set(holder.getAdapterPosition(), moment);
                    Moment moment1 = new Moment(moment.getAvatar(), moment.getUser(), moment.getContent(), moment.getPhoto(), moment.getLike());
                    moment1.update(moment.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                Log.d(TAG, "Update successfully");
                            } else {
                                Log.d(TAG, "Failed to update");
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });

            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Moment moment = moments.get(position);
            holder.textViewMomentUser.setText(moment.getUser());
            holder.textViewMomentContent.setText(moment.getContent());
            holder.textViewMomentTime.setText(moment.getCreatedAt());
            int like = moment.getLike();
            if (like != 0) {
                holder.textViewLike.setVisibility(View.VISIBLE);
                holder.textViewLike.setText(String.valueOf(like));
            }
            loadAvatar(holder.imageViewMomentUser, moment.getAvatar());
            loadPhoto(holder.imageViewMomentPhoto, moment.getPhoto());
        }

        @Override
        public int getItemCount() {
            return moments == null ? 0 : moments.size();
        }
    }

    private void loadAvatar(ImageView imageView, String avatar) {
        RequestOptions options = new RequestOptions().bitmapTransform(new RoundedCorners(20));
        if (avatar.contentEquals("123")) {
            Glide.with(MomentActivity.this)
                    .load(R.drawable.user)
                    .apply(options)
                    .into(imageView);
        } else {
            Glide.with(MomentActivity.this)
                    .load(avatar)
                    .placeholder(R.drawable.user)
                    .error(R.drawable.user)
                    .apply(options)
                    .into(imageView);
        }
    }

    private void loadPhoto(ImageView imageView, String photo) {
        RequestOptions options = new RequestOptions().bitmapTransform(new RoundedCorners(30));
        if (!photo.contentEquals("123")) {
            Glide.with(MomentActivity.this)
                    .load(photo)
                    .placeholder(R.drawable.photo)
                    .error(R.drawable.photo)
                    .apply(options)
                    .into(imageView);
        }
    }
}