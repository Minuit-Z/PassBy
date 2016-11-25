package com.ziye.passby;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.platform.comapi.map.L;

import java.util.List;

import Adapters.MyFriendsAdapter;
import beans.Friend;
import beans.User;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SQLQueryListener;
import cn.bmob.v3.listener.SaveListener;
import utils.SpUtil;

public class AddFriendActivity extends AppCompatActivity {
    private Toolbar tb_addfriend;
    private EditText et_addfriend;
    private ListView lv_addfriend;
    private Button btn_add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initData();
        initEvent();
    }

    private void initData() {
    }

    private void initEvent() {
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = et_addfriend.getText().toString();
                BmobQuery<User> query = new BmobQuery<User>();
                if (!name.isEmpty()) {
                    query.addWhereEqualTo("userName", name);
                }
                query.findObjects(new FindListener<User>() {
                    @Override
                    public void done(final List<User> list, BmobException e) {
                        if (e == null) {
                            lv_addfriend.setAdapter(new BaseAdapter() {
                                @Override
                                public int getCount() {
                                    return list.size();
                                }

                                @Override
                                public Object getItem(int position) {
                                    return list.get(position);
                                }

                                @Override
                                public long getItemId(int position) {
                                    return position;
                                }

                                @Override
                                public View getView(final int position, View convertView, ViewGroup parent) {

                                    ViewHolder1 hold = null;
                                    if (convertView == null) {
                                        hold = new ViewHolder1();
                                        convertView = View.inflate(AddFriendActivity.this, R.layout.item_lv_friend, null);
                                        hold.img = (ImageView) convertView.findViewById(R.id.img_item_lv_nickpic);
                                        hold.tv = (TextView) convertView.findViewById(R.id.tv_friend_name);
                                        hold.btn = (Button) convertView.findViewById(R.id.btn_add);
                                        convertView.setTag(hold);
                                    }
                                    hold = (ViewHolder1) convertView.getTag();
                                    //好友姓名
                                    final String name = list.get(position).getUserName();
                                    hold.tv.setText(name);
                                    //好友头像
                                    BmobQuery<User> query = new BmobQuery<>();
                                    query.addWhereEqualTo("userName", list.get(position).getUserName());
                                    query.findObjects(new FindListener<User>() {
                                        @Override
                                        public void done(List<User> list2, BmobException e) {
                                            if (e == null) {
                                                Toast.makeText(AddFriendActivity.this, list2.size(), Toast.LENGTH_SHORT).show();
                                                if (list2.get(0).getUserIcon() == null) {
                                                    Toast.makeText(AddFriendActivity.this, list.get(position).getUserName() + "的icon为空", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(AddFriendActivity.this, list2.get(1).getUserIcon(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }
                                    });
                                    hold.btn.setVisibility(View.VISIBLE);
                                    hold.btn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            //吧好友映射添加到Friend表中
                                            Friend f = new Friend();
                                            f.setUserName(SpUtil.getUserNamefromSP(AddFriendActivity.this, null));
                                            f.setFriendName(name);
                                            f.save(new SaveListener<String>() {
                                                @Override
                                                public void done(String s, BmobException e) {
                                                    Toast.makeText(AddFriendActivity.this, "添加完成", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                        }
                                    });
                                    return convertView;
                                }
                            });
                        }
                    }
                });
            }

        });


    }

    private void initView() {
        setContentView(R.layout.activity_add_friend);

        tb_addfriend = (Toolbar) findViewById(R.id.tb_addfriend);
        setSupportActionBar(tb_addfriend);
        tb_addfriend.setTitle("添加好友");

        et_addfriend = (EditText) findViewById(R.id.et_name);
        lv_addfriend = (ListView) findViewById(R.id.lv_addfriend);
        btn_add = (Button) findViewById(R.id.btn_addfriend_seek);
    }

    static class ViewHolder1 {
        ImageView img;
        TextView tv;
        Button btn;
    }
}
