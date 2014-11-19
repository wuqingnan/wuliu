package cn.boweikeji.wuliu.driver.bean;

public class Order {

	private String create_date;
	private String goods_name;
	private String goods_cd;
	private int is_order;
	private int is_ticked;
	private int state;
	private double distance;
	
	public String getCreate_date() {
		return create_date;
	}
	
	public void setCreate_date(String create_date) {
		this.create_date = create_date;
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

	/**
	 * 是否为预约单
	 * @return
	 */
	public boolean isOrder() {
		return getIs_order() > 0;
	}
	
	/**
	 * 是否已评价
	 * @return
	 */
	public boolean isTicked() {
		return getIs_ticked() > 0;
	}
}
