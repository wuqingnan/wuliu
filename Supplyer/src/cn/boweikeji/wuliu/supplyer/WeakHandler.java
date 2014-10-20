package cn.boweikeji.wuliu.supplyer;

import java.lang.ref.WeakReference;



import android.os.Handler;
import android.os.Message;

public abstract class WeakHandler<T> extends Handler {
	
	private WeakReference<T> mReference = null;
	
	public WeakHandler(T reference) {
		mReference = new WeakReference<T>(reference);
	}
	
	@Override
	public void handleMessage(Message msg) {
		T t = mReference.get();
		if (t == null || msg == null) {
			return;
		}
		handleMessage(t, msg);
	}
	
	public abstract void handleMessage(T t, Message msg);
	
}
