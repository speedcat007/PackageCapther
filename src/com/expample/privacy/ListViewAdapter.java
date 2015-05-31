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

	//运行上下文
	private Context context;
	//视图容器
	private LayoutInflater listContainer;

	//自定义控件集
	public final class ListItemView{               
		public ImageView image;     
		public TextView title;   
		public TextView info;   
		public ImageView lockView;
	}

	public ListViewAdapter(PrivacyActivity privacyActivity,Context context,MonitorDbManager monitorDbManager) {   
		this.privacyActivity = privacyActivity;
		this.context = context;
		//创建视图容器并设置上下文
		listContainer = LayoutInflater.from(context);
		this.monitorDbManager = monitorDbManager;
		initApks();
	}   

	//初始化
	public void initApks(){
		PackageManager pManager = context.getPackageManager();
		List<PackageInfo> apkList=new ArrayList<PackageInfo>();;
		//获取手机内所有应用
		List<PackageInfo> apkList0=pManager.getInstalledPackages(0);
		for (int i = 0; i < apkList0.size(); i++)
		{
			PackageInfo packageInfo =  (PackageInfo)apkList0.get(i);

			// 过滤掉系统应用
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
							strRight+=""+numRight+":读取通讯录\n";
						}
						if(quanxians[j].equals("android.permission.READ_SMS")){
							isPrivacy=true;
							numRight++;
							strRight+=""+numRight+":读取短信\n";
						}
						if(quanxians[j].equals("android.permission.READ_CALL_LOG")){
							isPrivacy=true;
							numRight++;
							strRight+=""+numRight+":读取通话记录\n";
						}
						if(quanxians[j].equals("android.permission.READ_LOGS")){
							isPrivacy=true;
							numRight++;
							strRight+=""+numRight+":读取系统日志\n";
						}
						if(quanxians[j].equals("android.permission.READ_CALENDAR")){
							isPrivacy=true;
							numRight++;
							strRight+=""+numRight+":读取日历数据\n";
						}
						if(quanxians[j].equals("com.android.browser.permission.READ_HISTORY_BOOKMARKS")){
							isPrivacy=true;
							numRight++;
							strRight+=""+numRight+":读取收藏夹和历史记录\n";
						}
					}
					if(isAcessNet){//可以访问网络
						if(isPrivacy && !packageInfo.packageName.equals("com.sohu.inputmethod.sogou")){//涉及到隐私
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
		//自定义视图   
		ListItemView  listItemView = null;   
		if (convertView == null) {   
			listItemView = new ListItemView();    
			//获取list_item布局文件的视图   
			convertView = listContainer.inflate(R.layout.list_item, null);   
			//获取控件对象   
			listItemView.image = (ImageView)convertView.findViewById(R.id.imageItem);   
			listItemView.title = (TextView)convertView.findViewById(R.id.itemTitle);   
			listItemView.info = (TextView)convertView.findViewById(R.id.itemInfo);   
			listItemView.lockView= (ImageView)convertView.findViewById(R.id.lockview);
			//设置控件集到convertView   
			convertView.setTag(listItemView);   
		}else {   
			listItemView = (ListItemView)convertView.getTag();   
		}   

		//设置文字和图片   
		PackageManager pManager = this.context.getPackageManager(); 

		apk t_apk=(apk)apks.get(position);
		PackageInfo pak= t_apk.pak;

		//设置图标
		Drawable icon=pManager.getApplicationIcon(pak.applicationInfo);
		listItemView.image.setImageDrawable(icon);

		//设置信息,程序名  
		String appname=pManager.getApplicationLabel(pak.applicationInfo).toString();

		ActivityManager mActivityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE); 
		boolean  isAppRunning = false;
		
		//获得当前应用
		ComponentName topActivity = mActivityManager.getRunningTasks(1).get(0).topActivity;
		String packageName = topActivity.getPackageName();

		if(pak.applicationInfo.packageName.equals(packageName)){
			//不是要监测的
			isAppRunning = true;
		}else {
			//看看后台
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
			appname+=" 正在运行";
		else
			appname+=" ";
		
		listItemView.title.setText(appname);   

		//权限信息
		listItemView.info.setTextSize(14);
		listItemView.info.setText("共有"+numRights.get(position)+"项隐私权限");

		LayoutParams para =  listItemView.lockView.getLayoutParams(); 
		para.width=56;
		para.height=56;
		listItemView.lockView.setLayoutParams(para);

		//根据状态设置
		if( t_apk.isLocked)
			listItemView.lockView.setBackgroundResource(R.drawable.deny);
		else
			listItemView.lockView.setBackgroundResource(R.drawable.accept);

		return convertView;   
	}

	//响应事件
	public void  dealClick(View view,int position){
		apk t_apk=(apk)apks.get(position);

		ListItemView  listItemView=(ListItemView)view.getTag();
		//图标设计
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
		//设置信息,程序名  
		PackageManager pManager = this.context.getPackageManager(); 
		String appname=pManager.getApplicationLabel(t_apk.pak.applicationInfo).toString();
		Dialog dialog = new AlertDialog.Builder(this.context)
		.setTitle(appname+"隐私权限：")//设置标题
		.setMessage(strRights.get(position))//设置内容
		.setPositiveButton("确定",//设置确定按钮
				new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int whichButton)
			{
				//点击“确定”转向登陆框
			}
		}).create();
		// 显示对话框
		dialog.show();

	}

	//获得应用
	public List<PackageInfo> getUserApps(){
		//获取手机内所有应用
		List<PackageInfo> apkList=new ArrayList<PackageInfo>();;
		PackageManager pManager =context.getPackageManager();
		List<PackageInfo> apkList0=pManager.getInstalledPackages(0);

		for (int i = 0; i < apkList0.size(); i++)
		{
			PackageInfo packageInfo = apkList0.get(i);
			// 过滤掉系统应用
			if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0){
				apkList.add(packageInfo);
			}
		}
		return  apkList;
	}


}
