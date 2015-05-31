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
//���ݿ�����࣬�������ݿ޵Ĺ���:�������������������ݿ�
public class MonitorDbManager {
	
	private Context context=null;

	/* ���ݿ���� */
	private  SQLiteDatabase	mSQLiteDatabase	= null;

	/* ���ݿ��� */
	private final static String	DATABASE_NAME	= "appView.db";
	
	/* ���� */
	private final static String	TABLE_NAME		= "isAppViewed";

	/* ���е��ֶ� ,id�Ͱ���*/
	private final static String	TABLE_ID		= "_id";
	
	private final static String	TABLE_PNAME		= "packageName";
	
	/* �������sql��� */
	private final static String	CREATE_TABLE	= "CREATE TABLE " + TABLE_NAME + " (" + TABLE_ID + " INTEGER PRIMARY KEY," + TABLE_PNAME +" TEXT)";

	/*���캯��*/
	public MonitorDbManager(Context context){
		this.context=context;
		// ���Ѿ����ڵ����ݿ�
		try
		{
			mSQLiteDatabase = context.openOrCreateDatabase(DATABASE_NAME, context.MODE_PRIVATE, null);
			//û�г�ʼ���ĳ�ʼ��
			if(!( new myConfig(context).isPrivacyDbinit() ))
				mSQLiteDatabase.execSQL(CREATE_TABLE);
		}
		catch (Exception e)
		{
			
		}
	}
		
	/*��ʼ����Ӧִ��һ��*/
	public void initTable(){
		mSQLiteDatabase.execSQL(CREATE_TABLE);
		PackageManager pManager =context.getPackageManager();
		List<PackageInfo> apkList=pManager.getInstalledPackages(0);
		for(int i=0;i<apkList.size();++i){
			 PackageInfo pak=apkList.get(i);
			//�Ѱ����Ž�ȥ
			ContentValues cv = new ContentValues();
			cv.put(TABLE_PNAME,pak.packageName);
            
			mSQLiteDatabase.insert(TABLE_NAME, null, cv);	
		}
	}
	
	/*����һ������*/ 
	public void AddDate(String packgaeName)
	{
		ContentValues cv = new ContentValues();
		cv.put(TABLE_PNAME,packgaeName);
		if(!this.mSQLiteDatabase.isOpen()){
			try
			{
				mSQLiteDatabase = context.openOrCreateDatabase(DATABASE_NAME, context.MODE_PRIVATE, null);
				//û�г�ʼ���ĳ�ʼ��
				if(!( new myConfig(context).isPrivacyDbinit() ))
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
	public void DeleteDate(String packgaeName)
	{  	
		if(!this.mSQLiteDatabase.isOpen()){
			try
			{
				mSQLiteDatabase = context.openOrCreateDatabase(DATABASE_NAME, context.MODE_PRIVATE, null);
				//û�г�ʼ���ĳ�ʼ��
				if(!( new myConfig(context).isPrivacyDbinit() ))
					mSQLiteDatabase.execSQL(CREATE_TABLE);
			}
			catch (Exception e)
			{
				return;
			}
		}
		mSQLiteDatabase.delete(TABLE_NAME, TABLE_PNAME+"=?", new String[]{packgaeName});
	}
		

	/*��ѯ���ݿ⣬�������е������İ���*/
	public List<String> getAppLockStates(){	
		List<String> pnames= new ArrayList<String>();
		if(!this.mSQLiteDatabase.isOpen()){
			try
			{
				mSQLiteDatabase = context.openOrCreateDatabase(DATABASE_NAME, context.MODE_PRIVATE, null);
				//û�г�ʼ���ĳ�ʼ��
				if(!( new myConfig(context).isPrivacyDbinit() ))
					mSQLiteDatabase.execSQL(CREATE_TABLE);
			}
			catch (Exception e)
			{
				return null;
			}
		}
		// ��ȡ���ݿ�Phones��Cursor
		Cursor result = mSQLiteDatabase.query(TABLE_NAME, new String[] { TABLE_ID, TABLE_PNAME}, null, null, null, null, null);
		result.moveToFirst(); 
		//��Ӽ�¼
		while (!result.isAfterLast()) { 
			pnames.add(result.getString(1));
			result.moveToNext(); 
		} 
		result.close(); 
	    return pnames;
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
	

