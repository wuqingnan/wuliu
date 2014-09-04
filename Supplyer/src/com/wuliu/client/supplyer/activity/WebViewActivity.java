package com.wuliu.client.supplyer.activity;

import butterknife.ButterKnife;
import butterknife.InjectView;

import com.wuliu.client.supplyer.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

public class WebViewActivity extends Activity {

	private static final String TAG = WebViewActivity.class.getSimpleName();
	
	public static final String URL_ABOUT = "http://interface.rrkd.cn/RRKDInterface/More/summary.htm";
	public static final String URL_GUIDE = "http://interface.rrkd.cn/RRKDInterface/More/help.htm";
	
	private static final String KEY_TITLE = "title";
	private static final String KEY_URL = "url";
	
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			if (view == mMenuBtn) {
				finish();
			}
		}
	};
	
	@InjectView(R.id.titlebar_leftBtn)
	ImageView mMenuBtn;
	@InjectView(R.id.titlebar_title)
	TextView mTitle;
	@InjectView(R.id.webview)
	WebView mWebView;
	
	private String mLoadUrl;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview);
		initView();
		initData();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mWebView.destroy();
	}
	
	private void initView() {
		ButterKnife.inject(this);
		mMenuBtn.setImageResource(R.drawable.btn_title_back);
		mMenuBtn.setOnClickListener(mOnClickListener);
		initWebView();
	}
	
	private void initWebView() {
		mWebView.getSettings().setDefaultTextEncodingName("UTF-8");
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		mWebView.setVerticalScrollBarEnabled(false);
		mWebView.setHorizontalScrollBarEnabled(false);
		mWebView.setAddStatesFromChildren(true);
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (url.startsWith("tel:")) {
					Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
					return true;
				}
				return false;
			}
		});
	}
	
	private void initData() {
		Intent intent = getIntent();
		String title = intent.getStringExtra(KEY_TITLE);
		mLoadUrl = intent.getStringExtra(KEY_URL);
		mTitle.setText(title);
		mWebView.loadUrl(mLoadUrl);
	}
	
	public static void startWebViewActivity(Context context, String title, String url) {
		Intent intent = new Intent(context, WebViewActivity.class);
		intent.putExtra(KEY_TITLE, title);
		intent.putExtra(KEY_URL, url);
		context.startActivity(intent);
	}
}
