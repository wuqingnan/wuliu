﻿package cn.boweikeji.wuliu.supplyer.bean;

public class Driver {

	private String driver_cd;
	private String driver_name;
	private String phone;
	private String credit_level;
	private int stars;
	private int trunk_type_code;
	
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
	
	public int getTrunk_type_code() {
		return trunk_type_code;
	}
	
	public void setTrunk_type_code(int trunk_type_code) {
		this.trunk_type_code = trunk_type_code;
	}
}