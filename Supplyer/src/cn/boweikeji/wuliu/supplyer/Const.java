package cn.boweikeji.wuliu.supplyer;

import cn.boweikeji.wuliu.supplyer.manager.LoginManager;
import android.content.Context;

public class Const {
	
	public static final String NULL = "-9";
	
	public static final String PREFERENCE_NAME = "preference";
	public static final String KEY_VERSION = "version";
	public static final String KEY_AUTO_LOGIN = "auto_login";
	
	private static final String URL_SYSTEM_MSG = "http://www.boweikeji.cn/index.php?m=content&c=index&a=lists&catid=24&user_cd=%1$s&user_type=SUPPLYER";
	public static final String URL_ABOUT = "http://www.boweikeji.cn/index.php?m=content&c=index&a=lists&catid=2";
	public static final String URL_GUIDE = "http://www.boweikeji.cn/index.php?m=content&c=index&a=lists&catid=18";
	public static final String URL_RULES = "http://www.boweikeji.cn/index.php?m=content&c=index&a=lists&catid=28";
	
	public static final String URL_LOGIN = "bss/loginCheck2.action";
	public static final String URL_LOGOUT = "bss/supplyerLogOut.action";
	public static final String URL_REGISTER = "bss/registerGoodSupplyer.action";
	public static final String URL_SEND_GOODS = "bss/sendGoodInfos.action";
	public static final String URL_ORDER_LIST = "bss/getAllMySupplys.action";
	public static final String URL_ORDER_DETAIL = "bss/getMySupplyCos.action";
	public static final String URL_CANCEL_ORDER = "bss/cancelMessage.action";
	public static final String URL_CHANGE_ORDER = "bss/changeCos.action";
	public static final String URL_CONSULT_ORDER = "bss/supplyerDropMessage.action";
	public static final String URL_CONFIRM_ORDER = "bss/supplyerCompleteMessage.action";
	public static final String URL_COMMENT_ORDER = "bss/ticketDriver.action";
	public static final String URL_POSITION_UPLOAD = "bss/collectSupplyInfos.action";
	public static final String URL_CHANGE_PASSWORD = "bss/changeSupplyerPwd.action";
	public static final String URL_SUGGEST = "bss/supplyerSuggest.action";
	public static final String URL_UPDATE = "bss/updateAPP.action";
	public static final String URL_NEAR_DRIVER = "bss/getNearDrivers.action";
	
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
			user_cd = LoginManager.getInstance().getUserInfo().getSupplyer_cd();
		}
		return String.format(URL_SYSTEM_MSG, user_cd);
	}
}
