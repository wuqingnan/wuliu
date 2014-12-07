package cn.boweikeji.wuliu.driver;

import cn.boweikeji.wuliu.driver.manager.LoginManager;
import android.content.Context;
import android.util.Log;

public class Const {
	
	public static final String NULL = "-9";
	
	private static final String URL_SYSTEM_MSG = "http://www.boweikeji.cn/index.php?m=content&c=index&a=lists&catid=24&user_cd=%1$s&user_type=DRIVER";
	
	public static final String URL_LOGIN = "bss/driverLoginCheck.action";
	public static final String URL_REGISTER = "bss/registerDriver.action";
	public static final String URL_ORDER_LIST = "bss/getMyRecords.action";
	public static final String URL_ORDER_DETAIL = "bss/getCoDetails.action";
	public static final String URL_ROB_ORDER = "bss/robMessage.action";
	public static final String URL_DROP_ORDER = "bss/driverDropMessage.action";
	public static final String URL_POSITION_UPLOAD = "bss/collectDriverInfos.action";
	public static final String URL_CHANGE_PASSWORD = "bss/changeSupplyerPwd.action";
	public static final String URL_SUGGEST = "bss/driverSuggest.action";
	public static final String URL_UPDATE = "bss/updateAPP.action";
	public static final String URL_NEAR_DRIVER = "bss/getNearDriversByDrv.action";
	public static final String URL_UPLOAD_IMAGE = "bss/doUpload.action";
	public static final String URL_FIND = "bss/qrySupplyRecords.action";
	
	public static final int PUSH_TYPE_DETAIL = 1;
	public static final int PUSH_TYPE_MSG = 2;
	public static final int PUSH_TYPE_PERSONAL = 3;
	public static final int PUSH_TYPE_ROB = 4;
	
	public static String clientid = NULL;
	
	private static String sFilePath = null;
	private static String sExternalPath = null;
	
	public static void init(Context context) {
		sFilePath = context.getFilesDir().getAbsolutePath();
		sExternalPath = context.getExternalFilesDir(null).getAbsolutePath();
	}
	
	public static String getFilePath() {
		return sFilePath;
	}
	
	public static String getExternalPath() {
		return sExternalPath;
	}
	
	public static String getMsgUrl() {
		String user_cd = NULL;
		if (LoginManager.getInstance().hasLogin()) {
			user_cd = LoginManager.getInstance().getUserInfo().getDriver_cd();
		}
		return String.format(URL_SYSTEM_MSG, user_cd);
	}
}
