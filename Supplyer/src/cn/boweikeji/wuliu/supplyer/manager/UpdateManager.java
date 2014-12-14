package cn.boweikeji.wuliu.supplyer.manager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import cn.boweikeji.wuliu.supplyer.Const;
import cn.boweikeji.wuliu.supplyer.R;
import cn.boweikeji.wuliu.supplyer.api.BaseParams;
import cn.boweikeji.wuliu.supplyer.bean.UpdateInfo;
import cn.boweikeji.wuliu.supplyer.http.AsyncHttp;
import cn.boweikeji.wuliu.supplyer.utils.DeviceInfo;
import cn.boweikeji.wuliu.supplyer.utils.FileUtils;

import com.loopj.android.http.JsonHttpResponseHandler;

public class UpdateManager {

	private static final String TAG = UpdateManager.class.getSimpleName();

	private static final String FILE_NAME = "/update.info";

	private static JsonHttpResponseHandler mResponseHandler = new JsonHttpResponseHandler() {

		public void onSuccess(int statusCode, Header[] headers,
				JSONObject response) {
			requestResult(response);
		};

		public void onFailure(int statusCode, Header[] headers,
				Throwable throwable, JSONObject errorResponse) {
			requestResult(null);
		};
	};

	public static void checkUpdate() {
		checkUpdate(mResponseHandler);
	}

	public static void checkUpdate(JsonHttpResponseHandler handler) {
		BaseParams params = new BaseParams();
		params.add("method", "updateAPP");
		params.add("app_name", "ANDSUP");
		params.add("now_version", "" + DeviceInfo.getVersionCode());
		AsyncHttp.get(Const.URL_UPDATE, params, handler);
	}

	private static void requestResult(JSONObject response) {
		if (response != null && response.length() > 0) {
			Log.d(TAG, "shizy---response: " + response.toString());
			try {
				int res = response.getInt("res");
				if (res == 2) {// 成功
					saveUpdateInfo(response.optJSONObject("infos"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public static void saveUpdateInfo(JSONObject infos) {
		if (infos == null) {
			return;
		}
		File file = new File(Const.getFilePath() + FILE_NAME);
		if (FileUtils.fileExist(file)) {
			FileUtils.deleteFile(file);
		}
		FileOutputStream fos = null;
		try {
			file.createNewFile();
			fos = new FileOutputStream(file);
			String string = infos.toString();
			fos.write(string.getBytes());
			fos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static UpdateInfo readUpdateInfo() {
		UpdateInfo info = null;
		File file = new File(Const.getFilePath() + FILE_NAME);
		if (FileUtils.fileExist(file)) {
			FileInputStream fis = null;
			ByteArrayOutputStream baos = null;
			try {
				fis = new FileInputStream(file);
				baos = new ByteArrayOutputStream();
				byte[] buf = new byte[256];
				int len = -1;
				while ((len = fis.read(buf)) > 0) {
					baos.write(buf, 0, len);
				}
				String string = new String(baos.toByteArray());
				info = new UpdateInfo(new JSONObject(string));
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			} finally {
				try {
					if (fis != null) {
						fis.close();
					}
					if (baos != null) {
						baos.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return info;
	}

	public static void clear() {
		File file = new File(Const.getFilePath() + FILE_NAME);
		if (FileUtils.fileExist(file)) {
			FileUtils.deleteFile(file);
		}
	}

	public static void showUpdateDialog(final Context context, final UpdateInfo info,
			DialogInterface.OnClickListener positiveListener,
			DialogInterface.OnClickListener negativeListener) {
		AlertDialog dialog = new AlertDialog.Builder(context)
				.setTitle(info.getVersionName())
				.setMessage(info.getContent())
				.setCancelable(false)
				.setPositiveButton(R.string.upgrade, positiveListener)
				.setNegativeButton(
						info.isForce() ? R.string.exit : R.string.cancel,
						negativeListener).create();
		dialog.show();
	}
}
