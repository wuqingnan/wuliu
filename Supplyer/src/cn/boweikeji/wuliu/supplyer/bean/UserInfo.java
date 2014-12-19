package cn.boweikeji.wuliu.supplyer.bean;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class UserInfo implements Serializable {

	private static final long serialVersionUID = -3150201271050256513L;

	private String supplyer_cd;
	private String supplyer_name;
	private int supplyer_type;
	private int state;
	private int credit_level;
	private String phone;
	private String passwd;

	public UserInfo() {

	}

	public UserInfo(JSONObject object) {
		update(object);
	}

	public void update(JSONObject object) {
		if (object != null) {
			setSupplyer_cd(object.optString("supplyer_cd"));
			setSupplyer_name(object.optString("supplyer_name"));
			setSupplyer_type(object.optInt("supplyer_type"));
			setState(object.optInt("state"));
			setCredit_level(object.optInt("credit_level"));
			setPhone(object.optString("phone"));
			if (object.has("passwd")) {
				setPasswd(object.optString("passwd"));
			}
		}
	}

	public String getSupplyer_cd() {
		return supplyer_cd;
	}

	public void setSupplyer_cd(String supplyer_cd) {
		this.supplyer_cd = supplyer_cd;
	}

	public String getSupplyer_name() {
		return supplyer_name;
	}

	public void setSupplyer_name(String supplyer_name) {
		this.supplyer_name = supplyer_name;
	}

	public int getSupplyer_type() {
		return supplyer_type;
	}

	public void setSupplyer_type(int supplyer_type) {
		this.supplyer_type = supplyer_type;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getCredit_level() {
		return credit_level;
	}

	public void setCredit_level(int credit_level) {
		this.credit_level = credit_level;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	@Override
	public String toString() {
		try {
			JSONObject obj = new JSONObject();
			obj.put("supplyer_cd", supplyer_cd);
			obj.put("supplyer_name", supplyer_name);
			obj.put("supplyer_type", supplyer_type);
			obj.put("state", state);
			obj.put("credit_level", credit_level);
			obj.put("phone", phone);
			obj.put("passwd", passwd);
			return obj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
}
