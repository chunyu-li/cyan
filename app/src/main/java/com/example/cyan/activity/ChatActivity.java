package com.example.cyan.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.cyan.MyApplication;
import com.example.cyan.Util;
import com.example.cyan.object.Chat;
import com.example.cyan.R;
import com.example.cyan.object.User;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * @author Chunyu Li
 * @File: ChatActivity.java
 * @Package com.example.cyan.activity
 * @date 12/12/20 8:06 PM
 * @Description: Get all chats from web server and decide which chats to add in list, then sort list
 */

public class ChatActivity extends AppCompatActivity {

    private TextView textViewChatUser;
    private ImageButton imageButtonChatBack;
    private Button buttonSend;
    private ImageView imageViewGetLocation;
    private EditText editTextChat;
    private RecyclerView recyclerView;
    private List<Chat> chats = new ArrayList<>();
    private User me;
    private String friendName;
    private String friendAvatar;
    private Thread thread;
    private boolean run;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initView();
        setEvent();
        updateChat();
        initThread();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        run = false;
    }

    private void initView() {
        me = MyApplication.getUser();
        friendName = getIntent().getStringExtra("username");
        friendAvatar = getIntent().getStringExtra("avatar");
        textViewChatUser = (TextView) findViewById(R.id.textViewChatUser);
        editTextChat = (EditText) findViewById(R.id.editTextChat);
        imageButtonChatBack = (ImageButton) findViewById(R.id.imageButtonChatBack);
        buttonSend = (Button) findViewById(R.id.buttonSend);
        imageViewGetLocation = (ImageView) findViewById(R.id.imageViewGetLocation);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewChat);
        textViewChatUser.setText(friendName);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ChatAdapter());
    }

    private void setEvent() {
        imageButtonChatBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = editTextChat.getText().toString();
                if (content.contentEquals(""))
                    return;
                Chat chat = new Chat(me.getUsername(), friendName, content, chats.size() + 1, false);
                chats.add(chat);
                recyclerView.getAdapter().notifyItemRangeChanged(chats.size() - 1, 1);
                recyclerView.scrollToPosition(chats.size() - 1);
                editTextChat.setText("");

                chat.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e != null) {
                            Snackbar snackbar = Snackbar.make(buttonSend, "Failed to send message", Snackbar.LENGTH_SHORT);
                            snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorRed));
                            snackbar.show();
                        }
                    }
                });
            }
        });

        imageViewGetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String shareUrl = MyApplication.getShareUrl();
                if (shareUrl.contentEquals("")) {
                    Util.showSnackBar("yellow", buttonSend, "You have not saved your location!", ChatActivity.this);
                    return;
                }
                editTextChat.setText(MyApplication.getShareUrl());
            }
        });
    }

    private void updateChat() {
        int previousSize = chats.size();
        String myName = me.getUsername();
        BmobQuery<Chat> bmobQuery = new BmobQuery<>();
        bmobQuery.findObjects(new FindListener<Chat>() {
            @Override
            public void done(List<Chat> list, BmobException e) {
                chats.clear();
                List<BmobObject> updateChats = new ArrayList<>();
                if (list != null) {
                    for (Chat chat : list) {
                        if (myName.contentEquals(chat.getSender()) && friendName.contentEquals(chat.getReceiver())) {
                            chats.add(chat);
                        }
                        if (myName.contentEquals(chat.getReceiver()) && friendName.contentEquals(chat.getSender())) {
                            chats.add(chat);
                            if (!chat.isRead()) {
                                chat.setRead(true);
                                Chat chat1 = new Chat(chat.getSender(), chat.getReceiver(), chat.getContent(), chat.getOrder(), chat.isRead());
                                chat1.setObjectId(chat.getObjectId());
                                updateChats.add(chat1);
                            }
                        }
                    }
                }
                Collections.sort(chats, new Comparator<Chat>() {
                    @Override
                    public int compare(Chat o1, Chat o2) {
                        return o1.getOrder() - o2.getOrder();
                    }
                });

                if (chats.size() != previousSize) {
                    recyclerView.setAdapter(new ChatAdapter());
                    recyclerView.scrollToPosition(chats.size() - 1);
                }

                if (!updateChats.isEmpty()) {
                    new BmobBatch().updateBatch(updateChats).doBatch(new QueryListListener<BatchResult>() {
                        @Override
                        public void done(List<BatchResult> results, BmobException e) {
                            if (e != null) {
                                Snackbar.make(textViewChatUser, "失败：" + e.getMessage() + "," + e.getErrorCode(), Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void initThread() {
        run = true;
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (run) {
                        Thread.sleep(1000);
                        updateChat();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

        class ViewHolder extends RecyclerView.ViewHolder {
            private TextView textView;
            private ImageView imageView;

            public ViewHolder(View view) {
                super(view);
                textView = view.findViewById(R.id.textView);
                imageView = view.findViewById(R.id.imageView);
            }
        }

        @NonNull
        @Override
        public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Chat chat = chats.get(position);
            holder.textView.setText(chat.getContent());

            if (chat.getSender().contentEquals(me.getUsername())) {
                loadImage(holder.imageView, me.getAvatar());
            } else if (chat.getSender().contentEquals(friendName)) {
                loadImage(holder.imageView, friendAvatar);
            }
        }

        @Override
        public int getItemCount() {
            return chats.size();
        }

        @Override
        public int getItemViewType(int position) {
            Chat chat = chats.get(position);
            if (chat.getSender().contentEquals(me.getUsername())) {
                return R.layout.chat_right_item;
            } else {
                return R.layout.chat_left_item;
            }
        }
    }

    private void loadImage(ImageView imageView, String avatar) {
        if (avatar == null)
            return;
        RequestOptions options = new RequestOptions().bitmapTransform(new RoundedCorners(20));
        if (avatar.contentEquals("123")) {
            Glide.with(this)
                    .load(R.drawable.user)
                    .apply(options)
                    .into(imageView);
        } else {
            Glide.with(this)
                    .load(avatar)
                    .placeholder(R.drawable.user)
                    .error(R.drawable.user)
                    .apply(options)
                    .into(imageView);
        }
    }

}