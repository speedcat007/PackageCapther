package com.example.activity;

import com.example.activity.R;
import com.example.config.myConfig;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class Setting extends Activity {
	myConfig myconfig;
	Context context;
	SeekBar seekBar;
	TextView textView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		myconfig=new myConfig(this);
		context=this;
		
		//初始化
		textView = (TextView)findViewById(R.id.thread_textV1);
		textView.setText("当前阈值为："+myconfig.getThrshold()+"B");
		
		seekBar = (SeekBar) findViewById(R.id.seekBar);
		seekBar.setProgress(myconfig.getThrshold()/2);
		
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
		{
			// 当拖动条的滑块位置发生改变时触发该方法
			@Override
			public void onProgressChanged(SeekBar arg0, int progress,
					boolean fromUser)
			{
				// 动态改变图片的透明度
				//image.setAlpha(progress);
				textView.setText("当前阈值为："+progress*2+"B");

			}
			@Override
			public void onStartTrackingTouch(SeekBar bar)
			{
			}
			@Override
			public void onStopTrackingTouch(SeekBar bar)
			{
			}
		});
		
		//设置按钮点击事件	
		Button ipBtn=(Button)findViewById(R.id.thread_btn);
		ipBtn.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				myconfig.setThrshold(seekBar.getProgress()*2);
				//提示是否成功
        		Dialog dialog = new AlertDialog.Builder(context)
        			.setTitle("提示")//设置标题
        			.setMessage("修改阈值成功")//设置内容
        			.setPositiveButton("确定",//设置确定按钮
        			new DialogInterface.OnClickListener() 
        			{
        				public void onClick(DialogInterface dialog, int whichButton)
        				{
        				}
        		}).create();
        	    // 显示对话框
        	    dialog.show(); 
			}
		});
		
		
	}
	
	 public boolean onKeyUp(int keyCode,KeyEvent event){
	    	switch(keyCode){
	    		case KeyEvent.KEYCODE_BACK:
	    			
	    			finish();
	    			Intent intent = new Intent();
					/* 指定intent要启动的类 */
					intent.setClass(Setting.this,MainActivity.class);
					/* 启动一个新的Activity */
					startActivity(intent);
					/* 关闭当前的Activity */
					finish();
	    		break;
	    	}
	    	
	    	return true;
	    }
}
