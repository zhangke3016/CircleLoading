package com.mrzk.circleloadinglibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by win7 on 2016/7/30.
 */
public class CircleLoadingView extends View{
    /** 圆环的画笔 */
    private Paint mPaint;
    /** 文字的画笔 */
    private Paint mTextPaint;
    /** 刻度的画笔 */
    private Paint mRollPaint;
    //进度刻度的画笔
    private Paint mRollDrawPaint;
    /** 圆环的宽度 */
    private int mStrokeWidth = 0;
    /** 字体的大小 */
    private int mTextSize = 0;
    /** 字体的颜色 */
    private int mTextColor = 0;
    /** 圆环所在区域 */
    private RectF oval;
    private Rect bounds;//获取文字的宽高  使文字居中
    private float mStartAngle = 180;//开始的角度
    private float mSweepAngle = 0;//划过的角度
    /** 刻度的背景色 */
    private int mGraduationBackgroundColor = Color.BLACK;
    /** 刻度的宽度 */
    private int mGraduationWidth = 0;
    private float mGraduationSweepAngle = 359.9f;//刻度划过的角度 如果为360度  获取刻度会默认从右边划过
    private int mMax = 0;//设置的最大值
    private int mProgress = 0;//设置的进度
    //分段颜色 外环
    private int[] OUT_SECTION_COLORS = {
            0xFFE5BD7D, 0xFFFAAA64,
            0xFFFFFFFF, 0xFF6AE2FD,
            0xFF8CD0E5, 0xFFA3CBCB,
            0xFFBDC7B3, 0xFFD1C299,
            0xFFE5BD7D};
    //内部刻度
    private int[] INNER_SECTION_COLORS = {
            0xFFE5BD7D, 0xFFFAAA64,
            0xFFFFFFFF, 0xFF6AE2FD,
            0xFF8CD0E5, 0xFFA3CBCB,
            0xFFBDC7B3, 0xFFD1C299,
            0xFFE5BD7D};
    /** 宽高的默认值 */
    private int nDesired = 0;
    private RectF oval2;//临时的内圆
    private RectF oval3;//临时的外圆
    /** 刻度的个数 */
    private int nGraduationCount = 35;
    /** 所有线的集合 */
    private List<Line> mLinesList = new ArrayList<Line>();
    /** 进度监听器 */
    private OnProgressListener mOnProgressListener;
    /**
     * 是否显示进度条的背景 默认为
     * @see #setShowGraduationBackgroundEnable(boolean)
     * */
    private boolean isShowGraduationBackground = true;
    /**
     *  是否显示外部进度框
     *  @see #setShowOutRollEnable(boolean)
     *  */
    private boolean isShowOutRoll = true;
    public CircleLoadingView(Context context) {
        this(context,null);
    }
    public CircleLoadingView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }
    public CircleLoadingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray typedArray = getResources().obtainAttributes(attrs, R.styleable.LoadingStyle);
        mTextSize = (int) typedArray.getDimension(R.styleable.LoadingStyle_textSize, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20, getResources().getDisplayMetrics()));
        mStrokeWidth = (int) typedArray.getDimension(R.styleable.LoadingStyle_strokeWidth, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()));
        mGraduationWidth = (int) typedArray.getDimension(R.styleable.LoadingStyle_graduationWidth, mStrokeWidth/2);
        mTextColor = (int) typedArray.getColor(R.styleable.LoadingStyle_textColor, Color.BLACK);
        mGraduationBackgroundColor = (int) typedArray.getColor(R.styleable.LoadingStyle_graduationBackgroundColor, Color.BLACK);
        mStartAngle = (int) typedArray.getInt(R.styleable.LoadingStyle_startAngle, 180);
        mMax = (int) typedArray.getInt(R.styleable.LoadingStyle_max, 0);
        mProgress = (int) typedArray.getInt(R.styleable.LoadingStyle_progress, 0);
        nGraduationCount = (int) typedArray.getInt(R.styleable.LoadingStyle_graduationCount, 35);
        isShowGraduationBackground =  typedArray.getBoolean(R.styleable.LoadingStyle_isShowGraduationBackground, true);
        isShowOutRoll =  typedArray.getBoolean(R.styleable.LoadingStyle_isShowOutRoll, true);
        typedArray.recycle();
        init();
    }
    /**
     * 设置进度监听
     * @param mOnProgressListener
     */
    public void setOnProgressListener(OnProgressListener mOnProgressListener) {
        this.mOnProgressListener = mOnProgressListener;
    }
    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);//消除锯齿
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(0xFF0099CC);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setFakeBoldText(true);//设置字体加粗
        mTextPaint.setTextSize(mTextSize);

        mRollPaint = new Paint(mPaint);
        mRollPaint.setColor(mGraduationBackgroundColor);
