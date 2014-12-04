package cn.boweikeji.wuliu.driver;

import android.content.Context;

public class Const {
	
	public static final String NULL = "-9";
	
	public static final String URL_SYSTEM_MSG = "http://style177.sinaapp.com/logistics/mobile/actList.html";
	
	public static final String URL_LOGIN = "http://218.21.213.76:7201/bss/driverLoginCheck.action";
	public static final String URL_REGISTER = "http://218.21.213.76:7201/bss/registerDriver.action";
	public static final String URL_ORDER_LIST = "http://218.21.213.76:7201/bss/getMyRecords.action";
	public static final String URL_ORDER_DETAIL = "http://218.21.213.76:7201/bss/getCoDetails.action";
	public static final String URL_ROB_ORDER = "http://218.21.213.76:7201/bss/robMessage.action";
	public static final String URL_DROP_ORDER = "http://218.21.213.76:7201/bss/driverDropMessage.action";
	public static final String URL_POSITION_UPLOAD = "http://218.21.213.76:7201/bss/collectDriverInfos.action";
	public static final String URL_CHANGE_PASSWORD = "http://218.21.213.76:7201/bss/changeSupplyerPwd.action";
	public static final String URL_SUGGEST = "http://218.21.213.76:7201/bss/driverSuggest.action";
	public static final String URL_UPDATE = "http://218.21.213.76:7201/bss/updateAPP.action";
	public static final String URL_NEAR_DRIVER = "http://218.21.213.76:7201/bss/getNearDriversByDrv.action";
	public static final String URL_UPLOAD_IMAGE = "http://218.21.213.76:7201/bss/doUpload.action";
	public static final String URL_FIND = "http://218.21.213.76:7201/bss/qrySupplyRecords.action";
	
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
}
