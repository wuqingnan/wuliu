package com.wuliu.client.city;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.wuliu.client.WLApplication;

public class CityManager {

	private static CityManager mInstance;
	private HashMap<String, HashMap<String, List<String>>> mCities = new HashMap<String, HashMap<String,List<String>>>();
	
	private CityManager() {
		String json = null;
		try {
			InputStream is = WLApplication.getContext().getResources().getAssets().open("city.json");
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int len = 0;
			byte[] buffer = new byte[1024];
			while ((len = is.read(buffer)) > 0) {
				baos.write(buffer, 0, len);
			}
			json = new String(baos.toByteArray());
			is.close();
			baos.close();
			is = null;
			baos = null;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
		if (json != null) {
			try {
				JSONObject fObj = null;
				JSONArray fArray = new JSONArray(json);
				JSONObject sObj = null;
				JSONArray sArray = null;
				JSONObject tObj = null;
				JSONArray tArray = null;
				
				for (int first = 0; first < fArray.length(); first++) {
					fObj = fArray.getJSONObject(first);
					if (fObj != null) {
						fObj = fArray.getJSONObject(first);
						sArray = fObj.optJSONArray("sub");
						HashMap<String, List<String>> cities = new HashMap<String, List<String>>();
						if (sArray != null) {
							for (int second = 0; second < sArray.length(); second++) {
								sObj = sArray.getJSONObject(second);
								tArray = sObj.optJSONArray("sub");
								List<String> list = new ArrayList<String>();
								if (tArray != null) {
									for (int third = 0; third < tArray.length(); third++) {
										tObj = tArray.getJSONObject(third);
										list.add(tObj.optString("name"));
									}
								}
								cities.put(sObj.optString("name"), list);
							}
						}
						mCities.put(fObj.optString("name"), cities);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
    }
	
    public static CityManager getInstance() {
        if (mInstance == null) {
        	mInstance = new CityManager();
        }
        return mInstance;
    }
    
    public String[] getFirstLevel() {
    	Set<String> set = mCities.keySet();
    	String[] result = new String[set.size()];
    	return set.toArray(result);
    }
	
    public String[] getSecondLevel(String key) {
    	if (key == null) {
    		return new String[0];
    	}
    	HashMap<String, List<String>> map = mCities.get(key);
    	if (map == null) {
    		return new String[0];
    	}
    	Set<String> set = map.keySet();
    	String[] result = new String[set.size()];
    	return set.toArray(result);
    }
    
    public String[] getThirdLevel(String firstKey, String secondKey) {
    	HashMap<String, List<String>> map = mCities.get(firstKey);
    	if (map == null) {
    		return new String[0];
    	}
    	List<String> list = map.get(secondKey);
    	if (list == null) {
    		return new String[0];
    	}
    	return list.toArray(new String[list.size()]);
    }
}
