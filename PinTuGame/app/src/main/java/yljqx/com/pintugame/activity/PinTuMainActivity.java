package yljqx.com.pintugame.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import yljqx.com.pintugame.R;
import yljqx.com.pintugame.tools.GameUtil;
import yljqx.com.pintugame.tools.ImagesUtil;
import yljqx.com.pintugame.tools.ScreenUtil;
import yljqx.com.pintugame.adapter.GridItemAdapter;
import yljqx.com.pintugame.bean.ItemBean;

/**
 * 拼图逻辑主页面：面板显示
 * Created by Stephen on 2016/8/16.
 */
public class PinTuMainActivity extends Activity implements View.OnClickListener {

    public static Bitmap mLastBitmap;//拼图完成时显示最后一张图片
    public static int TYPE = 2;
    private Timer mTimer;
    public static int COUNT_INDEX = 0; // 步数显示
    public static int TIMER_INDEX = 0;  // 计时显示

    /**
     * UI更新Handler
     */
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    // 更新计时器
                    TIMER_INDEX++;
                    mTvTimer.setText("" + TIMER_INDEX);
                    break;
                default:
                    break;
            }
        }
    };
    // 选择的图片
    private Bitmap mPicSelected;
    // PuzzlePanel
    private GridView mGvPuzzleMainDetail;
    private int mResId;
    private String mPicPath;
    private ImageView mImageView;
    // Button
    private Button mBtnBack;
    private Button mBtnImage;
    private Button mBtnRestart;
    // 显示步数
    private TextView mTvPuzzleMainCounts;
    // 计时器
    private TextView mTvTimer;
    // 切图后的图片
    private List<Bitmap> mBitmapItemLists = new ArrayList<Bitmap>();
    // GridView适配器
    private GridItemAdapter mAdapter;
    // Flag 是否已显示原图
    private boolean mIsShowImg;
    private TimerTask mTimerTask;//计时器线程
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pintu_main);

        // 获取选择的图片
        Bitmap picSelectedTemp;
        // 选择默认图片还是自定义图片
        mResId = getIntent().getExtras().getInt("picSelectedID");
        mPicPath = getIntent().getExtras().getString("mPicPath");
        if (mResId != 0) {
            picSelectedTemp = BitmapFactory.decodeResource(
                    getResources(), mResId);
        } else {
            picSelectedTemp = BitmapFactory.decodeFile(mPicPath);
        }
        TYPE = getIntent().getExtras().getInt("mType", 2);
        // 对图片处理
        handlerImage(picSelectedTemp);
        // 初始化Views
        initView();
        // 生成游戏数据
        generateGame();
        // GridView点击事件
        mGvPuzzleMainDetail.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view,
                                    int position, long arg3) {
                // 判断是否可移动
                if (GameUtil.isMoveable(position)) {
                    // 交换点击Item与空格的位置
                    GameUtil.swapItems(
                            GameUtil.mItemBeans.get(position),
                            GameUtil.mBlankItemBean);
                    // 重新获取图片
                    recreateData();
                    // 通知GridView更改UI
                    mAdapter.notifyDataSetChanged();
                    // 更新步数
                    COUNT_INDEX++;
                    mTvPuzzleMainCounts.setText("" + COUNT_INDEX);
                    // 判断是否成功
                    if (GameUtil.isSuccess()) {
                        // 将最后一张图显示完整
                        recreateData();
                        mBitmapItemLists.remove(TYPE * TYPE - 1);
                        mBitmapItemLists.add(mLastBitmap);
                        // 通知GridView更改UI
                        mAdapter.notifyDataSetChanged();
                        Toast.makeText(PinTuMainActivity.this, "拼图成功!",
                                Toast.LENGTH_LONG).show();
                        mGvPuzzleMainDetail.setEnabled(false);
                        mTimer.cancel();
                        mTimerTask.cancel();
                    }
                }
            }

        });
        // 返回按钮点击事件
        mBtnBack.setOnClickListener(this);
        // 显示原图按钮点击事件
        mBtnImage.setOnClickListener(this);
        // 重置按钮点击事件
        mBtnRestart.setOnClickListener(this);
        initView();
    }

    private void recreateData() {
        mBitmapItemLists.clear();
        for (ItemBean temp : GameUtil.mItemBeans) {
            mBitmapItemLists.add(temp.getmBitmap());
        }
    }

    private void generateGame() {
// 切图 获取初始拼图数据 正常顺序
        new ImagesUtil().createInitBitmap(
                TYPE, mPicSelected, PinTuMainActivity.this);
        // 生成随机数据
        GameUtil.getPuzzleGenerator();
        // 获取Bitmap集合
        for (ItemBean temp : GameUtil.mItemBeans) {
            mBitmapItemLists.add(temp.getmBitmap());
        }
        // 数据适配器
        mAdapter = new GridItemAdapter( mBitmapItemLists,PinTuMainActivity.this);
        mGvPuzzleMainDetail.setAdapter(mAdapter);
        // 启用计时器
        mTimer = new Timer(true);
        // 计时器线程
        mTimerTask = new TimerTask() {

            @Override
            public void run() {
                Message msg = new Message();
                msg.what = 1;
                mHandler.sendMessage(msg);
            }
        };
        // 每1000ms执行 延迟0s
        mTimer.schedule(mTimerTask, 0, 1000);
    }

    private void handlerImage(Bitmap bitmap) {
       // 将图片放大到固定尺寸
        int screenWidth = ScreenUtil.getScreenSize(this).widthPixels;
        int screenHeigt = ScreenUtil.getScreenSize(this).heightPixels;
        mPicSelected = new ImagesUtil().resizeBitmap(
                screenWidth * 0.8f, screenHeigt * 0.6f, bitmap);
    }

    private void initView() {

        mBtnBack = (Button) findViewById(R.id.btn_pinTu_back);
        mBtnImage = (Button) findViewById(R.id.btn_pinTu_img);
        mBtnRestart = (Button) findViewById(R.id.btn_pinTu_restart);
        // Flag 是否已显示原图
        mIsShowImg = false;
        // GridView
        mGvPuzzleMainDetail = (GridView) findViewById(
                R.id.pinTu_Grid);
        // 设置为N*N显示
        mGvPuzzleMainDetail.setNumColumns(TYPE);
        RelativeLayout.LayoutParams gridParams = new RelativeLayout.LayoutParams(
                mPicSelected.getWidth(),
                mPicSelected.getHeight());
        // 水平居中
        gridParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        // 其他格式属性
        gridParams.addRule(
                RelativeLayout.BELOW,
                R.id.ll_puzzle_main_spinner);
        // Grid显示
        mGvPuzzleMainDetail.setLayoutParams(gridParams);
        mGvPuzzleMainDetail.setHorizontalSpacing(0);
        mGvPuzzleMainDetail.setVerticalSpacing(0);
        // TV步数
        mTvPuzzleMainCounts = (TextView) findViewById(
                R.id.tv_count);
        mTvPuzzleMainCounts.setText("" + COUNT_INDEX);
        // TV计时器
        mTvTimer = (TextView) findViewById(R.id.tv_time);
        mTvTimer.setText("0秒");
        // 添加显示原图的View
        addImgView();
    }

    private void addImgView() {
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(
                R.id.rl_pinTu_main);
        mImageView = new ImageView(PinTuMainActivity.this);
        mImageView.setImageBitmap(mPicSelected);
        int x = (int) (mPicSelected.getWidth() * 0.9F);
        int y = (int) (mPicSelected.getHeight() * 0.9F);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(x, y);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        mImageView.setLayoutParams(params);
        relativeLayout.addView(mImageView);
        mImageView.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 返回按钮点击事件
            case R.id.btn_pinTu_back:
                PinTuMainActivity.this.finish();
                break;
            // 显示原图按钮点击事件
            case R.id.btn_pinTu_img:
                Animation animShow = AnimationUtils.loadAnimation(
                        PinTuMainActivity.this, R.anim.image_show_anim);
                Animation animHide = AnimationUtils.loadAnimation(
                        PinTuMainActivity.this, R.anim.image_exit_anim);
                if (mIsShowImg) {
                    mImageView.startAnimation(animHide);
                    mImageView.setVisibility(View.GONE);
                    mIsShowImg = false;
                } else {
                    mImageView.startAnimation(animShow);
                    mImageView.setVisibility(View.VISIBLE);
                    mIsShowImg = true;
                }
                break;
            // 重置按钮点击事件
            case R.id.btn_pinTu_restart:
                cleanConfig();
                generateGame();
                recreateData();
                // 通知GridView更改UI
                mTvPuzzleMainCounts.setText("" + COUNT_INDEX);
                mAdapter.notifyDataSetChanged();
                mGvPuzzleMainDetail.setEnabled(true);
                break;
            default:
                break;
        }
    }

    /**
     * 返回时调用
     */
    @Override
    protected void onStop() {
        super.onStop();
        // 清空相关参数设置
        cleanConfig();
        this.finish();
    }

    /**
     * 清空相关参数设置
     */
    private void cleanConfig() {
        // 清空相关参数设置
        GameUtil.mItemBeans.clear();
        // 停止计时器
        mTimer.cancel();
        mTimerTask.cancel();
        COUNT_INDEX = 0;
        TIMER_INDEX = 0;
        // 清除拍摄的照片
        if (mPicPath != null) {
            // 删除照片
            File file = new File(MainActivity.TEMP_IMAGE_PATH);
            if (file.exists()) {
                file.delete();
            }
        }
    }

}
