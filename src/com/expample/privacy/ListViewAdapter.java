package com.expample.privacy;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.example.activity.R;
import com.example.activity.R.drawable;
import com.example.activity.R.id;
import com.example.activity.R.layout;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.TrafficStats;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
class apk{
	PackageInfo pak;
	boolean isLocked;
}


public class ListViewAdapter extends BaseAdapter {

	PrivacyActivity privacyActivity;

	List<apk> apks=new ArrayList<apk>();
	List<Integer> numRights=new ArrayList<Integer>();
	List<String> strRights=new ArrayList<String>();

	MonitorDbManager monitorDbManager;

	//����������
	private Context context;
	//��ͼ����
	private LayoutInflater listContainer;

	//�Զ���ؼ���
	public final class ListItemView{               
		public ImageView image;     
		public TextView title;   
		public TextView info;   
		public ImageView lockView;
	}

	public ListViewAdapter(PrivacyActivity privacyActivity,Context context,MonitorDbManager monitorDbManager) {   
		this.privacyActivity = privacyActivity;
		this.context = context;
		//������ͼ����������������
		listContainer = LayoutInflater.from(context);
		this.monitorDbManager = monitorDbManager;
		initApks();
	}   

	//��ʼ��
	public void initApks(){
		PackageManager pManager = context.getPackageManager();
		List<PackageInfo> apkList=new ArrayList<PackageInfo>();;
		//��ȡ�ֻ�������Ӧ��
		List<PackageInfo> apkList0=pManager.getInstalledPackages(0);
		for (int i = 0; i < apkList0.size(); i++)
		{
			PackageInfo packageInfo =  (PackageInfo)apkList0.get(i);

			// ���˵�ϵͳӦ��
			if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0){
				try {
					String[] quanxians=pManager.getPackageInfo(packageInfo.packageName, PackageManager.GET_PERMISSIONS).requestedPermissions;

					boolean isAcessNet=false;
					boolean isPrivacy=false;
					String strRight="";
					int numRight=0;
					for(int j=0;j<quanxians.length;++j){
						if(quanxians[j].equals("android.permission.INTERNET")){
							isAcessNet=true;
						}
						if(quanxians[j].equals("android.permission.READ_CONTACTS")){
							isPrivacy=true;
							numRight++;
							strRight+=""+numRight+":��ȡͨѶ¼\n";
						}
						if(quanxians[j].equals("android.permission.READ_SMS")){
							isPrivacy=true;
							numRight++;
							strRight+=""+numRight+":��ȡ����\n";
						}
						if(quanxians[j].equals("android.permission.READ_CALL_LOG")){
							isPrivacy=true;
							numRight++;
							strRight+=""+numRight+":��ȡͨ����¼\n";
						}
						if(quanxians[j].equals("android.permission.READ_LOGS")){
							isPrivacy=true;
							numRight++;
							strRight+=""+numRight+":��ȡϵͳ��־\n";
						}
						if(quanxians[j].equals("android.permission.READ_CALENDAR")){
							isPrivacy=true;
							numRight++;
							strRight+=""+numRight+":��ȡ��������\n";
						}
						if(quanxians[j].equals("com.android.browser.permission.READ_HISTORY_BOOKMARKS")){
							isPrivacy=true;
							numRight++;
							strRight+=""+numRight+":��ȡ�ղؼк���ʷ��¼\n";
						}
					}
					if(isAcessNet){//���Է�������
						if(isPrivacy && !packageInfo.packageName.equals("com.sohu.inputmethod.sogou")){//�漰����˽
							apkList.add(packageInfo);
							strRights.add(strRight);
							numRights.add(new Integer(numRight));
						}
					}
				}catch(Exception e){
				}
			}
		}

		List<String> pnames = monitorDbManager.getAppLockStates();

