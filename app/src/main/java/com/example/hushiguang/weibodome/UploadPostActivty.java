package com.example.hushiguang.weibodome;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by HuShiGuang on 2015/12/28.
 */
public class UploadPostActivty extends AppCompatActivity {

    @InjectView(R.id.post_gridview)
    GridView mGridView;

    GridViewAdapter mAdapter;
    ArrayList<String> paths = new ArrayList<>();
    Context mContext;

    public final static int PIC_CODE = 1001;
    public final static int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 101;


    @OnClick(R.id.add_pic)
    void getPic() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
        } else {
            //应用有权限
            pickPhoto(PIC_CODE);
        }


    }


    /**
     * 处理adapter 发送的 消息
     *
     */
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    //处理删除按钮的消息
                    int postion = (int) msg.obj;
                    paths.remove(postion);
                    setListViewHeightBasedOnChildren(mGridView);
                    mAdapter.setPaths(paths);
                    if (paths.size() <= 1){
                        mGridView.setVisibility(View.GONE);
                    }

                    break;
                case 1:
                    //处理最后一张添加图片的消息
                    if (paths.size() <= 9 ){
                        pickPhoto(PIC_CODE);
                    }

                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        mContext = this;
        initView();
    }

    /**
     * 初始化数据
     */
    void initView() {
        mAdapter = new GridViewAdapter(paths, mContext, handler);
        String isFirstPic = "firstImage";
        paths.add(0, isFirstPic);
        mGridView.setAdapter(mAdapter);
        if (paths.size() > 1) {
            mGridView.setVisibility(View.VISIBLE);
        } else {
            mGridView.setVisibility(View.GONE);
        }
    }


    /**
     * 图库选取图片
     *
     * @param imageCode
     * @return
     */
    public boolean pickPhoto(int imageCode) {
        Intent getImage = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(getImage, imageCode);
        return true;
    }


    /***
     * 获取真实的路径
     *
     * @param uri
     * @return
     */
    public String uri2Path(Uri uri) {
        int actual_image_column_index;
        String img_path;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
        actual_image_column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        img_path = cursor.getString(actual_image_column_index);
        return img_path;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case PIC_CODE:
                    if (data != null) {
                        if (data.getData() != null) {
                            paths.add(0, uri2Path(data.getData()));
                            setListViewHeightBasedOnChildren(mGridView);
                            mAdapter.setPaths(paths);
                            mGridView.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Toast.makeText(mContext, "图片获取失败", Toast.LENGTH_SHORT).show();
                    }
                    break;

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {//用户给权限
                if (paths.size() <= 9 ){
                    pickPhoto(PIC_CODE);
                }
            } else {
                Toast.makeText(UploadPostActivty.this, "获取权限失败", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public static void setListViewHeightBasedOnChildren(GridView listView) {
        // 获取listview的adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        // 固定列宽，有多少列
        int col = 3;// listView.getNumColumns();
        int totalHeight = 0;
        // i每次加4，相当于listAdapter.getCount()小于等于4时 循环一次，计算一次item的高度，
        // listAdapter.getCount()小于等于8时计算两次高度相加
        for (int i = 0; i < listAdapter.getCount(); i += col) {
            // 获取listview的每一个item
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            // 获取item的高度和
            totalHeight += listItem.getMeasuredHeight();
        }

        // 获取listview的布局参数
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        // 设置高度
        params.height = totalHeight;
        // 设置margin
        ((ViewGroup.MarginLayoutParams) params).setMargins(10, 10, 10, 10);
        // 设置参数
        listView.setLayoutParams(params);
    }


}
