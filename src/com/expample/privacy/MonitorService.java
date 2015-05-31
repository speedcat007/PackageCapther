package com.expample.privacy;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.activity.MainActivity;
import com.example.activity.R;
import com.example.config.myConfig;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.TrafficStats;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.util.Log;


public class MonitorService extends Service {

	private Process process;
	private BufferedInputStream buferReader;
	private OutputStream buferWriter;
	private BufferedInputStream buferError;
	private LockTask lockTask;
	private Timer mTimer;
	private boolean isCatchRunning=false;
	private String tempFileName="";


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

	//String fileName = getSDPath() +"/" + name;//以name存在目录中
	/**
	 * 应用程序发生变化时调用此函数，更新service中数据库
	 */
	void  changeDate(){
		try{
			lockTask.pnames=lockTask.monitorDbManager.getAppLockStates();
		}catch(Exception e){
		}
	}

	// 实例化自定义的Binder类  
	private final IBinder mBinder = new LocalBinder();

	/** 
	 * 自定义的Binder类，这个是一个内部类，所以可以知道其外围类的对象，通过这个类，让Activity知道其Service的对象 
	 */  
	public class LocalBinder extends Binder {  
		MonitorService getService() {  
			// 返回Activity所关联的Service对象，这样在Activity里，就可调用Service里的一些公用方法和公用属性  
			return MonitorService.this;  
		}  
	}  

	/**
	 * 必须实现的方法
	 * 
	 */
	@Override
	public IBinder onBind(Intent arg0)
	{
		return  mBinder;
	}

	/**
	 * Service被创建时回调该方法。
	 * 
	 */
	@Override
	public void onCreate()
	{
		super.onCreate();
		//root
		if(!getRoot())
			Log.d("mdw","root falied");
		//安装tcpdump包
		installShark();
		Log.d("mdw","Service is Created");
	}

	/**
	 * Service被启动时回调该方法
	 * 
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		Log.d("mdw","Service is Started");
		//service已经打开
		myConfig myconfig=new myConfig(this);
		myconfig.setPrivacyOpen(true);
		//开始抓包
		startCatch();
		isCatchRunning=true;
		startTimer();
		return START_STICKY;
	}

	/**
	 * Service被关闭之前回调。
	 * 
	 */
	@Override
	public void onDestroy()
	{
		//service已经关闭
		myConfig myconfig=new myConfig(this);
		myconfig.setPrivacyOpen(false);

		
		//停止抓包
		if(isCatchRunning){
			stopCatch();
			isCatchRunning=false;
		}

		//取消定时操作
		if (mTimer != null) {
			mTimer.cancel();
			mTimer.purge();
			mTimer = null;
		}

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
		super.onDestroy();
	}

	/**
	 * 打开定时器
	 * 
	 */
	private void startTimer() {
		if (mTimer == null) {
			mTimer = new Timer();
			lockTask = new LockTask(this);
			mTimer.schedule(lockTask,0,3000L);//每隔3秒
		}
	}

	/**
	 * 停止抓包
	 * 
	 */
	void stopCatch(){
		try    	
		{      
			this.process.destroy();
			this.process = Runtime.getRuntime().exec("su");
			this.buferWriter =  this.process.getOutputStream();
			this.buferReader = new BufferedInputStream(this.process.getInputStream());
			this.buferError = new BufferedInputStream(this.process.getErrorStream());
			this.buferWriter.write("kill $(ps | grep tcpdump | tr -s ' ' | cut -d ' ' -f2)\n".getBytes("ASCII"));
			this.buferWriter.write("killall tcpdump\n".getBytes("ASCII"));
			this.buferWriter.write("busybox kill $(ps | grep tcpdump | tr -s ' ' | cut -d ' ' -f2)\n".getBytes("ASCII"));
			this.buferWriter.write("busybox killall tcpdump\n".getBytes("ASCII"));
			Log.d("mdw","停止抓包");
			return;
		}
		catch (IOException e)
		{
			Log.d("mdw","是否ROOT？");
		}
	}

