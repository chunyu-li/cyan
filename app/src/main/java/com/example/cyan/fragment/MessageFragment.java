package com.example.cyan.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.cyan.MyApplication;
import com.example.cyan.activity.ChatActivity;
import com.example.cyan.R;
import com.example.cyan.object.Chat;
import com.example.cyan.object.Message;
import com.example.cyan.object.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * @author Chunyu Li
 * @File: MessageFragment.java
 * @Package com.example.cyan.fragment
 * @date 12/12/20 8:29 PM
 * @Description: Get the chat objects from web server and use logical statements to convert them
 * into message objects.
 */

public class MessageFragment extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private String me;
    private List<Message> messages = new ArrayList<>();
    private List<Chat> chats = new ArrayList<>();
    private List<User> users;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_message, container, false);
        initView();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateData();
    }

    private void initView() {
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewMessage);
        me = MyApplication.getUser().getUsername();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        MessageAdapter messageAdapter = new MessageAdapter();
        recyclerView.setAdapter(messageAdapter);
    }

    private void updateData() {
        BmobQuery<User> bmobQuery = new BmobQuery<>();
        bmobQuery.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                users = new ArrayList<>(list);
                updateMessage();
            }
        });
    }

    private void updateMessage() {
        BmobQuery<Chat> bmobQuery = new BmobQuery<>();
        bmobQuery.findObjects(new FindListener<Chat>() {
            @Override
            public void done(List<Chat> list, BmobException e) {
                List<Message> previousMessages = new ArrayList<>(messages);
                messages.clear();
                Log.d("MessageFragment", String.valueOf(list == null));
                if (e != null) {
                    e.printStackTrace();
                }
                if (list != null) {
                    for (Chat chat : list) {
                        boolean find = false;
                        if (chat.getSender().contentEquals(me)) {
                            for (Message message : messages) {
                                if (message.getTitle().contentEquals(chat.getReceiver())) {
                                    if (chat.getOrder() > message.getOrder())
                                        message.setContent(chat.getContent());
                                    find = true;
                                    break;
                                }
                            }
                            if (!find)
                                messages.add(new Message(chat.getReceiver(), chat.getContent(), 0, chat.getOrder(), getAvatar(chat.getReceiver())));
                        } else if (chat.getReceiver().contentEquals(me)) {
                            for (Message message : messages) {
                                if (message.getTitle().contentEquals(chat.getSender())) {
                                    if (!chat.isRead()) {
                                        message.increasePrompt();
                                        Collections.swap(messages, messages.indexOf(message), 0);
                                    }
                                    if (chat.getOrder() > message.getOrder())
                                        message.setContent(chat.getContent());
                                    find = true;
                                    break;
                                }
                            }
                            if (!find) {
                                if (chat.isRead())
                                    messages.add(new Message(chat.getSender(), chat.getContent(), 0, chat.getOrder(), getAvatar(chat.getSender())));
                                else {
                                    messages.add(0, new Message(chat.getSender(), chat.getContent(), 1, chat.getOrder(), getAvatar(chat.getSender())));
                                }
                            }
                        }
                    }
                }
                if (!compareLists(previousMessages, messages))
                    recyclerView.setAdapter(new MessageAdapter());
            }
        });
    }

    private String getAvatar(String name) {
        for (User user : users) {
            if (user.getUsername().contentEquals(name))
                return user.getAvatar();
        }
        return "123";
    }

    private boolean compareLists(List<Message> list1, List<Message> list2) {
        if (list1.size() != list2.size())
            return false;
        for (int i = 0; i < list1.size(); i++) {
            if (!list1.get(i).getTitle().contentEquals(list2.get(i).getTitle()))
                return false;
            if (!list1.get(i).getContent().contentEquals(list2.get(i).getContent()))
                return false;
            if (list1.get(i).getPrompt() != list2.get(i).getPrompt())
                return false;
        }
        return true;
    }

    class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imageViewMessage;
            TextView textViewMessageTitle;
            TextView textViewMessageContent;
            TextView textViewMessagePrompt;

            public ViewHolder(View view) {
                super(view);
                imageViewMessage = (ImageView) view.findViewById(R.id.imageViewMessage);
                textViewMessageTitle = (TextView) view.findViewById(R.id.textViewMessageTitle);
                textViewMessageContent = (TextView) view.findViewById(R.id.textViewMessageContent);
                textViewMessagePrompt = (TextView) view.findViewById(R.id.textViewMessagePrompt);
            }
        }

        @NonNull
        @Override
        public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
            MessageAdapter.ViewHolder viewHolder = new MessageAdapter.ViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Message message = messages.get(viewHolder.getAdapterPosition());
                    Intent intent = new Intent(getContext(), ChatActivity.class);
                    intent.putExtra("username", message.getTitle());
                    intent.putExtra("avatar", message.getAvatar());
                    startActivity(intent);
                }
            });
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Message message = messages.get(position);
            holder.textViewMessageTitle.setText(message.getTitle());
            holder.textViewMessageContent.setText(message.getContent());
            loadImage(holder.imageViewMessage, message.getAvatar());

            if (message.getPrompt() != 0) {
                holder.textViewMessagePrompt.setVisibility(View.VISIBLE);
                holder.textViewMessagePrompt.setText(String.valueOf(message.getPrompt()));
            }
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }
    }

    private void loadImage(ImageView imageView, String avatar) {
        if (avatar == null)
            avatar = "123";
        RequestOptions options = new RequestOptions().bitmapTransform(new RoundedCorners(30));
        if (avatar.contentEquals("123")) {
            Glide.with(getContext())
                    .load(R.drawable.user)
                    .apply(options)
                    .into(imageView);
        } else {
            Glide.with(getContext())
                    .load(avatar)
                    .placeholder(R.drawable.user)
                    .error(R.drawable.user)
                    .apply(options)
                    .into(imageView);
        }
    }
}