//        mRollPaint.setStrokeWidth(mStrokeWidth/2);
        mRollPaint.setStrokeWidth(mGraduationWidth);

        mRollDrawPaint = new Paint(mPaint);
        mRollDrawPaint.setStrokeWidth(mGraduationWidth);

        oval = new RectF();
        bounds = new Rect();

        nDesired = dip2px(60);
    }
    /**
     * 初始化数据
     */
    private void initData() {
        mLinesList.clear();

        Path path = new Path();
        Path path1 = new Path();
        //从startAngle开始 绘制180角度
        path.addArc(oval2, mStartAngle, mGraduationSweepAngle);
        path1.addArc(oval3, mStartAngle, mGraduationSweepAngle);

        PathMeasure pm = new PathMeasure(path, false);
        float itemLength = pm.getLength()/(nGraduationCount-1);

        PathMeasure pm1 = new PathMeasure(path1, false);

        float[] pos = new float[2];
        float[] postemp = new float[2];
        for (int i = 0; i < nGraduationCount; i++) {
            pm.getPosTan(itemLength*i, pos , null );
            pm1.getPosTan(itemLength*i/pm.getLength()*pm1.getLength(), postemp , null);
            Line line = new Line();
            line.p1.x = pos[0];
            line.p1.y = pos[1];
            line.p2.x = postemp[0];
            line.p2.y = postemp[1];
            mLinesList.add(line);
        }
    }

    public int dip2px(int dip){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, getResources().getDisplayMetrics());
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(resolveMeasured(widthMeasureSpec, nDesired), resolveMeasured(heightMeasureSpec, nDesired));
    }
    /**
     * 设置渐变颜色
     */
    private void setSweepShader(int[] colors,Paint p) {
        SweepGradient sweepGradient = new SweepGradient(getWidth()/2, getHeight()/2, colors, null);
        p.setShader(sweepGradient);
    }
    /** 设置层叠颜色 */
    public void setOutColors(int[] colors){
        OUT_SECTION_COLORS = colors;
        setSweepShader(OUT_SECTION_COLORS,mPaint);
    }
    /** 设置层叠颜色 */
    public void setInnerGraduationColors(int[] colors){
        INNER_SECTION_COLORS = colors;
        setSweepShader(INNER_SECTION_COLORS,mRollDrawPaint);
    }
    /**
     *
     * @param measureSpec
     * @param desired
     * @return
     */
    private int resolveMeasured(int measureSpec, int desired)
    {
        int result = 0;
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (MeasureSpec.getMode(measureSpec)) {
            case MeasureSpec.UNSPECIFIED: //
                result = desired;
                break;
            case MeasureSpec.AT_MOST:  //wrap
                result = Math.min(specSize, desired);
                break;
            case MeasureSpec.EXACTLY:  //match
            default:
                result = specSize;
        }
        return result;
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int radiu = 0;
        if(oval.bottom<=0){
            radiu = (int) ((Math.min(getWidth(), getHeight()))/2-mPaint.getStrokeWidth());

            oval.left = getWidth()/2-radiu;
            oval.top = getHeight()/2-radiu;
            oval.right = getWidth()/2+radiu;
            oval.bottom = getHeight()/2+radiu;

            oval2 = new RectF();
            oval2.left = getWidth()/2-radiu/4f*3;
            oval2.top = getHeight()/2-radiu/4f*3;
            oval2.right = getWidth()/2+radiu/4f*3;
            oval2.bottom = getHeight()/2+radiu/4f*3;

            oval3 = new RectF();
            oval3.left = getWidth()/2-radiu/8f*7;
            oval3.top = getHeight()/2-radiu/8f*7;
            oval3.right = getWidth()/2+radiu/8f*7;
            oval3.bottom = getHeight()/2+radiu/8f*7;
        }
        //初始化数据
        initData();
        //设置渐变色
        setSweepShader(OUT_SECTION_COLORS,mPaint);
        setSweepShader(INNER_SECTION_COLORS,mRollDrawPaint);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        if(isShowOutRoll){
            canvas.drawArc(oval, mStartAngle, mSweepAngle, false, mPaint);
        }else{
            canvas.drawArc(oval, mStartAngle, 360, false, mPaint);
        }

        if(isShowGraduationBackground){
            for (int i = 0; i < mLinesList.size(); i++) {
                Line line = mLinesList.get(i);
                canvas.drawLine(line.p1.x, line.p1.y, line.p2.x, line.p2.y, mRollPaint);
            }
        }

//      int degree = (int) (Math.round(mGraduationSweepAngle)/ITEMCOUNT);
//      for (int i = 0; i < Math.round(mSweepAngle/degree); i++) {
        for (int i = 0; i < Math.round(mSweepAngle*nGraduationCount/360f); i++) {
            if(i<mLinesList.size()){
                Line line = mLinesList.get(i);
                canvas.drawLine(line.p1.x, line.p1.y, line.p2.x, line.p2.y, mRollDrawPaint);
            }
        }
        String strProgressText = "";
        if(mOnProgressListener !=null){//如果不为空  则为接口返回的值
            strProgressText = mOnProgressListener.OnProgress(mMax, mProgress);
        }else{
            strProgressText = mProgress+"/"+mMax;
        }
        mTextPaint.getTextBounds(strProgressText, 0, strProgressText.length(), bounds);
        canvas.drawText(strProgressText, oval.centerX()-bounds.width()/2, oval.centerY()+bounds.height()/2, mTextPaint);
    }

    /**
     * 设置是否显示外部进度条
     * @param isShowOutRoll
     */
    public void setShowOutRollEnable(boolean isShowOutRoll){
        this.isShowOutRoll = isShowOutRoll;
    }
    /**
     * 设置是否显示进度条的背景
     * @param isShowGraduationBackground
     */
    public void setShowGraduationBackgroundEnable(boolean isShowGraduationBackground){
        this.isShowGraduationBackground = isShowGraduationBackground;
    }
    /**
     * 设置显示进度数量
     * @param nGraduationCount
     */
    public void setGraduationCount(int nGraduationCount){
        this.nGraduationCount = nGraduationCount;
    }
    /**
     * 设置进度的背景颜色
     * @param mGraduationBackgroundColor
     */
    public void setGraduationBackgroundColor(int mGraduationBackgroundColor){
        this.mGraduationBackgroundColor = mGraduationBackgroundColor;
        mRollPaint.setColor(mGraduationBackgroundColor);
    }
    /**
     * 设置刻度的宽度
     * @param mGraduationWidth
     */
    public void setGraduationWidth(int mGraduationWidth){
        this.mGraduationWidth = mGraduationWidth;
        mRollPaint.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mGraduationWidth, getResources().getDisplayMetrics()));
        mRollDrawPaint.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mGraduationWidth, getResources().getDisplayMetrics()));
    }

    /**
     * 设置最大进度值
     * @param max
     */
    public void setMax(int max){
        this.mMax = max;
    }
    /**
     * 设置进度
     * @param progress
     */
    public void setProgress(int progress){
        this.mProgress = progress;
        if(mMax==0){
            throw new IllegalArgumentException("Max不能为0!");
        }
        mSweepAngle = 360f*mProgress/mMax;
        postInvalidate();
    }
    /**
     * 设置开始的角度  可以控制开始的位置 默认为180  即从左边开始
     * @param mStartAngle
     */
    public void setStartAngle(float mStartAngle){
        this.mStartAngle = mStartAngle;
    }

    /**
     * 设置字体颜色
     * @param mTextColor
     */
    public void setTextColor(int mTextColor){
        this.mTextColor = mTextColor;
        mTextPaint.setColor(mTextColor);
    }
    /**
     * 设置字体大小
     * @param mTextSize
     */
    public void setTextSize(int mTextSize){
        this.mTextSize = mTextSize;
        mTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mTextSize, getResources().getDisplayMetrics()));
    }
    /**
     * 用于外部判断当前进度状态
     */
    interface OnProgressListener{
        /**
         * 返回中间部分文字内容
         * @param max
         * @param progress
         * @return
         */
        String OnProgress(int max,int progress);
    }
    /**
     * 刻度对象
     */
    class Line{
        PointF p1 = new PointF();
        PointF p2 = new PointF();
    }
}
