package com.uugty.uu.dbhelp;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBOpenHelper extends SQLiteOpenHelper {

	public static final int VERSION = 1;// 数据库版本号
	public static final String DBNAME = "UU.db";// 数据库名称

	//----------------------表名称-----------------------------------------
	public static final String T_PICTURE = "T_PICTURE"; // 图片表
	public static final String T_ROADLINE = "T_ROADLINE";// 路线表

	//----------------------表结构-----------------------------------------
	/* 图片
	 * roadline_id 路线Id
	 * picture_name 图片名字
	 * picture_ratio 图片长宽比
	 * picture_describe 图片文字描述*/
	String picSql = "create table if not exists "
			+ T_PICTURE
			+ " (roadline_id text, "// 主键
			+ "picture_name text, " + "picture_ratio text, "
			+ "picture_describe text)";

	/* 路线
	 * roadline_id 路线Id
	 * roadline_bg_image 背景图片名字
	 * roadline_title 标题
	 * roadline_price 价格
	 * roadline_address 地点
	 * user_id 路线发布者*/
	String roadlineSql = "create table if not exists "
			+ T_ROADLINE
			+ " ( roadline_id text primary key, "// 主键
			+ "roadline_bg_image text," + "roadline_title text,"
            + "roadline_price text,"+ "roadline_address text,"
            + "roadline_create_time text,"
			+ "user_id text)";// 线路状态
	
	String citySql = "CREATE TABLE IF NOT EXISTS recentcity (id integer primary key autoincrement, name varchar(40), date INTEGER)";



	// 数据库操作单例
	private static DBOpenHelper mInstance;

	public DBOpenHelper(Context context) {
		// CursorFactory设置为null,使用默认值
		super(context, DBNAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		//删除以前的库  fabul.db
		db.execSQL(picSql);
		db.execSQL(roadlineSql);
		db.execSQL(citySql);
	}

	/**
	 * 获取单例数据库帮助对象
	 * 
	 * @Description
	 */
	public static synchronized DBOpenHelper getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new DBOpenHelper(context);
		}
		return mInstance;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO 更改数据库版本的操作
	}

}
