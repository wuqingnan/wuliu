package cn.boweikeji.wuliu.supplyer.activity;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.boweikeji.wuliu.http.AsyncHttp;
import cn.boweikeji.wuliu.supplyer.Const;
import cn.boweikeji.wuliu.supplyer.api.BaseParams;
import cn.boweikeji.wuliu.supplyer.bean.UserInfo;
import cn.boweikeji.wuliu.supplyer.manager.LoginManager;





import com.loopj.android.http.JsonHttpResponseHandler;

import cn.boweikeji.wuliu.supplyer.R;
import cn.boweikeji.wuliu.utils.EncryptUtil;
import cn.boweikeji.wuliu.utils.Util;
import cn.boweikeji.wuliu.view.ClearEditText;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ChangePasswordActivity extends BaseActivity {

	private static final String TAG = ChangePasswordActivity.class.getSimpleName();
	
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			if (view == mMenuBtn) {
				finish();
			} else if (view == mSubmit) {
				submit();
			}
		}
	};
	
	private JsonHttpResponseHandler mRequestHandler = new JsonHttpResponseHandler() {
		
		public void onFinish() {
			hideProgressDialog();
		};
		
		public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
			requestResult(response);
		};
		
		public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
			requestResult(null);
		};
	};
	
	
	@InjectView(R.id.titlebar_leftBtn)
	ImageView mMenuBtn;
	@InjectView(R.id.titlebar_title)
	TextView mTitle;
	@InjectView(R.id.password_old)
	ClearEditText mOldPassword;
	@InjectView(R.id.password_new)
	ClearEditText mNewPassword;
	@InjectView(R.id.password_repeat)
	ClearEditText mRepeatPassword;
	@InjectView(R.id.submit)
	Button mSubmit;
	
	private ProgressDialog mProgressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_password);
		initView();
	}
	
	private void initView() {
		ButterKnife.inject(this);
		mTitle.setText(R.string.title_change_password);
		mOldPassword.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_PASSWORD);
		mNewPassword.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_PASSWORD);
		mRepeatPassword.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_PASSWORD);
		mMenuBtn.setOnClickListener(mOnClickListener);
		mSubmit.setOnClickListener(mOnClickListener);
	}
	
	private void submit() {
		if (validCheck()) {
			showProgressDialog();
			String oldPass = EncryptUtil.encrypt(mOldPassword.getText().toString(), EncryptUtil.MD5);
			String newPass = EncryptUtil.encrypt(mNewPassword.getText().toString(), EncryptUtil.MD5);
			BaseParams params = new BaseParams();
			params.add("method", "changeSupplyerPwd");
			params.add("supplyer_cd", LoginManager.getInstance().getUserInfo().getSupplyer_cd());
			params.add("is_need", "1");
			params.add("old_pwd", oldPass);
			params.add("new_pwd", newPass);
			AsyncHttp.get(Const.URL_CHANGE_PASSWORD, params, mRequestHandler);
		}
	}
	
	private boolean validCheck() {
		boolean bRes = true;
		String oldPass = mOldPassword.getText().toString();
		String newPass = mNewPassword.getText().toString();
		String repeatPass = mRepeatPassword.getText().toString();
		if (oldPass == null || oldPass.equals("")) {
			Util.showTips(this, getString(
					R.string.old_password_empty));
			bRes = false;
		} else if (newPass == null || newPass.equals("")) {
			Util.showTips(this, getString(
					R.string.new_password_empty));
			bRes = false;
		} else if (repeatPass == null || repeatPass.equals("")) {
			Util.showTips(this, getString(
					R.string.repeat_password_empty));
			bRes = false;
		} else if (!Util.isUserValid(oldPass)) {
			Util.showTips(this, getResources().getString(
					R.string.old_password_invalid));
			bRes = false;
		} else if (!Util.isPasswordValid(newPass)) {
			Util.showTips(this, getResources().getString(
					R.string.new_password_invalid));
			bRes = false;
		} else if (!Util.isPasswordValid(repeatPass)) {
			Util.showTips(this, getResources().getString(
					R.string.repeat_password_invalid));
			bRes = false;
		} else if (!newPass.equals(repeatPass)) {
			Util.showTips(this, getResources().getString(
					R.string.repeat_diff_new));
			bRes = false;
		} else if (newPass.equals(oldPass)) {
			Util.showTips(this, getResources().getString(
					R.string.new_password_same));
			bRes = false;
		}
		return bRes;
	}
	
	private void showProgressDialog() {
		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDialog(this);
			mProgressDialog.setMessage(getString(R.string.requesting));
			mProgressDialog.setCancelable(false);
		}
		mProgressDialog.show();
	}
	
	private void hideProgressDialog() {
		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
		}
		mProgressDialog = null;
	}
	
	private void requestResult(JSONObject response) {
		if (response != null && response.length() > 0) {
			Log.d(TAG, "shizy---response: " + response.toString());
			try {
				int res = response.getInt("res");
				String msg = response.getString("msg");
				Util.showTips(this, msg);
				if (res == 2) {//成功
					String newPass = EncryptUtil.encrypt(mNewPassword.getText().toString(), EncryptUtil.MD5);
					UserInfo info = LoginManager.getInstance().getUserInfo();
					info.setPasswd(newPass);
					LoginManager.getInstance().setUserInfo(info);
					mOldPassword.setText(null);
					mNewPassword.setText(null);
					mRepeatPassword.setText(null);
				}
				return;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		Util.showTips(this, getString(R.string.request_failed));
	}
}
