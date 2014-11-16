package cn.boweikeji.wuliu.driver.bean;

import java.io.Serializable;

import com.baidu.location.BDLocation;

import android.text.TextUtils;
import cn.boweikeji.wuliu.driver.WLApplication;
import cn.boweikeji.wuliu.driver.api.BaseParams;
import cn.boweikeji.wuliu.driver.manager.LoginManager;

public class FindFilter implements Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2095827522299458864L;
	
	public static final int MESSAGE_FREE_ALL = 0;
	public static final int MESSAGE_FREE_YES = 1;
	public static final int MESSAGE_FREE_NO = 2;

	private String start_addr;
	
	private String end_addr;
	
	private int truck_type_code;
	
	private int mess_fee;
	
	private int page_num;
	
	private int goods_type_code;
	
	public FindFilter() {
		setPage_num(1);
	}

	public String getStart_addr() {
		return start_addr;
	}

	public void setStart_addr(String start_addr) {
		this.start_addr = start_addr;
	}

	public String getEnd_addr() {
		return end_addr;
	}

	public void setEnd_addr(String end_addr) {
		this.end_addr = end_addr;
	}

	public int getTruck_type_code() {
		return truck_type_code;
	}

	public void setTrunk_type_code(int trunk_type_code) {
		this.truck_type_code = trunk_type_code;
	}

	public int getMess_fee() {
		return mess_fee;
	}

	public void setMess_fee(int mess_fee) {
		this.mess_fee = mess_fee;
	}

	public int getPage_num() {
		return page_num;
	}

	public void setPage_num(int page_num) {
		this.page_num = page_num;
	}

	public int getGoods_type_code() {
		return goods_type_code;
	}

	public void setGoods_type_code(int goods_type_code) {
		this.goods_type_code = goods_type_code;
	}
	
	public BaseParams getFindParams() {
		BaseParams params = new BaseParams();
		params.add("method", "qrySupplyRecords");
		params.add("start_addr", TextUtils.isEmpty(getStart_addr()) ? BaseParams.PARAM_DEFAULT : getStart_addr());
		params.add("end_addr", TextUtils.isEmpty(getEnd_addr()) ? BaseParams.PARAM_DEFAULT : getEnd_addr());
		params.add("trunk_type_code", "" + getTruck_type_code());
		params.add("mess_fee", "" + getMess_fee());
		params.add("page_num", "" + getPage_num());
		params.add("goods_type_code", "" + getGoods_type_code());
		BDLocation location = WLApplication.getLocationClient().getLastKnownLocation();
		if (location == null) {
			params.add("gps_j", BaseParams.PARAM_DEFAULT);
			params.add("gps_w", BaseParams.PARAM_DEFAULT);
		} else {
			params.add("gps_j", "" + location.getLongitude());
			params.add("gps_w", "" + location.getLatitude());
		}
		if (LoginManager.getInstance().hasLogin()) {
			UserInfo info = LoginManager.getInstance().getUserInfo();
			params.add("driver_cd", info.getDriver_cd());
			params.add("passwd", info.getPasswd());
		} else {
			params.add("driver_cd", BaseParams.PARAM_DEFAULT);
			params.add("passwd", BaseParams.PARAM_DEFAULT);
		}
		return params;
	}
	
	
}
