package cn.boweikeji.wuliu.supplyer.bean;

import java.io.Serializable;

import org.json.JSONObject;

import android.text.TextUtils;
import cn.boweikeji.wuliu.supplyer.api.BaseParams;

public class Order implements Serializable {

	/**
	 * 序列化
	 */
	private static final long serialVersionUID = -5479381025006216331L;

	private static final int SOURCE_IPHONE = 0;
	private static final int SOURCE_ANDROID = 1;
	private static final int SOURCE_BROWSER = 2;

	private String goods_cd;
	private String goods_name;
	private int goods_value;
	private int goods_type_code;
	private int weight;
	private int truck_type_code;
	private int goods_cost;
	private int mess_fee;
	private int source_code;

	private double gps_addr_j;
	private double gps_addr_w;

	private int state;
	private int credit;
	private int stars;

	// 信息发送人电话
	private String sender_phone;
	// 信息发送人类型
	private int supplyer_type;
	private String supplyer_name;
	private String supplyer_phone;
	private String start_addr;

	private String reciver;
	private String reciver_phone;
	private String end_addr;

	private String pick_time;
	private int valid_type;

	private String remark;

	private String create_date;

	public Order() {
		source_code = SOURCE_ANDROID;
		state = 0;
	}

	public String getGoods_cd() {
		return goods_cd;
	}

	public void setGoods_cd(String goods_cd) {
		this.goods_cd = goods_cd;
	}

	public String getGoods_name() {
		return goods_name;
	}

	public void setGoods_name(String goods_name) {
		this.goods_name = goods_name;
	}

	public int getGoods_value() {
		return goods_value;
	}

	public void setGoods_value(int goods_value) {
		this.goods_value = goods_value;
	}

	public int getGoods_type_code() {
		return goods_type_code;
	}

