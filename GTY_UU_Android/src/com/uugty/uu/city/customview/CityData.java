package com.uugty.uu.city.customview;



import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * 357个中国城市
 * @author ck
 * @since 2014年2月7日 16:20:32
 */
public class CityData
{
	public static List<ContactItemInterface> getSampleContactList(String cityJson)
	{
		List<ContactItemInterface> list = new ArrayList<ContactItemInterface>();
		
		try
		{
			JSONObject jo1 = new JSONObject(cityJson);
			
			JSONArray ja1 = jo1.getJSONArray("cities");
			
			for(int i = 0; i < ja1.length(); i++)
			{
				String cityName = ja1.getString(i);
				
				list.add(new CityItem(cityName, PinYin.getPinYin(cityName)));
			}
		} 
		catch (JSONException e)
		{
			e.printStackTrace();
		}

		return list;
	}

}
