package com.example.cyan.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.cyan.R;
import com.example.cyan.Util;
import com.example.cyan.object.User;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * @author Chunyu Li
 * @File: ResetFragment.java
 * @Package com.example.cyan.fragment
 * @date 12/13/20 6:46 PM
 * @Description: Enter new password and confirm password to reset password
 */

public class ResetFragment extends Fragment {

    private View view;
    private ImageButton imageButtonResetBack;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private Button buttonReset;
    private User verifyUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_reset, container, false);
        initView();
        setEvent();
        return view;
    }

    private void initView() {
        imageButtonResetBack = view.findViewById(R.id.imageButtonResetBack);
        editTextPassword = view.findViewById(R.id.editTextPassword);
        editTextConfirmPassword = view.findViewById(R.id.editTextConfirmPassword);
        buttonReset = view.findViewById(R.id.buttonReset);
        Bundle bundle = ResetFragment.this.getArguments();
        verifyUser = (User) bundle.getSerializable("user");
    }

    private void setEvent() {
        imageButtonResetBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                String password = editTextPassword.getText().toString().trim();
                String confirmPassword = editTextConfirmPassword.getText().toString().trim();
                if (password.contentEquals("")) {
                    Util.showSnackBar("red", buttonReset, "The password cannot be empty!", getContext());
                    return;
                }
                if (!password.contentEquals(confirmPassword)) {
                    Util.showSnackBar("red", buttonReset, "Two passwords do not match!", getContext());
                    return;
                }
                verifyUser.setPassword(password);
                verifyUser.update(verifyUser.getObjectId(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            Util.showSnackBar("blue", buttonReset, "Your password has been reset successfully!", getContext());
                        } else {
                            Util.showSnackBar("red", buttonReset, "Failed to reset password, check your network!", getContext());
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
}