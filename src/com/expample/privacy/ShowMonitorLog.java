package com.expample.privacy;

import com.example.activity.MainActivity;
import com.example.activity.R;
import com.example.config.myConfig;

import android.app.Activity;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ShowMonitorLog extends Activity {
	
	LogDbManager logDbManager=null;
	String info_data;
	String info_time; 
	String info_way;
	String info_app;
	String info_app_ip;
	String info_port;
	String info_thread_size; 
	String info_max_size;
	Dialog dialog;
	myConfig myconfig;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_monitor_info);
		
		Log.d("mdw", "s0");
		 myconfig = new myConfig(this);
		 Log.d("mdw", "s1");
		logDbManager=new LogDbManager(this);
		
		//程序的路径 (包名+程序名).
		dialog = new Builder(this)
		.setTitle("提示")//设置标题
		.setMessage("报告保存成功！")//设置内容
		.setPositiveButton("确定",//设置确定按钮
		new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int whichButton)
			{
				//点击“确定”转向登陆框
			}
		}).create();
		Log.d("mdw", "s2");
		TextView tv_info_data=(TextView)findViewById(R.id.tv_day);
		info_data=myconfig.getinfo_data();
		tv_info_data.setText(info_data);
		
		TextView tv_info_time=(TextView)findViewById(R.id.tv_time);
		info_time=myconfig.getinfo_time();
		tv_info_time.setText(info_time);
		
		TextView tv_info_way=(TextView)findViewById(R.id.tv_way);
		info_way=myconfig.getinfo_way();
		tv_info_way.setText(info_way);
		
		TextView tv_info_app=(TextView)findViewById(R.id.tv_app);
		info_app=myconfig.getinfo_app();
		tv_info_app.setText(info_app);
		
		TextView tv_info_app_ip=(TextView)findViewById(R.id.tv_app_ip);
		info_app_ip=myconfig.getinfo_app_ip();
		tv_info_app_ip.setText(info_app_ip);
		
		TextView tv_info_thread_size=(TextView)findViewById(R.id.tv_thread_size);
		info_thread_size=myconfig.getinfo_thread_size();
		tv_info_thread_size.setText(info_thread_size);
		
		TextView tv_info_max_size=(TextView)findViewById(R.id.tv_max_packet_size);
		info_max_size=myconfig.getinfo_max_size();
		tv_info_max_size.setText(info_max_size);
		Log.d("mdw", "s3");
		//设置按钮点击事件
		Button btn_save_info=(Button)findViewById(R.id.btn_save_info);
		
		if(!myconfig.getSaveState())
			btn_save_info.setVisibility(View.INVISIBLE);
		else
			btn_save_info.setVisibility(View.VISIBLE);
		
		btn_save_info.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				logDbManager.AddDate(
						info_data, 
						info_time, 
						info_way, 
						info_app,
						info_app_ip,
						"80",
						info_thread_size, 
						info_max_size
					);
				 dialog.show();
			}
		});
		
		//设置按钮点击事件
		Button btn_exit=(Button)findViewById(R.id.btn_exit);
		btn_exit.setOnClickListener(new Button.OnClickListener() {
			final boolean state=myconfig.getSaveState();
			@Override
			public void onClick(View arg0) {
				if(!state){
					Intent intent = new Intent();
					/* 指定intent要启动的类 */
					intent.setClass(ShowMonitorLog.this,LogShowActivity.class);
					/* 启动一个新的Activity */
					startActivity(intent);
					/* 关闭当前的Activity */
					finish();
				}else{
				Intent intent = new Intent();
				/* 指定intent要启动的类 */
				intent.setClass(ShowMonitorLog.this,MainActivity.class);
				/* 启动一个新的Activity */
				startActivity(intent);
				/* 关闭当前的Activity */
				finish();
				}
			}
		});
	}
}
