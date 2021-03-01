package com.example.cyan.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.example.cyan.R;
import com.example.cyan.object.User;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * @author Chunyu Li
 * @File: RegisterFragment.java
 * @Package com.example.cyan.fragment
 * @date 12/12/20 8:30 PM
 * @Description: Create a new user object according to the input of user and upload it to the server
 */

public class RegisterFragment extends Fragment {

    private View view;
    private ImageButton imageButtonRegisterBack;
    private EditText editTextUsername;
    private EditText editTextPassword;
    private EditText editTextEmail;
    private Button buttonRegister;
    private ProgressBar progressBarRegister;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_register, container, false);
        initView();
        setEvent();
        return view;
    }

    private void initView() {
        imageButtonRegisterBack = (ImageButton) view.findViewById(R.id.imageButtonRegisterBack);
        editTextUsername = (EditText) view.findViewById(R.id.editTextUsername);
        editTextPassword = (EditText) view.findViewById(R.id.editTextPassword);
        editTextEmail = (EditText) view.findViewById(R.id.editTextEmail);
        buttonRegister = (Button) view.findViewById(R.id.buttonRegister);
        progressBarRegister = (ProgressBar) view.findViewById(R.id.progressBarRegister);
    }

    private void setEvent() {
        imageButtonRegisterBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBarRegister.setVisibility(View.VISIBLE);
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                String username = editTextUsername.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                String email = editTextEmail.getText().toString().trim();

                BmobQuery<User> bmobQuery = new BmobQuery<>();
                bmobQuery.findObjects(new FindListener<User>() {
                    @Override
                    public void done(List<User> list, BmobException e) {
                        if (list != null) {
                            for (User u : list) {
                                if (u.getUsername().contentEquals(username)) {
                                    Snackbar snackbar = Snackbar.make(buttonRegister, "The username has been used", Snackbar.LENGTH_SHORT);
                                    snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorRed));
                                    snackbar.show();
                                    progressBarRegister.setVisibility(View.GONE);
                                    return;
                                }
                            }
                        }
                        User user = new User(username, password, "123", email);
                        user.save(new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                if (e == null) {
                                    Snackbar snackbar = Snackbar.make(view, "Register successfully", Snackbar.LENGTH_SHORT);
                                    snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorBlue));
                                    snackbar.show();
                                    progressBarRegister.setVisibility(View.GONE);

                                    getActivity().onBackPressed();
                                } else {
                                    Snackbar snackbar = Snackbar.make(view, "Check your network connection", Snackbar.LENGTH_SHORT);
                                    snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorRed));
                                    snackbar.show();
                                    progressBarRegister.setVisibility(View.GONE);
                                }
                            }
                        });
                    }
                });

            }
        });
    }
}