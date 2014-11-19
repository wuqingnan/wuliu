package cn.boweikeji.wuliu.driver.bean.city;

import java.io.Serializable;

import org.json.JSONObject;

public class City implements ICityListItem, Serializable {

	/**
	 * 序列化
	 */
	private static final long serialVersionUID = -2044584787556920536L;
	
	private String mName;
	private String mProvice;
	private String mCode;
	
	public City() {
		
	}
	
	public City(JSONObject object) {
		if (object != null) {
			setName(object.optString("name"));
			setProvice(object.optString("provice"));
			setCode(object.optString("code"));
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
	
	public String getCode() {
		return mCode;
	}
	
	public void setCode(String code) {
		mCode = code;
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