	/**
	 * 开始抓包
	 * 
	 */
	void startCatch(){
		String catchStr;
		tempFileName = ("capture" + System.currentTimeMillis() / 1000L + ".pcap");
		String filename = "/data/data/"+ getPackageName() + "/files/tcpdump";
		catchStr = "chmod 777 "+ filename +" && " +  filename + " -v -s 0 -w "+getSDPath()+"/" + tempFileName +" src "+getPsdnIp()+
				" and tcp[20:2]=0x504f\n";
	
		
		//catchStr = "chmod 777 "+ filename +" && " +  filename + " -vv -s 0 -w /sdcard/" + tempFileName + "\n";
		//Log.d("mdw",catchStr);
		try
		{
			this.buferWriter.write(catchStr.getBytes("ASCII"));
			Log.d("mdw","开始抓包");
			Log.d("mdw","createFile:"+tempFileName);
			return;
		}
		catch (IOException e)
		{
			Log.d("mdw","是否ROOT？");
		}
	}

	/**
	 * 提升权限
	 * 
	 */
	boolean getRoot(){
		try
		{	
			this.process = Runtime.getRuntime().exec("su"); 
			this.buferWriter = this.process.getOutputStream();
			this.buferReader = new BufferedInputStream(this.process.getInputStream());
			this.buferError = new BufferedInputStream(this.process.getErrorStream());  
			return true;
		}
		catch (IOException e)
		{
			return false;
		}
	}

	/**
	 * 安装tcpdump包
	 * 
	 */
	void installShark()
	{
		String Path = "/data/data/" + getPackageName() + "/files";  //安装地点
		String Name = "tcpdump";  //文件名
		File file = new File(Path,Name);//创建文件
		try{
			InputStream is = getResources().openRawResource(R.raw.tcpdump);//获得安装文件
			if(!file.exists())//如果文件存在
			{
				//通过文件输入输出流拷贝文件
				FileOutputStream fos = openFileOutput(file.getName(), Context.MODE_WORLD_WRITEABLE);
				file.createNewFile();
				int length = is.available();
				byte[] buffer = new byte[length];
				int count = -1;
				while ((count = is.read(buffer)) > 0) {
					fos.write(buffer);
				}
				fos.close();
				is.close();
				return;
			};
		}
		catch(Exception e)  
		{  
		} 
	}	

