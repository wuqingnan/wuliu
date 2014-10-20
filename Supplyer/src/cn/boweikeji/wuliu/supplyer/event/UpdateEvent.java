package cn.boweikeji.wuliu.supplyer.event;

public class UpdateEvent extends BaseEvent {

	private boolean mNeedUpdate;
	private boolean mForce;
	private String mVersion;
	private String mUrl;
	private String mContent;
	
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
	
	public String getVersion() {
		return mVersion;
	}
	
	public void setVersion(String version) {
		mVersion = version;
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
