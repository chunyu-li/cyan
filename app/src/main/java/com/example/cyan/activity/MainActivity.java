package com.example.cyan.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.example.cyan.R;
import com.example.cyan.fragment.ContactFragment;
import com.example.cyan.fragment.LoginFragment;
import com.example.cyan.fragment.StartFragment;

/**
 * @author Chunyu Li
 * @File: MainActivity.java
 * @Package com.example.cyan.activity
 * @date 12/12/20 8:12 PM
 * @Description: This activity first add start fragment, then add login fragment after 2 seconds
 */

public class MainActivity extends AppCompatActivity {

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                addFragment(new LoginFragment());
            }
        };

        addFragment(new StartFragment());
        initThread();
    }

    private void addFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    private void initThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    Message message = new Message();
                    handler.sendMessage(message);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}