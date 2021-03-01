package com.example.cyan.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cyan.MyApplication;
import com.example.cyan.R;
import com.example.cyan.Util;
import com.example.cyan.object.Friend;
import com.example.cyan.object.Request;
import com.example.cyan.object.User;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * @author Chunyu Li
 * @File: AddFriendActivity.java
 * @Package com.example.cyan.activity
 * @date 12/12/20 8:05 PM
 * @Description: Create new request object and saves it on server
 */

public class AddFriendActivity extends AppCompatActivity {

    private ImageButton imageButtonAddBack;
    private EditText editTextAddFriend;
    private ImageButton imageButtonSearch;
    private ImageView imageViewSearchUser;
    private TextView textViewSearchUser;
    private Button buttonAddFriend;
    private String receiverId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        initView();
        setEvent();
    }

    private void initView() {
        imageButtonAddBack = findViewById(R.id.imageButtonAddBack);
        editTextAddFriend = findViewById(R.id.editTextAddFriend);
        imageButtonSearch = findViewById(R.id.imageButtonSearch);
        imageViewSearchUser = findViewById(R.id.imageViewSearchUser);
        textViewSearchUser = findViewById(R.id.textViewSearchUser);
        buttonAddFriend = findViewById(R.id.buttonAddFriend);
    }

    private void setEvent() {
        imageButtonAddBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imageButtonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) AddFriendActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(AddFriendActivity.this.getWindow().getDecorView().getWindowToken(), 0);

                String username = editTextAddFriend.getText().toString().trim();
                BmobQuery<User> bmobQuery = new BmobQuery<>();
                bmobQuery.findObjects(new FindListener<User>() {
                    @Override
                    public void done(List<User> list, BmobException e) {
                        if (e == null) {
                            for (User user : list) {
                                if (user.getUsername().contentEquals(username)) {
                                    textViewSearchUser.setText(username);
                                    imageViewSearchUser.setVisibility(View.VISIBLE);
                                    buttonAddFriend.setVisibility(View.VISIBLE);
                                    receiverId = user.getObjectId();
                                    return;
                                }
                            }
                            Snackbar snackbar = Snackbar.make(imageButtonSearch, "This user does not exist", Snackbar.LENGTH_SHORT);
                            snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorGold));
                            snackbar.show();
                        } else {
                            Snackbar snackbar = Snackbar.make(imageButtonSearch, "Check your network connection", Snackbar.LENGTH_SHORT);
                            snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorRed));
                            snackbar.show();
                        }
                    }
                });
            }
        });

        buttonAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String requesterId = MyApplication.getUser().getObjectId();
                String requesterName = MyApplication.getUser().getUsername();
                String requesterAvatar = MyApplication.getUser().getAvatar();

                if (requesterId.contentEquals(receiverId)) {
                    Snackbar snackbar = Snackbar.make(buttonAddFriend, "You cannot add yourself", Snackbar.LENGTH_SHORT);
                    snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorRed));
                    snackbar.show();
                    return;
                }

                Request request = new Request(requesterId, receiverId, requesterName, requesterAvatar);
                BmobQuery<Friend> bmobQuery = new BmobQuery<>();
                bmobQuery.findObjects(new FindListener<Friend>() {
                    @Override
                    public void done(List<Friend> list, BmobException e) {
                        if (list != null) {
                            for (Friend friend : list) {
                                if ((request.getRequesterId().contentEquals(friend.getUser1()) && request.getReceiverId().contentEquals(friend.getUser2()))
                                        || (request.getRequesterId().contentEquals(friend.getUser2()) && request.getReceiverId().contentEquals(friend.getUser1()))) {
                                    Util.showSnackBar("yellow", buttonAddFriend, "You are already friends", AddFriendActivity.this);
                                    return;
                                }
                            }
                        }
                        request.save(new SaveListener<String>() {
                            @Override
                            public void done(String objectId, BmobException e) {
                                Snackbar snackbar;
                                if (e == null) {
                                    snackbar = Snackbar.make(buttonAddFriend, "Request has been sent", Snackbar.LENGTH_SHORT);
                                    snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorBlue));
                                } else {
                                    snackbar = Snackbar.make(buttonAddFriend, "Do not send the same request twice", Snackbar.LENGTH_SHORT);
                                    snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorGold));
                                }
                                snackbar.show();
                            }
                        });
                    }
                });

            }
        });
    }
}