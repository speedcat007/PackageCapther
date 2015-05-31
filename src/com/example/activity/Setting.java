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
		
		//��ʼ��
		textView = (TextView)findViewById(R.id.thread_textV1);
		textView.setText("��ǰ��ֵΪ��"+myconfig.getThrshold()+"B");
		
		seekBar = (SeekBar) findViewById(R.id.seekBar);
		seekBar.setProgress(myconfig.getThrshold()/2);
		
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
		{
			// ���϶����Ļ���λ�÷����ı�ʱ�����÷���
			@Override
			public void onProgressChanged(SeekBar arg0, int progress,
					boolean fromUser)
			{
				// ��̬�ı�ͼƬ��͸����
				//image.setAlpha(progress);
				textView.setText("��ǰ��ֵΪ��"+progress*2+"B");

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
		
		//���ð�ť����¼�	
		Button ipBtn=(Button)findViewById(R.id.thread_btn);
		ipBtn.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				myconfig.setThrshold(seekBar.getProgress()*2);
				//��ʾ�Ƿ�ɹ�
        		Dialog dialog = new AlertDialog.Builder(context)
        			.setTitle("��ʾ")//���ñ���
        			.setMessage("�޸���ֵ�ɹ�")//��������
        			.setPositiveButton("ȷ��",//����ȷ����ť
        			new DialogInterface.OnClickListener() 
        			{
        				public void onClick(DialogInterface dialog, int whichButton)
        				{
        				}
        		}).create();
        	    // ��ʾ�Ի���
        	    dialog.show(); 
			}
		});
		
		
	}
	
	 public boolean onKeyUp(int keyCode,KeyEvent event){
	    	switch(keyCode){
	    		case KeyEvent.KEYCODE_BACK:
	    			
	    			finish();
	    			Intent intent = new Intent();
					/* ָ��intentҪ�������� */
					intent.setClass(Setting.this,MainActivity.class);
					/* ����һ���µ�Activity */
					startActivity(intent);
					/* �رյ�ǰ��Activity */
					finish();
	    		break;
	    	}
	    	
	    	return true;
	    }
}
