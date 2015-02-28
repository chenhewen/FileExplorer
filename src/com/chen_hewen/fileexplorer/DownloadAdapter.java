package com.chen_hewen.fileexplorer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 
 * <br>类描述: 
 * <br>功能详细描述:12121
 * 
 * @author  chenhewen
 * @date  [2014年8月4日]
 */
public class DownloadAdapter extends BaseAdapter {

	List mData;
	Context mContext;
	int mProgress;
	List<ViewHolder> mHolders;

	public DownloadAdapter(Context context) {
		mContext = context;
		mData = new ArrayList<DownloadItem>();
		mHolders = new ArrayList<ViewHolder>();
	}

	public void addData(List data) {
		mData = data;
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * 
	 * <br>类描述: 持有缓存对象
	 * <br>功能详细描述:
	 * 
	 * @author  chenhewen
	 * @date  [2014年8月4日]
	 */
	static class ViewHolder {
		ImageView mImage;
		TextView mName;
		ProgressBar mBar;
		Button mButton;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.download_list_item, null);
			holder.mImage = (ImageView) convertView.findViewById(R.id.download_fileicon);
			holder.mName = (TextView) convertView.findViewById(R.id.download_filename);
			holder.mButton = (Button) convertView.findViewById(R.id.download_btn);
			holder.mBar = (ProgressBar) convertView.findViewById(R.id.download_bar);
			holder.mButton.setOnClickListener(new BtnListener(position));
			mHolders.add(holder);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.mImage.setBackgroundResource(((DownloadItem) mData.get(position)).getImageId());
		holder.mName.setText(((DownloadItem) mData.get(position)).getName());

		return convertView;

	}

	/**
	 * 
	 * <br>类描述:响应下载事件
	 * <br>功能详细描述:
	 * 
	 * @author  chenhewen
	 * @date  [2014年8月4日]
	 */
	private class BtnListener implements OnClickListener {
		private int mPosition;
		private MyAsynctask mTask;
		private String mUrlStr;
		private String mPath;
		private String mFileName;

		public BtnListener(int position) {
			this.mPosition = position;
		}
		@Override
		public void onClick(View v) {

			DownloadItem item = (DownloadItem) mData.get(mPosition);
			Log.i("test", item.getName());
			mUrlStr = item.getUrlStr();
			mFileName = item.getName();
			mPath = item.getPath();
			ViewHolder holder = mHolders.get(mPosition);

			if (item.getState() == DownloadItem.State.UNSTART) {
				mTask = new MyAsynctask(mContext, holder, 1, DownloadAdapter.this, item);
				mTask.executeOnExecutor(Executors.newCachedThreadPool(), mUrlStr, mFileName, mPath);
				holder.mButton.setText("暂停");
			} else if (item.getState() == DownloadItem.State.BUSY) {
				mTask.setFlag(false);
				mTask = null;
				holder.mButton.setText("继续");
			} else if (item.getState() == DownloadItem.State.PAUSE) {
				holder.mButton.setText("暂停");
				mTask = new MyAsynctask(mContext, holder, 1, DownloadAdapter.this, item);
				mTask.executeOnExecutor(Executors.newCachedThreadPool(), mUrlStr, mFileName, mPath);
			} else if (item.getState() == DownloadItem.State.OVER) {
				holder.mButton.setText("完成");
			}
		}
	}

	public List<ViewHolder> getHolders() {
		return mHolders;
	}
}
