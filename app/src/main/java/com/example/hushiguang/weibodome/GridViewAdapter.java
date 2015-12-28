package com.example.hushiguang.weibodome;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by HuShiGuang on 2015/12/28.
 */
public class GridViewAdapter extends BaseAdapter {

    ArrayList<String> paths;
    Context mContext;
    Handler mHandler;

    public GridViewAdapter(ArrayList<String> paths, Context mContext, Handler mHandler) {
        this.paths = paths;
        this.mContext = mContext;
        this.mHandler = mHandler;
    }

    public void setPaths(ArrayList<String> paths) {
        this.paths = paths;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return paths.size();
    }

    @Override
    public Object getItem(int position) {
        return paths.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_gridview, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        //判断大小是否大于1 ，初始化的时候里面添加了一条数据
        if (paths.size() > 1) {
            if (!paths.get(position).equals("firstImage")) { //判断是否是最后的添加图片的按钮
                Glide.with(mContext).load(paths.get(position)).into(holder.mImageView);
                holder.mDeleteImage.setVisibility(View.VISIBLE);
                holder.mImageView.setEnabled(false);
            } else {
                holder.mDeleteImage.setVisibility(View.GONE);
                if (paths.size() > 9) {
                    holder.mImageView.setVisibility(View.GONE);
                    holder.mImageView.setEnabled(false);
                } else {
                    holder.mImageView.setEnabled(true);
                    holder.mImageView.setVisibility(View.VISIBLE);
                    //使用Glide去加载本地资源的图片
                    Glide.with(mContext).load(R.mipmap.ic_add_black_48dp).into(holder.mImageView);
                }

                //给最后一张图片设置添加图片事件
                holder.mImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //发送message消息，在主线程去处理
                        Message message = new Message();
                        message.what = 1;
                        mHandler.sendMessage(message);
                    }
                });


            }
        }

        //判断删除按钮是否含有点击事件，假如有不去重复new新的，减少内存使用
        if (!holder.mDeleteImage.hasOnClickListeners()) {
            holder.mDeleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //发送message消息，在主线程去处理
                    Message message = new Message();
                    message.obj = position;
                    message.what = 0;
                    mHandler.sendMessage(message);
                }
            });
        }


        return convertView;
    }

    public static class ViewHolder {

        ImageView mImageView;
        ImageView mDeleteImage;


        public ViewHolder(View itemView) {
            mImageView = (ImageView) itemView.findViewById(R.id.item_gridview_image);
            mDeleteImage = (ImageView) itemView.findViewById(R.id.item_gridview_delete);
        }

    }
}
