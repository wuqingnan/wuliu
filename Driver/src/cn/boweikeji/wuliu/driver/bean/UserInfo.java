package cn.boweikeji.wuliu.driver.bean;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class UserInfo implements Serializable {

	private static final long serialVersionUID = -3150201271050256513L;
	
	private String driver_cd;
	private String driver_name;
	private int driver_type;
	private String comp_name;
	private String attract_no;
	private int state;
	private String card_id;
	private int credit_level;
	private String phone;
	private String passwd;
	private int truck_type_code;
	private int load_weight;
	private String truck_no;
	private String city_code;
	private String city_name;
	
	public UserInfo () {
		
	}
	
	public UserInfo (JSONObject object) {
		update(object);
	}
	
	public void update(JSONObject object) {
		if (object != null) {
			setDriver_cd(object.optString("driver_cd"));
			setDriver_name(object.optString("driver_name"));
			setDriver_type(object.optInt("driver_type"));
			setComp_name(object.optString("comp_name"));
			setAttract_no(object.optString("attract_no"));
			setState(object.optInt("state"));
			setCard_id(object.optString("card_id"));
			setCredit_level(object.optInt("credit_level"));
			setPhone(object.optString("phone"));
			setTruck_type_code(object.optInt("trunk_type_code"));
			setLoad_weight(object.optInt("load_weight"));
			setTruck_no(object.optString("trunk_no"));
			setCity_code(object.optString("city_code"));
			setCity_name(object.optString("city_name"));
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

	public int getDriver_type() {
		return driver_type;
	}

	public void setDriver_type(int driver_type) {
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

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getCard_id() {
		return card_id;
	}

	public void setCard_id(String card_id) {
		this.card_id = card_id;
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

	public int getTruck_type_code() {
		return truck_type_code;
	}

	public void setTruck_type_code(int truck_type_code) {
		this.truck_type_code = truck_type_code;
	}

	public int getLoad_weight() {
		return load_weight;
	}

	public void setLoad_weight(int load_weight) {
		this.load_weight = load_weight;
	}

	public String getTruck_no() {
		return truck_no;
	}

	public void setTruck_no(String truck_no) {
		this.truck_no = truck_no;
	}

	public String getCity_code() {
		return city_code;
	}

	public void setCity_code(String city_code) {
		this.city_code = city_code;
	}

	public String getCity_name() {
		return city_name;
	}

	public void setCity_name(String city_name) {
		this.city_name = city_name;
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
			obj.put("load_weight", load_weight);
			obj.put("truck_no", truck_no);
			obj.put("city_code", city_code);
			obj.put("city_name", city_name);
			return obj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
}
