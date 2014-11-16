package cn.boweikeji.wuliu.driver.bean;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class UserInfo implements Serializable {

	private static final long serialVersionUID = -3150201271050256513L;
	
	private String driver_cd;
	private String driver_name;
	private String driver_type;
	private String comp_name;
	private String attract_no;
	private String state;
	private String card_id;
	private String credit_level;
	private String phone;
	private String passwd;
	private String truck_type_code;
	
	public UserInfo () {
		
	}
	
	public UserInfo (JSONObject object) {
		update(object);
	}
	
	public void update(JSONObject object) {
		if (object != null) {
			setDriver_cd(object.optString("driver_cd"));
			setDriver_name(object.optString("driver_name"));
			setDriver_type(object.optString("driver_type"));
			setComp_name(object.optString("comp_name"));
			setAttract_no(object.optString("attract_no"));
			setState(object.optString("state"));
			setCard_id(object.optString("card_id"));
			setCredit_level(object.optString("credit_level"));
			setPhone(object.optString("phone"));
			setTruck_type_code(object.optString("trunk_type_code"));
			if (object.has("passwd")) {
				setPasswd(object.optString("passwd"));
			}
		} 
	}
	
	public String getDriver_cd() {
		return driver_cd;
	}

	public void setDriver_cd(String driver_cd) {
		this.driver_cd = driver_cd;
	}

	public String getDriver_name() {
		return driver_name;
	}

	public void setDriver_name(String driver_name) {
		this.driver_name = driver_name;
	}

	public String getDriver_type() {
		return driver_type;
	}

	public void setDriver_type(String driver_type) {
		this.driver_type = driver_type;
	}

	public String getComp_name() {
		return comp_name;
	}

	public void setComp_name(String comp_name) {
		this.comp_name = comp_name;
	}

	public String getAttract_no() {
		return attract_no;
	}

	public void setAttract_no(String attract_no) {
		this.attract_no = attract_no;
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

	public String getTruck_type_code() {
		return truck_type_code;
	}

	public void setTruck_type_code(String truck_type_code) {
		this.truck_type_code = truck_type_code;
	}

	@Override
	public String toString() {
		try {
			JSONObject obj = new JSONObject();
			obj.put("driver_cd", driver_cd);
			obj.put("driver_name", driver_name);
			obj.put("driver_type", driver_type);
			obj.put("comp_name", comp_name);
			obj.put("attract_no", attract_no);
			obj.put("state", state);
			obj.put("card_id", card_id);
			obj.put("credit_level", credit_level);
			obj.put("trunk_type_code", truck_type_code);
			obj.put("phone", phone);
			obj.put("passwd", passwd);
			return obj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
}
