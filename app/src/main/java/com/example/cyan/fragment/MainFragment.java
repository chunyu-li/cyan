package com.example.cyan.fragment;

import android.app.Application;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cyan.DBOpenHelper;
import com.example.cyan.MyApplication;
import com.example.cyan.R;
import com.example.cyan.object.Request;
import com.example.cyan.object.User;
import com.example.cyan.activity.AddFriendActivity;
import com.google.android.material.tabs.TabLayout;
import com.example.cyan.fragment.ContactFragment;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * @author Chunyu Li
 * @File: MainFragment.java
 * @Package com.example.cyan.fragment
 * @date 12/12/20 8:27 PM
 * @Description: A frame to place different fragments, change the displaying fragment according to
 * click event
 */

public class MainFragment extends Fragment {

    private View view;
    private TabLayout tabLayout;
    private TextView textViewTitle;
    private ImageView imageViewPlus;
    private Fragment messageFragment;
    private Fragment contactFragment;
    private Fragment discoverFragment;
    private Fragment meFragment;
    private int index;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = (View) inflater.inflate(R.layout.fragment_main, container, false);
        initFragment();
        replaceFragment(messageFragment);
        index = 0;
        initView(view);
        setEvent();
        setTabLayout();
        initData();
        return view;
    }

    private void initView(View view) {
        tabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
        textViewTitle = (TextView) view.findViewById(R.id.textViewTitle);
        imageViewPlus = (ImageView) view.findViewById(R.id.imageViewPlus);
    }

    private void setEvent() {
        imageViewPlus.setOnClickListener(new View.OnClickListener() {
            PopupWindow popupWindow = null;
            boolean windowExist = false;

            @Override
            public void onClick(View view) {
                switch (index) {
                    case 0:
//                        if (popupWindow == null) {
//                            popupWindow = new PopupWindow(getActivity());
//                            LinearLayout menu = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.pop_menu_layout, null);
//                            popupWindow.setContentView(menu);
//                            Drawable drawable = getResources().getDrawable(R.drawable.pop_bg);
//                            popupWindow.setBackgroundDrawable(drawable);
//                            popupWindow.setFocusable(true);
//                        }
//                        if (windowExist == false) {
//                            popupWindow.showAsDropDown(view);
//                            windowExist = true;
//                        } else {
//                            popupWindow.dismiss();
//                            windowExist = false;
//                        }
                        break;
                    case 1:
                        Intent intent = new Intent(getContext(), AddFriendActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }

            }
        });
    }

    private void setTabLayout() {

        for (int i = 0; i < 4; i++) {
            tabLayout.addTab(tabLayout.newTab());
        }

        for (int i = 0; i < 4; i++) {
            tabLayout.getTabAt(i).setCustomView(makeTabView(i));
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ImageView imageView;
                switch (tab.getPosition()) {
                    case 0:
                        imageView = tab.getCustomView().findViewById(R.id.imageViewIcon);
                        imageView.setImageResource(R.drawable.message2);
                        textViewTitle.setText("Message");
                        replaceFragment(messageFragment);
                        index = 0;
                        imageViewPlus.setVisibility(View.INVISIBLE);
//                        imageViewPlus.setVisibility(View.VISIBLE);
//                        imageViewPlus.setImageResource(R.drawable.plus);
                        break;
                    case 1:
                        imageView = tab.getCustomView().findViewById(R.id.imageViewIcon);
                        imageView.setImageResource(R.drawable.contact2);
                        textViewTitle.setText("Contacts");
                        replaceFragment(contactFragment);
                        index = 1;
                        imageViewPlus.setVisibility(View.VISIBLE);
                        imageViewPlus.setImageResource(R.drawable.add_friend);
                        break;
                    case 2:
                        imageView = tab.getCustomView().findViewById(R.id.imageViewIcon);
                        imageView.setImageResource(R.drawable.discover2);
                        textViewTitle.setText("Discover");
                        replaceFragment(discoverFragment);
                        index = 2;
                        imageViewPlus.setVisibility(View.INVISIBLE);
                        break;
                    case 3:
                        imageView = tab.getCustomView().findViewById(R.id.imageViewIcon);
                        imageView.setImageResource(R.drawable.my2);
                        textViewTitle.setText("Me");
                        replaceFragment(meFragment);
                        index = 3;
                        imageViewPlus.setVisibility(View.INVISIBLE);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                ImageView imageView;
                switch (tab.getPosition()) {
                    case 0:
                        imageView = tab.getCustomView().findViewById(R.id.imageViewIcon);
                        imageView.setImageResource(R.drawable.message1);
                        break;
                    case 1:
                        imageView = tab.getCustomView().findViewById(R.id.imageViewIcon);
                        imageView.setImageResource(R.drawable.contact1);
                        break;
                    case 2:
                        imageView = tab.getCustomView().findViewById(R.id.imageViewIcon);
                        imageView.setImageResource(R.drawable.discover1);
                        break;
                    case 3:
                        imageView = tab.getCustomView().findViewById(R.id.imageViewIcon);
                        imageView.setImageResource(R.drawable.my1);
                        break;
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.center_layout, fragment);
        transaction.commit();
    }

    private View makeTabView(int position) {
        View tabView = LayoutInflater.from(this.getContext()).inflate(R.layout.tab_view, null);
        ImageView imageView = tabView.findViewById(R.id.imageViewIcon);
        switch (position) {
            case 0:
                imageView.setImageResource(R.drawable.message2);
                break;
            case 1:
                imageView.setImageResource(R.drawable.contact1);
                break;
            case 2:
                imageView.setImageResource(R.drawable.discover1);
                break;
            case 3:
                imageView.setImageResource(R.drawable.my1);
                break;
        }
        return tabView;
    }

    private void initFragment() {
        messageFragment = new MessageFragment();
        contactFragment = new ContactFragment();
        discoverFragment = new DiscoverFragment();
        meFragment = new MeFragment();
    }

    private void initData() {
        Bundle bundle = MainFragment.this.getArguments();
        User user = (User) bundle.getSerializable("user");
        MyApplication.setUser(user);
    }

}