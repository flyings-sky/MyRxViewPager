package andfans.com.myrxviewpager.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import andfans.com.myrxviewpager.R;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MyRxViewPager extends FrameLayout {
    //轮播图图片数量
    private final static int IMAGE_COUNT = 5;
    //自动轮播的时间间隔
    private final static int TIME_INTERVAL = 2;
    //自动轮播启用开关
    private final static boolean isAutoPlay = true;

    //放轮播图片的ImageView 的list
    private List<ImageView> imageViewsList;
    //放圆点的View的list
    private List<View> dotViewsList;

    private ViewPager viewPager;
    //当前轮播页
    private int currentItem  = 0;
    private Subscription subscription;

    public MyRxViewPager(Context context) {
        this(context,null);
    }
    public MyRxViewPager(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public MyRxViewPager(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater.from(context).inflate(R.layout.viewpager_item01, this, true);
        init(context);
        viewPager.setCurrentItem(1);
        if(isAutoPlay){
            startPlay();
        }

    }

    /**
     * 开始轮播图切换
     */
    private void startPlay(){
        subscription = Observable.interval(TIME_INTERVAL, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        if(imageViewsList != null && imageViewsList.size() > 0){
                            currentItem = (currentItem+1)%imageViewsList.size();
                            viewPager.setCurrentItem(currentItem,false);
                        }
                    }
                });
    }

    /**
     * 停止轮播图切换
     */
    public void stopPlay(){
        subscription.isUnsubscribed();
    }

    /**
     * 初始化Views等UI
     */
    private void init(Context context){
        int[] imagesResIds = new int[]{
                R.drawable.pic1,
                R.drawable.pic2,
                R.drawable.pic3,
                R.drawable.pic4,
                R.drawable.pic5,

        };
        imageViewsList = new ArrayList<>();
        dotViewsList = new ArrayList<>();
        dotViewsList.add(findViewById(R.id.v_dot1));
        dotViewsList.add(findViewById(R.id.v_dot2));
        dotViewsList.add(findViewById(R.id.v_dot3));
        dotViewsList.add(findViewById(R.id.v_dot4));
        dotViewsList.add(findViewById(R.id.v_dot5));
        for(int imageID : imagesResIds){
            ImageView view =  new ImageView(context);
            view.setImageBitmap(readBitMap(context,imageID));
            view.setScaleType(ImageView.ScaleType.FIT_XY);
            imageViewsList.add(view);
        }
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setFocusable(true);
        viewPager.setAdapter(new MyPagerAdapter());
        viewPager.setOnPageChangeListener(new MyPageChangeListener());
    }

    public static Bitmap readBitMap(Context context, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        //获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    /**
     * 填充ViewPager的页面适配器
     */
    private class MyPagerAdapter  extends PagerAdapter {

        /**
         * 移除某个图片
         */
        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager)container).removeView(imageViewsList.get(position));
        }

        /**
         * 在指定位置添加图片
         */

        @Override
        public Object instantiateItem(View container, int position) {
            ((ViewPager)container).addView(imageViewsList.get(position));
            return imageViewsList.get(position);
        }

        /**
         * 获取图片资源的数量
         */
        @Override
        public int getCount() {
            return imageViewsList.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }
    }

    /**
     * ViewPager的监听器
     * 当ViewPager中页面的状态发生改变时调用
     * @author caizhiming
     */
    private class MyPageChangeListener implements ViewPager.OnPageChangeListener {

        boolean isAutoPlay = false;

        @Override
        public void onPageScrollStateChanged(int arg0) {
            switch (arg0) {
                case 1:// 手势滑动，空闲中
                    isAutoPlay = false;
                    break;
                case 2:// 界面切换中
                    isAutoPlay = true;
                    break;
                case 0:// 滑动结束，即切换完毕或者加载完毕
                    // 当前为最后一张，此时从右向左滑，则切换到第一张
                    if (viewPager.getCurrentItem() == viewPager.getAdapter().getCount() - 1 && !isAutoPlay) {
                        viewPager.setCurrentItem(0);
                    }
                    // 当前为第一张，此时从左向右滑，则切换到最后一张
                    else if (viewPager.getCurrentItem() == 0 && !isAutoPlay) {
                        viewPager.setCurrentItem(viewPager.getAdapter().getCount() - 1);
                    }
                    break;
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int pos) {
            currentItem = pos;
            for(int i=0;i < dotViewsList.size();i++){
                if(i == pos){
                    (dotViewsList.get(pos)).setBackgroundResource(R.drawable.dot_selected);
                }else {
                    (dotViewsList.get(i)).setBackgroundResource(R.drawable.dot_noselected);
                }
            }
        }

    }

    /**
     * 销毁ImageView资源，回收内存
     */
    private void destoryBitmaps() {

        for (int i = 0; i < IMAGE_COUNT; i++) {
            ImageView imageView = imageViewsList.get(i);
            Drawable drawable = imageView.getDrawable();
            if (drawable != null) {
                //解除drawable对view的引用
                drawable.setCallback(null);
            }
        }
    }
}

