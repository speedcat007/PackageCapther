package com.example.activity;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import com.example.ui.ImageButton_define;
import com.expample.privacy.LogShowActivity;
import com.expample.privacy.PrivacyActivity;

import dalvik.system.DexFile;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//日志查看
		ImageButton_define btn_log_view = (ImageButton_define) findViewById(R.id.define_log_view);
		btn_log_view.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				/* 新建一个Intent对象 */
				Intent intent = new Intent();
				/* 指定intent要启动的类 */
				intent.setClass(MainActivity.this,LogShowActivity.class);
				/* 启动一个新的Activity */
				startActivity(intent);
				/* 关闭当前的Activity */
				finish();
			}
		});

		//隐私卫士
		ImageButton_define btn_zb = (ImageButton_define) findViewById(R.id.define_privacy);
		btn_zb.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				/* 新建一个Intent对象 */
				Intent intent = new Intent();
				/* 指定intent要启动的类 */
				intent.setClass(MainActivity.this,PrivacyActivity.class);
				/* 启动一个新的Activity */
				startActivity(intent);
				/* 关闭当前的Activity */
				finish();
			}
		});				

		//隐私卫士
		ImageButton_define btn_sz = (ImageButton_define) findViewById(R.id.define_sz);
		btn_sz.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				/* 新建一个Intent对象 */
				Intent intent = new Intent();
				/* 指定intent要启动的类 */
				intent.setClass(MainActivity.this,Setting.class);
				/* 启动一个新的Activity */
				startActivity(intent);
				/* 关闭当前的Activity */
				finish();
			}
		});				


	}


}
