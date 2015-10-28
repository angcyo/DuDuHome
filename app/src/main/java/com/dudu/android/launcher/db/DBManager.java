package com.dudu.android.launcher.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 数据库公共类
 * 
 */
public class DBManager {
	private static final String TAG = "DBManager";
	// 搜索过的地址数据库
	private static final String DB_NAME = "search_place.db";

	// 数据库版本
	private static final int DB_VERSION = 1;

	// 执行open()打开数据库时，保存返回的数据库对象
	private SQLiteDatabase mSQLiteDatabase = null;

	// 由SQLiteOpenHelper继承过来
	private DatabaseHelper mDatabaseHelper = null;

	// 本地Context对象
	private Context mContext = null;
	private static DBManager dbConn= null;
	
	// 查询游标对象
	private Cursor cursor;
	public final static String SEARCH_PLACE_TABLE="search_place_tab";   // 搜索过的地址表名
	public final static String FIELD_ID="_id"; 							
	public final static String FIELD_TITLE="_placeName";				// 搜索过的地址字段名
	
	
	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}
		@Override
		public void onCreate(SQLiteDatabase db) {
			String sql="Create table "+SEARCH_PLACE_TABLE+"("+FIELD_ID+" integer primary key autoincrement,"+FIELD_TITLE+" text );";
			db.execSQL(sql);
		
		}
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS"+SEARCH_PLACE_TABLE);
			onCreate(db);
		}
	}

	/**
	 * 构造函数
	 * 
	 * @param mContext
	 */
	private DBManager(Context mContext) {
		super();
		this.mContext = mContext;
	}
	
	public static DBManager getInstance(Context mContext){
		if (null == dbConn) {
			dbConn = new DBManager(mContext);
		}
		return dbConn;
	}

	/**
	 * 打开数据库
	 */
	public void open() {
		mDatabaseHelper = new DatabaseHelper(mContext);
		mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();
	}

	/**
	 * 关闭数据库
	 */
	public void close() {
		if (null != mDatabaseHelper) {
			mDatabaseHelper.close();
		}
		if (null != cursor) {
			cursor.close();
		}
	}

	/**
	 * 插入数据
	 * @param tableName 表名
	 * @param nullColumn null
	 * @param contentValues 字段值
	 * @return 新插入数据的ID，错误返null
	 * @throws Exception
	 */
	public long insert(String tableName, String nullColumn,
			ContentValues contentValues) throws Exception {
		try {
			return mSQLiteDatabase.insert(tableName, nullColumn, contentValues);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 通过主键ID删除数据
	 * @param tableName 表名
	 * @param key 主键
	 * @param id 主键
	 * @return 受影响的记录
	 * @throws Exception
	 */
	public long delete(String tableName, String key, int id) throws Exception {
		try {
			return mSQLiteDatabase.delete(tableName, key + " = " + id, null);
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 删除
	 * @param tableName
	 * @param content
	 * @return
	 */
	public boolean delete(String tableName, String content) {
		cursor = mSQLiteDatabase.query(tableName, null, null, null, null, null, null);
		String record = "";
		int id = -1;
		while(cursor!=null&&cursor.moveToNext()) {
			record = cursor.getString(cursor.getColumnIndex(FIELD_TITLE));
			if(record.equalsIgnoreCase(content)) {
				id = cursor.getInt(cursor.getColumnIndex(FIELD_ID));
				break;
			}
		}
		if(cursor!=null&&!cursor.isClosed())
			cursor.close();
		if(id != -1) {
			try {
				long result = delete(tableName, FIELD_ID, id);
				if(result != 0)
					return true;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}
		return false;
	}
	
	/**
	 * 删除搜索历史表
	 * @return
	 */
	public boolean deleteSeachHistory() {
		// TODO Auto-generated method stub
		SQLiteDatabase db= mDatabaseHelper.getWritableDatabase();		
		db.delete(SEARCH_PLACE_TABLE, null, null);
		return true;
	}
	
	/**
	 * 将搜索历史存入数据库
	 * @param content
	 * @return
	 */
	public boolean saveSeachHistory(String content) {
		// TODO Auto-generated method stub
		ContentValues cValue = new ContentValues();
		cValue.put(FIELD_TITLE, content);
		long result;
		try {
			result = insert(SEARCH_PLACE_TABLE, null, cValue);
			if(result==-1){
				return false;
			}else {
				return true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
	}

	/**
	 * @param tableName 表名
	 * @param columns 如果返回�?��列，则填null
	 * @return
	 * @throws Exception
	 */
	public Cursor findAll(String tableName, String [] columns) throws Exception{
		try {
			cursor = mSQLiteDatabase.query(tableName, columns, null, null, null, null, " _id desc");
			cursor.moveToFirst();
			return cursor;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 查询历史搜索记录
	 * @return 返回前5条
	 */
	public ArrayList<String> getSearchHistory() {
		ArrayList<String> list = new ArrayList<String>();
		String sql = "select * from "+SEARCH_PLACE_TABLE+" order by "+FIELD_ID+" limit 5";
		try {
			cursor = mSQLiteDatabase.rawQuery(sql, null);
			while(cursor!=null&&cursor.moveToNext()) {
				String record = cursor.getString(cursor.getColumnIndex(FIELD_TITLE));
				list.add(record);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			Log.d(TAG, "查询数据库数据失败");
		}finally{
			if(cursor!=null&&!cursor.isClosed()){
				cursor.close();
			}
		}
		return null;
	}
	
	/**
	 * 根据主键查找数据
	 * @param tableName 表名
	 * @param key 主键
	 * @param id  主键
	 * @param columns 如果返回�?��列，则填null
	 * @return Cursor游标
	 * @throws Exception 
	 */
	public Cursor findById(String tableName, String key, int id, String [] columns) throws Exception {
		try {
			return mSQLiteDatabase.query(tableName, columns, key + " = " + id, null, null, null, null);
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 根据条件查询数据
	 * @param tableName 表名
	 * @param names 查询条件
	 * @param values 查询条件�?
	 * @param columns 如果返回�?��列，则填null
	 * @param orderColumn 排序的列
	 * @param limit 限制返回�?
	 * @return Cursor游标
	 * @throws Exception
	 */
	public Cursor find(String tableName, String [] names, String [] values, String [] columns, String orderColumn, String limit) throws Exception{
		try {
			StringBuffer selection = new StringBuffer();
			for (int i = 0; i < names.length; i++) {
				selection.append(names[i]);
				selection.append(" = ?");
				if (i != names.length - 1) {
					selection.append(",");
				}
			}
			cursor = mSQLiteDatabase.query(true, tableName, columns, selection.toString(), values, null, null, orderColumn, limit);
			cursor.moveToFirst();
			return cursor;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 
	 * @param tableName 表名
	 * @param names 查询条件
	 * @param values 查询条件�?
	 * @param args 更新�?值对
	 * @return true或false
	 * @throws Exception
	 */
	public boolean udpate(String tableName, String [] names, String [] values, ContentValues args) throws Exception{
		try {
			StringBuffer selection = new StringBuffer();
			for (int i = 0; i < names.length; i++) {
				selection.append(names[i]);
				selection.append(" = ?");
				if (i != names.length - 1) {
					selection.append(",");
				}
			}
			return mSQLiteDatabase.update(tableName, args, selection.toString(), values) > 0;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 执行sql语句，包括创建表、删除�?插入
	 * 
	 * @param sql
	 */
	public void executeSql(String sql) {
		mSQLiteDatabase.execSQL(sql);
	}
	
	// 查询所有的搜索历史记录
	public ArrayList<String> getAllSearchHistory() {
		ArrayList<String> list = new ArrayList<String>();
		try {
			cursor = mSQLiteDatabase.query(SEARCH_PLACE_TABLE, null, null, null, null, null, "_id desc");
			while(cursor!=null&&cursor.moveToNext()) {
				String record = cursor.getString(cursor.getColumnIndex(FIELD_TITLE));
				list.add(record);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			Log.d("DBManager", "查询数据库数据失败");
		}finally{
			if(cursor!=null&&!cursor.isClosed()){
				cursor.close();
			}
		}
		return null;
	}
}
