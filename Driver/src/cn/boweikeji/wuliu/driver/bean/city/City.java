package cn.boweikeji.wuliu.driver.bean.city;

import org.json.JSONObject;

public class City implements ICityListItem {

	private String mName;
	private String mProvice;
	
	public City() {
		
	}
	
	public City(JSONObject object) {
		if (object != null) {
			setName(object.optString("name"));
			setProvice(object.optString("provice"));
		}
	}
	
	public String getName() {
		return mName;
	}
	
	public void setName(String name) {
		mName = name;
	}
	
	public String getProvice() {
		return mProvice;
	}
	
	public void setProvice(String provice) {
		mProvice = provice;
	}
	
	@Override
	public int getType() {
		return TYPE_CITY;
	}

	@Override
	public String getTitle() {
		return getName();
	}
	
}