	public void setGoods_type_code(int goods_type_code) {
		this.goods_type_code = goods_type_code;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public int getTruck_type_code() {
		return truck_type_code;
	}

	public void setTruck_type_code(int truck_type_code) {
		this.truck_type_code = truck_type_code;
	}

	public int getGoods_cost() {
		return goods_cost;
	}

	public void setGoods_cost(int goods_cost) {
		this.goods_cost = goods_cost;
	}

	public int getMess_fee() {
		return mess_fee;
	}

	public void setMess_fee(int mess_fee) {
		this.mess_fee = mess_fee;
	}

	public int getSource_code() {
		return source_code;
	}

	public void setSource_code(int source_code) {
		this.source_code = source_code;
	}

	public double getGps_addr_w() {
		return gps_addr_w;
	}

	public void setGps_addr_w(double gps_addr_w) {
		this.gps_addr_w = gps_addr_w;
	}

	public double getGps_addr_j() {
		return gps_addr_j;
	}

	public void setGps_addr_j(double gps_addr_j) {
		this.gps_addr_j = gps_addr_j;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getCredit() {
		return credit;
	}

	public void setCredit(int credit) {
		this.credit = credit;
	}

	public int getStars() {
		return stars;
	}

	public void setStars(int stars) {
		this.stars = stars;
	}

	public String getSender_phone() {
		return sender_phone;
	}

	public void setSender_phone(String phone) {
		this.sender_phone = phone;
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

	public String getPick_time() {
		return pick_time;
	}

	public void setPick_time(String pick_time) {
		this.pick_time = pick_time;
	}

	public int getValid_type() {
		return valid_type;
	}

	public void setValid_type(int valid_type) {
		this.valid_type = valid_type;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getCreate_date() {
		return create_date;
	}

	public void setCreate_date(String create_date) {
		this.create_date = create_date;
	}

	public BaseParams getPublishParams() {
		BaseParams params = new BaseParams();
		params.add("goods_name",
				TextUtils.isEmpty(goods_name) ? BaseParams.PARAM_DEFAULT
						: goods_name);
		params.add("goods_value", goods_value > 0 ? "" + goods_value
				: BaseParams.PARAM_DEFAULT);
		params.add("goods_type_code", "" + goods_type_code);
		params.add("weight", weight >= 0 ? "" + weight
				: BaseParams.PARAM_DEFAULT);
		params.add("trunk_type_code", "" + truck_type_code);
		params.add("goods_cost", goods_cost > 0 ? "" + goods_cost
				: BaseParams.PARAM_DEFAULT);
		params.add("mess_fee", "" + mess_fee);
		params.add("source_code", "" + source_code);
		params.add("gps_addr_j", gps_addr_j > 0 ? "" + gps_addr_j
				: BaseParams.PARAM_DEFAULT);
		params.add("gps_addr_w", gps_addr_w > 0 ? "" + gps_addr_w
				: BaseParams.PARAM_DEFAULT);
		params.add("state", "" + state);
		params.add("start_addr", start_addr);
		params.add("end_addr", end_addr);
		params.add("reciver",
				TextUtils.isEmpty(reciver) ? BaseParams.PARAM_DEFAULT : reciver);
		params.add("reciver_phone",
				TextUtils.isEmpty(reciver_phone) ? BaseParams.PARAM_DEFAULT
						: reciver_phone);
		params.add("remark",
				TextUtils.isEmpty(remark) ? BaseParams.PARAM_DEFAULT : remark);
		params.add("pick_time",
				TextUtils.isEmpty(pick_time) ? BaseParams.PARAM_DEFAULT
						: pick_time);
		params.add("valid_type", "" + valid_type);
		params.add("supplyer_name",
				TextUtils.isEmpty(supplyer_name) ? BaseParams.PARAM_DEFAULT
						: supplyer_name);
		params.add("supplyer_phone",
				TextUtils.isEmpty(supplyer_phone) ? BaseParams.PARAM_DEFAULT
						: supplyer_phone);
		return params;
	}

	public BaseParams getChangeParams() {
		BaseParams params = new BaseParams();
		params.add("goods_cd", goods_cd);
		params.add("supplyer_name",
				TextUtils.isEmpty(supplyer_name) ? BaseParams.PARAM_DEFAULT
						: supplyer_name);
		params.add("supplyer_phone",
				TextUtils.isEmpty(supplyer_phone) ? BaseParams.PARAM_DEFAULT
						: supplyer_phone);
		params.add("start_addr", start_addr);
		params.add("end_addr", end_addr);
		params.add("reciver_name",
				TextUtils.isEmpty(reciver) ? BaseParams.PARAM_DEFAULT : reciver);
		params.add("reciver_phone",
				TextUtils.isEmpty(reciver_phone) ? BaseParams.PARAM_DEFAULT
						: reciver_phone);
		params.add("mess_fee", "" + mess_fee);
		params.add("goods_cost", goods_cost >= 0 ? "" + goods_cost
				: BaseParams.PARAM_DEFAULT);
		params.add("remark",
				TextUtils.isEmpty(remark) ? BaseParams.PARAM_DEFAULT : remark);
		return params;
	}

	public static Order parseOrderDetailJson(JSONObject infos) {
		Order order = new Order();
		order.setGoods_cd(infos.optString("goods_cd"));
		order.setState(infos.optInt("state"));
		order.setPick_time(infos.optString("pick_time"));
		order.setCreate_date(infos.optString("create_date"));
		order.setGoods_name(infos.optString("goods_name"));
		order.setGoods_type_code(infos.optInt("goods_type_code"));
		order.setTruck_type_code(infos.optInt("need_trunk_type"));
		order.setWeight(infos.optInt("weight"));
		order.setGoods_value(infos.optInt("goods_value"));
		order.setMess_fee(infos.optInt("mess_fee"));
		order.setGoods_cost(infos.optInt("goods_cost"));
		order.setRemark(infos.optString("remark"));

		order.setSender_phone(infos.optString("sender_phone"));
		order.setSupplyer_type(infos.optInt("supplyer_type"));
		order.setSupplyer_name(infos.optString("supplyer_name"));
		order.setSupplyer_phone(infos.optString("supplyer_phone"));
		order.setStart_addr(infos.optString("start_addr"));
		order.setReciver(infos.optString("reciver"));
		order.setReciver_phone(infos.optString("reciver_phone"));
		order.setEnd_addr(infos.optString("end_addr"));

		order.setCredit(infos.optInt("credit"));
		order.setStars(infos.optInt("stars"));
		return order;
	}
}
