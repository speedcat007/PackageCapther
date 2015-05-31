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
	
	/** ����ServiceConnection�����ڰ�Service��*/  
	private ServiceConnection mConnection = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_privacy);

		context=this;
		
		//��ʼ�����ݿ�
		monitorDbManager = new MonitorDbManager(this);
		Log.d("mdw","MonitorDbManager Init");
		//���ý���
		listView =(ListView)findViewById(R.id.listApps2);
		//���������� 
		listViewAdapter = new ListViewAdapter(this,this,monitorDbManager);
		//����������   
		listView.setAdapter(listViewAdapter); 
		//���õ���¼�
		listView.setOnItemClickListener(this);
		//���ó�ʱ�����¼�
		listView.setOnItemLongClickListener(this);

		//��ʼ�� myconfig
		myconfig=new myConfig(this);

		// ��������Service��Intent
		final Intent intent = new Intent();

		// ΪIntent����Action����    
	

		//��ť��ʼ��
		checkBtn=(CheckBox)findViewById(R.id.checkBtn2);
		isServiceOpen=myconfig.getPrivacyOpen();
		if(isServiceOpen){
			startService(new Intent(context, MonitorService.class));
			checkBtn.setChecked(true);
			checkBtn.setText("�Ѵ�");
		}else{
			checkBtn.setChecked(false);
			checkBtn.setText("�ѹر�");
		}
		//��ť�¼�		
		checkBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){ 
			@Override 
			public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) { 
				// TODO Auto-generated method stub 	
				myconfig.setPrivacyOpen(isChecked);
				if(isChecked){ 
					// ����ָ��Serivce
					//��̨�򿪷���
					startService(new Intent(context, MonitorService.class));
					checkBtn.setChecked(true);
					checkBtn.setText("�Ѵ�");
				}else{ 
					// ָֹͣ��Serivce
					try{
					unbindService(mConnection);
					}catch (Exception e){
						
					}
					final Intent intent = new Intent();
					// ΪIntent����Action����
					intent.setAction("com.expample.privacy.MonitorService");
					Log.d("mdw","!checked,close!");
					stopService(intent);
					checkBtn.setChecked(false);
					checkBtn.setText("�ѹر�");
					
					//��������
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

	//�����¼�
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
		// ��Service���󶨺�ͻ����mConnetion���onServiceConnected����  
		Log.d("mdw", "start");
		Intent intent = new Intent(this, MonitorService.class);  
		//mConnection
		Log.d("mdw", "mConnection");
		mConnection = new ServiceConnection() {  

			@Override  
			public void onServiceConnected(ComponentName className,  
					IBinder service) {  
				// �Ѿ�����LocalService��ǿתIBinder���󣬵��÷����õ�LocalService����  
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


	//������service----------------------begin----------------------------------  
	@Override  
	protected void onStop() { 
		super.onStop(); 
		this.monitorDbManager.closeDataBase();
		//this.unbindService(mConnection);
		//Log.d("mdw","stop1");
		// ��������Service��Intent
		//final Intent intent = new Intent();

		// ΪIntent����Action����
		//intent.setAction("com.expample.privacy.MonitorService");
		//if(!checkBtn.isChecked()){
			//Log.d("mdw","!checked,close!");
			//stopService(intent);

		//}
	}  

	/** �Ͷ�ȡService�������� */  
	public  void changeDateBase() { 
		Log.d("mdw", "mBound):"+mBound);

		if (mBound) {  
			Log.d("mdw", "mService.changeDate();");
			// ��Service�Ķ���ȥ��ȡ�����  
			mService.changeDate();
		}  
	}  


	//������service----------------------end-------------------------------------


	public boolean onKeyUp(int keyCode,KeyEvent event){
		switch(keyCode){
		case KeyEvent.KEYCODE_BACK:
			finish();
			Intent intent = new Intent();
			/* ָ��intentҪ�������� */
			intent.setClass(PrivacyActivity.this,MainActivity.class);
			/* ����һ���µ�Activity */
			startActivity(intent);
			/* �رյ�ǰ��Activity */
			finish();
			break;
		}
		return true;
	}

	public String getSDPath(){ 
		File sdDir = null; 
		boolean sdCardExist = Environment.getExternalStorageState()   
				.equals(android.os.Environment.MEDIA_MOUNTED);   //�ж�sd���Ƿ���� 
		if   (sdCardExist)   
		{                               
			sdDir = Environment.getExternalStorageDirectory();//��ȡ��Ŀ¼ 
		}   
		return sdDir.toString(); 

	}

}