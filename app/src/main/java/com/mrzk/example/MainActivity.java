package com.mrzk.example;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.mrzk.circleloadinglibrary.CircleLoadingView;

import java.lang.ref.SoftReference;


/**
 * Created by win7 on 2016-8-22.
 */
public class MainActivity extends AppCompatActivity {

    private static final int PROGRESS = 1;
    CircleLoadingView lv_loading;
    int[] colors = {0xFFE5BD7D, 0xFFFAAA64,
            0xFFFFFFFF, 0xFF6AE2FD,
            0xFF8CD0E5, 0xFFA3CBCB,
            0xFFBDC7B3, 0xFFD1C299, 0xFFE5BD7D};
    int progress = 0;
    int max = 300;

    private MyHandler mHandler = new MyHandler(this);
    class MyHandler extends android.os.Handler{
        SoftReference<Activity> softReference ;
        public MyHandler(Activity activity){
            softReference = new SoftReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (softReference.get() !=null){
                switch (msg.what){
                    case PROGRESS:
                        progress+=5;
                        if (progress<=max){
                            lv_loading.setProgress(progress);
                            mHandler.sendEmptyMessageDelayed(PROGRESS,200);
                        }
                        break;
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         lv_loading = (CircleLoadingView) findViewById(R.id.clv_loading);
        lv_loading.setOutColors(colors);//设置外部圆环颜色
        lv_loading.setMax(max);
        lv_loading.setOnProgressListener(new CircleLoadingView.OnProgressListener() {
            @Override
            public String OnProgress(int max, int progress) {

                return (int)(progress * 100f / max) + "%";
            }
        });
    }
    public void Start(View v){
        progress = 0;
        mHandler.removeCallbacksAndMessages(null);
        mHandler.sendEmptyMessage(PROGRESS);
    }
}
