package com.example.cyan.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cyan.activity.MapActivity;
import com.example.cyan.activity.MomentActivity;
import com.example.cyan.R;

/**
 * @author Chunyu Li
 * @File: DiscoverFragment.java
 * @Package com.example.cyan.fragment
 * @date 12/12/20 8:19 PM
 * @Description: I was going to implement multiple functions in discover interface, but I just implement basic
 * moment function due to the short time
 */

public class DiscoverFragment extends Fragment {

    private View view;
    private ConstraintLayout layoutMoment;
    private ConstraintLayout layoutLocation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_discover, container, false);
        initView();
        setEvent();
        return view;
    }

    private void initView() {
        layoutMoment = view.findViewById(R.id.layoutMoment);
        layoutLocation = view.findViewById(R.id.layoutLocation);
    }

    private void setEvent() {
        layoutMoment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MomentActivity.class);
                startActivity(intent);
            }
        });

        layoutLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MapActivity.class);
                startActivity(intent);
            }
        });
    }
}