	/**
	 * 用来获取手机拨号上网（包括CTWAP和CTNET）时由PDSN分配给手机终端的源IP地址。
	 * 
	 */
	public static String getPsdnIp() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (Exception e) {
		}
		return "";
	}

	/**
	 * 定时器任务
	 * 
	 */
	class LockTask extends TimerTask {

		private Context mContext;
		//int i=0;
		List<String> pnames; //锁住的包名
		private ActivityManager mActivityManager;
		private myConfig myconfig;
		MonitorDbManager monitorDbManager;
		LearnDbManager learnDbManager; 
		File file;
		LockTask lockTask=this;
		
		int notChangeNum=0;
		Vector<String> vStrDestIp = new Vector<String>();
		Vector<Long> vLongPacketSize = new Vector<Long>();


		public LockTask(Context context) {
			mContext = context;
			mActivityManager = (ActivityManager) context.getSystemService("activity");
			monitorDbManager = new MonitorDbManager(context);
			learnDbManager = new LearnDbManager(context);
			pnames = monitorDbManager.getAppLockStates();
			myconfig = new myConfig(context);
		}

		@Override
		public void run() {

			/*if(TrafficStats.getTotalTxBytes()==oldnum){
				Log.d("mdw2", "num:"+TrafficStats.getTotalTxBytes());
				return;
			}else{
				//if(TrafficStats.getTotalTxBytes()-oldnum>=20000 && oldnum!=0){
				//	needCut=false;
				//}
				Log.d("mdw2", "num2:"+TrafficStats.getTotalTxBytes());
				oldnum=TrafficStats.getTotalTxBytes();
			}*/

			//停止抓包
			if(isCatchRunning){
				stopCatch();
				isCatchRunning=false;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			//如果没有打开就返回
			if(!myconfig.getPrivacyOpen()){
				//重新开始
				startCatch();
				isCatchRunning=true;
				return;
			}
			
			//获得当前应用
			ComponentName topActivity = mActivityManager.getRunningTasks(1).get(0).topActivity;
			String packageName = topActivity.getPackageName();

			//不是要监测的
			if (!pnames.contains(packageName)) {
				//看看后台
				List<ActivityManager.RunningServiceInfo> runServiceList = mActivityManager.getRunningServices(Integer.MAX_VALUE); 
				boolean find=false;
				for(int i=0;i<runServiceList.size();++i){
					packageName=runServiceList.get(i).service.getPackageName();
					if(pnames.contains(packageName)){
						find=true;
						break;
					}
				}
				if(!find){
					//重新开始
					Log.d("mmm", "没有找到可疑应用");
					startCatch();
					isCatchRunning=true;
					return;
				}
			}

			if(!packnames0.isEmpty())
				packnames0.clear();
			packnames0.add(packageName);
			
			//分析包
			//解析
			//***********************************************  
			file = new File(getSDPath()+"/"+tempFileName);   
			if(file.exists()){
				Log.d("mdw","文件存在！") ;
				Analysize task = new Analysize();  
			    task.execute();
			}else{
				Log.d("mdw","文件不存在！") ;
			}
			//***********************************************

			//重新开始
			startCatch();
			isCatchRunning=true;
		}

		/**
		 * 异步读取
		 */
		
		 //上传通讯录
	    class Analysize extends AsyncTask<Void , Integer, Void> {

	    	@Override
	    	protected Void doInBackground(Void... params) {
	    
	    		Log.d("mdw",file.getAbsolutePath()+" "+file.length());
	    		do{
					Log.d("mdd", "6");
					if(file.length() > 100*1024){//超过100K
						notChangeNum=0;
						break;
					}
					ReadFile readfile = new ReadFile();
					Log.d("mdw", "60");
					List<PacketModel> packets=null;
					try{
					 packets = readfile.readFiletoArraylist(file.getAbsolutePath()) ;
					
					} catch (Exception e){
						Log.d("mdd", file.getAbsolutePath());
						Log.d("mdd", "size："+packets.size());
					}
					Log.d("mdd", "61");
					if(packets.isEmpty()){
						//Log.d("mdd", "62");
						Log.d("mdw","包为空!") ;
						notChangeNum++;
						if(notChangeNum>=10){
							if(!vStrDestIp.isEmpty())
								vStrDestIp.clear();
							if(!vLongPacketSize.isEmpty())	
								vLongPacketSize.clear();
							notChangeNum=0;
						}
						break;
					}else{
						Log.d("mdw","包不为空!") ;
						notChangeNum=0;
					}
			
					//解析每个包
					int count = 0 ;
					while(count < packets.size()){
						PacketModel packet = packets.get(count) ;
						String strDestIp = packet.getIp_destination();
						Long longPacketSize = packet.getCaplen();
						
						Log.d("ss1", packet.getProtocol1());
						Log.d("ss2", packet.getProtocol2());
						
						//添加到向量中
						boolean isExit=false;
						int i;
						for(i=0;i<vStrDestIp.size();++i){
							if(vStrDestIp.get(i).equals(strDestIp)){
								isExit=true;
								break;
							}
						}
						if(isExit){
							Log.d("mdw",""+ longPacketSize);
							vLongPacketSize.set(i,vLongPacketSize.get(i)+longPacketSize);
						}else{
							vStrDestIp.add(strDestIp);
							vLongPacketSize.add(longPacketSize);
						}
						count++;
					}
					//找到最大的
					long maxLongPacketSize = -1;
					int index=-1;
					for(int i=0;i<vLongPacketSize.size();++i){
						if(vLongPacketSize.get(i)>maxLongPacketSize){
							maxLongPacketSize = vLongPacketSize.get(i);
							index=i;
						}
					}
					Log.d("mdw", "cut off");
					if(index!=-1){
						int thradhold= myconfig.getThrshold();
						if(vLongPacketSize.get(index)>thradhold){//Thread
							//关闭网络
							NetworkManager networkManager = new NetworkManager(mContext);
							do{
								try {
									if(networkManager.isWifiConnected()){
										networkManager.toggleWiFi(false);
										showTip( lockTask,mContext,"关闭wifi",
												vStrDestIp.get(index),
												""+thradhold+" B",""+vLongPacketSize.get(index)+" B", myconfig);
										if(!vStrDestIp.isEmpty())
											vStrDestIp.clear();
										if(!vLongPacketSize.isEmpty())	
											vLongPacketSize.clear();
										notChangeNum=0;
										break;

									}
								} catch (Exception e) {
									Log.d("mdw","关闭wifi出错");
								}
								try {
									if(networkManager.isMobileConnected()){
										networkManager.toggleGprs(false);
										showTip( lockTask,mContext,"关闭GPRS",
												vStrDestIp.get(index),
												""+thradhold+" B",""+vLongPacketSize.get(index)+" B",myconfig);
										if(!vStrDestIp.isEmpty())
											vStrDestIp.clear();
										if(!vLongPacketSize.isEmpty())	
											vLongPacketSize.clear();
										notChangeNum=0;
										break;
									}
								} catch (Exception e) {
									Log.d("mdw","关闭GPRS出错");
								}
								try {
									if(!networkManager.isAirplaneModeOn())
										networkManager.toggleAirplaneMode(true);
									showTip(lockTask,mContext,"打开飞行模式",
											vStrDestIp.get(index),
											""+thradhold+" B",""+vLongPacketSize.get(index)+" B", myconfig);
									if(!vStrDestIp.isEmpty())
										vStrDestIp.clear();
									if(!vLongPacketSize.isEmpty())	
										vLongPacketSize.clear();
									notChangeNum=0;
								} catch (Exception e) {
									Log.d("mdw","打开飞行模式出错");
								}
							}while(false);
						}
					}

				}while(false);
				return null;

				//删除文件
				//if(file.exists()){
				//	Log.d("mdw","deleteFile"+file.getName());
				//file.delete();
				//}
	    	}

	    	protected void onProgressUpdate(Integer... progress) {//在调用publishProgress之后被调用，在ui线程执行  
	    		if(isCancelled()){ 
	    			return;
	    		}
	         }  
	    	
	    	protected void onPostExecute() {//后台任务执行完之后被调用，在ui线程执行  
	           
	        }  
	    	
	    	protected void onPreExecute () {//在 doInBackground(Params...)之前被调用，在ui线程执行  
	       
	        }  
	          
	        protected void onCancelled () {//在ui线程执行  
	    
	        }  
	    }
	    
	    
	    /**
		 * 返回可疑对象
		 * 
		 */
		List<String> packnames0=new ArrayList<String>();
		public String getAppnames(String info_app_ip){
			String allAppnames="";
			//获得前台的应用
			ComponentName topActivity = mActivityManager.getRunningTasks(1).get(0).topActivity;
			String packageName = topActivity.getPackageName();
			packnames0.add(packageName);
			//获得后台的应用
			List<ActivityManager.RunningServiceInfo> runServiceList = mActivityManager.getRunningServices(Integer.MAX_VALUE); 
			for(int i=0;i<runServiceList.size();++i){
				packageName=runServiceList.get(i).service.getPackageName();
				if(!packnames0.contains(packageName)){
					packnames0.add(packageName);
				}
			}

			//找到非系统应用,并去掉自身
			PackageManager pManager = getPackageManager(); 
			List<String> packnames1= new ArrayList<String>();
			for(int i=0;i<packnames0.size();++i){
				packageName=packnames0.get(i);
				if(packageName.equals("com.example.activity"))//不包括自身
					continue;
				try {
					ApplicationInfo AI = pManager.getApplicationInfo(packageName, 
							PackageManager.GET_META_DATA | PackageManager.GET_SHARED_LIBRARY_FILES);
					if ((AI.flags & ApplicationInfo.FLAG_SYSTEM) == 0){ //非系统用户
						packnames1.add(packageName);
					}
				} catch (NameNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			//找到具有权限的系统应用
			List<String> packnames2= new ArrayList<String>();
			for(int i=0;i<packnames1.size();++i){
				packageName=packnames1.get(i);
				try {
					String[] quanxians=pManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS).requestedPermissions;

					boolean isAcessNet=false;
					boolean isPrivacy=false;
					for(int j=0;j<quanxians.length;++j){
						if(quanxians[j].equals("android.permission.INTERNET")){
							isAcessNet=true;
						}
						if(quanxians[j].equals("android.permission.READ_CONTACTS")){
							isPrivacy=true;
						}
						if(quanxians[j].equals("android.permission.READ_SMS")){
							isPrivacy=true;
						}
						if(quanxians[j].equals("android.permission.READ_CALL_LOG")){
							isPrivacy=true;
						}
						if(quanxians[j].equals("android.permission.READ_LOGS")){
							isPrivacy=true;
						}
						if(quanxians[j].equals("android.permission.READ_CALENDAR")){
							isPrivacy=true;
						}
						if(quanxians[j].equals("com.android.browser.permission.READ_HISTORY_BOOKMARKS")){
							isPrivacy=true;
						}
					}
					if(isAcessNet){//可以访问网络
						if(isPrivacy){//涉及到隐私
							packnames2.add(packageName);
						}
					}
				} catch (NameNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			//看看是否已经有已经知道的对应关系
			List<String> packnames3= new ArrayList<String>();
			int pos=info_app_ip.lastIndexOf(".");
			String str=info_app_ip.substring(0,pos);
			List<String> learnedPackageNames=learnDbManager.getPackageNameByIP(str);
			for(int i=0;i<packnames2.size();++i){
				packageName=packnames2.get(i);
				if(pnames.contains(packageName)){//不是信任的
					if(learnedPackageNames.size()>0){//如果已经有知识了
						if(learnedPackageNames.contains(packageName))//符合
							if(!packnames3.contains(packageName))
								packnames3.add(packageName);
					}else{
						if(!packnames3.contains(packageName))
							packnames3.add(packageName);
					}
				}
			}


			if(packnames3.size()==0){
				packnames3=packnames2;
			}

			if(packnames3.size()==1){//学习
				if(!learnedPackageNames.contains(packnames3.get(0))){
					int pos2=info_app_ip.lastIndexOf(".");
					String str2=info_app_ip.substring(0,pos2);
					Log.d("add", str2+""+packnames3.get(0));
					learnDbManager.AddDate(str2,packnames3.get(0));
				}
			}

			ApplicationInfo AI;
			boolean isAdded=false;
			List<String> packnames4= new ArrayList<String>();
			for(int i=0;i<packnames3.size();++i){
				try {
					AI = pManager.getApplicationInfo(packnames3.get(i), 
							PackageManager.GET_META_DATA | PackageManager.GET_SHARED_LIBRARY_FILES);
					if(packnames4.contains(pManager.getApplicationLabel(AI).toString()))
						continue;
					if(isAdded)
						allAppnames+="\n";
					else
						isAdded=true;
					allAppnames += pManager.getApplicationLabel(AI).toString();
					//allAppnames += " "+TrafficStats.getUidTxBytes(AI.uid);
					packnames4.add(pManager.getApplicationLabel(AI).toString());
				} catch (NameNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			Log.d("mdw2", "retuern ok");
			return allAppnames;
		}
	    
	    
	    
	}
	
	
	
	

	/**
	 * 显示信息
	 * 
	 */
	public void showTip(LockTask lockTask,Context context,String strAction,
			String info_app_ip,String info_thread_size,String info_max_size, myConfig myconfig){


		//获得内容
		String appname=lockTask.getAppnames(info_app_ip);
		Log.d("mdw",strAction);
		String text=strAction+"："+appname+"短时间内上传较多数据!";

		//存到数据库中 
		Date date=new java.util.Date(System.currentTimeMillis());
		String dateStr=""+(date.getYear()+1900)+"-";
		if(date.getMonth()+1<10)
			dateStr+="0";
		dateStr+=(date.getMonth()+1)+"-";
		if(date.getDate()<10)
			dateStr+="0";
		dateStr+=date.getDate();

		String time="";
		if(date.getHours()<10)
			time+="0";
		time+=date.getHours()+":";
		if(date.getMinutes()<10)
			time+="0";
		time+=date.getMinutes()+":";
		if(date.getSeconds()<10)
			time+="0";
		time+=date.getSeconds();

		myconfig.setinfo_data(dateStr);
		myconfig.setinfo_time(time);
		myconfig.setinfo_way(strAction);
		myconfig.setinfo_app(appname);
		myconfig.setinfo_app_ip(info_app_ip);
		myconfig.setinfo_thread_size(info_thread_size);
		myconfig.setinfo_max_size(info_max_size);
		myconfig.setSaveState(true);

		//声明通知（消息）管理器
		NotificationManager	m_NotificationManager;
		Intent				m_Intent;
		PendingIntent		m_PendingIntent;
		//声明Notification对象
		Notification		m_Notification;

		//初始化NotificationManager对象
		m_NotificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

		//点击通知时转移内容
		m_Intent = new Intent(context,ShowMonitorLog.class);
		//主要是设置点击通知时显示内容的类
		m_PendingIntent = PendingIntent.getActivity(context, 0, m_Intent, 0);
		//构造Notification对象
		m_Notification = new Notification();

		//设置通知在状态栏显示的图标
		m_Notification.icon = R.drawable.ic_launcher;
		//当我们点击通知时显示的内容
		m_Notification.tickerText = "隐私卫士监测信息........";
		//通知时发出默认的声音
		m_Notification.defaults = Notification.DEFAULT_SOUND;
		//设置通知显示的参数
		m_Notification.setLatestEventInfo(context, "点击查看报告", text, m_PendingIntent);
		//
		m_Notification.flags=Notification.FLAG_AUTO_CANCEL;
		//可以理解为执行这个通知
		m_NotificationManager.notify(0, m_Notification);	
	}
	
	
	

}
