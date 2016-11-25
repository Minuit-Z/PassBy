package Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.ziye.passby.R;

import java.util.List;

import beans.Share;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import utils.BitmapCache;
import utils.SpUtil;

/**
 * 分享列表的adapter
 * Created by 德帅 on 2016/8/26.
 * action:
 */
public class MyAdapter extends BaseAdapter {
    private Context c;
    private List<Share> list;
    private Handler handl;
    private int index; //焦点的位置

    public MyAdapter(Context c, List<Share> list, Handler handler) {
        this.c = c;
        this.handl = handler;
        this.list = list;
    }

    public void refreshData(List<Share> data) {
        this.list = data;
        notifyDataSetChanged();
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
    public View getView(final int position, View convertView, ViewGroup
            parent) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(c, R.layout.item_list_share, null);
            holder = new ViewHolder();
            holder.img_nick = (ImageView) convertView.findViewById(R.id.img_item_head_pic);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_item_head_username);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_item_head_time);
            holder.tv_msg = (TextView) convertView.findViewById(R.id.tv_item_message);
            holder.img_msg = (ImageView) convertView.findViewById(R.id.img_item_file);
            holder.et_mycomment = (EditText) convertView.findViewById(R.id.et_item_my_comment);
            holder.tv_comment= (TextView) convertView.findViewById(R.id.tv_item_comment);
            holder.btn_commit = (Button) convertView.findViewById(R.id.btn_item_send_comment);

            convertView.setTag(holder);
        }
        holder = (ViewHolder) convertView.getTag();

        final LruCache<String, Bitmap> lru = new LruCache<>(20);
        //用户名
        holder.tv_name.setText(list.get(position).getUserName());
        //头像
        switch (list.get(position).getUserName()){
            case "ziye":
                holder.img_nick.setImageResource(R.drawable.drawer_avatar);
                break;
            case "mtf":
                holder.img_nick.setImageResource(R.drawable.imgm);
                break;
            case "王蕊芳":
                holder.img_nick.setImageResource(R.drawable.imgw);
                break;
            case "wby":
                holder.img_nick.setImageResource(R.drawable.imgwb);
                break;
            case "任星凯":
                holder.img_nick.setImageResource(R.drawable.imgr);
                break;
            default:
                holder.img_nick.setImageResource(R.drawable.drawer_avatar);
        }
        //发表日期
        holder.tv_time.setText(list.get(position).getCreatedAt());
        //文字分享信息
        holder.tv_msg.setText(list.get(position).getShareText());
        //评论(发表评论时,以____符号作为空行符号,出现在评论的末尾)
        String text = list.get(position).getComment();
        if (text != null) {
            text = text.replace("____", "\n");
            holder.tv_comment.setText(text);
        }

        //评论框
        holder.et_mycomment.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    index = position;
                }
                return false;
            }
        });
        holder.et_mycomment.clearFocus();
        if (index != -1 && index == position) {
            // 如果当前的行下标和点击事件中保存的index一致，手动为EditText设置焦点。
            holder.et_mycomment.requestFocus();
        }
        holder.et_mycomment.setSelection(holder.et_mycomment.getText().length());
        //发表评论按钮
        final Button btn_cache=holder.btn_commit;
        final EditText et_cache=holder.et_mycomment;
        btn_cache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!btn_cache.getText().toString().isEmpty()) {
                    //如果评论的EditText不为空,可以开始点击按钮
                    //在已有评论的末尾加上____,,头部加上UserName:
                    Share s = new Share();
                    String old_comment = list.get(position).getComment();
                    if (old_comment == null) {
                        old_comment = "";
                    }
                    s.setComment(old_comment +
                            SpUtil.getUserNamefromSP(c, null) + ":" +
                            et_cache.getText().toString().trim() +
                            "____");
                    s.update(list.get(position).getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                Toast.makeText(c, btn_cache.getText().toString().trim(), Toast.LENGTH_SHORT).show();
                                Message msg = Message.obtain();
                                msg.what = 1;
                                handl.sendMessage(msg);
                                btn_cache.clearFocus();
                                btn_cache.setFocusable(false);
                                btn_cache.setFocusableInTouchMode(false);

                                InputMethodManager imm = (InputMethodManager) c.getSystemService(c.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(btn_cache.getWindowToken(), 0);
                            } else {
                                Log.i("_____更新评论失败______", e.toString());
                            }
                        }
                    });
                } else {
                    Toast.makeText(c, "输入数据不能为空", Toast.LENGTH_SHORT).show();
                }

                et_cache.setText("");
            }
        });
        //图片分享信息
        if (list.get(position).getShareFile() != null) {
            RequestQueue queue = Volley.newRequestQueue(c);

            ImageLoader loader = new ImageLoader(queue, new BitmapCache());
            ImageLoader.ImageListener listener = loader.getImageListener(
                    holder.img_msg, R.drawable.no_pic, R.mipmap.ic_launcher
            );

            Log.i("++++++++++++++++++++", list.get(position).getShareFile());
            loader.get(list.get(position).getShareFile(), listener);
        }
        return convertView;
    }


    static class ViewHolder {
        ImageView img_nick;//头像
        TextView tv_name;//昵称
        TextView tv_time;//时间
        TextView tv_msg;//文字分享
        ImageView img_msg;//图片分享
        TextView tv_comment;//评论
        Button btn_commit;//发表按钮
        EditText et_mycomment;//评论框
    }
}
