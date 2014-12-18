package cn.boweikeji.wuliu.supplyer.bean;

public class Driver {

	private String driver_cd;
	private String driver_name;
	private String phone;
	private String credit_level;
	private int stars;
	private int truck_type_code;
	private String truck_no;
	private int load_weight;

	public int getStars() {
		return stars;
	}

	public void setStars(int stars) {
		this.stars = stars;
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

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getCredit_level() {
		return credit_level;
	}

	public void setCredit_level(String credit_level) {
		this.credit_level = credit_level;
	}

	public int getTruck_type_code() {
		return truck_type_code;
	}

	public void setTruck_type_code(int trunk_type_code) {
		this.truck_type_code = trunk_type_code;
	}

	public String getTruck_no() {
		return truck_no;
	}

	public void setTruck_no(String truck_no) {
		this.truck_no = truck_no;
	}

	public int getLoad_weight() {
		return load_weight;
	}

	public void setLoad_weight(int load_weight) {
		this.load_weight = load_weight;
	}

}
