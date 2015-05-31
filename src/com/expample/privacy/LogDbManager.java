package com.expample.privacy;

import java.util.ArrayList;
import java.util.List;

import com.example.config.myConfig;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
//���ݿ�����࣬�������ݿ޵Ĺ���:�������������������ݿ�
public class LogDbManager {

	private Context context=null;
	/* ���ݿ���� */
	private  SQLiteDatabase	mSQLiteDatabase	= null;

	/* ���ݿ��� */
	private final static String	DATABASE_NAME	= "appView.db";

	/* ���� */
	private final static String	TABLE_NAME		= "log";

	/* ���е��ֶ� ,id�Ͱ���*/
	private final static String	TABLE_ID		= "_id";
	private final static String	INFO_FILE_NAME	= "info_file_name";
	private final static String	INFO_DATA		= "info_data";
	private final static String	INFO_TIME		= "info_time";
	private final static String	INFO_WAY		= "info_way";
	private final static String	INFO_APP		= "info_app";
	private final static String	INFO_APP_IP		= "info_app_ip";
	private final static String	INFO_PORT		= "info_port";
	private final static String	INFO_THREAD_SIZE= "info_thread_size";
	private final static String	INFO_MAX_SIZE	= "info_max_size";

	/* �������sql��� */
	private final static String	CREATE_TABLE	= "CREATE TABLE " + TABLE_NAME + " (" 
			+ TABLE_ID + " INTEGER PRIMARY KEY," 
			+ INFO_FILE_NAME+" TEXT,"
			+ INFO_DATA +" TEXT,"
			+ INFO_TIME +" TEXT,"
			+ INFO_WAY+" TEXT,"
			+ INFO_APP+" TEXT,"
			+ INFO_APP_IP+" TEXT,"
			+ INFO_PORT+" TEXT,"
			+ INFO_THREAD_SIZE+" TEXT,"
			+ INFO_MAX_SIZE+" TEXT"
			+ ")";

	/*���캯��*/
	public LogDbManager(Context context){
		this.context=context;
		// ���Ѿ����ڵ����ݿ�
		try
		{
			mSQLiteDatabase = context.openOrCreateDatabase(DATABASE_NAME, context.MODE_PRIVATE, null);
			//û�г�ʼ���ĳ�ʼ��
			if(!( new myConfig(context).isLogDbinit() ))
				mSQLiteDatabase.execSQL(CREATE_TABLE);
		}
		catch (Exception e)
		{

		}
	}


	/*����һ������*/ 
	public void AddDate(String info_data,String info_time,String info_way,String info_app,
			String info_app_ip,String info_port,String info_thread_size,String info_max_size)
	{
		ContentValues cv = new ContentValues();
		cv.put(INFO_FILE_NAME,info_data+" "+info_time);
		cv.put(INFO_DATA,info_data);
		cv.put(INFO_TIME,info_time);
		cv.put(INFO_WAY,info_way);
		cv.put(INFO_APP,info_app);
		cv.put(INFO_APP_IP,info_app_ip);
		cv.put(INFO_PORT,info_port);
		cv.put(INFO_THREAD_SIZE,info_thread_size);
		cv.put(INFO_MAX_SIZE,info_max_size);

		if(!this.mSQLiteDatabase.isOpen()){
			try
			{
				mSQLiteDatabase = context.openOrCreateDatabase(DATABASE_NAME, context.MODE_PRIVATE, null);
				//û�г�ʼ���ĳ�ʼ��
				if(!( new myConfig(context).isLogDbinit() ))
					mSQLiteDatabase.execSQL(CREATE_TABLE);
			}
			catch (Exception e)
			{
				return;
			}
		}
		mSQLiteDatabase.insert(TABLE_NAME, null, cv);	
	}

