package com.chen_hewen.fileexplorer;

/**
 * 
 * <br>类描述: 下载项
 * <br>功能详细描述:
 * 
 * @author  chenhewen
 * @date  [2014年8月4日]
 */
public class DownloadItem {
	private int mImageId;
	private String mName;
	private String mUrlStr;
	private State mState = State.UNSTART;
	private String mPath;

	/**
	 * 
	 * <br>类描述: 下载状态
	 * <br>功能详细描述:
	 * 
	 * @author  chenhewen
	 * @date  [2014年8月4日]
	 */
	public enum State {
		UNSTART, BUSY, PAUSE, OVER
	}

	public DownloadItem(int imageId, String path, String name, String urlStr) {
		this.mImageId = imageId;
		this.mPath = path;
		this.mName = name;
		this.mUrlStr = urlStr;
	}

	public String getUrlStr() {
		return mUrlStr;
	}

	public void setUrlStr(String urlStr) {
		this.mUrlStr = urlStr;
	}

	public int getImageId() {
		return mImageId;
	}

	public void setImageId(int mImageId) {
		this.mImageId = mImageId;
	}

	public String getName() {
		return mName;
	}
	public void setName(String mName) {
		this.mName = mName;
	}

	public State getState() {
		return mState;
	}

	public void setState(State state) {
		this.mState = state;
	}

	public String getPath() {
		return mPath;
	}

	public void setPath(String path) {
		this.mPath = path;
	}

}
