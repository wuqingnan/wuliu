package cn.boweikeji.wuliu.supplyer.manager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import cn.boweikeji.wuliu.supplyer.WLApplication;
import cn.boweikeji.wuliu.supplyer.bean.Area;
import cn.boweikeji.wuliu.supplyer.dao.AreaDao;

public class AreaManager {

	private static final String TAG = AreaManager.class.getSimpleName();
	
	public static void cityToDatabase(AreaDao dao, Context context) {
		String json = null;
		try {
			InputStream is = context.getResources().getAssets()
					.open("area");
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
				final String jsonStr = json;
				final AreaDao areaDao = dao;
				dao.callBatchTasks(new Callable<Void>() {
				    public Void call() throws SQLException {
				    	try {
							parseJSONArray(areaDao, new JSONArray(jsonStr), 0);
						} catch (JSONException e) {
							e.printStackTrace();
						}
				        return null;
				    }
				});
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}

	private static void parseJSONArray(AreaDao dao, JSONArray array, int parentId)
			throws JSONException {
		if (array == null || array.length() == 0) {
			return;
		}
		JSONObject obj = null;
		for (int i = 0; i < array.length(); i++) {
			obj = array.getJSONObject(i);
			if (obj != null) {
				int id = insert(dao, obj, parentId);
				parseJSONArray(dao, obj.optJSONArray("sub"), id);
			}
		}
	}

	private static int insert(AreaDao dao, JSONObject obj, int parentId) {
		Area area = new Area();
		area.setParentId(parentId);
		area.setName(obj.optString("name"));
		try {
			dao.createIfNotExists(area);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return area.getId();
	}

	public String[] getFirstLevel() {
		return null;
	}

	public String[] getSecondLevel(String key) {
		return null;
	}

	public String[] getThirdLevel(String firstKey, String secondKey) {
		return null;
	}
}
