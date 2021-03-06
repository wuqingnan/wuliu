package cn.boweikeji.wuliu.driver.bean;

import java.io.Serializable;

import org.json.JSONObject;

public class Order implements Serializable {

	/**
	 * 序列化
	 */
	private static final long serialVersionUID = -5746465561829742849L;

	private String create_date;
	private String pick_time;
	private String goods_name;
	private String goods_cd;
	private int goods_value;
	private int goods_cost;
	private int goods_type_code;
	private int truck_type_code;
	private int weight;
	private int mess_fee;
	private int is_order;
	private int is_ticked;
	private int state;
	private double distance;
	private String remark;

	// 信息发送人电话
	private String phone;
	// 信息发送人类型
	private int supplyer_type;
	private String supplyer_name;
	private String supplyer_phone;
	private String start_addr;
	private String reciver;
	private String reciver_phone;
	private String end_addr;

	public String getCreate_date() {
		return create_date;
	}

	public void setCreate_date(String create_date) {
		this.create_date = create_date;
	}

	public String getPick_time() {
		return pick_time;
	}

	public void setPick_time(String pick_time) {
		this.pick_time = pick_time;
	}

	public String getGoods_name() {
		return goods_name;
	}

	public void setGoods_name(String goods_name) {
		this.goods_name = goods_name;
	}

	public String getGoods_cd() {
		return goods_cd;
	}

	public void setGoods_cd(String goods_cd) {
		this.goods_cd = goods_cd;
	}

	public int getGoods_value() {
		return goods_value;
	}

	public void setGoods_value(int goods_value) {
		this.goods_value = goods_value;
	}

	public int getGoods_cost() {
		return goods_cost;
	}

	public void setGoods_cost(int goods_cost) {
		this.goods_cost = goods_cost;
	}

	public int getGoods_type_code() {
		return goods_type_code;
	}

	public void setGoods_type_code(int goods_type_code) {
		this.goods_type_code = goods_type_code;
	}

	public int getTruck_type_code() {
		return truck_type_code;
	}

	public void setTruck_type_code(int truck_type_code) {
		this.truck_type_code = truck_type_code;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public int getMess_fee() {
		return mess_fee;
	}

	public void setMess_fee(int mess_fee) {
		this.mess_fee = mess_fee;
	}

	public int getIs_order() {
		return is_order;
	}

	public void setIs_order(int is_order) {
		this.is_order = is_order;
	}

	public int getIs_ticked() {
		return is_ticked;
	}

	public void setIs_ticked(int is_ticked) {
		this.is_ticked = is_ticked;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public int getSupplyer_type() {
		return supplyer_type;
	}

	public void setSupplyer_type(int supplyer_type) {
		this.supplyer_type = supplyer_type;
	}

	public String getSupplyer_name() {
		return supplyer_name;
	}

	public void setSupplyer_name(String supplyer_name) {
		this.supplyer_name = supplyer_name;
	}

	public String getSupplyer_phone() {
		return supplyer_phone;
	}

	public void setSupplyer_phone(String supplyer_phone) {
		this.supplyer_phone = supplyer_phone;
	}

	public String getStart_addr() {
		return start_addr;
	}

	public void setStart_addr(String start_addr) {
		this.start_addr = start_addr;
	}

	public String getReciver() {
		return reciver;
	}

	public void setReciver(String reciver) {
		this.reciver = reciver;
	}

	public String getReciver_phone() {
		return reciver_phone;
	}

	public void setReciver_phone(String reciver_phone) {
		this.reciver_phone = reciver_phone;
	}

	public String getEnd_addr() {
		return end_addr;
	}

	public void setEnd_addr(String end_addr) {
		this.end_addr = end_addr;
	}

	/**
	 * 是否为预约单
	 * 
	 * @return
	 */
	public boolean isOrder() {
		return getIs_order() > 0;
	}

	/**
	 * 是否已评价
	 * 
	 * @return
	 */
	public boolean isTicked() {
		return getIs_ticked() > 0;
	}

	/**
	 * 是否免费
	 * 
	 * @return
	 */
	public boolean isFree() {
		return getMess_fee() == 1;
	}

	public static Order parseOrderDetailJson(JSONObject infos) {
		Order order = new Order();
		order.setGoods_cd(infos.optString("goods_cd"));
		order.setState(infos.optInt("state"));
		order.setPick_time(infos.optString("pick_time"));
		order.setCreate_date(infos.optString("create_date"));
		order.setGoods_name(infos.optString("goods_name"));
		order.setGoods_type_code(infos.optInt("goods_type_code"));
		order.setTruck_type_code(infos.optInt("trunk_type_code"));
		order.setWeight(infos.optInt("weight"));
		order.setGoods_value(infos.optInt("goods_value"));
		order.setMess_fee(infos.optInt("mess_fee"));
		order.setGoods_cost(infos.optInt("goods_cost"));
		order.setRemark(infos.optString("remark"));

		order.setPhone(infos.optString("phone"));
		order.setSupplyer_type(infos.optInt("supplyer_type"));
		order.setSupplyer_name(infos.optString("supplyer_name"));
		order.setSupplyer_phone(infos.optString("supplyer_phone"));
		order.setStart_addr(infos.optString("start_addr"));
		order.setReciver(infos.optString("reciver"));
		order.setReciver_phone(infos.optString("reciver_phone"));
		order.setEnd_addr(infos.optString("end_addr"));
		return order;
	}
}
