package cn.boweikeji.wuliu.supplyer.bean;

import java.io.Serializable;

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
	
	private String mGoodsCD;
	private String mGoodsName;
	private int mGoodsValue;
	private int mGoodsType;
	private int mWeight;
	private int mTrunkType;
	private int mPay;
	private int mFree;
	private int mSource;
	
	private double mLat;
	private double mLon;
	
	private int mState;
	private int mCredit;
	private int mStars;
	
	private String mFromName;
	private String mFromPhone;
	private String mFromAddress;
	
	private String mToName;
	private String mToPhone;
	private String mToAddress;
	
	private String mPickTime;
	private int mValidTime;
	
	private String mRemarks;
	
	private String mCreateDate;
	
	public Order() {
		mSource = SOURCE_ANDROID;
		mState = 0;
	}

	public String getGoodsCD() {
		return mGoodsCD;
	}

	public void setGoodsCD(String goodsCD) {
		mGoodsCD = goodsCD;
	}

	public String getGoodsName() {
		return mGoodsName;
	}

	public void setGoodsName(String goodsName) {
		mGoodsName = goodsName;
	}

	public int getGoodsValue() {
		return mGoodsValue;
	}
	
	public void setGoodsValue(int goodsValue) {
		mGoodsValue = goodsValue;
	}
	
	public int getGoodsType() {
		return mGoodsType;
	}

	public void setGoodsType(int goodsType) {
		mGoodsType = goodsType;
	}

	public int getWeight() {
		return mWeight;
	}

	public void setWeight(int weight) {
		mWeight = weight;
	}

	public int getTrunkType() {
		return mTrunkType;
	}

	public void setTrunkType(int trunkType) {
		mTrunkType = trunkType;
	}

	public int getPay() {
		return mPay;
	}
	
	public void setPay(int pay) {
		mPay = pay;
	}

	public int getFree() {
		return mFree;
	}

	public void setFree(int free) {
		mFree = free;
	}

	public int getSource() {
		return mSource;
	}

	public void setSource(int source) {
		mSource = source;
	}

	public double getLat() {
		return mLat;
	}

	public void setLat(double lat) {
		mLat = lat;
	}

	public double getLon() {
		return mLon;
	}

	public void setLon(double lon) {
		mLon = lon;
	}

	public int getState() {
		return mState;
	}

	public void setState(int state) {
		mState = state;
	}

	public int getCredit() {
		return mCredit;
	}

	public void setCredit(int credit) {
		mCredit = credit;
	}

	public int getStars() {
		return mStars;
	}

	public void setStars(int stars) {
		mStars = stars;
	}

	public String getFromName() {
		return mFromName;
	}

	public void setFromName(String fromName) {
		mFromName = fromName;
	}

	public String getFromPhone() {
		return mFromPhone;
	}

	public void setFromPhone(String fromPhone) {
		mFromPhone = fromPhone;
	}

	public String getFromAddress() {
		return mFromAddress;
	}

	public void setFromAddress(String fromAddress) {
		mFromAddress = fromAddress;
	}

	public String getToName() {
		return mToName;
	}

	public void setToName(String toName) {
		mToName = toName;
	}

	public String getToPhone() {
		return mToPhone;
	}

	public void setToPhone(String toPhone) {
		mToPhone = toPhone;
	}

	public String getToAddress() {
		return mToAddress;
	}

	public void setToAddress(String toAddress) {
		mToAddress = toAddress;
	}

	public String getPickTime() {
		return mPickTime;
	}

	public void setPickTime(String pickTime) {
		mPickTime = pickTime;
	}

	public int getValidTime() {
		return mValidTime;
	}

	public void setValidTime(int validTime) {
		mValidTime = validTime;
	}
	
	public String getRemarks() {
		return mRemarks;
	}
	
	public void setRemarks(String remarks) {
		mRemarks = remarks;
	}
	
	public String getCreateDate() {
		return mCreateDate;
	}

	public void setCreateDate(String createDate) {
		mCreateDate = createDate;
	}

	public BaseParams getPublishParams() {
		BaseParams params = new BaseParams();
		params.add("goods_name", TextUtils.isEmpty(mGoodsName) ? BaseParams.PARAM_DEFAULT : mGoodsName);
		params.add("goods_value", mGoodsValue >= 0 ? "" + mGoodsValue : BaseParams.PARAM_DEFAULT);
		params.add("goods_type_code", "" + mGoodsType);
		params.add("weight", mWeight >= 0 ? "" + mWeight : BaseParams.PARAM_DEFAULT);
		params.add("trunk_type_code", "" + mTrunkType);
		params.add("goods_cost", mPay >= 0 ? "" + mPay : BaseParams.PARAM_DEFAULT);
		params.add("mess_fee", "" + mFree);
		params.add("source_code", "" + mSource);
		params.add("gps_addr_j", mLon > 0 ? "" + mLon : BaseParams.PARAM_DEFAULT);
		params.add("gps_addr_w", mLat > 0 ? "" + mLat : BaseParams.PARAM_DEFAULT);
		params.add("state", "" + mState);
		params.add("start_addr", mFromAddress);
		params.add("end_addr", mToAddress);
		params.add("reciver", mToName);
		params.add("reciver_phone", mToPhone);
		params.add("remark", TextUtils.isEmpty(mRemarks) ? BaseParams.PARAM_DEFAULT : mRemarks);
		params.add("pick_time", TextUtils.isEmpty(mPickTime) ? BaseParams.PARAM_DEFAULT : mPickTime);
		params.add("valid_type", "" + mValidTime);
		params.add("supplyer_name", mFromName);
		params.add("supplyer_phone", mFromPhone);
		return params;
	}
	
	public BaseParams getChangeParams() {
		BaseParams params = new BaseParams();
		params.add("goods_cd", mGoodsCD);
		params.add("supplyer_name", mFromName);
		params.add("supplyer_phone", mFromPhone);
		params.add("start_addr", mFromAddress);
		params.add("end_addr", mToAddress);
		params.add("reciver_name", mToName);
		params.add("reciver_phone", mToPhone);
		params.add("mess_fee", "" + mFree);
		params.add("goods_cost", mPay >= 0 ? "" + mPay : BaseParams.PARAM_DEFAULT);
		params.add("remark", TextUtils.isEmpty(mRemarks) ? BaseParams.PARAM_DEFAULT : mRemarks);
		return params;
	}
}
