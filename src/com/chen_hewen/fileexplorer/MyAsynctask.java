package com.chen_hewen.fileexplorer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.chen_hewen.fileexplorer.DownloadAdapter.ViewHolder;

/**
 * 
 * <br>类描述:  线程类
 * <br>功能详细描述:
 * 
 * @author  chenhewen
 * @date  [2014年8月4日]
 */
public class MyAsynctask extends AsyncTask<String, Integer, String> {
	Context mContext;
	ViewHolder mHolder;

	String mUrlStr;
	URL mURL;
	HttpURLConnection mConn;
	InputStream mIs;
	BufferedInputStream mBis;
	OutputStream mOs;
	BufferedOutputStream mBos;
	long mSize;
	private boolean mFlag = true;
	SharedPreferences mSp;
	Editor mEditor;
	long mSavedSize;
	int mTaskId;
	BaseAdapter mAdapter;
	DownloadItem mItem;
	String mFileName;
	String mFilePath;
	long mDefaultSize = 9000000l;

	public MyAsynctask(Context context, ViewHolder holder, int taskId, BaseAdapter adapter,
			DownloadItem item) {
		mContext = context;
		mHolder = holder;
		mTaskId = taskId;
		mAdapter = adapter;
		this.mItem = item;
	}

	@SuppressLint("NewApi")
	@Override
	protected String doInBackground(String... params) {
		Log.d("asynctask", "taskId=" + mTaskId);
		String urlStr = params[0];
		mFileName = params[1];
		mFilePath = params[2];

		try {
			ConnectivityManager manager = (ConnectivityManager) mContext
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
			if (wifi != State.CONNECTED) {
				return "wifiFail";
			}

			mURL = new URL(urlStr);
			mConn = (HttpURLConnection) mURL.openConnection();

			byte[] buffer = new byte[1024];
			String str = mFilePath + "/" + mFileName;

			File f = new File(str);

			//如果文件存在，读取文件下载进度，文件大小，继续下载
			//文件不存在，则创建下载记录文件，并将文件大小存入其中
			if (f.exists()) {
				mSp = mContext.getSharedPreferences("savedData", Context.MODE_PRIVATE);
				mSize = mSp.getLong(mFileName + "_size", 1);
				mSavedSize = mSp.getLong(mFileName + "_savedSize", 2);
				//showProgress(mSavedSize, mSize);
				//publishProgress((int)(mSavedSize * 1.0f / mSize * 100));
				if (mSavedSize >= mSize) {
					//测试用
					f.delete();
					mSavedSize = 0;
					/*publishProgress(-1);*/
				}
				mConn.setRequestProperty("Range", "bytes=" + mSavedSize + "-" + mSize);
			} else {
				mItem.setState(DownloadItem.State.UNSTART);
				mConn.setRequestProperty("Accept-Encoding", "identity");
				mSize = mConn.getHeaderFieldDate("Content-Length", mDefaultSize);

				mSp = mContext.getSharedPreferences("savedData", Context.MODE_PRIVATE);
				mEditor = mSp.edit();
				mEditor.putLong(mFileName + "_size", mSize);
				mEditor.commit();
			}
			FileOutputStream fos = new FileOutputStream(str, true);
			mIs = mConn.getInputStream();
			mBis = new BufferedInputStream(mIs);
			mBos = new BufferedOutputStream(fos);

			int length = 0;
			//从网络读取文件，写入指定文件中
			while (((length = mBis.read(buffer)) != -1) && mFlag == true) {
				mItem.setState(DownloadItem.State.BUSY);
				mSavedSize += length;
				mBos.write(buffer, 0, length);
				mBos.flush();
				if (((mSavedSize / length) % 100) == 0) {
					showProgress(mSavedSize, mSize);
				}
			}

			if (mFlag == false) {
				mItem.setState(DownloadItem.State.PAUSE);
			}
			if (mFlag == true) {
				publishProgress(100);
				mItem.setState(DownloadItem.State.OVER);

			}
			//停止读取文件时，暂停、下载完成：都要将下载进度保存到文件中
			mSp = mContext.getSharedPreferences("savedData", mContext.MODE_PRIVATE);
			mEditor = mSp.edit();
			mEditor.putLong(mFileName + "_savedSize", mSavedSize);
			mEditor.commit();

			if (mSize == mDefaultSize && mFlag == true) {
				mSp = mContext.getSharedPreferences("savedData", mContext.MODE_PRIVATE);
				mEditor = mSp.edit();
				mSavedSize = mDefaultSize;
				mEditor.putLong(mFileName + "_savedSize", mSavedSize);
				mEditor.commit();
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			return mFileName + "该连接不存在";
		} finally {
			try {
				if (mBis != null) {
					mBis.close();
				}
				if (mBos != null) {
					mBos.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		//Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();;
		if (result.equals("wifiFail")) {
			Toast.makeText(mContext, "Wifi unconnected", Toast.LENGTH_SHORT).show();
		}
		super.onPostExecute(result);
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		if (values[0] == -1) {
			//mHolder.button.setText("完成");
			return;
		} else if (values[0] == 100) {
			mHolder.mButton.setText("完成");
			Intent intent = new Intent();
			intent.setAction("android.intent.action.MY_BROADCAST");
			intent.putExtra("filePath", mFilePath);
			intent.putExtra("fileName", mFileName);
			mContext.sendBroadcast(intent);
		}
		mHolder.mBar.setProgress(values[0]);
		super.onProgressUpdate(values);
	}

	public void setFlag(boolean f) {
		mFlag = f;
	}

	private void showProgress(long saveSize, long size) {
		publishProgress((int) (saveSize * 1.0f / size * 100));
	}

}
