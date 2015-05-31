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
				.equals(android.os.Environment.MEDIA_MOUNTED);   //�ж�sd���Ƿ���� 
		if   (sdCardExist)   
		{                               
			sdDir = Environment.getExternalStorageDirectory();//��ȡ��Ŀ¼ 
		}   
		return sdDir.toString(); 

	}

	//String fileName = getSDPath() +"/" + name;//��name����Ŀ¼��
	/**
	 * Ӧ�ó������仯ʱ���ô˺���������service�����ݿ�
	 */
	void  changeDate(){
		try{
			lockTask.pnames=lockTask.monitorDbManager.getAppLockStates();
		}catch(Exception e){
		}
	}

	// ʵ�����Զ����Binder��  
	private final IBinder mBinder = new LocalBinder();

	/** 
	 * �Զ����Binder�࣬�����һ���ڲ��࣬���Կ���֪������Χ��Ķ���ͨ������࣬��Activity֪����Service�Ķ��� 
	 */  
	public class LocalBinder extends Binder {  
		MonitorService getService() {  
			// ����Activity��������Service����������Activity��Ϳɵ���Service���һЩ���÷����͹�������  
			return MonitorService.this;  
		}  
	}  

	/**
	 * ����ʵ�ֵķ���
	 * 
	 */
	@Override
	public IBinder onBind(Intent arg0)
	{
		return  mBinder;
	}

	/**
	 * Service������ʱ�ص��÷�����
	 * 
	 */
	@Override
	public void onCreate()
	{
		super.onCreate();
		//root
		if(!getRoot())
			Log.d("mdw","root falied");
		//��װtcpdump��
		installShark();
		Log.d("mdw","Service is Created");
	}

	/**
	 * Service������ʱ�ص��÷���
	 * 
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		Log.d("mdw","Service is Started");
		//service�Ѿ���
		myConfig myconfig=new myConfig(this);
		myconfig.setPrivacyOpen(true);
		//��ʼץ��
		startCatch();
		isCatchRunning=true;
		startTimer();
		return START_STICKY;
	}

	/**
	 * Service���ر�֮ǰ�ص���
	 * 
	 */
	@Override
	public void onDestroy()
	{
		//service�Ѿ��ر�
		myConfig myconfig=new myConfig(this);
		myconfig.setPrivacyOpen(false);

		
		//ֹͣץ��
		if(isCatchRunning){
			stopCatch();
			isCatchRunning=false;
		}

		//ȡ����ʱ����
		if (mTimer != null) {
			mTimer.cancel();
			mTimer.purge();
			mTimer = null;
		}

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
		super.onDestroy();
	}

	/**
	 * �򿪶�ʱ��
	 * 
	 */
	private void startTimer() {
		if (mTimer == null) {
			mTimer = new Timer();
			lockTask = new LockTask(this);
			mTimer.schedule(lockTask,0,3000L);//ÿ��3��
		}
	}

	/**
	 * ֹͣץ��
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
			Log.d("mdw","ֹͣץ��");
			return;
		}
		catch (IOException e)
		{
			Log.d("mdw","�Ƿ�ROOT��");
		}
	}

	/**
	 * ��ʼץ��
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
			Log.d("mdw","��ʼץ��");
			Log.d("mdw","createFile:"+tempFileName);
			return;
		}
		catch (IOException e)
		{
			Log.d("mdw","�Ƿ�ROOT��");
		}
	}

	/**
	 * ����Ȩ��
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
	 * ��װtcpdump��
	 * 
	 */
	void installShark()
	{
		String Path = "/data/data/" + getPackageName() + "/files";  //��װ�ص�
		String Name = "tcpdump";  //�ļ���
		File file = new File(Path,Name);//�����ļ�
		try{
			InputStream is = getResources().openRawResource(R.raw.tcpdump);//��ð�װ�ļ�
			if(!file.exists())//����ļ�����
			{
				//ͨ���ļ���������������ļ�
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
	 * ������ȡ�ֻ���������������CTWAP��CTNET��ʱ��PDSN������ֻ��ն˵�ԴIP��ַ��
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
	 * ��ʱ������
	 * 
	 */
	class LockTask extends TimerTask {

		private Context mContext;
		//int i=0;
		List<String> pnames; //��ס�İ���
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

			//ֹͣץ��
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
			
			//���û�д򿪾ͷ���
			if(!myconfig.getPrivacyOpen()){
				//���¿�ʼ
				startCatch();
				isCatchRunning=true;
				return;
			}
			
			//��õ�ǰӦ��
			ComponentName topActivity = mActivityManager.getRunningTasks(1).get(0).topActivity;
			String packageName = topActivity.getPackageName();

			//����Ҫ����
			if (!pnames.contains(packageName)) {
				//������̨
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
					//���¿�ʼ
					Log.d("mmm", "û���ҵ�����Ӧ��");
					startCatch();
					isCatchRunning=true;
					return;
				}
			}

			if(!packnames0.isEmpty())
				packnames0.clear();
			packnames0.add(packageName);
			
			//������
			//����
			//***********************************************  
			file = new File(getSDPath()+"/"+tempFileName);   
			if(file.exists()){
				Log.d("mdw","�ļ����ڣ�") ;
				Analysize task = new Analysize();  
			    task.execute();
			}else{
				Log.d("mdw","�ļ������ڣ�") ;
			}
			//***********************************************

			//���¿�ʼ
			startCatch();
			isCatchRunning=true;
		}

		/**
		 * �첽��ȡ
		 */
		
		 //�ϴ�ͨѶ¼
	    class Analysize extends AsyncTask<Void , Integer, Void> {

	    	@Override
	    	protected Void doInBackground(Void... params) {
	    
	    		Log.d("mdw",file.getAbsolutePath()+" "+file.length());
	    		do{
					Log.d("mdd", "6");
					if(file.length() > 100*1024){//����100K
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
						Log.d("mdd", "size��"+packets.size());
					}
					Log.d("mdd", "61");
					if(packets.isEmpty()){
						//Log.d("mdd", "62");
						Log.d("mdw","��Ϊ��!") ;
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
						Log.d("mdw","����Ϊ��!") ;
						notChangeNum=0;
					}
			
					//����ÿ����
					int count = 0 ;
					while(count < packets.size()){
						PacketModel packet = packets.get(count) ;
						String strDestIp = packet.getIp_destination();
						Long longPacketSize = packet.getCaplen();
						
						Log.d("ss1", packet.getProtocol1());
						Log.d("ss2", packet.getProtocol2());
						
						//��ӵ�������
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
					//�ҵ�����
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
							//�ر�����
							NetworkManager networkManager = new NetworkManager(mContext);
							do{
								try {
									if(networkManager.isWifiConnected()){
										networkManager.toggleWiFi(false);
										showTip( lockTask,mContext,"�ر�wifi",
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
									Log.d("mdw","�ر�wifi����");
								}
								try {
									if(networkManager.isMobileConnected()){
										networkManager.toggleGprs(false);
										showTip( lockTask,mContext,"�ر�GPRS",
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
									Log.d("mdw","�ر�GPRS����");
								}
								try {
									if(!networkManager.isAirplaneModeOn())
										networkManager.toggleAirplaneMode(true);
									showTip(lockTask,mContext,"�򿪷���ģʽ",
											vStrDestIp.get(index),
											""+thradhold+" B",""+vLongPacketSize.get(index)+" B", myconfig);
									if(!vStrDestIp.isEmpty())
										vStrDestIp.clear();
									if(!vLongPacketSize.isEmpty())	
										vLongPacketSize.clear();
									notChangeNum=0;
								} catch (Exception e) {
									Log.d("mdw","�򿪷���ģʽ����");
								}
							}while(false);
						}
					}

				}while(false);
				return null;

				//ɾ���ļ�
				//if(file.exists()){
				//	Log.d("mdw","deleteFile"+file.getName());
				//file.delete();
				//}
	    	}

	    	protected void onProgressUpdate(Integer... progress) {//�ڵ���publishProgress֮�󱻵��ã���ui�߳�ִ��  
	    		if(isCancelled()){ 
	    			return;
	    		}
	         }  
	    	
	    	protected void onPostExecute() {//��̨����ִ����֮�󱻵��ã���ui�߳�ִ��  
	           
	        }  
	    	
	    	protected void onPreExecute () {//�� doInBackground(Params...)֮ǰ�����ã���ui�߳�ִ��  
	       
	        }  
	          
	        protected void onCancelled () {//��ui�߳�ִ��  
	    
	        }  
	    }
	    
	    
	    /**
		 * ���ؿ��ɶ���
		 * 
		 */
		List<String> packnames0=new ArrayList<String>();
		public String getAppnames(String info_app_ip){
			String allAppnames="";
			//���ǰ̨��Ӧ��
			ComponentName topActivity = mActivityManager.getRunningTasks(1).get(0).topActivity;
			String packageName = topActivity.getPackageName();
			packnames0.add(packageName);
			//��ú�̨��Ӧ��
			List<ActivityManager.RunningServiceInfo> runServiceList = mActivityManager.getRunningServices(Integer.MAX_VALUE); 
			for(int i=0;i<runServiceList.size();++i){
				packageName=runServiceList.get(i).service.getPackageName();
				if(!packnames0.contains(packageName)){
					packnames0.add(packageName);
				}
			}

			//�ҵ���ϵͳӦ��,��ȥ������
			PackageManager pManager = getPackageManager(); 
			List<String> packnames1= new ArrayList<String>();
			for(int i=0;i<packnames0.size();++i){
				packageName=packnames0.get(i);
				if(packageName.equals("com.example.activity"))//����������
					continue;
				try {
					ApplicationInfo AI = pManager.getApplicationInfo(packageName, 
							PackageManager.GET_META_DATA | PackageManager.GET_SHARED_LIBRARY_FILES);
					if ((AI.flags & ApplicationInfo.FLAG_SYSTEM) == 0){ //��ϵͳ�û�
						packnames1.add(packageName);
					}
				} catch (NameNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			//�ҵ�����Ȩ�޵�ϵͳӦ��
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
					if(isAcessNet){//���Է�������
						if(isPrivacy){//�漰����˽
							packnames2.add(packageName);
						}
					}
				} catch (NameNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			//�����Ƿ��Ѿ����Ѿ�֪���Ķ�Ӧ��ϵ
			List<String> packnames3= new ArrayList<String>();
			int pos=info_app_ip.lastIndexOf(".");
			String str=info_app_ip.substring(0,pos);
			List<String> learnedPackageNames=learnDbManager.getPackageNameByIP(str);
			for(int i=0;i<packnames2.size();++i){
				packageName=packnames2.get(i);
				if(pnames.contains(packageName)){//�������ε�
					if(learnedPackageNames.size()>0){//����Ѿ���֪ʶ��
						if(learnedPackageNames.contains(packageName))//����
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

			if(packnames3.size()==1){//ѧϰ
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
	 * ��ʾ��Ϣ
	 * 
	 */
	public void showTip(LockTask lockTask,Context context,String strAction,
			String info_app_ip,String info_thread_size,String info_max_size, myConfig myconfig){


		//�������
		String appname=lockTask.getAppnames(info_app_ip);
		Log.d("mdw",strAction);
		String text=strAction+"��"+appname+"��ʱ�����ϴ��϶�����!";

		//�浽���ݿ��� 
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

		//����֪ͨ����Ϣ��������
		NotificationManager	m_NotificationManager;
		Intent				m_Intent;
		PendingIntent		m_PendingIntent;
		//����Notification����
		Notification		m_Notification;

		//��ʼ��NotificationManager����
		m_NotificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

		//���֪ͨʱת������
		m_Intent = new Intent(context,ShowMonitorLog.class);
		//��Ҫ�����õ��֪ͨʱ��ʾ���ݵ���
		m_PendingIntent = PendingIntent.getActivity(context, 0, m_Intent, 0);
		//����Notification����
		m_Notification = new Notification();

		//����֪ͨ��״̬����ʾ��ͼ��
		m_Notification.icon = R.drawable.ic_launcher;
		//�����ǵ��֪ͨʱ��ʾ������
		m_Notification.tickerText = "��˽��ʿ�����Ϣ........";
		//֪ͨʱ����Ĭ�ϵ�����
		m_Notification.defaults = Notification.DEFAULT_SOUND;
		//����֪ͨ��ʾ�Ĳ���
		m_Notification.setLatestEventInfo(context, "����鿴����", text, m_PendingIntent);
		//
		m_Notification.flags=Notification.FLAG_AUTO_CANCEL;
		//�������Ϊִ�����֪ͨ
		m_NotificationManager.notify(0, m_Notification);	
	}
	
	
	

}
