package com.wuliu.client.fragment;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.wuliu.client.Const;
import com.wuliu.client.R;
import com.wuliu.client.activity.MainActivity;
import com.wuliu.client.api.BaseParams;
import com.wuliu.client.bean.Order;
import com.wuliu.client.bean.UserInfo;
import com.wuliu.client.manager.LoginManager;
import com.wuliu.client.utils.EncryptUtil;
import com.wuliu.client.utils.Util;

public class OrderFragment extends BaseFragment {

	private static final String TAG = OrderFragment.class.getSimpleName();
	
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			if (view == mMenuBtn) {
				if (getActivity() instanceof MainActivity) {
					((MainActivity) getActivity())
							.onClickTitle(OrderFragment.this);
				}
			}
		}
	};

	private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			Log.d(TAG, "shizy---arg2: " + arg2);
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
	@InjectView(R.id.order_list)
	ListView mListView;
	
	private View mRootView;
	private ProgressDialog mProgressDialog;
	
	private int mPage;
	private boolean mMore;
	
	private OrderAdapter mAdapter = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_order, null);
		return mRootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		init();
	}

	private void init() {
		ButterKnife.inject(this, mRootView);
		initTitle();
		initView();
		initData();
	}

	private void initTitle() {
		mTitle.setText(R.string.title_order);
		mMenuBtn.setImageResource(R.drawable.btn_title_back);
		mMenuBtn.setOnClickListener(mOnClickListener);
	}

	private void initView() {
		mAdapter = new OrderAdapter(getActivity());
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(mOnItemClickListener);
	}
	
	private void initData() {
		mPage = 1;
		mMore = true;
		loadData();
	}
	
	private void loadData() {
		if (mMore) {
			showProgressDialog();
			
			AsyncHttpClient client = new AsyncHttpClient();
			client.setURLEncodingEnabled(true);
			
			UserInfo info = LoginManager.getInstance().getUserInfo();
			BaseParams params = new BaseParams();
			params.add("method", "getAllMySupplys");
			params.add("supplyer_cd", info.getSupplyer_cd());
			params.add("passwd", info.getPasswd());
			params.add("page_num", "" + mPage);
			
			Log.d(TAG, "URL: " + AsyncHttpClient.getUrlWithQueryString(true, Const.URL_ORDER_LIST, params));
			client.get(Const.URL_ORDER_LIST, params, mRequestHandler);
		}
	}
	
	private void showProgressDialog() {
		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDialog(getActivity());
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
				Util.showTips(getActivity(), msg);
				if (res == 2) {//³É¹¦
					mPage++;
					int pageCount = response.getInt("pagetotalnum");
					if (mPage > pageCount) {
						mMore = false;
					}
					JSONArray infos = response.optJSONArray("infos");
					if (infos != null && infos.length() > 0) {
						JSONObject temp = null;
						Order order = null;
						List<Order> data = new ArrayList<Order>();
						for (int i = 0; i < infos.length(); i++) {
							temp = infos.optJSONObject(i);
							order = new Order();
							order.setState(temp.optInt("state"));
							order.setGoodsCD(temp.optString("goods_cd"));
							order.setGoodsName(temp.optString("goods_name"));
							order.setCreateDate(temp.optString("create_date"));
							data.add(order);
						}
						mAdapter.addData(data);
						mAdapter.notifyDataSetChanged();
					}
				}
				return;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		Util.showTips(getActivity(), getString(R.string.request_failed));
	}
	
	public static class OrderAdapter extends BaseAdapter {
		
		private Context mContext;
		private LayoutInflater mInflater;
		private List<Order> mData = new ArrayList<Order>();
		
		public OrderAdapter(Context context) {
			mContext = context;
			mInflater = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			return mData.size();
		}

		@Override
		public Object getItem(int arg0) {
			return mData.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		public void clear() {
			mData.clear();
		}
		
		public void addData(List<Order> order) {
			mData.addAll(order);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup viewGroup) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.order_list_item, null);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			Order order = mData.get(position);
			holder.refresh(order);
			return convertView;
		}
		
	}
	
	public static class ViewHolder {
		
		@InjectView(R.id.order_list_item_name)
		TextView mOrderName;
		@InjectView(R.id.order_list_item_date)
		TextView mOrderDate;
		@InjectView(R.id.order_list_item_state)
		TextView mState;
		
		public ViewHolder(View parent) {
			ButterKnife.inject(this, parent);
		}
		
		public void refresh(Order order) {
			if (order != null) {
				mState.setText(order.getState() + "");
				mOrderName.setText(order.getGoodsName());
				mOrderDate.setText(order.getCreateDate());
			}
		}
		
	}
}
