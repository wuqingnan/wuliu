package cn.boweikeji.wuliu.driver.bean.city;

public class Section implements ICityListItem {

	private String mLetter;
	
	public String getLetter() {
		return mLetter;
	}

	public void setLetter(String letter) {
		mLetter = letter;
	}

	@Override
	public int getType() {
		return TYPE_SECTION;
	}

	@Override
	public String getTitle() {
		return getLetter();
	}

}