	/*ɾ��һ������*/
	public void DeleteDate(String info_file_name)
	{  	
		if(!this.mSQLiteDatabase.isOpen()){
			try
			{
				mSQLiteDatabase = context.openOrCreateDatabase(DATABASE_NAME, context.MODE_PRIVATE, null);
				//û�г�ʼ���ĳ�ʼ��
				if(!( new myConfig(context).isLogDbinit() ))
					mSQLiteDatabase.execSQL(CREATE_TABLE);
			}
			catch (Exception e)
			{
				return;
			}
		}
		mSQLiteDatabase.delete(TABLE_NAME, INFO_FILE_NAME+"=?", new String[]{info_file_name});
	}


	/*��ѯ���ݿ⣬�������е�����־�ļ���*/
	public List<String> getLogFiles(){	
		List<String> pnames= new ArrayList<String>();
		if(!this.mSQLiteDatabase.isOpen()){
			try
			{
				mSQLiteDatabase = context.openOrCreateDatabase(DATABASE_NAME, context.MODE_PRIVATE, null);
				//û�г�ʼ���ĳ�ʼ��
				if(!( new myConfig(context).isLogDbinit() ))
					mSQLiteDatabase.execSQL(CREATE_TABLE);
			}
			catch (Exception e)
			{
				return null;
			}
		}
		// ��ȡ���ݿ�Phones��Cursor
		Cursor result = mSQLiteDatabase.query(TABLE_NAME, new String[] { TABLE_ID,INFO_FILE_NAME }, null, null, null, null, null);
		result.moveToFirst(); 
		//��Ӽ�¼
		while (!result.isAfterLast()) { 
			pnames.add(result.getString(1));
			result.moveToNext(); 
		} 
		result.close(); 
		return pnames;
	}

	/*��ѯ���ݿ⣬��ָ����־�ļ��浽���ݿ���*/
	public void getLogFileByName(String info_file_name){	
		//List<String> pnames= new ArrayList<String>();
		if(!this.mSQLiteDatabase.isOpen()){
			try
			{
				mSQLiteDatabase = context.openOrCreateDatabase(DATABASE_NAME, context.MODE_PRIVATE, null);
				//û�г�ʼ���ĳ�ʼ��
				if(!( new myConfig(context).isLogDbinit() ))
					mSQLiteDatabase.execSQL(CREATE_TABLE);
			}
			catch (Exception e)
			{
				return ;
			}
		}
		// ��ȡ���ݿ�Phones��Cursor
		Cursor result = mSQLiteDatabase.query(TABLE_NAME, new String[] {
				TABLE_ID,INFO_DATA,INFO_TIME,INFO_WAY,INFO_APP,
				INFO_APP_IP,INFO_PORT,INFO_THREAD_SIZE,INFO_MAX_SIZE},
				INFO_FILE_NAME+"=?", new String[]{info_file_name}, null, null, null);

		result.moveToFirst(); 
		
		//��Ӽ�¼
		myConfig myconfig =new myConfig(context);

		while (!result.isAfterLast()) { 
			myconfig.setinfo_data(result.getString(result.getColumnIndex(INFO_DATA)));
			myconfig.setinfo_time(result.getString(result.getColumnIndex(INFO_TIME)));
			myconfig.setinfo_way(result.getString(result.getColumnIndex(INFO_WAY)));
			myconfig.setinfo_app(result.getString(result.getColumnIndex(INFO_APP)));
			myconfig.setinfo_app_ip(result.getString(result.getColumnIndex(INFO_APP_IP)));
			//myconfig.setinfo_port(result.getString(result.getColumnIndex(INFO_PORT)));
			myconfig.setinfo_thread_size(result.getString(result.getColumnIndex(INFO_THREAD_SIZE)));
			myconfig.setinfo_max_size(result.getString(result.getColumnIndex(INFO_MAX_SIZE)));
			result.moveToNext();
		} 
		result.close(); 
		return ;
	}

	/* ɾ�����ݿ� */
	public void DeleteDataBase()
	{

		this.context.deleteDatabase(DATABASE_NAME);
	}


	/* ɾ��һ���� */
	public void DeleteTable()
	{
		mSQLiteDatabase.execSQL("DROP TABLE " + TABLE_NAME);
	}

	//�ر����ݿ�
	public void closeDataBase(){
		if(this.mSQLiteDatabase.isOpen())
			this.mSQLiteDatabase.close();
	}



}


