package beans;

import cn.bmob.v3.BmobObject;

/**
 * 用户表,对应bmob中的User表,存放所有用户信息的表单
 * Created by 德帅 on 2016/8/11.
 *
 */
public class User extends BmobObject{

    private String userName;  //用户名
    private String userPass;  //用户密码
    private String userIcon;//用户头像的url

    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
    }

    public String getUserPass() {
        return userPass;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserIcon() {
        return userIcon;
    }
}
