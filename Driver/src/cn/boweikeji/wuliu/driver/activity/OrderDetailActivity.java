package cn.boweikeji.wuliu.driver.activity;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.boweikeji.wuliu.driver.Const;
import cn.boweikeji.wuliu.driver.R;
import cn.boweikeji.wuliu.driver.adapter.OrderDetailAdapter;
import cn.boweikeji.wuliu.driver.api.BaseParams;
import cn.boweikeji.wuliu.driver.bean.Order;
import cn.boweikeji.wuliu.driver.bean.UserInfo;
import cn.boweikeji.wuliu.driver.event.OrderEvent;
import cn.boweikeji.wuliu.driver.manager.LoginManager;


import cn.boweikeji.wuliu.http.AsyncHttp;
import cn.boweikeji.wuliu.utils.Util;

import com.loopj.android.http.JsonHttpResponseHandler;

import de.greenrobot.event.EventBus;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class OrderDetailActivity extends BaseActivity {

	private static final String TAG = OrderDetailActivity.class.getSimpleName();
	
	private static final String KEY_GOODS_CD = "goods_cd";
	
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			if (view == mMenuBtn) {
				finish();
			} else if (view == mRobBtn) {
				robOrder();
			} else if (view == mDropBtn) {
				showDropDialog();
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
	
	private JsonHttpResponseHandler mOperateHandler = new JsonHttpResponseHandler() {
		
		public void onFinish() {
			hideProgressDialog();
		};
		
		public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
			operateResult(response);
		};
		
		public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
			operateResult(null);
		};
	};
	
	@InjectView(R.id.titlebar_leftBtn)
	ImageView mMenuBtn;
	@InjectView(R.id.titlebar_title)
	TextView mTitle;
	@InjectView(R.id.order_detail)
	ListView mListView;
	@InjectView(R.id.order_detail_footer)
	View mFooter;
	@InjectView(R.id.rob_order)
	Button mRobBtn;
	@InjectView(R.id.drop_order)
	Button mDropBtn;
	
	private ProgressDialog mProgressDialog;
	
	private String mGoodsCD;
	
	private OrderDetailAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_detail);
		initView();
		initData();
	}
	
	private void initView() {
		ButterKnife.inject(this);
		mTitle.setText(R.string.title_order_detail);
		mMenuBtn.setOnClickListener(mOnClickListener);
		initFooter();
		initList();
	}
	
	private void initFooter() {
		mRobBtn.setOnClickListener(mOnClickListener);
		mDropBtn.setOnClickListener(mOnClickListener);
	}
	
	private void initList() {
		mAdapter = new OrderDetailAdapter(this);
		mListView.setAdapter(mAdapter);
		mListView.setFooterDividersEnabled(false);
	}
	
	private void updateFooter(Order order) {
		int state = order.getState();
		if (state == 0 || state == 7 || state == 21 || state == 22) {
			mRobBtn.setVisibility(View.VISIBLE);
			mDropBtn.setVisibility(View.GONE);
			if (mListView.getFooterViewsCount() <= 0) {
				mListView.addFooterView(mFooter);
			}
		} else if (state == 1) {
			mRobBtn.setVisibility(View.GONE);
			mDropBtn.setVisibility(View.VISIBLE);
			if (mListView.getFooterViewsCount() <= 0) {
				mListView.addFooterView(mFooter);
			}
		} else {
			mListView.removeFooterView(mFooter);
		}
	}

	private void initData() {
		Intent intent = getIntent();
		mGoodsCD = intent.getStringExtra(KEY_GOODS_CD);
		loadDetail();
	}
	
	private void loadDetail() {
		showProgressDialog();
		UserInfo info = LoginManager.getInstance().getUserInfo();
		BaseParams params = new BaseParams();
		params.add("method", "getCoDetails");
		params.add("driver_cd", info.getDriver_cd());
		params.add("passwd", info.getPasswd());
		params.add("goods_cd", "" + mGoodsCD);
		AsyncHttp.get(Const.URL_ORDER_DETAIL, params, mRequestHandler);
	}
	
	private void refresh() {
		loadDetail();
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
				if (res == 2) {//成功
					parseJson(response.optJSONObject("info"));
				} else {
					Util.showTips(this, msg);
				}
				return;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		Util.showTips(this, getString(R.string.request_failed));
	}
	
	private void operateResult(JSONObject response) {
		if (response != null && response.length() > 0) {
			Log.d(TAG, "shizy---response: " + response.toString());
			try {
				int res = response.getInt("res");
				String msg = response.getString("msg");
				if (res == 2) {//成功
					refresh();
					EventBus.getDefault().post(new OrderEvent(mGoodsCD));
				} else {
					Util.showTips(this, msg);
				}
				return;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		Util.showTips(this, getString(R.string.request_failed));
	}
	
	private void parseJson(JSONObject infos) {
		Order order = Order.parseOrderDetailJson(infos);
		updateFooter(order);
		mAdapter.setData(order);
	}
	
	private void robOrder() {
		showProgressDialog();
		UserInfo info = LoginManager.getInstance().getUserInfo();
		BaseParams params = new BaseParams();
		params.add("method", "robMessage");
		params.add("driver_cd", info.getDriver_cd());
		params.add("passwd", info.getPasswd());
		params.add("goods_cd", "" + mGoodsCD);
		AsyncHttp.get(Const.URL_ROB_ORDER, params, mOperateHandler);
	}
	
	private void dropOrder() {
		showProgressDialog();
		UserInfo info = LoginManager.getInstance().getUserInfo();
		BaseParams params = new BaseParams();
		params.add("method", "driverDropMessage");
		params.add("driver_cd", info.getDriver_cd());
		params.add("passwd", info.getPasswd());
		params.add("goods_cd", "" + mGoodsCD);
		AsyncHttp.get(Const.URL_DROP_ORDER, params, mOperateHandler);
	}
	
	private void showDropDialog() {
		AlertDialog dialog = new AlertDialog.Builder(this)
		.setTitle(R.string.drop_order)
		.setMessage(R.string.order_drop_message)
		.setCancelable(true)
		.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				dropOrder();
			}
		})
		.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		})
		.create();
		dialog.show();
	}
	
	public static void startOrderDetailActivity(Context context, String goods_cd) {
		Intent intent = new Intent(context, OrderDetailActivity.class);
		intent.putExtra(KEY_GOODS_CD, goods_cd);
		context.startActivity(intent);
	}
}
