package cn.boweikeji.wuliu.driver.receiver;

import org.json.JSONException;
import org.json.JSONObject;

import cn.boweikeji.wuliu.driver.Const;
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

	public static final String TAG = DriverReceiver.class.getSimpleName();
	
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
		case PushConsts.GET_CLIENTID:
			Const.clientid = bundle.getString("clientid");
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
			String infos = obj.optJSONObject("infos").toString();
			sendNotify(context, type, title, infos);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void sendNotify(Context context, int type, String title, String infos) {
		int id = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
		Intent intent = new Intent(context, DispatchReceiver.class);
		intent.putExtra("type", type);
		intent.putExtra("infos", infos);

		PendingIntent pIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		Notification notification = new NotificationCompat.Builder(context)
				.setTicker(title)
				.setAutoCancel(true)
				.setWhen(System.currentTimeMillis())
				.setDefaults(Notification.DEFAULT_SOUND)
				.setSmallIcon(R.drawable.icon)
				.setLargeIcon(
						BitmapFactory.decodeResource(context.getResources(),
								R.drawable.icon))
				.setContentTitle("物流生意宝").setContentText(title)
				.setContentIntent(pIntent).build();
		
		((NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE)).notify(id,
				notification);
	}

}
