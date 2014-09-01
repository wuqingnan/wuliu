package com.wuliu.client.bean;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class UserInfo implements Serializable {

	private static final long serialVersionUID = -3150201271050256513L;
	
	private String supplyer_cd;
	private String supplyer_name;
	private String supplyer_type;
	private String state;
	private String card_id;
	private String credit_level;
	private String phone;
	private String passwd;
	
	public UserInfo () {
		
	}
	
	public UserInfo (JSONObject object) {
		update(object);
	}
	
	public void update(JSONObject object) {
		if (object != null) {
			setSupplyer_cd(object.optString("supplyer_cd"));
			setSupplyer_name(object.optString("supplyer_name"));
			setSupplyer_type(object.optString("supplyer_type"));
			setState(object.optString("state"));
			setCard_id(object.optString("card_id"));
			setCredit_level(object.optString("credit_level"));
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

	public String getSupplyer_type() {
		return supplyer_type;
	}

	public void setSupplyer_type(String supplyer_type) {
		this.supplyer_type = supplyer_type;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCard_id() {
		return card_id;
	}

	public void setCard_id(String card_id) {
		this.card_id = card_id;
	}

	public String getCredit_level() {
		return credit_level;
	}

	public void setCredit_level(String credit_level) {
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
			obj.put("card_id", card_id);
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
