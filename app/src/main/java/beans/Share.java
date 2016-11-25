package beans;

import cn.bmob.v3.BmobObject;

/**
 * 分享记录表,每个User都有用一个分享记录表,存放分享的信息
 * Created by 德帅 on 2016/8/11.
 * action:
 */
public class Share extends BmobObject{
    private String userName;//分享的主角
    private String shareText; //分享的文字内容
    private String shareFile; //分享的文件内容(图片,或者视频)
    private String comment;//评论信息

    public void setShareFile(String path) {
        this.shareFile = path;
    }

    public void setShareText(String shareText) {
        this.shareText = shareText;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

//    public BmobDate getCreatedAt() {
//        return createdAt;
//    }

    public String getShareFile() {
        return shareFile;
    }

    public String getShareText() {
        return shareText;
    }

    public String getComment() {
        return comment;
    }


    public String getUserName() {
        return userName;
    }
}
