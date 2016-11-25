package Fragments;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.ziye.passby.ShareActivity;
import com.ziye.passby.MainActivity;
import com.ziye.passby.R;

import java.util.List;

import Adapters.MyFriendsAdapter;
import beans.Friend;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import utils.SpUtil;


public class FriendsFragment extends Fragment {

    private ListView lv_frag_friend;
    private MainActivity c;//充当上下文对象
    private Toolbar toolbar;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        c= (MainActivity) getActivity();
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);

        FloatingActionButton floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ShareActivity.class);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    getActivity().startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                else
                    startActivity(intent);
            }
        });

        lv_frag_friend= (ListView) rootView.findViewById(R.id.lv_frag_firend);
        BmobQuery<Friend> query = new BmobQuery<>();
        query.addWhereEqualTo("userName", SpUtil.getUserNamefromSP(getActivity(), null));
        query.findObjects(new FindListener<Friend>() {
            @Override
            public void done(List<Friend> list, BmobException e) {
                if (e == null) {
                    if(list.size()>0){
                        System.out.println("+++++++++++++++="+list.size());
                        lv_frag_friend.setAdapter(new MyFriendsAdapter(list, c,0));
                    }

                }

            }
        });

        return rootView;
    }
}
