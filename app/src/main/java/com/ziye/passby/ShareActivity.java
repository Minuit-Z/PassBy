package com.ziye.passby;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import Adapters.MyAdapter;
import absClass.AppBarStateChangeListener;
import beans.Friend;
import beans.Share;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import utils.SpUtil;

public class ShareActivity extends AppCompatActivity {

    private SwipeRefreshLayout sr;
    private AppBarLayout ab;
    private ListView lv;
    private CollapsingToolbarLayout collapsingToolbar;
    private FloatingActionButton fbtn_addShare;
    private Toolbar toolbar;
    private MyAdapter adapter;
    private ArrayList<String> friendNames = new ArrayList<>(); //这个list为Share表的查询提供限定条件
    private List<Share> listItem = new ArrayList<>();
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Toast.makeText(ShareActivity.this, "___toast__", Toast.LENGTH_SHORT).show();
                    regetData();
                    break;
            }
        }
    };

    private int mLastY = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initData();
        initEvent();
        setupToolbar();

        xialaData();
    }

    private void initEvent() {

        //跳转到"添加分享的界面"
        fbtn_addShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ShareActivity.this,AddShareActivity.class));
            }
        });

    }

    private void initData() {
        collapsingToolbar.setTitle("分享");

        switch (SpUtil.getStyleCode(ShareActivity.this)){
            case 1:
                //默认主题
                findViewById(R.id.backdrop).setBackgroundResource(R.mipmap.default_style);
                break;
            case 2:
                //武乡
                findViewById(R.id.backdrop).setBackgroundResource(R.mipmap.wx_1);
                break;
            case 3:
                //太原
                findViewById(R.id.backdrop).setBackgroundResource(R.mipmap.ty);
                break;
            default:
                findViewById(R.id.backdrop).setBackgroundResource(R.drawable.drawer_background);
        }
    }

    private void initView() {
        setContentView(R.layout.activity_share);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        fbtn_addShare= (FloatingActionButton) findViewById(R.id.fbtn_add_share);
        sr = (SwipeRefreshLayout) findViewById(R.id.sr);
        lv = (ListView) findViewById(R.id.lv);

    }

    private void setupToolbar() {
        //根据滑动的情况,设置下拉刷新是否可用
        ab = (AppBarLayout) findViewById(R.id.appbar);
        ab.addOnOffsetChangedListener(new AppBarStateChangeListener() {

            @Override
            public void onStateChanged(AppBarLayout appBarLayout, final State state) {
                if (state == State.EXPANDED) {
                    //展开状态
                    sr.setEnabled(true);

                } else if (state == State.COLLAPSED) {
                    //折叠状态
                    sr.setEnabled(false);
                } else {
                    //中间状态
                    sr.setEnabled(false);
                }
            }
        });

        sr.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                sr.setRefreshing(true);
                //加载数据
                xialaData();
            }
        });

    }

    /**
     * 下拉刷新,重新加载listView
     *@time 2016/9/5 20:56
     *@author ziye
     */
    private void xialaData() {
        final BmobQuery<Friend> query = new BmobQuery<Friend>();
        //设定查询条件: Friend表中的userName=当前登录的用户
        query.addWhereEqualTo("userName", SpUtil.getUserNamefromSP(ShareActivity.this, null));
        query.findObjects(new FindListener<Friend>() {
            @Override
            public void done(List<Friend> list, BmobException e) {
                if (list != null) {
                    for (Friend f : list) {
                        friendNames.add(f.getFriendName());
                    }
                    Toast.makeText(ShareActivity.this, "list is not empty", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ShareActivity.this, "list is empty", Toast.LENGTH_SHORT).show();
                }
                friendNames.add(SpUtil.getUserNamefromSP(ShareActivity.this, null));//查找动态也将自己的动态查找出来

                BmobQuery<Share> query4share = new BmobQuery<Share>();
                query4share.addWhereContainedIn("userName", friendNames);
                query4share.order("-createdAt");
                query4share.findObjects(new FindListener<Share>() {
                    @Override
                    public void done(final List<Share> list, BmobException e) {
                        //此处的list为share表中的list
                        if (e == null) {
                            if (list.size() == 0) {
                                Toast.makeText(ShareActivity.this, "no new data", Toast.LENGTH_SHORT).show();
                            } else {
                                //开始封装adapter
                                listItem = list;
                                Log.i("_______list____", listItem.size() + "");
                                adapter = new MyAdapter(ShareActivity.this, listItem, handler);
                                lv.setAdapter(adapter);
                                new FinishDownRefresh().execute();
                            }
                        } else {
                            Toast.makeText(ShareActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    //下拉刷新数据,并停止动画操作,隐藏刷新的Item
    private class FinishDownRefresh extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            sr.setRefreshing(false);
        }
    }
    private void regetData() {
        final BmobQuery<Friend> query = new BmobQuery<Friend>();
        //设定查询条件: Friend表中的userName=当前登录的用户
        query.addWhereEqualTo("userName", SpUtil.getUserNamefromSP(ShareActivity.this, null));
        query.findObjects(new FindListener<Friend>() {
            @Override
            public void done(List<Friend> list, BmobException e) {
                if (list != null) {
                    for (Friend f : list) {
                        friendNames.add(f.getFriendName());
                    }
                    Toast.makeText(ShareActivity.this, "list is not empty", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ShareActivity.this, "list is empty", Toast.LENGTH_SHORT).show();
                }
                friendNames.add(SpUtil.getUserNamefromSP(ShareActivity.this, null));//查找动态也将自己的动态查找出来

                BmobQuery<Share> query4share = new BmobQuery<Share>();
                query4share.addWhereContainedIn("userName", friendNames);
                query4share.setLimit(5); //只查询前5条结果
                query4share.order("-createdAt");
                query4share.findObjects(new FindListener<Share>() {
                    @Override
                    public void done(final List<Share> list, BmobException e) {
                        //此处的list为share表中的list
                        if (e == null) {
                            if (list.size() == 0) {
                                Toast.makeText(ShareActivity.this, "no new data", Toast.LENGTH_SHORT).show();
                            } else {
                                //开始封装adapter
                                listItem = list;
                                Log.i("_______list____", listItem.get(0).getComment());
                                adapter.refreshData(listItem);
                            }
                        } else {
                            Toast.makeText(ShareActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

}
