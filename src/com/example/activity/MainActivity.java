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

		//��־�鿴
		ImageButton_define btn_log_view = (ImageButton_define) findViewById(R.id.define_log_view);
		btn_log_view.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				/* �½�һ��Intent���� */
				Intent intent = new Intent();
				/* ָ��intentҪ�������� */
				intent.setClass(MainActivity.this,LogShowActivity.class);
				/* ����һ���µ�Activity */
				startActivity(intent);
				/* �رյ�ǰ��Activity */
				finish();
			}
		});

		//��˽��ʿ
		ImageButton_define btn_zb = (ImageButton_define) findViewById(R.id.define_privacy);
		btn_zb.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				/* �½�һ��Intent���� */
				Intent intent = new Intent();
				/* ָ��intentҪ�������� */
				intent.setClass(MainActivity.this,PrivacyActivity.class);
				/* ����һ���µ�Activity */
				startActivity(intent);
				/* �رյ�ǰ��Activity */
				finish();
			}
		});				

		//��˽��ʿ
		ImageButton_define btn_sz = (ImageButton_define) findViewById(R.id.define_sz);
		btn_sz.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				/* �½�һ��Intent���� */
				Intent intent = new Intent();
				/* ָ��intentҪ�������� */
				intent.setClass(MainActivity.this,Setting.class);
				/* ����һ���µ�Activity */
				startActivity(intent);
				/* �رյ�ǰ��Activity */
				finish();
			}
		});				


	}


}
