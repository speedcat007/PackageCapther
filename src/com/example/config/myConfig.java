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
	//������
	private final String configs="configs";
	private final String lockDbint="lockDbint";//�ֻ������ݿ���û�д�
	private final String isLockOpen="isLockOpen";//�ж��ֻ����Ƿ��Ѿ���
	private final String contactip="contactIp";//�ϴ�ͨѶ¼��Ip
	private final String isPrivacyOpen="isPrivacyOpen";//�ж��ϴ�����Ƿ��
	private final String monitorDbint="monitorDbint";//�ϴ�������û�д�
	private final String logDbint="logDbint";//��־���ݿ���û�д�
	private final String learnDbint="learnDbintt";//ѧϰ���ݿ���û�д�
	private final String info_data="info_data";//�ϴ���ⱨ������
	private final String info_time="info_time";//�ϴ���ⱨ��ʱ��
	private final String info_way="info_way";//�ϴ���ⱨ���ȡ�ֶ�
	private final String info_app="info_app";//�ϴ����Ŀ�����
	private final String info_app_ip="info_app_ip";//�ϴ���ⱨĿ��IP��ַ
	private final String info_thread_size="info_thread_size";//�ϴ������ֵ
	private final String info_max_size="info_max_size";//�ϴ���ⱨ��������С
	private final String saveState="saveState";//save��ť�Ƿ�ɼ�
	private final String thrshold="thrshold";//��ֵ
	private final String running="running";//����
	
	//��������
	private Context context;

	//��ʼ������
	public myConfig(Context context){
		this.context=context;
	}

	//��ȡApplock���ݿ���û�г�ʼ��
	public boolean isLockDbinit(){
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.configs, Context.MODE_PRIVATE);
		boolean isInit=sharedPreferences.getBoolean(this.lockDbint,false);
		if(!isInit){//û�еĻ���ʼ��
			Editor editor = sharedPreferences.edit();//��ȡ�༭��
			editor.putBoolean(this.lockDbint, true);
			editor.putBoolean(this.isLockOpen, false);//��ʼʱ���ǹرյ�
			editor.commit();//�ύ�޸�
		}
		Log.d("isLockDbinit",""+isInit);
		return isInit;
	}

	//��ȡLog���ݿ���û�г�ʼ��
	public boolean isLearnDbint() {
		// TODO Auto-generated method stub
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.configs, Context.MODE_PRIVATE);
		boolean isInit=sharedPreferences.getBoolean(this.learnDbint,false);
		if(!isInit){//û�еĻ���ʼ��
			Editor editor = sharedPreferences.edit();//��ȡ�༭��
			editor.putBoolean(this.learnDbint, true);
			editor.commit();//�ύ�޸�
		}
		Log.d("isLearnDbint",""+isInit);
		return isInit;
	}

	//��ȡLog���ݿ���û�г�ʼ��
	public boolean isLogDbinit() {
		// TODO Auto-generated method stub
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.configs, Context.MODE_PRIVATE);
		boolean isInit=sharedPreferences.getBoolean(this.logDbint,false);
		if(!isInit){//û�еĻ���ʼ��
			Editor editor = sharedPreferences.edit();//��ȡ�༭��
			editor.putBoolean(this.logDbint, true);
			editor.commit();//�ύ�޸�
		}
		Log.d("isPrivacyDbinit",""+isInit);
		return isInit;
	}

	//��ȡMonitor���ݿ���û�г�ʼ��
	public boolean isPrivacyDbinit() {
		// TODO Auto-generated method stub
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.configs, Context.MODE_PRIVATE);
		boolean isInit=sharedPreferences.getBoolean(this.monitorDbint,false);
		if(!isInit){//û�еĻ���ʼ��
			Editor editor = sharedPreferences.edit();//��ȡ�༭��
			editor.putBoolean(this.monitorDbint, true);
			editor.putBoolean(this.isPrivacyOpen, false);//��ʼʱ���ǹرյ�
			editor.commit();//�ύ�޸�
		}
		Log.d("isPrivacyDbinit",""+isInit);
		return isInit;
	}
	
	
	/**
	 * �����ֵ
	 * @return
	 */
	public int getThrshold(){
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.configs, Context.MODE_PRIVATE);
		return sharedPreferences.getInt(this.thrshold,700);		
	}
	/**
	 * ������ֵ
	 * @param state
	 */
	public void setThrshold(int val){
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.configs, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();//��ȡ�༭��
		editor.putInt(this.thrshold, val);
		editor.commit();//�ύ�޸�
	}

	/**
	 * ���save��ť��״̬
	 * @return
	 */
	public boolean getSaveState(){
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.configs, Context.MODE_PRIVATE);
		return sharedPreferences.getBoolean(this.saveState,true);		
	}
	/**
	 * ����save��ť��״̬
	 * @param state
	 */
	public void setSaveState(boolean state){
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.configs, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();//��ȡ�༭��
		editor.putBoolean(this.saveState, state);
		editor.commit();//�ύ�޸�
	}

	/**
	 * ���IP
	 * @return
	 */
	public String getIp(){
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.configs, Context.MODE_PRIVATE);
		return sharedPreferences.getString(this.contactip,"10.210.107.174");		
	}
	/**
	 * ����IP
	 * @param state
	 */
	public void setIp(String ip){
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.configs, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();//��ȡ�༭��
		editor.putString(this.contactip, ip);//��ʼʱ���ǹرյ�
		editor.commit();//�ύ�޸�
	}
	/**
	 * ���Password
	 * @return
	 */
	public String getPassword(){
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.configs, Context.MODE_PRIVATE);
		return sharedPreferences.getString("password","123");		
	}
	/**
	 * ����Password
	 * @param state
	 */
	public void setPasswordp(String password){
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.configs, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();//��ȡ�༭��
		editor.putString("password",password);//��ʼʱ���ǹرյ�
		editor.commit();//�ύ�޸�
	}

	/**
	 * ����ļ�״̬
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
	 * ��privatekey����
	 */
	public PrivateKey getPrivateKey(String filename) {
		PrivateKey privateKey=null;
		SharedPreferences preferences = context.getSharedPreferences("base64",Context.MODE_PRIVATE);
		String privateKeyBase64 = preferences.getString(filename, "");
		//Log.d("get",filename+"��;��"+privateKeyBase64 );
		if (privateKeyBase64 == "") {
			return null;
		}
		//��ȡ�ֽ�
		byte[] base64 = Base64.decodeBase64(privateKeyBase64.getBytes());
		KeyFactory keyFactory;
		try {
			keyFactory = KeyFactory.getInstance("RSA");
			PKCS8EncodedKeySpec pKCS8EncodedKeySpec =new PKCS8EncodedKeySpec(base64);
			privateKey = keyFactory.generatePrivate(pKCS8EncodedKeySpec);
			//Log.d("get",filename+"��;��"+privateKey.toString() );
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // ָ���㷨RSA,�õ�һ��KeyFactory��ʵ��


		return privateKey;
	}
	/**
	 * ����privatekey����
	 */
	public void savePrivateKey(String filename,PrivateKey privateKey) {
		SharedPreferences preferences = context.getSharedPreferences("base64",Context.MODE_PRIVATE);
		//���ֽ��������base64���ַ���
		String privateKeyBase64 = new String(Base64.encodeBase64(privateKey.getEncoded()));
		Editor editor = preferences.edit();
		editor.putString(filename, privateKeyBase64);
		editor.commit();

	}
	/**
	 * ��ó�������״̬
	 * @return
	 */
	public boolean getLockOpen(){
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.configs, Context.MODE_PRIVATE);
		return sharedPreferences.getBoolean(this.isLockOpen,false);		
	}
	/**
	 * ���ó�������״̬
	 * @param state
	 */
	public void setIsLockOpen(boolean state){
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.configs, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();//��ȡ�༭��
		editor.putBoolean(this.isLockOpen, state);//��ʼʱ���ǹرյ�
		editor.commit();//�ύ�޸�
	}
	/**
	 * ����ϴ�����״̬
	 * @return
	 */
	public boolean getPrivacyOpen() {
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.configs, Context.MODE_PRIVATE);
		return sharedPreferences.getBoolean(this.isPrivacyOpen,false);		
	}
	/**
	 * ��������״̬
	 * @param state
	 */
	public void setRunning(boolean state){
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.configs, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();//��ȡ�༭��
		editor.putBoolean(this.running, state);//��ʼʱ���ǹرյ�
		editor.commit();//�ύ�޸�
	}
	
	/**
	 * �������״̬
	 * @return
	 */
	public boolean getRunning() {
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.configs, Context.MODE_PRIVATE);
		return sharedPreferences.getBoolean(this.running,false);		
	}
	/**
	 * �����ϴ�����״̬
	 * @param state
	 */
	public void setPrivacyOpen(boolean state){
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.configs, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();//��ȡ�༭��
		editor.putBoolean(this.isPrivacyOpen, state);//��ʼʱ���ǹرյ�
		editor.commit();//�ύ�޸�
	}
	

	/**
	 * ���info_data
	 * @return
	 */
	public String getinfo_data(){
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.configs, Context.MODE_PRIVATE);
		return sharedPreferences.getString(this.info_data,"δ֪");		
	}
	/**
	 * ����info_data
	 * @param state
	 */
	public void setinfo_data(String info_data){
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.configs, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();//��ȡ�༭��
		editor.putString(this.info_data, info_data);
		editor.commit();//�ύ�޸�
	}

	/**
	 * ���info_time
	 * @return
	 */
	public String getinfo_time(){
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.configs, Context.MODE_PRIVATE);
		return sharedPreferences.getString(this.info_time,"δ֪");		
	}
	/**
	 * ����info_time
	 * @param state
	 */
	public void setinfo_time(String info_time){
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.configs, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();//��ȡ�༭��
		editor.putString(this.info_time, info_time);
		editor.commit();//�ύ�޸�
	}

	/**
	 * ���info_way
	 * @return
	 */
	public String getinfo_way(){
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.configs, Context.MODE_PRIVATE);
		return sharedPreferences.getString(this.info_way,"δ֪");		
	}
	/**
	 * ����info_way
	 * @param state
	 */
	public void setinfo_way(String info_way){
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.configs, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();//��ȡ�༭��
		editor.putString(this.info_way, info_way);
		editor.commit();//�ύ�޸�
	}

	/**
	 * ���info_app
	 * @return
	 */
	public String getinfo_app(){
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.configs, Context.MODE_PRIVATE);
		return sharedPreferences.getString(this.info_app,"δ֪");		
	}
	/**
	 * ����info_app
	 * @param state
	 */
	public void setinfo_app(String info_app){
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.configs, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();//��ȡ�༭��
		editor.putString(this.info_app, info_app);
		editor.commit();//�ύ�޸�
	}

	/**
	 * ���info_app_ip
	 * @return
	 */
	public String getinfo_app_ip(){
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.configs, Context.MODE_PRIVATE);
		return sharedPreferences.getString(this.info_app_ip,"δ֪");		
	}
	/**
	 * ����info_app_ip
	 * @param state
	 */
	public void setinfo_app_ip(String info_app_ip){
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.configs, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();//��ȡ�༭��
		editor.putString(this.info_app_ip, info_app_ip);
		editor.commit();//�ύ�޸�
	}

	/**
	 * ���info_thread_size
	 * @return
	 */
	public String getinfo_thread_size(){
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.configs, Context.MODE_PRIVATE);
		return sharedPreferences.getString(this.info_thread_size,"δ֪");		
	}
	/**
	 * ����info_thread_size
	 * @param state
	 */
	public void setinfo_thread_size(String info_thread_size){
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.configs, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();//��ȡ�༭��
		editor.putString(this.info_thread_size, info_thread_size);
		editor.commit();//�ύ�޸�
	}

	/**
	 * ���info_max_size
	 * @return
	 */
	public String getinfo_max_size(){
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.configs, Context.MODE_PRIVATE);
		return sharedPreferences.getString(this.info_max_size,"δ֪");		
	}
	/**
	 * ����info_max_size
	 * @param state
	 */
	public void setinfo_max_size(String info_max_size){
		SharedPreferences sharedPreferences = context.getSharedPreferences(this.configs, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();//��ȡ�༭��
		editor.putString(this.info_max_size, info_max_size);
		editor.commit();//�ύ�޸�
	}

}
