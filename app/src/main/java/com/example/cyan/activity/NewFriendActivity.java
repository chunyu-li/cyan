package com.example.cyan.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cyan.DBOpenHelper;
import com.example.cyan.R;
import com.example.cyan.fragment.ContactFragment;
import com.example.cyan.object.Friend;
import com.example.cyan.object.Request;
import com.example.cyan.object.User;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * @author Chunyu Li
 * @File: NewFriendActivity.java
 * @Package com.example.cyan.activity
 * @date 12/12/20 8:14 PM
 * @Description: First get the request objects from web server, then upload a new friend object
 * depending on the choice of user
 */

public class NewFriendActivity extends AppCompatActivity {

    private ImageButton imageButtonFriendBack;
    private RecyclerView recyclerViewNewFriend;
    private ImageView imageViewAddFriend;
    private List<Request> list;
    private DBOpenHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friend);
        initData();
        initView();
        setEvent();
    }

    private void initView() {
        imageButtonFriendBack = findViewById(R.id.imageButtonFriendBack);
        recyclerViewNewFriend = findViewById(R.id.recyclerViewNewFriend);
        imageViewAddFriend = findViewById(R.id.imageViewAddFriend);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewNewFriend.setLayoutManager(layoutManager);
        NewFriendAdapter adapter = new NewFriendAdapter(list);
        recyclerViewNewFriend.setAdapter(adapter);
    }

    private void setEvent() {
        imageButtonFriendBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imageViewAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewFriendActivity.this, AddFriendActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initData() {
        helper = new DBOpenHelper(this);
        list = helper.getRequestData();
    }

    class NewFriendAdapter extends RecyclerView.Adapter<NewFriendActivity.NewFriendAdapter.ViewHolder> {

        List<Request> list;

        public NewFriendAdapter(List<Request> list) {
            this.list = list;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imageViewNewFriend;
            TextView textViewNewFriend;
            ImageButton imageButtonAccept;
            ImageButton imageButtonRefuse;

            public ViewHolder(View view) {
                super(view);
                imageViewNewFriend = (ImageView) view.findViewById(R.id.imageViewNewFriend);
                textViewNewFriend = (TextView) view.findViewById(R.id.textViewNewFriend);
                imageButtonAccept = (ImageButton) view.findViewById(R.id.imageButtonAccept);
                imageButtonRefuse = (ImageButton) view.findViewById(R.id.imageButtonRefuse);
            }
        }

        @NonNull
        @Override
        public NewFriendAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_friend_item, parent, false);
            NewFriendAdapter.ViewHolder viewHolder = new NewFriendAdapter.ViewHolder(view);
            viewHolder.imageButtonAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Request request = list.get(viewHolder.getAdapterPosition());
                    list.remove(request);
                    recyclerViewNewFriend.setAdapter(new NewFriendAdapter(list));
                    BmobQuery<Request> bmobQuery = new BmobQuery<>();
                    bmobQuery.findObjects(new FindListener<Request>() {
                        @Override
                        public void done(List<Request> list, BmobException e) {
                            for (Request r : list) {
                                if (r.getRequesterId().contentEquals(request.getRequesterId()) && r.getReceiverId().contentEquals(request.getReceiverId())) {
                                    request.setObjectId(r.getObjectId());
                                    request.delete(new UpdateListener() {
                                        public void done(BmobException e) {
                                            if (e == null) {
                                                helper.deleteRequest(request.getRequesterId(), request.getReceiverId());
                                                Friend friend = new Friend(request.getRequesterId(), request.getReceiverId());
                                                friend.save(new SaveListener<String>() {
                                                    @Override
                                                    public void done(String s, BmobException e) {
                                                        if (e == null) {
                                                            Snackbar snackbar = Snackbar.make(imageButtonFriendBack, "You have accepted the request from " + request.getRequesterName(), Snackbar.LENGTH_SHORT);
                                                            snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorBlue));
                                                            snackbar.show();
                                                        } else {
                                                            Snackbar snackbar = Snackbar.make(imageButtonFriendBack, "Check your network connection", Snackbar.LENGTH_SHORT);
                                                            snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorRed));
                                                            snackbar.show();
                                                        }
                                                    }
                                                });
                                            } else {
                                                Snackbar snackbar = Snackbar.make(imageButtonFriendBack, "Check your network connection", Snackbar.LENGTH_SHORT);
                                                snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorRed));
                                                snackbar.show();
                                            }
                                        }

                                    });
                                    return;
                                }
                            }
                        }
                    });
                }
            });

            viewHolder.imageButtonRefuse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Request request = list.get(viewHolder.getAdapterPosition());
                    list.remove(request);
                    recyclerViewNewFriend.setAdapter(new NewFriendAdapter(list));
                    BmobQuery<Request> bmobQuery = new BmobQuery<>();
                    bmobQuery.findObjects(new FindListener<Request>() {
                        @Override
                        public void done(List<Request> list, BmobException e) {
                            for (Request r : list) {
                                if (r.getRequesterId().contentEquals(request.getRequesterId()) && r.getReceiverId().contentEquals(request.getReceiverId())) {
                                    request.setObjectId(r.getObjectId());
                                    request.delete(new UpdateListener() {
                                        public void done(BmobException e) {
                                            if (e == null) {
                                                Snackbar snackbar = Snackbar.make(imageButtonFriendBack, "You have refused the request from " + request.getRequesterName(), Snackbar.LENGTH_SHORT);
                                                snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorBlue));
                                                snackbar.show();
                                                helper.deleteRequest(request.getRequesterId(), request.getReceiverId());
                                            } else {
                                                Snackbar snackbar = Snackbar.make(imageButtonFriendBack, "Check your network connection", Snackbar.LENGTH_SHORT);
                                                snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorRed));
                                                snackbar.show();
                                            }
                                        }

                                    });
                                    return;
                                }
                            }
                        }
                    });
                }
            });
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull NewFriendAdapter.ViewHolder holder, int position) {
            Request request = list.get(position);
            holder.textViewNewFriend.setText(request.getRequesterName());
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

}