package com.ziye.passby;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.transition.Explode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.List;

import beans.User;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import utils.SpUtil;


public class Login extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private Button btGo;
    private CardView cv;
    private FloatingActionButton fab_float;
    private RelativeLayout login_root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        initView();
        initData();
        initEvent();
    }

    private void initView() {
        setContentView(R.layout.activity_login);

        etPassword = (EditText) findViewById(R.id.et_password);
        etUsername = (EditText) findViewById(R.id.et_username);
        btGo = (Button) findViewById(R.id.bt_go);
        cv = (CardView) findViewById(R.id.cv);
        fab_float = (FloatingActionButton) findViewById(R.id.fab_float);
        login_root = (RelativeLayout) findViewById(R.id.login_root);
    }

    private void initData() {
        String spUserName = SpUtil.getUserNamefromSP(Login.this, null);
        if (spUserName != null) {
            etUsername.setText(spUserName);
        }
    }

    private void initEvent() {

        fab_float.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWindow().setExitTransition(null);
                getWindow().setEnterTransition(null);
                ActivityOptions options =
                        ActivityOptions.makeSceneTransitionAnimation(Login.this, fab_float, fab_float.getTransitionName());
                startActivity(new Intent(Login.this, RegisterActivity.class), options.toBundle());
            }
        });

        btGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Explode explode = new Explode();
                explode.setDuration(500);

                getWindow().setExitTransition(explode);
                getWindow().setEnterTransition(explode);

                String username = etUsername.getText().toString().trim();
                String userpass = etPassword.getText().toString().trim();

                //检查是否可以登录
                checkLogin(username, userpass);
            }
        });

    }

    private void checkLogin(final String username, final String userpass) {

        Snackbar.make(login_root, "waiting......", Snackbar.LENGTH_LONG).show();
        BmobQuery<User> query = new BmobQuery<User>();
        query.addWhereEqualTo("userName", username);
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e == null) {
                    if (userpass.equals(list.get(0).getUserPass().trim())) {
                        Log.i("_____", "user1");
                        SpUtil.setUserNamefromSP(username, Login.this);//向sp写入数据
                        ActivityOptionsCompat oc2 = ActivityOptionsCompat.makeSceneTransitionAnimation(Login.this);
                        Intent i2 = new Intent(Login.this, MainActivity.class);
                        startActivity(i2, oc2.toBundle());
                    } else {
                        Snackbar.make(login_root,e.toString(),Snackbar.LENGTH_LONG).show();
                        etPassword.setText("");
                    }
                }
            }
        });

    }

}
