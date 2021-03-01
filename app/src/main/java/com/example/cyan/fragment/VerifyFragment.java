package com.example.cyan.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * @author Chunyu Li
 * @File: VerifyFragment.java
 * @Package com.example.cyan.fragment
 * @date 12/13/20 6:46 PM
 * @Description: Program will send a email with verification code to user's email
 * to verify his account
 */

public class VerifyFragment extends Fragment {

    private View view;
    private ImageButton imageButtonVerifyBack;
    private EditText editTextUsername;
    private EditText editTextEmail;
    private EditText editTextCode;
    private Button buttonSend;
    private Button buttonVerify;
    private User verifyUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_verify, container, false);
        initView();
        setEvent();
        return view;
    }

    private void initView() {
        imageButtonVerifyBack = view.findViewById(R.id.imageButtonVerifyBack);
        editTextUsername = view.findViewById(R.id.editTextUsername);
        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextCode = view.findViewById(R.id.editTextCode);
        buttonSend = view.findViewById(R.id.buttonSend);
        buttonVerify = view.findViewById(R.id.buttonVerify);
    }

    private void setEvent() {
        imageButtonVerifyBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                String username = editTextUsername.getText().toString().trim();
                String email = editTextEmail.getText().toString().trim();
                BmobQuery<User> bmobQuery = new BmobQuery<>();
                bmobQuery.findObjects(new FindListener<User>() {
                    @Override
                    public void done(List<User> list, BmobException e) {
                        if (list != null) {
                            for (User user : list) {
                                if (user.getUsername().contentEquals(username)) {
                                    if (!user.getEmail().contentEquals(email)) {
                                        Util.showSnackBar("red", buttonSend, "This is not your email, try again", getContext());
                                        return;
                                    }
                                    Util.generateCode();
                                    Util.sendEmail(email, buttonSend, getContext());
                                    verifyUser = user;
                                    return;
                                }
                            }
                        }
                        Util.showSnackBar("red", buttonSend, "The username does not exist!", getContext());
                    }
                });
            }
        });

        buttonVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                String code = editTextCode.getText().toString().trim();
                if (code.contentEquals(Util.getVerificationCode())) {
                    Util.showSnackBar("blue", buttonSend, "The code is right, you can reset your password now!", getContext());
                    addFragment(new ResetFragment(), true);
                } else {
                    Util.showSnackBar("red", buttonSend, "The code is not right, try again!", getContext());
                }
            }
        });
    }

    private void addFragment(Fragment fragment, boolean hasBundle) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (hasBundle) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("user", verifyUser);
            fragment.setArguments(bundle);
        }
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack("verify");
        fragmentTransaction.commit();
    }
}