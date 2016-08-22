![这里写图片描述](http://img.blog.csdn.net/20160728124114283)

进度的刻度、宽度、颜色可以随意设定：

![进度环](http://img.blog.csdn.net/20160730120609367)



attrs.xml:
```
       <declare-styleable name="LoadingStyle">
        <attr name="textSize" format="dimension|reference"/><!-- 字体大小 -->
        <attr name="textColor" format="color|reference"/><!-- 字体颜色 -->
        <attr name="strokeWidth" format="dimension|reference"/><!-- 圆环大小 -->
        <attr name="isShowGraduationBackground" format="boolean"/><!-- 是否显示背景刻度 -->
        <attr name="isShowOutRoll" format="boolean"/><!-- 是否显示外部进度框 -->
        <attr name="startAngle" format="integer|reference"/><!-- 开始的角度 -->
        <attr name="max" format="integer|reference"/><!-- 最大值 -->
        <attr name="progress" format="integer|reference"/><!-- 默认进度值 -->
        <attr name="graduationBackgroundColor" format="color|reference"/><!-- 刻度的背景颜色 -->
        <attr name="graduationWidth" format="dimension|reference"/><!-- 刻度的宽度 -->
        <attr name="graduationCount" format="integer|reference"/><!-- 刻度的个数 -->
    </declare-styleable>
```
###使用

layout文件：	
```
 xmlns:app="http://schemas.android.com/apk/res-auto"<!--设置命名空间 -->

  <com.mrzk.circleloadinglibrary.CircleLoadingView
        android:id="@+id/clv_loading"
        android:layout_width="250dp"
        android:layout_height="250dp"
        android:layout_centerInParent="true"
        app:textSize="35sp"
        app:textColor="#f60"
        app:strokeWidth="10dp"
        app:isShowGraduationBackground="true"
        app:startAngle="180"
        app:graduationBackgroundColor="#ccc"
        app:graduationWidth="5dp"
        app:graduationCount="10"
        app:isShowOutRoll="true"
        />
```

```
int[] colors = {0xFFE5BD7D, 0xFFFAAA64,
	                0xFFFFFFFF, 0xFF6AE2FD,
	                0xFF8CD0E5, 0xFFA3CBCB,
	                0xFFBDC7B3, 0xFFD1C299, 0xFFE5BD7D};
		lv_loading.setTextColor(Color.BLACK);//设置中心文字颜色
		lv_loading.setMax(500);//设置最大进度
		lv_loading.setShowGraduationBackgroundEnable(true);//是否显示刻度背景
		lv_loading.setGraduationBackgroundColor(Color.GRAY);//刻度的背景颜色
		lv_loading.setStartAngle(180);//设置开始旋转角度
		lv_loading.setGraduationCount(10);//设置刻度数
		lv_loading.setGraduationWidth(5);//设置刻度的宽度
		lv_loading.setOutColors(colors);//设置外部圆环颜色
		lv_loading.setInnerGraduationColors(colors);//设置内部刻度进度颜色
		lv_loading.setTextSize(35);//设置内部文字字体大小
		lv_loading.setShowOutRollEnable(false);//设置是否显示外部进度框
		lv_loading.setOnProgressListener(new OnProgressListener() {
			@Override
			public String OnProgress(int max, int progress) {
				
				return progress*100f/max+"%";
			}
		});
```
