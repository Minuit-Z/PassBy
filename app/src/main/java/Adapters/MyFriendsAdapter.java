package Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ziye.passby.R;
import java.util.List;
import beans.Friend;
import beans.User;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import utils.SpUtil;

/**
 * Created by 德帅 on 2016/9/1.
 * action:
 */
public class MyFriendsAdapter extends BaseAdapter {

    private List<Friend> list;
    private Context c;
    private int sign; //标记符号,,,0时没有添加按钮,,1是有按钮

    public MyFriendsAdapter(List<Friend> list, Context c,int sign) {
        this.list = list;
        this.c = c;
        this.sign=sign;
    }

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
            convertView = View.inflate(c, R.layout.item_lv_friend, null);
            hold.img = (ImageView) convertView.findViewById(R.id.img_item_lv_nickpic);
            hold.tv = (TextView) convertView.findViewById(R.id.tv_friend_name);
            hold.btn= (Button) convertView.findViewById(R.id.btn_add);
            convertView.setTag(hold);
        }
        hold = (ViewHolder1) convertView.getTag();
        //好友姓名
        final String name = list.get(position).getFriendName();
        hold.tv.setText(name);
        //好友头像
        switch (name){
            case "ziye":
                hold.img.setImageResource(R.drawable.drawer_avatar);
                break;
            case "mtf":
                hold.img.setImageResource(R.drawable.imgm);
                break;
            case "王蕊芳":
                hold.img.setImageResource(R.drawable.imgw);
                break;
            case "wby":
                hold.img.setImageResource(R.drawable.imgwb);
                break;
            case "任星凯":
                hold.img.setImageResource(R.drawable.imgr);
                break;
            default:
                hold.img.setImageResource(R.drawable.drawer_avatar);
        }
        BmobQuery<User> query = new BmobQuery<>();
        query.addWhereEqualTo("userName", list.get(position).getFriendName());
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list2, BmobException e) {
                if (e == null) {
                    Toast.makeText(c, list2.size(), Toast.LENGTH_SHORT).show();
                    if (list2.get(0).getUserIcon() == null) {
                        Toast.makeText(c, list.get(position).getFriendName() + "的icon为空", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(c, list2.get(1).getUserIcon(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        if (sign==0){
            //标记为0,不显示btn
            hold.btn.setVisibility(View.INVISIBLE);
        }else {
            //标记不为0,按钮可用
            hold.btn.setVisibility(View.VISIBLE);
            hold.btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //吧好友映射添加到Friend表中
                    Friend f=new Friend();
                    f.setUserName(SpUtil.getUserNamefromSP(c,null));
                    f.setFriendName(name);
                    f.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            Toast.makeText(c, "添加完成", Toast.LENGTH_SHORT).show();
                        }

                    });
                }
            });
        }
        return convertView;
    }

    static class ViewHolder1 {
        ImageView img;
        TextView tv;
        Button btn;
    }
}
