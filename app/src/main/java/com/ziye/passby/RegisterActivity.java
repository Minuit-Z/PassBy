package com.ziye.passby;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.transition.Explode;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import java.util.List;

import beans.User;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import utils.SpUtil;


public class RegisterActivity extends AppCompatActivity {

    FloatingActionButton fab;
    CardView cvAdd;
    private EditText etUsername,etPassword,etRepeatPass;
    private Button btn_register;
    private RelativeLayout root;
    static boolean isUserNameAvailable = false;
    static boolean isUserSaveSuccess=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ShowEnterAnimation();
        }

        initView();
        initEvent();
    }

    private void initEvent() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateRevealClose();
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=etUsername.getText().toString().trim();
                String pass=etPassword.getText().toString().trim();
                String pass_repeat=etRepeatPass.getText().toString().trim();
                if ("".equals(pass)){
                    Snackbar.make(root,"先把密码写上啊喂",Snackbar.LENGTH_SHORT)
                            .setAction("啊哈~", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //不写操作,默认关闭snackBar
                                }
                            })
                            .setActionTextColor(getResources().getColor(R.color.gray))
                            .show();
                }else if (!pass.equals(pass_repeat)){
                    Snackbar.make(root,"两次输入秘密啊不一致",Snackbar.LENGTH_SHORT)
                            .setAction("啊哈~", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            }).show();
                }else if (pass.equals(pass_repeat)){
                    if (checkUserNameAvailable(name)){
                        User user=new User();
                        user.setUserName(name);
                        user.setUserPass(pass);

                        if (saveUser(user)){
                            //注册完成,跳转至主界面,并存入SpUtil
                            SpUtil.setUserNamefromSP(name,RegisterActivity.this);


                            Explode explode = new Explode();
                            explode.setDuration(500);
                            getWindow().setExitTransition(explode);
                            getWindow().setEnterTransition(explode);
                            ActivityOptionsCompat oc2 = ActivityOptionsCompat.makeSceneTransitionAnimation(RegisterActivity.this);
                            Intent i2 = new Intent(RegisterActivity.this, MainActivity.class);
                            //跳转至主界面
                            startActivity(i2, oc2.toBundle());
                        }
                    }
                }
            }
        });
    }

    /**
     * 用户注册
     *@time 2016/9/2 20:43
     *@author ziye
     */
    private boolean saveUser(User user) {
        user.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                Snackbar.make(root,"注册完成,请等待....",Snackbar.LENGTH_SHORT).show();

                if (e==null){
                    Log.i("success",s);
                    isUserSaveSuccess=true;
                }else {
                    Log.i("Register",e.toString());
                    isUserSaveSuccess=false;
                }
            }
        });
        return isUserSaveSuccess;
    }
    /**
     * 检查用户名是否可用
     * 用户名必须是唯一的
     *
     * @time 2016/8/12 16:14
     * @author ziye
     */
    private boolean checkUserNameAvailable(final String username) {
        BmobQuery<User> query = new BmobQuery<User>();
        query.addQueryKeys("userName");
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e == null) {
                    //查询完成,开始匹配
                    for (int i = 0; i < list.size(); i++) {
                        if (username.equals(list.get(i).getUserName())) {
                            //发现匹配,返回false
                            isUserNameAvailable = false;
                        } else {
                            //不匹配,返回true
                            isUserNameAvailable = true;
                        }
                    }
                }else {
                    Log.i("4",e.toString());
                }
            }
        });
        return isUserNameAvailable;
    }

    private void initView() {
        setContentView(R.layout.activity_register);

        fab= (FloatingActionButton) findViewById(R.id.fab);
        cvAdd= (CardView) findViewById(R.id.cv_add);
        etUsername= (EditText) findViewById(R.id.et_register_username);
        etPassword= (EditText) findViewById(R.id.et_register_password);
        etRepeatPass= (EditText) findViewById(R.id.et_register_repeatpassword);
        btn_register= (Button) findViewById(R.id.btn_register);
        root= (RelativeLayout) findViewById(R.id.root);
    }

    private void ShowEnterAnimation() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.fabtransition);
        getWindow().setSharedElementEnterTransition(transition);

        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                cvAdd.setVisibility(View.GONE);
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                transition.removeListener(this);
                animateRevealShow();
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }


        });
    }

    /**
     * 刚进入的过场动画
     *@time 2016/9/2 20:22
     *@author ziye
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void animateRevealShow() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd, cvAdd.getWidth()/2,0, fab.getWidth() / 2, cvAdd.getHeight());
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                cvAdd.setVisibility(View.VISIBLE);
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }

    /**
     * 跳到前一页面的过场动画
     *@time 2016/9/2 20:21
     *@author ziye
     */
    public void animateRevealClose() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd,cvAdd.getWidth()/2,0, cvAdd.getHeight(), fab.getWidth() / 2);
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                cvAdd.setVisibility(View.INVISIBLE);
                super.onAnimationEnd(animation);
                fab.setImageResource(R.mipmap.plus);
                RegisterActivity.super.onBackPressed();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }
    @Override
    public void onBackPressed() {
        animateRevealClose();
    }
}
