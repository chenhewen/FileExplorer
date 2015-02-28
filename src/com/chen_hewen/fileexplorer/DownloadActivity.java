package com.chen_hewen.fileexplorer;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.chen_hewen.fileexplorer.DownloadAdapter.ViewHolder;

@SuppressLint("NewApi")
/**
 * 
 * <br>类描述: 下载Activity
 * <br>功能详细描述: 创建并绑定数据
 * 
 * @author  chenhewen
 * @date  [2014年8月4日]
 */
public class DownloadActivity extends Activity {

	DownloadAdapter mAdapter;
	ListView mDownloadListView;
	List mData;
	Button mDownAll;
	DownloadItem mItem;
	String mFilePath;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.download);
		
		Intent intent = getIntent();
		mFilePath = intent.getStringExtra("curPath");
		
		mDownloadListView = (ListView) findViewById(R.id.downlist);
		mDownAll = (Button) findViewById(R.id.downAll);
		
		mAdapter = new DownloadAdapter(this);
		mAdapter.addData(initData());		
		mDownloadListView.setAdapter(mAdapter);	
		
		mDownAll.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				List<ViewHolder> hs = mAdapter.getHolders();
				//holder不知道为什么多创建一次最后一个ListView中的项
				for (int i = 0; i < hs.size() - 1; i++) {
					hs.get(i).mButton.callOnClick();
				}
				
			}
		});
	}
	
	public List initData(){
				
		mData = new ArrayList<DownloadItem>();
		
		mItem = new DownloadItem(R.drawable.tashuo, mFilePath, "winrar.exe",
				"http://w.x.baidu.com/alading/anquan_soft_down_b/10849");
		mData.add(mItem);
		
		
		mItem = new DownloadItem(R.drawable.nibuzhidaodeshi, mFilePath, "你不知道的事.mp3",
				"http://music.baidu.com/data/music/file?link=http://yinyueshiting.baidu.com/data2/music/38819127/501544230400128.mp3?xcode=09d3b47c47a26ae7ea9b7ad99e2a0e3732efc148bb0b283b&song_id=501544");
		mData.add(mItem);
		
		mItem = new DownloadItem(R.drawable.nibuzhidaodeshi, mFilePath, "哈哈.mp3",
				"http://music.baidu.com/data/music/file?link=http://yinyueshiting.baidu.com/data2/music/38819127/501544230400128.mp3?xcode=09d3b47c47a26ae7ea9b7ad99e2a0e3732efc148bb0b283b&song_id=501544");
		mData.add(mItem);
		
		return mData;
	}
}
