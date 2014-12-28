package cn.boweikeji.wuliu.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.boweikeji.wuliu.supplyer.Const;
import cn.boweikeji.wuliu.supplyer.R;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

public class Util {

	public static boolean isPhoneNumber(String number) {
		if (number == null || number.length() != 11) {
			return false;
		}
		Pattern pattern = Pattern.compile("^1[3,5,7,8]\\d{9}$");
		// 正则匹配11位号码是否满足手机号码格式
		if (pattern.matcher(number).matches()) {
			return true;
		}
		return false;
	}

	/**
	 * 判断是否为正整数或0
	 * 
	 * @param number
	 * @return
	 */
	public static boolean isInteger(String number) {
		if (number == null || number.length() == 0) {
			return false;
		}
		Pattern pattern = Pattern.compile("^(0|[1-9][0-9]*)$");
		// 正则匹配11位号码是否满足手机号码格式
		if (pattern.matcher(number).matches()) {
			return true;
		}
		return false;
	}

	/**
	 * 判断是否为正小数
	 * 
	 * @param number
	 * @return
	 */
	public static boolean isDecimal(String number) {
		if (number == null || number.length() == 0) {
			return false;
		}
		Pattern pattern = Pattern.compile("^(0|[1-9][0-9]*)\\.[0-9]{1,}$");
		// 正则匹配11位号码是否满足手机号码格式
		if (pattern.matcher(number).matches()) {
			return true;
		}
		return false;
	}

	/**
	 * 验证密码合法性
	 * 
	 * @return true-valid false invalid
	 */
	public static boolean isPasswordValid(String data) {
		if (data != null && !data.equals("")) {
			// 匹配英文数字下划线，长度6-16之间
			String rex = "[a-z0-9A-Z_]{6,16}";
			Pattern pattern = Pattern.compile(rex);
			Matcher m = pattern.matcher(data);
			return m.matches();
		}
		return false;
	}

	/**
	 * 验证用户名合法性
	 * 
	 * @return true-valid false invalid
	 */
	public static boolean isUserValid(String data) {
		if (data != null && !data.equals("")) {
			// 匹配中英文下划线，长度4-16之间
			String rex = "[a-z0-9A-Z_\u4e00-\u9fa5]{4,16}";
			Pattern pattern = Pattern.compile(rex);
			Matcher m = pattern.matcher(data);
			return m.matches();
		}
		return false;
	}

	/**
	 * 验证效验码合法性
	 * 
	 * @return true-valid false invalid
	 */
	public static boolean isCodeValid(String code) {
		if (code != null && !code.equals("")) {
			String rex = "[0-9]{4}";
			Pattern pattern = Pattern.compile(rex);
			Matcher m = pattern.matcher(code);
			return m.matches();
		}
		return false;
	}

	/**
	 * 验证身份证号码合法性
	 * 
	 * @return true-valid false invalid
	 */
	public static boolean isIDNumberValid(String code) {
		if (code != null && !code.equals("")) {
			String rex = "([0-9]{17})([0-9Xx])";
			Pattern pattern = Pattern.compile(rex);
			Matcher m = pattern.matcher(code);
			return m.matches();
		}
		return false;
	}

	/**
	 * 显示提示
	 * 
	 * @param tips
	 */
	public static void showTips(Context context, String tips) {
		Toast.makeText(context, tips, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 发送短信
	 * 
	 * @param context
	 * @param phone
	 * @param msg
	 */
	public static void sendMessage(Context context, String phone, String msg) {
		Intent intent = null;
		if (phone != null && Util.isPhoneNumber(phone)) {
			Uri uri = Uri.parse("smsto:" + phone);
			intent = new Intent(Intent.ACTION_SENDTO, uri);
		} else {
			intent = new Intent(Intent.ACTION_VIEW);
			intent.setType("vnd.android-dir/mms-sms");
		}
		intent.putExtra("sms_body", msg);
		context.startActivity(intent);
	}

	/**
	 * 显示分享框
	 * 
	 * @param activity
	 */
	public static void showShare(Activity activity) {
		ShareSDK.initSDK(activity);
		OnekeyShare oks = new OnekeyShare();
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();

		// 分享时Notification的图标和文字
		oks.setNotification(R.drawable.icon,
				activity.getString(R.string.app_name));
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle("物流生意宝，是一款覆盖全国的智能找货，找车手机软件");
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl("http://boweikeji.cn/");
		// text是分享文本，所有平台都需要这个字段
		oks.setText("物流生意宝，这是一款覆盖全国的智能找货，找车手机软件。可以随时随地准确撮合成物流生意，让发货人不再难找到合适的货车，不再让车俩在为空车奔波为犯愁。是博为科技有限责任公司荣誉推出的一款基于移动互联网的手机客户端产品。欢迎大家使用手机扫描下载，注册使用。");
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		oks.setImageUrl("http://interface.boweikeji.cn/bss/uploadfiles/allCode.png");
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl("http://boweikeji.cn/");
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		oks.setComment("");
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite(activity.getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl("http://boweikeji.cn/");

		// 启动分享GUI
		oks.show(activity);
	}
}
