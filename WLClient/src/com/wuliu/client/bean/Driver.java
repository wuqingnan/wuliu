package com.wuliu.client.bean;

public class Driver {

	private String driver_cd;
	private String driver_name;
	private String phone;
	private String credit_level;
	private int stars;
	
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
	
}
