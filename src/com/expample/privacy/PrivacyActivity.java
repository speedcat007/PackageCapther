package com.expample.privacy;

import java.io.File;
import java.util.List;

import com.example.activity.MainActivity;
import com.example.activity.R;
import com.example.config.myConfig;
import com.expample.privacy.MonitorService.LocalBinder;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;

public class PrivacyActivity extends Activity implements OnItemClickListener,OnItemLongClickListener{

	private ListView listView;
	private ListViewAdapter listViewAdapter;
	private MonitorDbManager monitorDbManager;

	private MonitorService  mService;  
	private  boolean mBound = false;
	private CheckBox checkBtn;

	private boolean isServiceOpen=false;
	private myConfig myconfig;

	Context context;
	
	/** 定交ServiceConnection，用于绑定Service的*/  
	private ServiceConnection mConnection = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_privacy);

		context=this;
		
		//初始化数据库
		monitorDbManager = new MonitorDbManager(this);
		Log.d("mdw","MonitorDbManager Init");
		//设置界面
		listView =(ListView)findViewById(R.id.listApps2);
		//创建适配器 
		listViewAdapter = new ListViewAdapter(this,this,monitorDbManager);
		//配置适配器   
		listView.setAdapter(listViewAdapter); 
		//设置点击事件
		listView.setOnItemClickListener(this);
		//设置长时间点击事件
		listView.setOnItemLongClickListener(this);

		//初始化 myconfig
		myconfig=new myConfig(this);

		// 创建启动Service的Intent
		final Intent intent = new Intent();

		// 为Intent设置Action属性    
	

		//按钮初始化
		checkBtn=(CheckBox)findViewById(R.id.checkBtn2);
		isServiceOpen=myconfig.getPrivacyOpen();
		if(isServiceOpen){
			startService(new Intent(context, MonitorService.class));
			checkBtn.setChecked(true);
			checkBtn.setText("已打开");
		}else{
			checkBtn.setChecked(false);
			checkBtn.setText("已关闭");
		}
		//按钮事件		
		checkBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){ 
			@Override 
			public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) { 
				// TODO Auto-generated method stub 	
				myconfig.setPrivacyOpen(isChecked);
				if(isChecked){ 
					// 启动指定Serivce
					//后台打开服务
					startService(new Intent(context, MonitorService.class));
					checkBtn.setChecked(true);
					checkBtn.setText("已打开");
				}else{ 
					// 停止指定Serivce
					try{
					unbindService(mConnection);
					}catch (Exception e){
						
					}
					final Intent intent = new Intent();
					// 为Intent设置Action属性
					intent.setAction("com.expample.privacy.MonitorService");
					Log.d("mdw","!checked,close!");
					stopService(intent);
					checkBtn.setChecked(false);
					checkBtn.setText("已关闭");
					
					//进行清理
					File f = new File(getSDPath()+"/");    
					File[] files = f.listFiles();
					if(files != null)  
					{    
						int count = files.length;
						for (int i = 0; i < count; i++) {    
							File tfile = files[i];    
							String tmp_str=tfile.getName();
						
							if(tmp_str.endsWith(".pcap")){
								tfile.delete();
								Log.d("mmm",tfile.getName() );
							}
						}    
					}    
				} 
			} 
		}); 		

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) { 
		// TODO Auto-generated method stub
		listViewAdapter.dealClick(view, position);
	}

	//长按事件
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		listViewAdapter.dealLongClick(view, position);
		return true;
	}
	
	@Override  
	protected void onResume(){
		super.onResume();
	}


	@Override  
	protected void onStart() {  
		super.onStart();  
		// 绑定Service，绑定后就会调用mConnetion里的onServiceConnected方法  
		Log.d("mdw", "start");
		Intent intent = new Intent(this, MonitorService.class);  
		//mConnection
		Log.d("mdw", "mConnection");
		mConnection = new ServiceConnection() {  

			@Override  
			public void onServiceConnected(ComponentName className,  
					IBinder service) {  
				// 已经绑定了LocalService，强转IBinder对象，调用方法得到LocalService对象  
				LocalBinder binder = (LocalBinder) service;  
				mService = binder.getService();  
				mBound = true;  
				Log.d("mdw", "start:mBound2 "+mBound);
			}  

			@Override  
			public void onServiceDisconnected(ComponentName arg0) {  
				mBound = false;  
				Log.d("mdw", "start:mBound3 "+mBound);
			}  
		};  
		boolean success = bindService(intent, mConnection, Context.BIND_AUTO_CREATE); 
		Log.d("mdw", "start:bind "+success);
	}  


	//操作手service----------------------begin----------------------------------  
	@Override  
	protected void onStop() { 
		super.onStop(); 
		this.monitorDbManager.closeDataBase();
		//this.unbindService(mConnection);
		//Log.d("mdw","stop1");
		// 创建启动Service的Intent
		//final Intent intent = new Intent();

		// 为Intent设置Action属性
		//intent.setAction("com.expample.privacy.MonitorService");
		//if(!checkBtn.isChecked()){
			//Log.d("mdw","!checked,close!");
			//stopService(intent);

		//}
	}  

	/** 就读取Service里的随机数 */  
	public  void changeDateBase() { 
		Log.d("mdw", "mBound):"+mBound);

		if (mBound) {  
			Log.d("mdw", "mService.changeDate();");
			// 用Service的对象，去读取随机数  
			mService.changeDate();
		}  
	}  


	//操作手service----------------------end-------------------------------------


	public boolean onKeyUp(int keyCode,KeyEvent event){
		switch(keyCode){
		case KeyEvent.KEYCODE_BACK:
			finish();
			Intent intent = new Intent();
			/* 指定intent要启动的类 */
			intent.setClass(PrivacyActivity.this,MainActivity.class);
			/* 启动一个新的Activity */
			startActivity(intent);
			/* 关闭当前的Activity */
			finish();
			break;
		}
		return true;
	}

	public String getSDPath(){ 
		File sdDir = null; 
		boolean sdCardExist = Environment.getExternalStorageState()   
				.equals(android.os.Environment.MEDIA_MOUNTED);   //判断sd卡是否存在 
		if   (sdCardExist)   
		{                               
			sdDir = Environment.getExternalStorageDirectory();//获取跟目录 
		}   
		return sdDir.toString(); 

	}

}