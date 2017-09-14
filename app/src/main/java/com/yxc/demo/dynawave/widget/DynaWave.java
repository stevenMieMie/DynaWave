package com.yxc.demo.dynawave.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DrawFilter;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.yxc.demo.dynawave.utils.UIUtils;

/**
 * 动态水波纹view
 * Created by yuexingchuan on 17/9/14.
 */

public class DynaWave extends View {

    //波纹颜色
    private static final int WAVE_PAINT_COLOR = 0x880000aa;
    //y = Asin(wx+b)+h
    private static final float STARETCH_Factor_A = 20;
    private static final float OFFSET_Y = 0;
    //第一条水波移动速度
    private static final int TRANSLATE_X_SPEED_ONE = 7;
    //第二条水波纹移动速度
    private static final int TRANSLATE_X_SPEED_TWO = 5;
    private float mCycleFactorW;

    private int mTotalWidth, mTotalHeidht;
    private float[] mYPositions;
    private float[] mResetOnePositions;
    private float[] mResetTwoPositions;
    private int mXOffsetSpeedOne;
    private int mXOffsetSpeedTwo;
    private int mXOneOffset;
    private int mXTwoOffset;

    private Paint mWavePaint;
    private DrawFilter mDrawFilter;


    public DynaWave(Context context) {
        super(context);
        initialize(context);
    }

    public DynaWave(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    private void initialize(Context context) {
        //将dp转化为px，用于控制不同分辨率上移动速度基本一致
        mXOffsetSpeedOne = UIUtils.dip2px(context, TRANSLATE_X_SPEED_ONE);
        mXOffsetSpeedTwo = UIUtils.dip2px(context, TRANSLATE_X_SPEED_TWO);

        //初始绘制波纹的画笔
        mWavePaint = new Paint();
        //去除画笔锯齿
        mWavePaint.setAntiAlias(true);
        //设置风格为实线
        mWavePaint.setStyle(Paint.Style.FILL);
        //设置画笔颜色
        mWavePaint.setColor(WAVE_PAINT_COLOR);
        mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        new Thread() {
            public void run() {
                while (true) {
                    try {
                        // 为了保证效果的同时，尽可能将cpu空出来，供其他部分使用
                        Thread.sleep(30);
                    } catch (InterruptedException e) {
                    }
                    postInvalidate();
                }
            }
        }.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //从canvas层面去除绘制时的锯齿
        canvas.setDrawFilter(mDrawFilter);
        resetPositionY();

        for (int i = 0; i < mTotalWidth; i++) {
            //减400只是为了控制波纹绘制的y在屏幕的位置，大家可以改成一个变量，然后动态改变这个变量，从而形成波纹上升下降效果
            //绘制第一条水波纹
            canvas.drawLine(i, mTotalHeidht - mResetOnePositions[i] - 400, i,
                    mTotalHeidht, mWavePaint);

            //绘制第二条水波纹
            canvas.drawLine(i, mTotalHeidht - mResetTwoPositions[i] - 400, i,
                    mTotalHeidht, mWavePaint);
        }

        //改变两条波纹的移动点
        mXOneOffset += mXOffsetSpeedOne;
        mXTwoOffset += mXOffsetSpeedTwo;

        //如果一定移动到结尾处，则重头记录
        if (mXOneOffset > mTotalWidth)
            mXOneOffset = 0;
        if (mXTwoOffset > mTotalWidth)
            mXTwoOffset = 0;

        //引发view重绘，一般可以考虑延迟20-30ms重绘，空出时间片
//        postInvalidate();

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //记录下view的宽高
        mTotalWidth = w;
        mTotalHeidht = h;
        //用于保存原始波纹的y值
        mYPositions = new float[mTotalWidth];
        //用于保存波纹一的y值
        mResetOnePositions = new float[mTotalWidth];
        //用于保存波纹二的y值
        mResetTwoPositions = new float[mTotalWidth];

        //将周期定为view的总宽度
        mCycleFactorW = (float) (2 * Math.PI / mTotalWidth);

        //根据view总宽度得出所有对应的y值
        for (int i = 0; i < mTotalWidth; i++) {
            mYPositions[i] = (float) (STARETCH_Factor_A * Math.sin(mCycleFactorW * i) + OFFSET_Y);
        }
    }

    //重置Y坐标
    private void resetPositionY() {
        //mXOneOffset代表当前第一条水波纹要移动的距离
        int yOneInterval = mYPositions.length - mXOneOffset;
        //使用System.arraycopy方式重新填充第一条水波纹的数据
        System.arraycopy(mYPositions, mXOneOffset, mResetOnePositions, 0, yOneInterval);
        System.arraycopy(mYPositions, 0, mResetOnePositions, yOneInterval, mXOneOffset);

        int yTwoInterval = mYPositions.length - mXTwoOffset;
        System.arraycopy(mYPositions, mXTwoOffset, mResetTwoPositions, 0, yTwoInterval);
        System.arraycopy(mYPositions, 0, mResetTwoPositions, yTwoInterval, mXTwoOffset);

    }


}
