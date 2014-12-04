package cn.boweikeji.wuliu.driver.activity;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.boweikeji.wuliu.driver.Const;
import cn.boweikeji.wuliu.driver.R;
import cn.boweikeji.wuliu.driver.api.BaseParams;
import cn.boweikeji.wuliu.driver.bean.Order;
import cn.boweikeji.wuliu.driver.bean.UserInfo;
import cn.boweikeji.wuliu.driver.event.OrderEvent;
import cn.boweikeji.wuliu.driver.manager.LoginManager;
import cn.boweikeji.wuliu.driver.utils.Util;
import de.greenrobot.event.EventBus;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class RobOrderActivity extends BaseActivity {

	private static final String TAG = RobOrderActivity.class.getSimpleName();
	private static final String KEY_ORDER = "order";
	
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			if (view == mDetailBtn) {
				detail();
			} else if (view == mRobBtn) {
				robOrder();
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
	
	@InjectView(R.id.bespeak)
	ImageView mBespeak;
	@InjectView(R.id.distance)
	TextView mDistance;
	@InjectView(R.id.name)
	TextView mName;
	@InjectView(R.id.free)
	TextView mFree;
	@InjectView(R.id.picktime)
	TextView mPickTime;
	@InjectView(R.id.from)
	TextView mFrom;
	@InjectView(R.id.to)
	TextView mTo;
	@InjectView(R.id.btn_detail)
	Button mDetailBtn;
	@InjectView(R.id.btn_rob)
	TextView mRobBtn;
	
	private ProgressDialog mProgressDialog;
	
	private Order mOrder;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rob);
		init();
	}
	
	private void init() {
		ButterKnife.inject(this);
		initView();
		initData();
	}
	
	private void initView() {
		mRobBtn.setOnClickListener(mOnClickListener);
		mDetailBtn.setOnClickListener(mOnClickListener);
	}
	
	private void initData() {
		Intent intent = getIntent();
		mOrder = (Order) intent.getSerializableExtra(KEY_ORDER);
		mBespeak.setImageResource(mOrder.isOrder() ? R.drawable.bg_msg_num : R.drawable.bg_msg_num);
		mDistance.setText("距您" + mOrder.getDistance() + "公里");
		mName.setText(mOrder.getGoods_name());
		mFree.setText(mOrder.getMess_fee() == 1 ? "免费公开" : "付费公开");
		mPickTime.setText(mOrder.getPick_time());
		mFrom.setText(mOrder.getStart_addr());
		mTo.setText(mOrder.getEnd_addr());
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
	
	private void detail() {
		OrderDetailActivity.startOrderDetailActivity(this, mOrder.getGoods_cd());
		finish();
	}
	
	private void robOrder() {
		showProgressDialog();
		AsyncHttpClient client = new AsyncHttpClient();
		client.setURLEncodingEnabled(true);
		UserInfo info = LoginManager.getInstance().getUserInfo();
		BaseParams params = new BaseParams();
		params.add("method", "robMessage");
		params.add("driver_cd", info.getDriver_cd());
		params.add("passwd", info.getPasswd());
		params.add("goods_cd", "" + mOrder.getGoods_cd());
		
		Log.d(TAG, "URL: " + AsyncHttpClient.getUrlWithQueryString(true, Const.URL_ROB_ORDER, params));
		client.get(Const.URL_ROB_ORDER, params, mRequestHandler);
	}
	
	private void requestResult(JSONObject response) {
		if (response != null && response.length() > 0) {
			Log.d(TAG, "shizy---response: " + response.toString());
			try {
				int res = response.getInt("res");
				String msg = response.getString("msg");
				Util.showTips(this, msg);
				if (res == 2) {//成功
					EventBus.getDefault().post(new OrderEvent(mOrder.getGoods_cd()));
					OrderDetailActivity.startOrderDetailActivity(this, mOrder.getGoods_cd());
					finish();
				}
				return;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		Util.showTips(this, getString(R.string.request_failed));
	}
	
	public static void startRobOrderActivity(Context context, Order order) {
		context.startActivity(robOrderIntent(context, order));
	}
	
	public static Intent robOrderIntent(Context context, Order order) {
		Intent intent = new Intent(context, RobOrderActivity.class);
		intent.putExtra(KEY_ORDER, order);
		return intent;
	}
}
