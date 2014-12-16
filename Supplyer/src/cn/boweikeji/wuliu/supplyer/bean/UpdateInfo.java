package cn.boweikeji.wuliu.supplyer.bean;

import org.json.JSONObject;

import cn.boweikeji.wuliu.utils.DeviceInfo;

public class UpdateInfo {

	private boolean mNeedUpdate;
	private boolean mForce;
	private int mVersionCode;
	private String mVersionName;
	private String mUrl;
	private String mContent;

	public UpdateInfo(JSONObject infos) {
		setForce(infos.optInt("isforce") == 1);
		setVersionCode(infos.optInt("version_cd"));
		setVersionName(infos.optString("version_name"));
		setUrl(infos.optString("url"));
		setContent(infos.optString("content"));
		setNeedUpdate(getVersionCode() > DeviceInfo.getVersionCode());
	}

	public boolean isNeedUpdate() {
		return mNeedUpdate;
	}
	
	public void setNeedUpdate(boolean needUpdate) {
		mNeedUpdate = needUpdate;
	}

	public boolean isForce() {
		return mForce;
	}

	public void setForce(boolean force) {
		mForce = force;
	}

	public int getVersionCode() {
		return mVersionCode;
	}

	public void setVersionCode(int versionCode) {
		mVersionCode = versionCode;
	}
	
	public String getVersionName() {
		return mVersionName;
	}
	
	public void setVersionName(String versionName) {
		mVersionName = versionName;
	}

	public String getUrl() {
		return mUrl;
	}

	public void setUrl(String url) {
		mUrl = url;
	}

	public String getContent() {
		return mContent;
	}

	public void setContent(String content) {
		mContent = content;
	}
}
