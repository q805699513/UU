package com.uugty.uu.db.service;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.uugty.uu.dbhelp.DBOpenHelper;
import com.uugty.uu.entity.RoadLine;
import com.uugty.uu.entity.RoadLineEntity;

import java.util.ArrayList;
import java.util.List;

public class RoadLineService {
	private Context context;

	public RoadLineService(Context context) {
		this.context = context;
	}

	public void saveRoadLine(RoadLineEntity roadLineEntity) {
		SQLiteDatabase db = DBOpenHelper.getInstance(context)
				.getWritableDatabase();
		db.beginTransaction();
		ContentValues values = new ContentValues();
		values.put("roadline_id", roadLineEntity.getRoadlineId());
		values.put("roadline_bg_image", roadLineEntity.getRoadlineBackground());
		values.put("roadline_title", roadLineEntity.getRoadlineTitle());
		values.put("roadline_price", roadLineEntity.getRoadlinePrice());
		values.put("roadline_address", roadLineEntity.getRoadlineGoalArea());
		values.put("roadline_create_time", roadLineEntity.getRoadlineCreateDate());
		values.put("user_id", roadLineEntity.getUserId());
		db.insert(DBOpenHelper.T_ROADLINE, null, values);
		
		for(int i=0;i<roadLineEntity.getRoadlineDescribes().size();i++){
			ContentValues values1 = new ContentValues();
			values1.put("roadline_id", roadLineEntity.getRoadlineId());
			values1.put("picture_name", roadLineEntity.getRoadlineDescribes().get(i).getDescribeImage());
			values1.put("picture_ratio", roadLineEntity.getRoadlineDescribes().get(i).getDescribeTime());
			values1.put("picture_describe", roadLineEntity.getRoadlineDescribes().get(i).getDescribeArea());
			db.insert(DBOpenHelper.T_PICTURE, null, values1);
		}
		
		// 提交事务
		db.setTransactionSuccessful();
		// 结束事务
		db.endTransaction();
		db.close();

	}
	
	//查询路线
	public ArrayList<RoadLineEntity> queryAllRoadLine(String userId) {
			SQLiteDatabase db = DBOpenHelper.getInstance(context).getReadableDatabase();
			
			ArrayList<RoadLineEntity> roadLineEntityList = new ArrayList<RoadLineEntity>();
			List<RoadLine> routeMarkLs = new ArrayList<RoadLine>();
			 
			Cursor cursor = db.query(DBOpenHelper.T_ROADLINE, new String[] { "roadline_id",
					"roadline_bg_image", "roadline_title", "roadline_price","roadline_address","roadline_create_time" },
					"user_id=?", new String[] { userId }, null, null, null);
			
			while (cursor.moveToNext()) {
				routeMarkLs.clear();
				RoadLineEntity roadLineEntity = new RoadLineEntity();
				String roadline_id = cursor.getString(cursor.getColumnIndex("roadline_id"));
				//根据roadline_id查询图片
				Cursor cursor1 = db.query(DBOpenHelper.T_PICTURE, new String[] { "picture_name",
						"picture_ratio", "picture_describe" },
						"roadline_id=?", new String[] { roadline_id }, null, null, null);
				while(cursor1.moveToNext()){
					String picture_name = cursor1.getString(cursor1.getColumnIndex("picture_name"));
					String picture_ratio = cursor1.getString(cursor1
							.getColumnIndex("picture_ratio"));
					String picture_describe = cursor1.getString(cursor1
							.getColumnIndex("picture_describe"));
					routeMarkLs.add(new RoadLine(picture_name,picture_ratio,picture_describe));
				}
				cursor1.close();
				roadLineEntity.setRoadLineDescribes(routeMarkLs);//路线图片
				roadLineEntity.setRoadlineId(roadline_id);
				roadLineEntity.setRoadlineBackground(cursor.getString(cursor.getColumnIndex("roadline_bg_image")));
				roadLineEntity.setRoadlineTitle(cursor.getString(cursor.getColumnIndex("roadline_title")));
				roadLineEntity.setRoadlinePrice(cursor.getString(cursor.getColumnIndex("roadline_price")));
				roadLineEntity.setRoadlineGoalArea(cursor.getString(cursor.getColumnIndex("roadline_address")));
				roadLineEntity.setRoadlineCreateDate(cursor.getString(cursor.getColumnIndex("roadline_create_time")));
				roadLineEntity.setRoadlineStatus("edit"); //编辑中
				roadLineEntityList.add(roadLineEntity);
			}
			cursor.close();
			db.close();
			return roadLineEntityList;
		}
	
	//删除路线
	public void deleteData(String roadlineId) {
		SQLiteDatabase db = DBOpenHelper.getInstance(context).getWritableDatabase();
		db.delete(DBOpenHelper.T_ROADLINE, "roadline_id=?", new String[] { roadlineId });
		db.delete(DBOpenHelper.T_PICTURE, "roadline_id=?", new String[] { roadlineId });
		db.close();
	}
	
	//查询某条路线
	public RoadLineEntity selectRoutLine(String roadlineId) {
		RoadLineEntity roadLineEntity = new RoadLineEntity();
		List<RoadLine> routeMarkLs = new ArrayList<RoadLine>();
		SQLiteDatabase db = DBOpenHelper.getInstance(context).getReadableDatabase();
		
		Cursor cursor = db.query(DBOpenHelper.T_ROADLINE, new String[] { "roadline_id",
				"roadline_bg_image", "roadline_title", "roadline_price","roadline_address","roadline_create_time" },
				"roadline_id=?", new String[] { roadlineId }, null, null, null);
		
		while (cursor.moveToNext()) {
			routeMarkLs.clear();
			//根据roadline_id查询图片
			Cursor cursor1 = db.query(DBOpenHelper.T_PICTURE, new String[] { "picture_name",
					"picture_ratio", "picture_describe" },
					"roadline_id=?", new String[] { roadlineId }, null, null, null);
			while(cursor1.moveToNext()){
				String picture_name = cursor1.getString(cursor1.getColumnIndex("picture_name"));
				String picture_ratio = cursor1.getString(cursor1
						.getColumnIndex("picture_ratio"));
				String picture_describe = cursor1.getString(cursor1
						.getColumnIndex("picture_describe"));
				routeMarkLs.add(new RoadLine(picture_name,picture_ratio,picture_describe));
			}
			cursor1.close();
			roadLineEntity.setRoadLineDescribes(routeMarkLs);//路线图片
			roadLineEntity.setRoadlineId(roadlineId);
			roadLineEntity.setRoadlineBackground(cursor.getString(cursor.getColumnIndex("roadline_bg_image")));
			roadLineEntity.setRoadlineTitle(cursor.getString(cursor.getColumnIndex("roadline_title")));
			roadLineEntity.setRoadlinePrice(cursor.getString(cursor.getColumnIndex("roadline_price")));
			roadLineEntity.setRoadlineGoalArea(cursor.getString(cursor.getColumnIndex("roadline_address")));
			roadLineEntity.setRoadlineCreateDate(cursor.getString(cursor.getColumnIndex("roadline_create_time")));
		}
		cursor.close();
		db.close();
		return roadLineEntity;
	}
}
