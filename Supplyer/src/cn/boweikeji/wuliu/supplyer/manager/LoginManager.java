package cn.boweikeji.wuliu.supplyer.manager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import cn.boweikeji.wuliu.supplyer.Const;
import cn.boweikeji.wuliu.supplyer.api.BaseParams;
import cn.boweikeji.wuliu.supplyer.bean.UserInfo;
import cn.boweikeji.wuliu.supplyer.event.LoginEvent;
import cn.boweikeji.wuliu.supplyer.event.LogoutEvent;
import cn.boweikeji.wuliu.supplyer.utils.DeviceInfo;
import cn.boweikeji.wuliu.supplyer.utils.FileUtils;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.ResponseHandlerInterface;

import de.greenrobot.event.EventBus;

public class LoginManager {

	private static final String TAG = LoginManager.class.getSimpleName();
	
	private static final String FILE_NAME = "/user.info";
	
	private JsonHttpResponseHandler mResponseHandler = new JsonHttpResponseHandler() {
		
		public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
			if (response != null && response.length() > 0) {
				Log.d(TAG, "shizy---response: " + response.toString());
				try {
					int res = response.getInt("res");
					if (res == 2) {
						setLogin(true);
						mUserInfo.update(response.optJSONObject("supplyer"));
						setUserInfo(mUserInfo);
						EventBus.getDefault().post(new LoginEvent());
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		};
	};
	
	private boolean mHasLogin;
	private UserInfo mUserInfo;
	
	private static LoginManager mInstance;
	
	private LoginManager () {
		mHasLogin = false;
	}
	
	public static LoginManager getInstance() {
		if (mInstance == null) {
			mInstance = new LoginManager();
		}
		return mInstance;
	}
	
	public boolean hasLogin() {
		return mHasLogin;
	}
	
	public void setLogin(boolean hasLogin) {
		mHasLogin = hasLogin;
	}
	
	public void login(String supplyer_cd, String passwd, ResponseHandlerInterface handler) {
		AsyncHttpClient client = new AsyncHttpClient();
		client.setURLEncodingEnabled(true);
		
		BaseParams params = new BaseParams();
		params.add("method", "loginCheck2");
		params.add("supplyer_cd", supplyer_cd);
		params.add("passwd", passwd);
		
		Log.d(TAG, "URL: " + AsyncHttpClient.getUrlWithQueryString(true, Const.URL_LOGIN, params));
		client.get(Const.URL_LOGIN, params, handler);
	}
	
	public void autoLogin() {
		if (mUserInfo == null) {
			read();
		}
		if (mUserInfo != null && !hasLogin()) {
			login(mUserInfo.getSupplyer_cd(), mUserInfo.getPasswd(), mResponseHandler);
		}
	}
	
	public void logout() {
		clear();
		mHasLogin = false;
		mUserInfo = null;
		EventBus.getDefault().post(new LogoutEvent());
	}
	
	public void setUserInfo(UserInfo info) {
		mUserInfo = info;
		save();
	}
	
	public UserInfo getUserInfo() {
		return mUserInfo;
	}
	
	private void save() {
		if (mUserInfo == null) {
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
            String string = mUserInfo.toString();
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
	
	private void read() {
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
				mUserInfo = new UserInfo(new JSONObject(string));
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
	}
	
	private void clear() {
		File file = new File(Const.getFilePath() + FILE_NAME);
		if (FileUtils.fileExist(file)) {
			FileUtils.deleteFile(file);
		}
	}
}
