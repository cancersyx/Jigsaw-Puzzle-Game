package yljqx.com.pintugame.tools;

import java.util.ArrayList;
import java.util.List;

import yljqx.com.pintugame.activity.PinTuMainActivity;
import yljqx.com.pintugame.bean.ItemBean;

/**
 * Created by Stephen on 2016/8/17.
 */
public class GameUtil {
    public static List<ItemBean> mItemBeans = new ArrayList<ItemBean>();//游戏信息单元格
    public static ItemBean mBlankItemBean = new ItemBean();//空格单元格

    /**
     * 判断点击的图片是否可以移动
     * @param position
     * @return 能否移动
     */
    public static boolean isMoveable(int position){
        int type = PinTuMainActivity.TYPE;
        //获取空格item
        int blankId = GameUtil.mBlankItemBean.getmItemId() - 1;
        //不同行相差为type
        if (Math.abs(blankId - position) == type){
            return true;
        }
        //相同行相差为1
        if ((blankId/type == position/type) && Math.abs(blankId - position) == 1){
            return true;
        }
        return false;
    }

    /**
     * 生成随机的item
     */
    public static void getPuzzleGenerator(){
        int index = 0;
        //随机打乱顺序
        for (int i=0;i<mItemBeans.size();i++){
            index = (int) (Math.random()* PinTuMainActivity.TYPE* PinTuMainActivity.TYPE);
            swapItems(mItemBeans.get(index),GameUtil.mBlankItemBean);
        }
        List<Integer> data = new ArrayList<Integer>();
        for (int i=0;i<mItemBeans.size();i++){
            data.add(mItemBeans.get(i).getmBitmapId());
        }
        //判断是否有解
        if (canSolve(data)){
            return;
        }else {
            getPuzzleGenerator();
        }

    }

    /**
     * 该数据是否有解
     * @param data 拼图组数据
     * @return 该数据是否有解
     */
    private static boolean canSolve(List<Integer> data) {
        //获取空格ID
        int blankId = GameUtil.mBlankItemBean.getmItemId();
        //可行性原则
        if (data.size()%2 ==1){
            return getInversions(data) %2 ==0;
        }else{
            //从下往上数，空格位于奇数行
            if (((blankId - 1 )/ PinTuMainActivity.TYPE) % 2 ==1){
                return getInversions(data) % 2 == 0;
            }else {
                //从下往上数，空格位于偶数行
                return getInversions(data) % 2 ==1;
            }
        }

    }

    /**
     * 计算倒置和算法
     * @param data 拼图数组数据
     * @return 该序列的倒置和
     */
    private static int getInversions(List<Integer> data) {
        int inversions = 0;
        int inversionsCount = 0;
        for (int i=0;i<data.size();i++){
            for (int j = i + 1;j<data.size();j++){
                int index = data.get(i);
                if (data.get(j) !=0 && data.get(j)<index ){
                    inversionsCount++;
                }
            }
            inversions +=inversionsCount;
            inversionsCount = 0;
        }
        return inversions;
    }

    /**
     * 交换空格与点击Item的位置
     * @param from 交换图
     * @param blank 空白图
     */
    public static void swapItems(ItemBean from, ItemBean blank) {
        ItemBean tempItemBean = new ItemBean();
        //交换BitmapId
        tempItemBean.setmBitmapId(from.getmBitmapId());
        from.setmBitmapId(blank.getmBitmapId());
        blank.setmBitmapId(tempItemBean.getmBitmapId());
        //交换Bitmap
        tempItemBean.setmBitmap(from.getmBitmap());
        from.setmBitmap(blank.getmBitmap());
        blank.setmBitmap(tempItemBean.getmBitmap());
        //设置新的Blank
        GameUtil.mBlankItemBean = from;

    }

    /**
     * 是否拼图成功
     * @return
     */
    public static boolean isSuccess(){
        for (ItemBean tempBean : GameUtil.mItemBeans){
            if (tempBean.getmBitmapId() !=0 && (tempBean.getmItemId()) == tempBean.getmBitmapId()){
                continue;
            }else if (tempBean.getmBitmapId() == 0 && tempBean.getmItemId() == PinTuMainActivity.TYPE * PinTuMainActivity.TYPE){
                continue;
            }else {
                return false;
            }
        }
        return true;
    }




}
