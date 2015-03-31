package cn.boweikeji.wuliu.driver.aidl;

import com.baidu.location.BDLocation;

import android.os.Parcel;
import android.os.Parcelable;

public class WLLocation implements Parcelable {

	public static final Parcelable.Creator<WLLocation> CREATOR = new Parcelable.Creator<WLLocation>() {
		@Override
		public WLLocation createFromParcel(Parcel source) {
			return new WLLocation(source);
		}

		@Override
		public WLLocation[] newArray(int size) {
			return new WLLocation[size];
		}
	};
	
	private BDLocation mLocation;
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	public WLLocation(Parcel in) {
		readFromParcel(in);
	}
	
	public WLLocation(BDLocation location) {
		mLocation = location;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(mLocation, flags);
	}

	public void readFromParcel(Parcel in) {
		mLocation = in.readParcelable(BDLocation.class.getClassLoader());
	}
	
	public BDLocation getBDLocation() {
		return mLocation;
	}
}
