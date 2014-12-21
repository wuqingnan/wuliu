package cn.boweikeji.wuliu.driver.bean;

import java.io.Serializable;

import android.text.TextUtils;
import cn.boweikeji.wuliu.driver.Const;
import cn.boweikeji.wuliu.driver.api.BaseParams;
import cn.boweikeji.wuliu.driver.manager.LoginManager;
import cn.boweikeji.wuliu.utils.EncryptUtil;

public class RegisterInfo implements Serializable {

	/**
	 * 序列化
	 */
	private static final long serialVersionUID = -3889870888210489041L;

	private String driver_name;
	private int driver_type;
	private String comp_name;
	private String phone;
	private String card_id;
	private String card_photo;
	private String trunk_no;
	private String area_code;
	private int truck_type_code;
	private float load_weight;
	private String remark;
	private String passwd;
	private String attract_no;
	private String mIDImagePath;

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

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getCard_id() {
		return card_id;
	}

	public void setCard_id(String card_id) {
		this.card_id = card_id;
	}

	public String getCard_photo() {
		return card_photo;
	}

	public void setCard_photo(String card_photo) {
		this.card_photo = card_photo;
	}

	public String getTrunk_no() {
		return trunk_no;
	}

	public void setTrunk_no(String trunk_no) {
		this.trunk_no = trunk_no;
	}

	public String getArea_code() {
		return area_code;
	}

	public void setArea_code(String area_code) {
		this.area_code = area_code;
	}

	public int getTruck_type_code() {
		return truck_type_code;
	}

	public void setTruck_type_code(int truck_type_code) {
		this.truck_type_code = truck_type_code;
	}

	public float getLoad_weight() {
		return load_weight;
	}

	public void setLoad_weight(float load_weight) {
		this.load_weight = load_weight;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public String getAttract_no() {
		return attract_no;
	}

	public void setAttract_no(String attract_no) {
		this.attract_no = attract_no;
	}

	public String getIDImagePath() {
		return mIDImagePath;
	}

	public void setIDImagePath(String path) {
		mIDImagePath = path;
	}
	
	public String getMD5Passwd() {
		if (getPasswd() != null) {
			return EncryptUtil.encrypt(getPasswd(), EncryptUtil.MD5);
		}
		return null;
	}

	public BaseParams getRegisterParams() {
		BaseParams params = new BaseParams();
		params.add("method", "registerDriver");
		params.add("driver_name", getDriver_name());
		params.add("driver_type", "" + getDriver_type());
		params.add("comp_name", getDriver_type() == 1 ? getComp_name() : BaseParams.PARAM_DEFAULT);
		params.add("phone", getPhone());
		params.add("card_id", getCard_id());
		params.add("card_photo", getCard_photo());
		params.add("trunk_no", getTrunk_no());
		params.add("area_code", getArea_code());
		params.add("trunk_type_code", "" + getTruck_type_code());
		params.add("load_weight", "" + getLoad_weight());
		params.add("remark", TextUtils.isEmpty(getRemark()) ? BaseParams.PARAM_DEFAULT : getRemark());
		params.add("passwd", getMD5Passwd());
		params.add("attract_no", TextUtils.isEmpty(getAttract_no()) ? BaseParams.PARAM_DEFAULT : getAttract_no());
		return params;
	}
	
	public BaseParams getChangeProfileParams() {
		BaseParams params = new BaseParams();
		params.add("method", "changeDriver");
		params.add("driver_cd", LoginManager.getInstance().getUserInfo().getDriver_cd());
		params.add("driver_name", getDriver_name());
		params.add("driver_type", "" + getDriver_type());
		params.add("comp_name", getDriver_type() == 1 ? getComp_name() : BaseParams.PARAM_DEFAULT);
		params.add("card_id", getCard_id());
		params.add("card_photo", TextUtils.isEmpty(getCard_photo()) ? BaseParams.PARAM_DEFAULT : getCard_photo());
		params.add("trunk_no", getTrunk_no());
		params.add("area_code", getArea_code());
		params.add("trunk_type_code", "" + getTruck_type_code());
		params.add("load_weight", "" + getLoad_weight());
		params.add("remark", TextUtils.isEmpty(getRemark()) ? BaseParams.PARAM_DEFAULT : getRemark());
		params.add("passwd", getMD5Passwd());
		return params;
	}

}
