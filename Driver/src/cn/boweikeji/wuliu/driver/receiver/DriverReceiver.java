package cn.boweikeji.wuliu.driver.receiver;

import org.json.JSONException;
import org.json.JSONObject;

import cn.boweikeji.wuliu.driver.R;
import cn.boweikeji.wuliu.driver.activity.MainActivity;

import com.igexin.sdk.PushConsts;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class DriverReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		Log.d("GetuiSdkDemo", "onReceive() action=" + bundle.getInt("action"));
		switch (bundle.getInt(PushConsts.CMD_ACTION)) {
		case PushConsts.GET_MSG_DATA:
			byte[] payload = bundle.getByteArray("payload");
			if (payload != null) {
				String data = new String(payload);
				Log.d("GetuiSdkDemo", "Got Payload:" + data);
				parseData(context, data);
			}
			break;
		default:
			break;
		}
	}

	private void parseData(Context context, String data) {
		if (data == null || data.trim().length() == 0) {
			return;
		}
		try {
			JSONObject obj = new JSONObject(data);
			int type = obj.optInt("type");
			String title = obj.optString("title");
			sendNotify(context, type, title);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void sendNotify(Context context, int type, String title) {
		int id = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
//		Intent intent = new Intent(context, MainActivity.class);
//		intent.setAction(Intent.ACTION_MAIN);
//		intent.addCategory(Intent.CATEGORY_LAUNCHER);
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//		intent.putExtra("type", type);
		Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());

		PendingIntent pIntent = PendingIntent.getActivity(context, id, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		Notification notification = new NotificationCompat.Builder(context)
				.setTicker(title)
				.setAutoCancel(true)
				.setWhen(System.currentTimeMillis())
				.setSmallIcon(R.drawable.icon)
				.setLargeIcon(
						BitmapFactory.decodeResource(context.getResources(),
								R.drawable.portrait_default))
				.setContentTitle("物流生意宝").setContentText(title)
				.setContentIntent(pIntent).build();
		
		((NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE)).notify(id,
				notification);
	}

}
