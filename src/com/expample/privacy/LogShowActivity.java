package com.expample.privacy;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.activity.MainActivity;
import com.example.activity.R;
import com.example.config.myConfig;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class LogShowActivity extends Activity {

	private List<String> fileNames = null;
	private LogDbManager logDbManager = null;
	myConfig myconfig=null;
	@Override  
	protected void onCreate(Bundle savedInstanceState)  {  
		super.onCreate(savedInstanceState);    
		setContentView(R.layout.logfile_list);   

		logDbManager=new LogDbManager(this);
		myconfig=new myConfig(this);
		
		ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();     
		fileNames = logDbManager.getLogFiles();
		for (int i = 0; i <  fileNames.size(); i++) {    
			HashMap<String,String> item = new HashMap<String,String>();    
			item.put("filename","��־"+i+" ʱ�䣺"+fileNames.get(i)) ; 
			list.add(item);
		}    
		
		SimpleAdapter listAdapter = new SimpleAdapter(this,list,R.layout.logfile,
				new String[]{"filename"},new int[]{R.id.log_file_name});   
	
		//�����б�

		ListView mListView= (ListView)findViewById(R.id.log_file_list);
		mListView.setAdapter(listAdapter);

		mListView.setOnItemClickListener(new OnItemClickListener() {			//�̰��¼�
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,long id) {  
				
				logDbManager.getLogFileByName(fileNames.get(position));
				myconfig.setSaveState(false);
				Intent intent = new Intent();
				/* ָ��intentҪ�������� */
				intent.setClass(LogShowActivity.this,ShowMonitorLog.class);
				/* ����һ���µ�Activity */
				startActivity(intent);
				/* �رյ�ǰ��Activity */
				finish();
				
			}
		});

		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {			//�����¼�
			public boolean onItemLongClick(AdapterView parent, View view, int position, long id) {
				
							final String t_filename=fileNames.get(position);
			        		new AlertDialog.Builder(LogShowActivity.this).setTitle("��ʾ")
			        		.setMessage("��־"+position+" ʱ�䣺"+t_filename+"\n                ȷ��Ҫɾ����")
			        		.setPositiveButton("ȷ��",new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									logDbManager.DeleteDate(t_filename);
									Intent intent = new Intent();
									/* ָ��intentҪ�������� */
									intent.setClass(LogShowActivity.this,LogShowActivity.class);
									/* ����һ���µ�Activity */
									startActivity(intent);
									/* �رյ�ǰ��Activity */
									finish();
								}
							}).setNegativeButton("ȡ��",new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
								}
							}).create().show();
	            
	                
	                return true;
			}
		});
	}   
	
    public boolean onKeyUp(int keyCode,KeyEvent event){
    	switch(keyCode){
    		case KeyEvent.KEYCODE_BACK:
    			Intent intent = new Intent();
				/* ָ��intentҪ�������� */
				intent.setClass(LogShowActivity.this,MainActivity.class);
				/* ����һ���µ�Activity */
				startActivity(intent);
				/* �رյ�ǰ��Activity */
				finish();
    		break;
    	}
    	return true;
    }


}