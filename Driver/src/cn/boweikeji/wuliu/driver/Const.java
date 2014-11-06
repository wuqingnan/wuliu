package cn.boweikeji.wuliu.driver;

import android.content.Context;

public class Const {
	
	public static final String NULL = "-9";
	
	public static final String URL_SYSTEM_MSG = "http://style177.sinaapp.com/logistics/mobile/actList.html";
	
	public static final String URL_LOGIN = "http://218.21.213.76:7201/bss/loginCheck2.action";
	public static final String URL_REGISTER = "http://218.21.213.76:7201/bss/registerGoodSupplyer.action";
	public static final String URL_SEND_GOODS = "http://218.21.213.76:7201/bss/sendGoodInfos.action";
	public static final String URL_ORDER_LIST = "http://218.21.213.76:7201/bss/getAllMySupplys.action";
	public static final String URL_ORDER_DETAIL = "http://218.21.213.76:7201/bss/getMySupplyCos.action";
	public static final String URL_CANCEL_ORDER = "http://218.21.213.76:7201/bss/cancelMessage.action";
	public static final String URL_CHANGE_ORDER = "http://218.21.213.76:7201/bss/changeCos.action";
	public static final String URL_CONSULT_ORDER = "http://218.21.213.76:7201/bss/supplyerDropMessage.action";
	public static final String URL_CONFIRM_ORDER = "http://218.21.213.76:7201/bss/supplyerCompleteMessage.action";
	public static final String URL_COMMENT_ORDER = "http://218.21.213.76:7201/bss/ticketDriver.action";
	public static final String URL_POSITION_UPLOAD = "http://218.21.213.76:7201/bss/collectDriverInfos.action";
	public static final String URL_CHANGE_PASSWORD = "http://218.21.213.76:7201/bss/changeSupplyerPwd.action";
	public static final String URL_SUGGEST = "http://218.21.213.76:7201/bss/supplyerSuggest.action";
	public static final String URL_UPDATE = "http://218.21.213.76:7201/bss/updateAPP.action";
	public static final String URL_NEAR_DRIVER = "http://218.21.213.76:7201/bss/getNearDriversByDrv.action";
	
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
