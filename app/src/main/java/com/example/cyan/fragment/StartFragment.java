package com.example.cyan.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cyan.R;

/**
 * @author Chunyu Li
 * @File: StartFragment.java
 * @Package com.example.cyan.fragment
 * @date 12/12/20 8:30 PM
 * @Description: The static start fragment, last for 2 seconds
 */

public class StartFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_start, container, false);
    }
}