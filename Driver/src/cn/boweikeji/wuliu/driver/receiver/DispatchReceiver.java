package cn.boweikeji.wuliu.driver.receiver;

import java.util.List;

import cn.boweikeji.wuliu.driver.Const;
import cn.boweikeji.wuliu.driver.activity.MainActivity;
import cn.boweikeji.wuliu.driver.activity.WelcomeActivity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DispatchReceiver extends BroadcastReceiver {

	private static final String TAG = DispatchReceiver.class.getSimpleName();
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "shizy---DispatchReceiver");
		intent.putExtra("push", true);
		if (isAppRunning(context)) {
			int type = intent.getIntExtra("type", -1);
			if (baseActivity(context).equals(MainActivity.class.getName()) && type == Const.PUSH_TYPE_ROB) {
				Intent newIntent = new Intent(context, MainActivity.class);
				newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				newIntent.putExtras(intent);
				context.startActivity(newIntent);
			} else {
				openApp(context, intent);
			}
		} else {
			openApp(context, intent);
		}
	}
	
	private void openApp(Context context, Intent intent) {
		Intent newIntent = new Intent(context, WelcomeActivity.class);
		newIntent.setAction(Intent.ACTION_MAIN);
		newIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		newIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		newIntent.putExtras(intent);
		context.startActivity(newIntent);
	}

	/**
	 * 判断应用是否已经启动
	 * @param context
	 * @return
	 */
	private boolean isAppRunning(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> list = am.getRunningTasks(100);
		for (RunningTaskInfo info : list) {
			if (info.topActivity.getPackageName().equals(context.getPackageName())
					&& info.baseActivity.getPackageName().equals(context.getPackageName())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 获取栈底Activity
	 * @param context
	 * @return
	 */
	private String baseActivity(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> list = am.getRunningTasks(100);
		for (RunningTaskInfo info : list) {
			if (info.baseActivity.getPackageName().equals(context.getPackageName())) {
				return info.baseActivity.getClassName();
			}
		}
		return "";
	}
}
