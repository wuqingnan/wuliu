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
import cn.boweikeji.wuliu.driver.bean.FindFilter;
import cn.boweikeji.wuliu.driver.utils.Util;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class FindResultActivity extends BaseActivity {

	private static final String TAG = FindResultActivity.class.getSimpleName();
	
	private static final String KEY_FILTER = "filter";
	
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			if (view == mBack) {
				finish();
			}
		}
	};
	
	private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
		@Override
		public void onRefresh() {
			refresh();
		}
	};
	
	private JsonHttpResponseHandler mRequestHandler = new JsonHttpResponseHandler() {
		
		public void onFinish() {
			loadFinish();
		};
		
		public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
			requestResult(response);
		};
		
		public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
			requestResult(null);
		};
	};
	
	@InjectView(R.id.titlebar_leftBtn)
	ImageView mBack;
	@InjectView(R.id.titlebar_title)
	TextView mTitle;
	@InjectView(R.id.swiperefresh)
	SwipeRefreshLayout mSwipeRefreshLayout;
	@InjectView(R.id.listview)
	ListView mListView;
	@InjectView(R.id.emptyview)
	TextView mEmptyView;
	
	private FindFilter mFilter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_result);
		init();
	}
	
	private void init() {
		ButterKnife.inject(this);
		initTitle();
		initView();
		initData();
	}
	
	private void initTitle() {
		mTitle.setText(R.string.title_find_result);
		mBack.setImageResource(R.drawable.btn_title_back);
		mBack.setOnClickListener(mOnClickListener);
	}
	
	private void initView() {
		mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
		mSwipeRefreshLayout.setColorSchemeResources(R.color.color1, R.color.color2, R.color.color3,
                R.color.color4);
	}
	
	private void initData() {
		Intent intent = getIntent();
		mFilter = (FindFilter) intent.getSerializableExtra(KEY_FILTER);
		mSwipeRefreshLayout.setRefreshing(true);
		refresh();
	}
	
    private void refresh() {
    	mFilter.setPage_num(1);
    	mEmptyView.setVisibility(View.GONE);
    	mSwipeRefreshLayout.setEnabled(false);
    	loadData();
    }
    
    private void loadData() {
    	AsyncHttpClient client = new AsyncHttpClient();
		client.setURLEncodingEnabled(true);
		
		BaseParams params = mFilter.getFindParams();
		
		Log.d(TAG, "URL: " + AsyncHttpClient.getUrlWithQueryString(true, Const.URL_FIND, params));
		client.get(Const.URL_FIND, params, mRequestHandler);
    }
	
    private void requestResult(JSONObject response) {
		if (response != null && response.length() > 0) {
			Log.d(TAG, "shizy---response: " + response.toString());
			try {
				int res = response.getInt("res");
				String msg = response.getString("msg");
				Util.showTips(this, msg);
				if (res == 2) {//成功
					int pagetotalnum = response.optInt("pagetotalnum");
					if (pagetotalnum <= 0) {
						mEmptyView.setVisibility(View.VISIBLE);
					}
				}
				return;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		Util.showTips(this, getString(R.string.register_failed));
	}
    
    private void loadFinish() {
    	mSwipeRefreshLayout.setEnabled(true);
    	mSwipeRefreshLayout.setRefreshing(false);
    }
    
	public static void startFindResultActivity(Context context, FindFilter filter) {
		Intent intent = new Intent(context, FindResultActivity.class);
		intent.putExtra(KEY_FILTER, filter);
		context.startActivity(intent);
	}
}
