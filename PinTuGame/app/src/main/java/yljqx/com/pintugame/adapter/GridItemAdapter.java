package yljqx.com.pintugame.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.List;

import yljqx.com.pintugame.tools.ScreenUtil;

/**
 * Created by Stephen on 2016/8/16.
 */
public class GridItemAdapter extends BaseAdapter {

    private List<Bitmap> mBitmapItemLists;
    private Context context;

    public GridItemAdapter(List<Bitmap> mBitmapItemLists, Context context) {
        this.mBitmapItemLists = mBitmapItemLists;
        this.context = context;
    }

    @Override
    public int getCount() {
        return mBitmapItemLists.size();
    }

    @Override
    public Object getItem(int position) {
        return mBitmapItemLists.get(position);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ImageView iv_pic_item = null;
        int density = (int) ScreenUtil.getDeviceDensity(context);
        if (convertView == null){
            iv_pic_item = new ImageView(context);
            //设置布局图片
            iv_pic_item.setLayoutParams(new GridView.LayoutParams(
                    80*density,
                    100*density
            ));
            //设置显示的类型
            iv_pic_item.setScaleType(ImageView.ScaleType.FIT_XY);

        }else{
            iv_pic_item = (ImageView) convertView;
        }

        iv_pic_item.setImageBitmap(mBitmapItemLists.get(position));
        return iv_pic_item;
    }
}