		for(int i=0;i<apkList.size();++i){

			apk t_apk=new apk();
			PackageInfo pak = apkList.get(i);
			t_apk.pak=pak;

			boolean islocked=false;
			for(int j=0;j<pnames.size();++j){
				if(pak.packageName.equals((String)pnames.get(j))){
					islocked=true;
					break;
				}
			}

			t_apk.isLocked=islocked;

			apks.add(t_apk);

		}	
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.apks.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {      
		//�Զ�����ͼ   
		ListItemView  listItemView = null;   
		if (convertView == null) {   
			listItemView = new ListItemView();    
			//��ȡlist_item�����ļ�����ͼ   
			convertView = listContainer.inflate(R.layout.list_item, null);   
			//��ȡ�ؼ�����   
			listItemView.image = (ImageView)convertView.findViewById(R.id.imageItem);   
			listItemView.title = (TextView)convertView.findViewById(R.id.itemTitle);   
			listItemView.info = (TextView)convertView.findViewById(R.id.itemInfo);   
			listItemView.lockView= (ImageView)convertView.findViewById(R.id.lockview);
			//���ÿؼ�����convertView   
			convertView.setTag(listItemView);   
		}else {   
			listItemView = (ListItemView)convertView.getTag();   
		}   

		//�������ֺ�ͼƬ   
		PackageManager pManager = this.context.getPackageManager(); 

		apk t_apk=(apk)apks.get(position);
		PackageInfo pak= t_apk.pak;

		//����ͼ��
		Drawable icon=pManager.getApplicationIcon(pak.applicationInfo);
		listItemView.image.setImageDrawable(icon);

		//������Ϣ,������  
		String appname=pManager.getApplicationLabel(pak.applicationInfo).toString();

		ActivityManager mActivityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE); 
		boolean  isAppRunning = false;
		
		//��õ�ǰӦ��
		ComponentName topActivity = mActivityManager.getRunningTasks(1).get(0).topActivity;
		String packageName = topActivity.getPackageName();

		if(pak.applicationInfo.packageName.equals(packageName)){
			//����Ҫ����
			isAppRunning = true;
		}else {
			//������̨
			List<ActivityManager.RunningServiceInfo> runServiceList = mActivityManager.getRunningServices(Integer.MAX_VALUE); 
			for(int i=0;i<runServiceList.size();++i){
				packageName=runServiceList.get(i).service.getPackageName();
				if(pak.applicationInfo.packageName.equals(packageName)){
					isAppRunning=true;
					break;
				}
			}
		
		}
		
		
		if(appname.length()>=23){
			String temp="";
			for(int i=0;i<22;++i)
				temp+=appname.charAt(i);
			temp+="...";
			appname=temp;
		}
		if(isAppRunning)
			appname+=" ��������";
		else
			appname+=" ";
		
		listItemView.title.setText(appname);   

		//Ȩ����Ϣ
		listItemView.info.setTextSize(14);
		listItemView.info.setText("����"+numRights.get(position)+"����˽Ȩ��");

		LayoutParams para =  listItemView.lockView.getLayoutParams(); 
		para.width=56;
		para.height=56;
		listItemView.lockView.setLayoutParams(para);

		//����״̬����
		if( t_apk.isLocked)
			listItemView.lockView.setBackgroundResource(R.drawable.deny);
		else
			listItemView.lockView.setBackgroundResource(R.drawable.accept);

		return convertView;   
	}

	//��Ӧ�¼�
	public void  dealClick(View view,int position){
		apk t_apk=(apk)apks.get(position);

		ListItemView  listItemView=(ListItemView)view.getTag();
		//ͼ�����
		if(!t_apk.isLocked){
			listItemView.lockView.setBackgroundResource(R.drawable.deny);
			monitorDbManager.AddDate(t_apk.pak.packageName);
			t_apk.isLocked=true;
		}else{
			listItemView.lockView.setBackgroundResource(R.drawable.accept);
			monitorDbManager.DeleteDate(t_apk.pak.packageName);
			t_apk.isLocked=false;
		}

		Log.d("mdw", "privacyActivity.changeDateBase()");
		privacyActivity.changeDateBase();
	}


	public void dealLongClick(View view, int position) {
		apk t_apk=(apk)apks.get(position);
		//������Ϣ,������  
		PackageManager pManager = this.context.getPackageManager(); 
		String appname=pManager.getApplicationLabel(t_apk.pak.applicationInfo).toString();
		Dialog dialog = new AlertDialog.Builder(this.context)
		.setTitle(appname+"��˽Ȩ�ޣ�")//���ñ���
		.setMessage(strRights.get(position))//��������
		.setPositiveButton("ȷ��",//����ȷ����ť
				new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int whichButton)
			{
				//�����ȷ����ת���½��
			}
		}).create();
		// ��ʾ�Ի���
		dialog.show();

	}

	//���Ӧ��
	public List<PackageInfo> getUserApps(){
		//��ȡ�ֻ�������Ӧ��
		List<PackageInfo> apkList=new ArrayList<PackageInfo>();;
		PackageManager pManager =context.getPackageManager();
		List<PackageInfo> apkList0=pManager.getInstalledPackages(0);

		for (int i = 0; i < apkList0.size(); i++)
		{
			PackageInfo packageInfo = apkList0.get(i);
			// ���˵�ϵͳӦ��
			if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0){
				apkList.add(packageInfo);
			}
		}
		return  apkList;
	}


}
