package com.example.config;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import org.apache.commons.codec.binary.Base64;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class myConfig {
	//参数名
	private final String configs="configs";
	private final String lockDbint="lockDbint";//手机锁数据库有没有打开
	private final String isLockOpen="isLockOpen";//判断手机锁是否已经打开
	private final String contactip="contactIp";//上传通讯录的Ip
	private final String isPrivacyOpen="isPrivacyOpen";//判断上传监测是否打开
	private final String monitorDbint="monitorDbint";//上传监测库有没有打开
	private final String logDbint="logDbint";//日志数据库有没有打开
	private final String learnDbint="learnDbintt";//学习数据库有没有打开
	private final String info_data="info_data";//上传监测报告日期
	private final String info_time="info_time";//上传监测报告时间
	private final String info_way="info_way";//上传监测报告采取手段
	private final String info_app="info_app";//上传监测目标程序
	private final String info_app_ip="info_app_ip";//上传监测报目的IP地址
	private final String info_thread_size="info_thread_size";//上传监测阈值
	private final String info_max_size="info_max_size";//上传监测报告最大包大小
	private final String saveState="saveState";//save按钮是否可见
	private final String thrshold="thrshold";//阈值
	private final String running="running";//运行
	
	//环境变量
	private Context context;

	//初始化函数
	public myConfig(Context context){
		this.context=context;
	}

	//读取Applock数据库有没有初始化
	public boolean isLockDbinit(){
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.configs, Context.MODE_PRIVATE);
		boolean isInit=sharedPreferences.getBoolean(this.lockDbint,false);
		if(!isInit){//没有的话初始化
			Editor editor = sharedPreferences.edit();//获取编辑器
			editor.putBoolean(this.lockDbint, true);
			editor.putBoolean(this.isLockOpen, false);//开始时锁是关闭的
			editor.commit();//提交修改
		}
		Log.d("isLockDbinit",""+isInit);
		return isInit;
	}

	//读取Log数据库有没有初始化
	public boolean isLearnDbint() {
		// TODO Auto-generated method stub
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.configs, Context.MODE_PRIVATE);
		boolean isInit=sharedPreferences.getBoolean(this.learnDbint,false);
		if(!isInit){//没有的话初始化
			Editor editor = sharedPreferences.edit();//获取编辑器
			editor.putBoolean(this.learnDbint, true);
			editor.commit();//提交修改
		}
		Log.d("isLearnDbint",""+isInit);
		return isInit;
	}

	//读取Log数据库有没有初始化
	public boolean isLogDbinit() {
		// TODO Auto-generated method stub
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.configs, Context.MODE_PRIVATE);
		boolean isInit=sharedPreferences.getBoolean(this.logDbint,false);
		if(!isInit){//没有的话初始化
			Editor editor = sharedPreferences.edit();//获取编辑器
			editor.putBoolean(this.logDbint, true);
			editor.commit();//提交修改
		}
		Log.d("isPrivacyDbinit",""+isInit);
		return isInit;
	}

	//读取Monitor数据库有没有初始化
	public boolean isPrivacyDbinit() {
		// TODO Auto-generated method stub
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.configs, Context.MODE_PRIVATE);
		boolean isInit=sharedPreferences.getBoolean(this.monitorDbint,false);
		if(!isInit){//没有的话初始化
			Editor editor = sharedPreferences.edit();//获取编辑器
			editor.putBoolean(this.monitorDbint, true);
			editor.putBoolean(this.isPrivacyOpen, false);//开始时锁是关闭的
			editor.commit();//提交修改
		}
		Log.d("isPrivacyDbinit",""+isInit);
		return isInit;
	}
	
	
	/**
	 * 获得阈值
	 * @return
	 */
	public int getThrshold(){
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.configs, Context.MODE_PRIVATE);
		return sharedPreferences.getInt(this.thrshold,700);		
	}
	/**
	 * 设置阈值
	 * @param state
	 */
	public void setThrshold(int val){
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.configs, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();//获取编辑器
		editor.putInt(this.thrshold, val);
		editor.commit();//提交修改
	}

	/**
	 * 获得save按钮的状态
	 * @return
	 */
	public boolean getSaveState(){
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.configs, Context.MODE_PRIVATE);
		return sharedPreferences.getBoolean(this.saveState,true);		
	}
	/**
	 * 设置save按钮的状态
	 * @param state
	 */
	public void setSaveState(boolean state){
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.configs, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();//获取编辑器
		editor.putBoolean(this.saveState, state);
		editor.commit();//提交修改
	}

	/**
	 * 获得IP
	 * @return
	 */
	public String getIp(){
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.configs, Context.MODE_PRIVATE);
		return sharedPreferences.getString(this.contactip,"10.210.107.174");		
	}
	/**
	 * 设置IP
	 * @param state
	 */
	public void setIp(String ip){
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.configs, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();//获取编辑器
		editor.putString(this.contactip, ip);//开始时锁是关闭的
		editor.commit();//提交修改
	}
	/**
	 * 获得Password
	 * @return
	 */
	public String getPassword(){
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.configs, Context.MODE_PRIVATE);
		return sharedPreferences.getString("password","123");		
	}
	/**
	 * 设置Password
	 * @param state
	 */
	public void setPasswordp(String password){
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.configs, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();//获取编辑器
		editor.putString("password",password);//开始时锁是关闭的
		editor.commit();//提交修改
	}

	/**
	 * 获得文件状态
	 * @return
	 */
	public boolean getIsJiaMi(String filename){
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.configs, Context.MODE_PRIVATE);
		String getback=sharedPreferences.getString(filename,"");	
		if(getback.equals(""))
			return false;
		else
			return true;
	}

	/**
	 * 读privatekey对象
	 */
	public PrivateKey getPrivateKey(String filename) {
		PrivateKey privateKey=null;
		SharedPreferences preferences = context.getSharedPreferences("base64",Context.MODE_PRIVATE);
		String privateKeyBase64 = preferences.getString(filename, "");
		//Log.d("get",filename+"　;　"+privateKeyBase64 );
		if (privateKeyBase64 == "") {
			return null;
		}
		//读取字节
		byte[] base64 = Base64.decodeBase64(privateKeyBase64.getBytes());
		KeyFactory keyFactory;
		try {
			keyFactory = KeyFactory.getInstance("RSA");
			PKCS8EncodedKeySpec pKCS8EncodedKeySpec =new PKCS8EncodedKeySpec(base64);
			privateKey = keyFactory.generatePrivate(pKCS8EncodedKeySpec);
			//Log.d("get",filename+"　;　"+privateKey.toString() );
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // 指定算法RSA,得到一个KeyFactory的实例


		return privateKey;
	}
	/**
	 * 设置privatekey对象
	 */
	public void savePrivateKey(String filename,PrivateKey privateKey) {
		SharedPreferences preferences = context.getSharedPreferences("base64",Context.MODE_PRIVATE);
		//将字节流编码成base64的字符窜
		String privateKeyBase64 = new String(Base64.encodeBase64(privateKey.getEncoded()));
		Editor editor = preferences.edit();
		editor.putString(filename, privateKeyBase64);
		editor.commit();

	}
	/**
	 * 获得程序锁的状态
	 * @return
	 */
	public boolean getLockOpen(){
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.configs, Context.MODE_PRIVATE);
		return sharedPreferences.getBoolean(this.isLockOpen,false);		
	}
	/**
	 * 设置程序锁的状态
	 * @param state
	 */
	public void setIsLockOpen(boolean state){
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.configs, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();//获取编辑器
		editor.putBoolean(this.isLockOpen, state);//开始时锁是关闭的
		editor.commit();//提交修改
	}
	/**
	 * 获得上传检测的状态
	 * @return
	 */
	public boolean getPrivacyOpen() {
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.configs, Context.MODE_PRIVATE);
		return sharedPreferences.getBoolean(this.isPrivacyOpen,false);		
	}
	/**
	 * 设置运行状态
	 * @param state
	 */
	public void setRunning(boolean state){
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.configs, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();//获取编辑器
		editor.putBoolean(this.running, state);//开始时锁是关闭的
		editor.commit();//提交修改
	}
	
	/**
	 * 获得运行状态
	 * @return
	 */
	public boolean getRunning() {
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.configs, Context.MODE_PRIVATE);
		return sharedPreferences.getBoolean(this.running,false);		
	}
	/**
	 * 设置上传检测的状态
	 * @param state
	 */
	public void setPrivacyOpen(boolean state){
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.configs, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();//获取编辑器
		editor.putBoolean(this.isPrivacyOpen, state);//开始时锁是关闭的
		editor.commit();//提交修改
	}
	

	/**
	 * 获得info_data
	 * @return
	 */
	public String getinfo_data(){
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.configs, Context.MODE_PRIVATE);
		return sharedPreferences.getString(this.info_data,"未知");		
	}
	/**
	 * 设置info_data
	 * @param state
	 */
	public void setinfo_data(String info_data){
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.configs, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();//获取编辑器
		editor.putString(this.info_data, info_data);
		editor.commit();//提交修改
	}

	/**
	 * 获得info_time
	 * @return
	 */
	public String getinfo_time(){
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.configs, Context.MODE_PRIVATE);
		return sharedPreferences.getString(this.info_time,"未知");		
	}
	/**
	 * 设置info_time
	 * @param state
	 */
	public void setinfo_time(String info_time){
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.configs, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();//获取编辑器
		editor.putString(this.info_time, info_time);
		editor.commit();//提交修改
	}

	/**
	 * 获得info_way
	 * @return
	 */
	public String getinfo_way(){
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.configs, Context.MODE_PRIVATE);
		return sharedPreferences.getString(this.info_way,"未知");		
	}
	/**
	 * 设置info_way
	 * @param state
	 */
	public void setinfo_way(String info_way){
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.configs, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();//获取编辑器
		editor.putString(this.info_way, info_way);
		editor.commit();//提交修改
	}

	/**
	 * 获得info_app
	 * @return
	 */
	public String getinfo_app(){
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.configs, Context.MODE_PRIVATE);
		return sharedPreferences.getString(this.info_app,"未知");		
	}
	/**
	 * 设置info_app
	 * @param state
	 */
	public void setinfo_app(String info_app){
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.configs, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();//获取编辑器
		editor.putString(this.info_app, info_app);
		editor.commit();//提交修改
	}

	/**
	 * 获得info_app_ip
	 * @return
	 */
	public String getinfo_app_ip(){
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.configs, Context.MODE_PRIVATE);
		return sharedPreferences.getString(this.info_app_ip,"未知");		
	}
	/**
	 * 设置info_app_ip
	 * @param state
	 */
	public void setinfo_app_ip(String info_app_ip){
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.configs, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();//获取编辑器
		editor.putString(this.info_app_ip, info_app_ip);
		editor.commit();//提交修改
	}

	/**
	 * 获得info_thread_size
	 * @return
	 */
	public String getinfo_thread_size(){
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.configs, Context.MODE_PRIVATE);
		return sharedPreferences.getString(this.info_thread_size,"未知");		
	}
	/**
	 * 设置info_thread_size
	 * @param state
	 */
	public void setinfo_thread_size(String info_thread_size){
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.configs, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();//获取编辑器
		editor.putString(this.info_thread_size, info_thread_size);
		editor.commit();//提交修改
	}

	/**
	 * 获得info_max_size
	 * @return
	 */
	public String getinfo_max_size(){
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.configs, Context.MODE_PRIVATE);
		return sharedPreferences.getString(this.info_max_size,"未知");		
	}
	/**
	 * 设置info_max_size
	 * @param state
	 */
	public void setinfo_max_size(String info_max_size){
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.configs, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();//获取编辑器
		editor.putString(this.info_max_size, info_max_size);
		editor.commit();//提交修改
	}

}
