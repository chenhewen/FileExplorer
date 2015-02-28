package com.chen_hewen.fileexplorer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * <br>类描述: 主界面Activity
 * <br>功能详细描述:
 * 
 * @author  chenhewen
 * @date  [2014年8月4日]
 */
public class MainActivity extends Activity {

	FileHelper mFileHelper;
	List<Map<String, Object>> mData;
	SimpleAdapter mAdapter;
	int mPosition;

	ListView mListView;
	Button mRootbtn;
	Button mUpbtn;
	TextView mPathtxt;
	Button mNewbtn;
	Button mDownload;

	EditText mFileNameInputtxt;
	Builder mBuilder;
	String mCreateFileName;

	SharedPreferences mSharedPreferences;
	MyBroadcastReceiver mReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mListView = (ListView) findViewById(R.id.filelist);
		mPathtxt = (TextView) findViewById(R.id.pathtxt);
		mRootbtn = (Button) findViewById(R.id.rootbtn);
		mUpbtn = (Button) findViewById(R.id.upbtn);
		mNewbtn = (Button) findViewById(R.id.newbtn);
		mDownload = (Button) findViewById(R.id.download);

		mFileHelper = new FileHelper(this);
		File[] files = mFileHelper.openMemeryDirAndReturnFiles();
		goInto(files);

		mAdapter = new SimpleAdapter(this, mData, R.layout.list_item, new String[] { "image",
				"name" }, new int[] { R.id.fileicon, R.id.filename });
		mListView.setAdapter(mAdapter);
		initListener();
		registerForContextMenu(mListView);
		mSharedPreferences = getSharedPreferences("savedData", MODE_PRIVATE);

		mReceiver = new MyBroadcastReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.MY_BROADCAST");
		this.registerReceiver(mReceiver, filter);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		menu.setHeaderTitle("菜单");
		menu.add(0, 0, 0, "剪切");
		/*		if(mFileHelper.getCutFileName() != null){
					menu.add(0, 1, 1, "粘贴");
				}*/
		menu.add(0, 2, 2, "删除");
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		Map map = mData.get(mPosition);
		int itemId = item.getItemId();
		String itemName = map.get("name").toString();;

		switch (itemId) {
			case 0 :
				mFileHelper.cutFile(itemName);
				break;
			/*		case 1:
						mFileHelper.pasteFile();
						break;*/
			case 2 :

				mFileHelper.deleteInCurPath(itemName);
				mData.remove(map);
				mAdapter.notifyDataSetChanged();
				break;
		}

		return super.onContextItemSelected(item);
	}

	public void refreshPathTxt() {
		mPathtxt.setText(mFileHelper.getCurPath());
	}

	public Map addItemToList(int id, String name) {
		Map map = new HashMap<String, Object>();
		map.put("image", id);
		map.put("name", name);
		return map;
	}

	public void showBuilder() {
		mFileNameInputtxt = (EditText) LayoutInflater.from(this).inflate(R.layout.alert, null);
		mBuilder = new Builder(this);
		mBuilder.setTitle("文件夹名称").setIcon(android.R.drawable.ic_dialog_info)
				.setView(mFileNameInputtxt).setNegativeButton("取消", null)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mCreateFileName = mFileNameInputtxt.getText().toString();
						mFileHelper.createDirInCurPath(mCreateFileName);
						Map map = addItemToList(R.drawable.ic_launcher, mCreateFileName);
						if (mCreateFileName.equals("")) {
							Toast.makeText(MainActivity.this, "文件名不能为空", Toast.LENGTH_SHORT).show();
						} else if (mData.contains(map)) {
							Toast.makeText(MainActivity.this, "该文件夹已存在", Toast.LENGTH_SHORT).show();
						} else {
							mData.add(map);
							mAdapter.notifyDataSetChanged();
						}
					}
				});
		mBuilder.show();
	}

	public void goInto(File[] files) {
		if (files == null) {
			return;
		}
		mData = new ArrayList<Map<String, Object>>();
		for (File f : files) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("image", R.drawable.ic_launcher);
			map.put("name", f.getName());
			mData.add(map);
		}
		mAdapter = new SimpleAdapter(this, mData, R.layout.list_item, new String[] { "image",
				"name" }, new int[] { R.id.fileicon, R.id.filename });
		mListView.setAdapter(mAdapter);
		refreshPathTxt();
	}

	public void initListener() {

		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapter, View view, int position, long id) {
				mPosition = position;
				return false;
			}
		});

		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
				Map<String, Object> map = (Map) mListView.getItemAtPosition(position);
				String fileName = (String) map.get("name");
				if (mFileHelper.pathToFile(mFileHelper.joinCurPath(fileName)).isFile()) {
					mFileHelper.openFile(fileName);
					return;
				}
				File[] files = mFileHelper.openDirAndReturnFiles(fileName);
				goInto(files);
			}
		});

		mUpbtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				File[] files = mFileHelper.backToParentDirAndReturnFiles();
				if (files == null) {
					Toast.makeText(MainActivity.this, "已经是根目录了", Toast.LENGTH_SHORT).show();
					return;
				}
				goInto(files);
			}
		});

		mRootbtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				File[] files = mFileHelper.openRootDirAndReturnFiles();
				goInto(files);
			}
		});

		mNewbtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showBuilder();
			}
		});

		mDownload.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, DownloadActivity.class);
				intent.putExtra("curPath", mFileHelper.getCurPath());
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//getMenuInflater().inflate(R.menu.main, menu);
		menu.add(0, 0, 0, "粘贴");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		String name = mFileHelper.getCutFileName();
		switch (item.getItemId()) {
			case 0 :
				if (name != null) {
					String newPath = mFileHelper.joinCurPath(name);
					mFileHelper.moveTo(newPath);
					Map map = addItemToList(R.drawable.ic_launcher, mFileHelper.getCutFileName());
					goInto(mFileHelper.getDirsAndFiles(mFileHelper.getCurPath()));
					//mAdapter.notifyDataSetChanged();
				} else {
					Toast.makeText(MainActivity.this, "你没有复制内容", Toast.LENGTH_SHORT).show();
				}
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * 
	 * <br>类描述:  定义广播
	 * <br>功能详细描述:
	 * 
	 * @author  chenhewen
	 * @date  [2014年8月4日]
	 */
	public class MyBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String filePath = intent.getStringExtra("filePath");
			String fileName = intent.getStringExtra("fileName");
			Map map = addItemToList(R.drawable.ic_launcher, fileName);
			if (mData.contains(map)) {
				return;
			}
			mData.add(map);
			mAdapter.notifyDataSetChanged();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}

	@Override
	public void onBackPressed() {
		Editor editor = mSharedPreferences.edit();
		editor.putString("lastPath", mFileHelper.getCurPath());
		editor.commit();
		super.onBackPressed();
	}
}
