package com.ziye.passby;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import beans.Share;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;
import utils.SpUtil;

public class AddShareActivity extends AppCompatActivity {

    private static final int TAKE_PHOTO = 1;
    private static final int CHOOSE_PHOTO = 2;

    public File pic = new File(Environment.getExternalStorageDirectory(), "img.jpg");
    public File pic2 = new File(Environment.getExternalStorageDirectory(), "img2.jpg");
    private Uri imgUri;
    private Toolbar tb_addshare;
    private String imagePath;
    private EditText et_addshare_addmsg;
    private Bitmap bm;
    private ImageView img_addshare_showpic;
    private String[] dialogItem = new String[]{"从相册里选择", "拍照"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initData();
        initEvent();
    }

    private void initView() {
        setContentView(R.layout.activity_add_share);

        img_addshare_showpic = (ImageView) findViewById(R.id.img_addshare_showpic);
        tb_addshare = (Toolbar) findViewById(R.id.tb_addshare);
        et_addshare_addmsg = (EditText) findViewById(R.id.et_addshare_addmsg);


    }

    private void initEvent() {
        tb_addshare.setOnMenuItemClickListener(tb_listener);

        img_addshare_showpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder buidler = new AlertDialog.Builder(AddShareActivity.this);
                buidler.setTitle("请选择图片");
                buidler.setIcon(R.drawable.alertdialog);
                buidler.setItems(dialogItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (dialogItem[which]) {
                            case "从相册里选择":
                                choosePhotos();
                                break;
                            case "拍照":
                                takePhotos();
                                break;
                        }
                    }
                });
                buidler.show();

            }
        });
    }


    private void initData() {
        tb_addshare.setTitle("写动态");
        setSupportActionBar(tb_addshare);

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        tb_addshare.inflateMenu(R.menu.menu_add_share_commit);
        return super.onCreateOptionsMenu(menu);
    }

    private Toolbar.OnMenuItemClickListener tb_listener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {

            if (!et_addshare_addmsg.getText().toString().isEmpty()) {
                if (bm != null) {
                    final BmobFile bmobFile = new BmobFile(new File(copyImgToImg2(bm).getPath()));
                    //先把文件上传
                    bmobFile.uploadblock(new UploadFileListener() {

                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                //bmobFile.getFileUrl()--返回的上传文件的完整地址
                                //吧上传好的晚间的url写入数据库
                                String path = bmobFile.getFileUrl();
                                Share s = new Share();
                                s.setUserName(SpUtil.getUserNamefromSP(AddShareActivity.this, null));
                                s.setShareText(et_addshare_addmsg.getText().toString().trim());
                                s.setShareFile(path);
                                s.save(new SaveListener<String>() {
                                    @Override
                                    public void done(String s, BmobException e) {
                                        if (e == null) {
                                            Toast.makeText(AddShareActivity.this, s, Toast.LENGTH_SHORT).show();
                                        } else {
                                            Log.i("_____exception_____", e.toString());
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(AddShareActivity.this, "上传失败" + e.toString(), Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onProgress(Integer value) {
                            // 返回的上传进度（百分比）
                            if (value == 1) {
                                Toast.makeText(AddShareActivity.this, "上传完成", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(AddShareActivity.this, "上传中....", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
            //直接回到分享界面
            finish();
            return true;
        }
    };

    /**
     * 调用相机进行图片的获取
     *
     * @time 2016/8/29 16:23
     * @author ziye
     */
    private void takePhotos() {
        try {
            if (pic.exists()) {
                //如果已经存在,删除文件
                pic.delete();
            }
            pic.createNewFile();
            imgUri = Uri.fromFile(pic);
            //调用相机
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
            startActivityForResult(intent, TAKE_PHOTO);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从相册中选择图片
     *
     * @time 2016/8/30 10:13
     * @author ziye
     */
    private void choosePhotos() {
        //实现打开相册的intent,并跳转
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(pic.getPath(), options);
                    options.inPreferredConfig = Bitmap.Config.ARGB_4444;

                    //压缩图片为500x900
                    options.inSampleSize = calculateInSampleSize(options, 500, 900);
                    options.inJustDecodeBounds = false;
                    bm = BitmapFactory.decodeFile(pic.getPath(), options);
                    img_addshare_showpic.setImageBitmap(bm);
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    handleImage(data);
                }

        }
    }

    /**
     * 处理来自相册的图片
     *
     * @time 2016/8/30 10:29
     * @author ziye
     */
    private void handleImage(Intent data) {
        imagePath = null;
        Uri uri = data.getData();
        Toast.makeText(AddShareActivity.this, uri.toString(), Toast.LENGTH_SHORT).show();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            //如果是document类型的Uri,通过Document id来处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.document".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];  //解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "+" + id;

                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //普通路径的图片Uri,用普通方式处理
            imagePath = getImagePath(uri, null);
        }
            //展示图片
            showPic(imagePath);
    }

    /**
     * 展示图片
     *
     * @time 2016/8/30 10:53
     * @author ziye
     */
    private void showPic(String imagePath) {
        if (imagePath != null) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imagePath, options);
            options.inPreferredConfig = Bitmap.Config.ARGB_4444;

            //压缩图片为500*900
            options.inSampleSize = calculateInSampleSize(options,500, 900);
            options.inJustDecodeBounds = false;
            bm = BitmapFactory.decodeFile(imagePath,options);
            img_addshare_showpic.setImageBitmap(bm);

        }
    }

    /**
     * 根据Uri和selection获取图片的真实路径
     *
     * @time 2016/8/30 10:39
     * @author ziye
     */
    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor c = getContentResolver().query(uri, null, selection, null, null);

        if (c != null) {
            if (c.moveToFirst()) {
                path = c.getString(c.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            c.close();
        }
        return path;
    }

    /**
     * 计算应该压缩的程度
     *
     * @time 2016/8/29 17:09
     * @author ziye
     */
    private int calculateInSampleSize(BitmapFactory.Options op, int reqWidth, int reqheight) {
        int originalWidth = op.outWidth;
        int originalHeight = op.outHeight;
        int inSampleSize = 1;
        if (originalWidth > reqWidth || originalHeight > reqheight) {
            int halfWidth = originalWidth / 2;
            int halfHeight = originalHeight / 2;
            while ((halfWidth / inSampleSize > reqWidth)
                    && (halfHeight / inSampleSize > reqheight)) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    /**
     * 吧压缩过的比特位图放在文件中,准备上传
     *
     * @time 2016/8/30 8:51
     * @author ziye
     */
    private File copyImgToImg2(Bitmap bm) {

        if (pic2.exists()) {
            pic2.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(pic2);
            bm.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return pic2;
    }
}
