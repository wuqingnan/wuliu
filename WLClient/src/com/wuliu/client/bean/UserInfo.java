package com.wuliu.client.bean;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class UserInfo implements Serializable {

	private static final long serialVersionUID = -3150201271050256513L;
	
	private String mUsername;
	private String mPassword;
	
	public UserInfo () {
		
	}
	
	public UserInfo (JSONObject object) {
		if (object != null) {
			setUserName(object.optString("username"));
			setPassword(object.optString("password"));
		} 
	}
	
	public String getUserName() {
		return mUsername;
	}
	
	public void setUserName(String username) {
		mUsername = username;
	}
	
	public String getPassword() {
		return mPassword;
	}
	
	public void setPassword(String password) {
		mPassword = password;
	}
	
	@Override
	public String toString() {
		try {
			JSONObject obj = new JSONObject();
			obj.put("username", mUsername);
			obj.put("password", mPassword);
			return obj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
}
