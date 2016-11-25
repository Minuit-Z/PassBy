package beans;


import cn.bmob.v3.BmobObject;

/**
 * 好友映射表,每个User用户都会有这么一张表用来存放所添加的好友
 * Created by 德帅 on 2016/8/11.
 * action:
 */
public class Friend extends BmobObject {

    private String friendName;
    private String userName;


    public void setFriendName(String friendName) {
        this.friendName = friendName;
        friendName.split("");
    }

    public String getFriendName() {
        return friendName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
