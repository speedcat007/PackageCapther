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
public class LearnDbManager {
	
	private Context context=null;

	/* ���ݿ���� */
	private  SQLiteDatabase	mSQLiteDatabase	= null;

	/* ���ݿ��� */
	private final static String	DATABASE_NAME	= "appView.db";
	
	/* ���� */
	private final static String	TABLE_NAME		= "Learned_IP_APP";

	/* ���е��ֶ� ,id�Ͱ���*/
	private final static String	TABLE_ID		= "_id";
	private final static String	IP				= "IP";
	private final static String	PACKAGE_NAME	= "PACKAGE_NAME"; //����


	/* �������sql��� */
	private final static String	CREATE_TABLE	= "CREATE TABLE " + TABLE_NAME + " (" 
			+ TABLE_ID + " INTEGER PRIMARY KEY," 
			+ IP+" TEXT,"
			+ PACKAGE_NAME +" TEXT"
			+ ")";

	/*���캯��*/
	public LearnDbManager(Context context){
		this.context=context;
		// ���Ѿ����ڵ����ݿ�
		try
		{
			mSQLiteDatabase = context.openOrCreateDatabase(DATABASE_NAME, context.MODE_PRIVATE, null);
			//û�г�ʼ���ĳ�ʼ��
			if(!( new myConfig(context).isLearnDbint() ))
				mSQLiteDatabase.execSQL(CREATE_TABLE);
		}
		catch (Exception e)
		{

		}
	}


	/*����һ������*/ 
	public void AddDate(String ip,String package_name)
	{
		ContentValues cv = new ContentValues();
		cv.put(IP,ip);
		cv.put(PACKAGE_NAME,package_name);
		
		List<String> pnames=this.getPackageNameByIP(ip);
		if(pnames.contains(package_name))//���˾Ͳ������
			return;
		
		if(!this.mSQLiteDatabase.isOpen()){
			try
			{
				mSQLiteDatabase = context.openOrCreateDatabase(DATABASE_NAME, context.MODE_PRIVATE, null);
				//û�г�ʼ���ĳ�ʼ��
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

	/*ɾ��һ��ip��ַ������*/
	public void DeleteDate(String ip)
	{  	
		if(!this.mSQLiteDatabase.isOpen()){
			try
			{
				mSQLiteDatabase = context.openOrCreateDatabase(DATABASE_NAME, context.MODE_PRIVATE, null);
				//û�г�ʼ���ĳ�ʼ��
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

	/*��ѯ���ݿ⣬��ָ����־�ļ��浽���ݿ���*/
	public List<String> getPackageNameByIP(String ip){	
		List<String> pnames= new ArrayList<String>();
		if(!this.mSQLiteDatabase.isOpen()){
			try
			{
				mSQLiteDatabase = context.openOrCreateDatabase(DATABASE_NAME, context.MODE_PRIVATE, null);
				//û�г�ʼ���ĳ�ʼ��
				if(!( new myConfig(context).isLearnDbint() ))
					mSQLiteDatabase.execSQL(CREATE_TABLE);
			}
			catch (Exception e)
			{
				return pnames;
			}
		}
		// ��ȡ���ݿ�Phones��Cursor
		Cursor result = mSQLiteDatabase.query(TABLE_NAME, new String[] {
				TABLE_ID,PACKAGE_NAME},
				IP+"=?", new String[]{ip}, null, null, null);
		result.moveToFirst(); 
		//��Ӽ�¼
		while (!result.isAfterLast()) { 
			pnames.add(result.getString(result.getColumnIndex(PACKAGE_NAME)));
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


