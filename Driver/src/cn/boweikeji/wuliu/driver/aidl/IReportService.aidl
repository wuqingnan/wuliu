package cn.boweikeji.wuliu.driver.aidl;
import cn.boweikeji.wuliu.driver.aidl.ILocationListener;
import cn.boweikeji.wuliu.driver.aidl.WLLocation;

interface IReportService {
	
	void setLocationListener(in ILocationListener listener);
	
	void setUserCd(String userCd);
	
	void setClientId(String clientId);
	
	void reportLocation();
	
	WLLocation getLastKnownLocation();
	
}