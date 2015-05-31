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
//数据库管理类，帮助数据哭的管理:创建，操作，更新数据库
public class MonitorDbManager {
	
	private Context context=null;

	/* 数据库对象 */
	private  SQLiteDatabase	mSQLiteDatabase	= null;

	/* 数据库名 */
	private final static String	DATABASE_NAME	= "appView.db";
	
	/* 表名 */
	private final static String	TABLE_NAME		= "isAppViewed";

	/* 表中的字段 ,id和包名*/
	private final static String	TABLE_ID		= "_id";
	
	private final static String	TABLE_PNAME		= "packageName";
	
	/* 创建表的sql语句 */
	private final static String	CREATE_TABLE	= "CREATE TABLE " + TABLE_NAME + " (" + TABLE_ID + " INTEGER PRIMARY KEY," + TABLE_PNAME +" TEXT)";

	/*构造函数*/
	public MonitorDbManager(Context context){
		this.context=context;
		// 打开已经存在的数据库
		try
		{
			mSQLiteDatabase = context.openOrCreateDatabase(DATABASE_NAME, context.MODE_PRIVATE, null);
			//没有初始化的初始化
			if(!( new myConfig(context).isPrivacyDbinit() ))
				mSQLiteDatabase.execSQL(CREATE_TABLE);
		}
		catch (Exception e)
		{
			
		}
	}
		
	/*初始化表，应执行一次*/
	public void initTable(){
		mSQLiteDatabase.execSQL(CREATE_TABLE);
		PackageManager pManager =context.getPackageManager();
		List<PackageInfo> apkList=pManager.getInstalledPackages(0);
		for(int i=0;i<apkList.size();++i){
			 PackageInfo pak=apkList.get(i);
			//把包名放进去
			ContentValues cv = new ContentValues();
			cv.put(TABLE_PNAME,pak.packageName);
            
			mSQLiteDatabase.insert(TABLE_NAME, null, cv);	
		}
	}
	
	/*插入一个数据*/ 
	public void AddDate(String packgaeName)
	{
		ContentValues cv = new ContentValues();
		cv.put(TABLE_PNAME,packgaeName);
		if(!this.mSQLiteDatabase.isOpen()){
			try
			{
				mSQLiteDatabase = context.openOrCreateDatabase(DATABASE_NAME, context.MODE_PRIVATE, null);
				//没有初始化的初始化
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
	
	/*删除一个数据*/
	public void DeleteDate(String packgaeName)
	{  	
		if(!this.mSQLiteDatabase.isOpen()){
			try
			{
				mSQLiteDatabase = context.openOrCreateDatabase(DATABASE_NAME, context.MODE_PRIVATE, null);
				//没有初始化的初始化
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
		

	/*查询数据库，返回所有的上锁的包名*/
	public List<String> getAppLockStates(){	
		List<String> pnames= new ArrayList<String>();
		if(!this.mSQLiteDatabase.isOpen()){
			try
			{
				mSQLiteDatabase = context.openOrCreateDatabase(DATABASE_NAME, context.MODE_PRIVATE, null);
				//没有初始化的初始化
				if(!( new myConfig(context).isPrivacyDbinit() ))
					mSQLiteDatabase.execSQL(CREATE_TABLE);
			}
			catch (Exception e)
			{
				return null;
			}
		}
		// 获取数据库Phones的Cursor
		Cursor result = mSQLiteDatabase.query(TABLE_NAME, new String[] { TABLE_ID, TABLE_PNAME}, null, null, null, null, null);
		result.moveToFirst(); 
		//添加记录
		while (!result.isAfterLast()) { 
			pnames.add(result.getString(1));
			result.moveToNext(); 
		} 
		result.close(); 
	    return pnames;
	}

	/* 删除数据库 */
	public void DeleteDataBase()
	{

		this.context.deleteDatabase(DATABASE_NAME);
	}


	/* 删除一个表 */
	public void DeleteTable()
	{
		mSQLiteDatabase.execSQL("DROP TABLE " + TABLE_NAME);
	}
	
	//关闭数据库
	public void closeDataBase(){
		if(this.mSQLiteDatabase.isOpen())
			this.mSQLiteDatabase.close();
	}
	
	
		
}
	

