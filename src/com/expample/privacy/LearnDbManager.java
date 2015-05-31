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
public class LearnDbManager {
	
	private Context context=null;

	/* 数据库对象 */
	private  SQLiteDatabase	mSQLiteDatabase	= null;

	/* 数据库名 */
	private final static String	DATABASE_NAME	= "appView.db";
	
	/* 表名 */
	private final static String	TABLE_NAME		= "Learned_IP_APP";

	/* 表中的字段 ,id和包名*/
	private final static String	TABLE_ID		= "_id";
	private final static String	IP				= "IP";
	private final static String	PACKAGE_NAME	= "PACKAGE_NAME"; //包名


	/* 创建表的sql语句 */
	private final static String	CREATE_TABLE	= "CREATE TABLE " + TABLE_NAME + " (" 
			+ TABLE_ID + " INTEGER PRIMARY KEY," 
			+ IP+" TEXT,"
			+ PACKAGE_NAME +" TEXT"
			+ ")";

	/*构造函数*/
	public LearnDbManager(Context context){
		this.context=context;
		// 打开已经存在的数据库
		try
		{
			mSQLiteDatabase = context.openOrCreateDatabase(DATABASE_NAME, context.MODE_PRIVATE, null);
			//没有初始化的初始化
			if(!( new myConfig(context).isLearnDbint() ))
				mSQLiteDatabase.execSQL(CREATE_TABLE);
		}
		catch (Exception e)
		{

		}
	}


	/*插入一个数据*/ 
	public void AddDate(String ip,String package_name)
	{
		ContentValues cv = new ContentValues();
		cv.put(IP,ip);
		cv.put(PACKAGE_NAME,package_name);
		
		List<String> pnames=this.getPackageNameByIP(ip);
		if(pnames.contains(package_name))//有了就不添加了
			return;
		
		if(!this.mSQLiteDatabase.isOpen()){
			try
			{
				mSQLiteDatabase = context.openOrCreateDatabase(DATABASE_NAME, context.MODE_PRIVATE, null);
				//没有初始化的初始化
				if(!( new myConfig(context).isLearnDbint() ))
					mSQLiteDatabase.execSQL(CREATE_TABLE);
			}
			catch (Exception e)
			{
				return;
			}
		}
		mSQLiteDatabase.insert(TABLE_NAME, null, cv);	
	}

	/*删除一个ip地址的数据*/
	public void DeleteDate(String ip)
	{  	
		if(!this.mSQLiteDatabase.isOpen()){
			try
			{
				mSQLiteDatabase = context.openOrCreateDatabase(DATABASE_NAME, context.MODE_PRIVATE, null);
				//没有初始化的初始化
				if(!( new myConfig(context).isLearnDbint() ))
					mSQLiteDatabase.execSQL(CREATE_TABLE);
			}
			catch (Exception e)
			{
				return;
			}
		}
		mSQLiteDatabase.delete(TABLE_NAME, IP+"=?", new String[]{ip});
	}

	/*查询数据库，将指定日志文件存到数据库中*/
	public List<String> getPackageNameByIP(String ip){	
		List<String> pnames= new ArrayList<String>();
		if(!this.mSQLiteDatabase.isOpen()){
			try
			{
				mSQLiteDatabase = context.openOrCreateDatabase(DATABASE_NAME, context.MODE_PRIVATE, null);
				//没有初始化的初始化
				if(!( new myConfig(context).isLearnDbint() ))
					mSQLiteDatabase.execSQL(CREATE_TABLE);
			}
			catch (Exception e)
			{
				return pnames;
			}
		}
		// 获取数据库Phones的Cursor
		Cursor result = mSQLiteDatabase.query(TABLE_NAME, new String[] {
				TABLE_ID,PACKAGE_NAME},
				IP+"=?", new String[]{ip}, null, null, null);
		result.moveToFirst(); 
		//添加记录
		while (!result.isAfterLast()) { 
			pnames.add(result.getString(result.getColumnIndex(PACKAGE_NAME)));
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


