package com.example.cyan.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.cyan.R;
import com.example.cyan.Util;
import com.example.cyan.object.User;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * @author Chunyu Li
 * @File: LoginFragment.java
 * @Package com.example.cyan.fragment
 * @date 12/12/20 8:26 PM
 * @Description: Get the user objects from web server and decide whether user can log in by the
 * input of user
 */

public class LoginFragment extends Fragment {

    private View view;
    private Button buttonLogin;
    private TextView textViewRegister;
    private TextView textViewForget;
    private EditText editTextUsername;
    private EditText editTextPassword;
    private ProgressBar progressBarLogin;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_login, container, false);
        initView();
        setEvent();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        editTextUsername.setText("");
        editTextPassword.setText("");
    }

    private void initView() {
        buttonLogin = (Button) view.findViewById(R.id.buttonLogin);
        textViewRegister = (TextView) view.findViewById(R.id.textViewRegister);
        textViewForget = (TextView) view.findViewById(R.id.textViewForget);
        editTextUsername = (EditText) view.findViewById(R.id.editTextUsername);
        editTextPassword = (EditText) view.findViewById(R.id.editTextPassword);
        progressBarLogin = (ProgressBar) view.findViewById(R.id.progressBarLogin);
    }

    private void setEvent() {
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBarLogin.setVisibility(View.VISIBLE);
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                String username = editTextUsername.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                BmobQuery<User> bmobQuery = new BmobQuery<>();
                bmobQuery.findObjects(new FindListener<User>() {
                    @Override
                    public void done(List<User> list, BmobException e) {
                        if (e == null) {
                            for (User user : list) {
                                if (user.getUsername().contentEquals(username) && user.getPassword().contentEquals(password)) {

                                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    MainFragment mainFragment = new MainFragment();
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("user", user);
                                    mainFragment.setArguments(bundle);
                                    fragmentTransaction.replace(R.id.fragment_container, mainFragment);
                                    fragmentTransaction.commit();

                                    Util.showSnackBar("blue", buttonLogin, "Login successfully", getContext());
                                    progressBarLogin.setVisibility(View.GONE);
                                    return;
                                }
                            }
                            Util.showSnackBar("red", buttonLogin, "The username or password is wrong", getContext());
                            progressBarLogin.setVisibility(View.GONE);
                        } else {
                            Util.showSnackBar("red", buttonLogin, "Check your network connection", getContext());
                            progressBarLogin.setVisibility(View.GONE);
                        }
                    }
                });

            }
        });

        textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFragment(new RegisterFragment());
            }
        });

        textViewForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFragment(new VerifyFragment());
            }
        });
    }

    private void addFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack("login");
        fragmentTransaction.commit();
    }
}