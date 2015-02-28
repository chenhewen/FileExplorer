package com.chen_hewen.fileexplorer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;

/**
 * 
 * <br>类描述: 文件辅助类
 * <br>功能详细描述:
 * 
 * @author  chenhewen
 * @date  [2014年8月4日]
 */
public class FileHelper {

	public static final int OPEN_JUMP = 0x001;
	public static final int OPEN_BY = 0x002;

	private Context mContext;
	private SharedPreferences mSharedPreferences;

	private String mSDPath;
	private String mFileName;
	private String mCurPath;
	private String mCutFilePathWithFileName;
	private String mPastedFilePathWithFileName;

	public FileHelper(Context context) {
		this.mContext = context;
		initCurPath();
	}

	/*********************************************路径相关的辅助类*********************************************/

	public String withSlashBefore(String str) {
		return "/" + str;
	}

	//获取当前路径
	public String getCurPath() {
		return mCurPath;
	}

	//初始化SD卡路径的值，当前路径的值

	//如果第一次启动程序，将根目录赋值给mCurPath
	//如果不是第一次，那么从文件里读出上一次退出的路径，赋值给mCurPath

	//先实现从根路径进入,也就是每次启动应用mCurPath的值为根路径的值
	public void initCurPath() {
		mSharedPreferences = mContext.getSharedPreferences("savedData", Context.MODE_PRIVATE);
		String lastPath = mSharedPreferences.getString("lastPath", null);
		if (lastPath == null) {
			mCurPath = mSDPath == null ? getSDCardRootPath() : mSDPath;
		} else {
			getSDCardRootPath();
			mCurPath = lastPath;
		}
	}

	//同步文件路径
	public String correctCurPath() {
		mCurPath = mCurPath.substring(0, mCurPath.lastIndexOf("/"));
		return mCurPath;
	}

	//同步文件路径
	public String correctCurPath(String path) {
		if (path == mSDPath) {
			return mSDPath;
		}
		mCurPath += withSlashBefore(path);
		return mCurPath;
	}

	//连接文件路径，但是不改变当前路径
	public String joinCurPath(String path) {
		return mCurPath + withSlashBefore(path);
	}

	//将路径转换成文件
	public File pathToFile(String path) {
		return new File(path);
	}

	//判断当前是否在根路径
	public boolean isInRootPath() {
		return mCurPath.equals(mSDPath);
	}

	/*********************************************SD卡相关类*********************************************/

	//判断SD卡是否存在
	public boolean isSDCardExist() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	//获取SD卡根路径
	public String getSDCardRootPath() {
		if (!isSDCardExist()) {
			return null;
		}
		mSDPath = Environment.getExternalStorageDirectory().toString();

		return mSDPath;
	}

	/*********************************************获取文件夹目录结构*********************************************/

	//获取指定路径下的文件夹和文件
	public File[] getDirsAndFiles(String path) {
		File file = new File(path);
		File[] files = file.listFiles();
		return files;
	}

	//进入文件，同时更新当前路径！
	public File[] openDirAndReturnFiles(String path) {
		correctCurPath(path);
		return getDirsAndFiles(mCurPath);
	}

	public File[] openDirAndReturnFiles(String path, int flag) {
		if (flag == OPEN_JUMP) {
			mCurPath = path;
			return getDirsAndFiles(path);
		}
		return openDirAndReturnFiles(path);
	}

	//返回父目录，同时更新当前路径！
	public File[] backToParentDirAndReturnFiles() {
		if (mCurPath.equals(mSDPath)) {
			return null;
		}
		correctCurPath();
		return getDirsAndFiles(mCurPath);
	}
	/*	
		public File[] refreshCurFiles(){
			return getDirsAndFiles(mCurPath);
		}*/

	//强制回到根路径
	public File[] openRootDirAndReturnFiles() {
		return openDirAndReturnFiles(mSDPath, OPEN_JUMP);
	}

	//回到记忆路径
	public File[] openMemeryDirAndReturnFiles() {
		return openDirAndReturnFiles(mCurPath, OPEN_JUMP);
	}

	/*********************************************创建文件夹*********************************************/

	//在指定路径新建文件夹
	public boolean createDir(String path) {
		File file = new File(path);
		if (file.isDirectory()) {
			return false;
		}
		return file.mkdirs();
	}

	//在当前路径创建指定文件夹
	public boolean createDirInCurPath(String path) {
		return createDir(joinCurPath(path));
	}

	/***************************************************删除、复制、剪切、粘贴文件夹*********************************/

	//删除当前文件夹下的所有内容，参数为路径
	public void deleteInCurPath(String path) {
		deleteInCurPath(new File(joinCurPath(path)));
	}

	//删除当前文件夹下所有内容，参数。
	//该方法内部实现遍历的时候要注意要先删除子文件夹，delete方法有关。
	public void deleteInCurPath(File file) {
		if (!file.exists()) {
			return;
		}
		File[] files = file.listFiles();
		//当files为空，意味着它是一个“文件”不是文件夹，当length=0
		if (files == null || files.length == 0) {
			file.delete();
		} else {
			for (File f : files) {
				deleteInCurPath(f);
			}
			//当把子文件夹删除完成后，删除父文件夹，这个只能最后删除。因为该方法只能删除空文件夹。
			file.delete();
		}
	}

	//复制文件
	public void cutFile(String fileName) {
		mFileName = fileName;
		mCutFilePathWithFileName = joinCurPath(fileName);
	}

	//强制覆盖文件
	public void createOrOverwriteFile() {
		BufferedInputStream bufis = null;
		BufferedOutputStream bufos = null;
		try {
			byte[] buffer = new byte[1024];
			bufis = new BufferedInputStream(new FileInputStream(mCutFilePathWithFileName));
			bufos = new BufferedOutputStream(new FileOutputStream(mPastedFilePathWithFileName));
			while (bufis.read(buffer) != -1) {
				bufos.write(buffer);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (bufis != null) {
					bufis.close();
					bufos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	//粘贴文件
	public boolean pasteFile() {
		mPastedFilePathWithFileName = joinCurPath(mFileName);
		File file = new File(mPastedFilePathWithFileName);

		if (file.exists()) {
			return false;
		}
		createOrOverwriteFile();
		return true;
	}

	public String getCutFileName() {
		return mFileName;
	}

	//移动文件
	public void moveTo(String path) {
		pathToFile(mCutFilePathWithFileName).renameTo(pathToFile(path));
	}

	/************************************打开应用程序*************************************************************/
	public void openFile(String path) {
		File file = pathToFile(joinCurPath(path));
		openFile(file);
	}

	public void openFile(File file) {

		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//设置intent的Action属性  
		intent.setAction(Intent.ACTION_VIEW);
		//获取文件file的MIME类型  
		String type = getMIMEType(file);
		//设置intent的data和Type属性。  
		intent.setDataAndType(/*uri*/Uri.fromFile(file), type);
		//跳转  
		mContext.startActivity(intent);

	}

	private String getMIMEType(File file) {

		String type = "*/*";
		String fName = file.getName();
		//获取后缀名前的分隔符"."在fName中的位置。  
		int dotIndex = fName.lastIndexOf(".");
		if (dotIndex < 0) {
			return type;
		}
		/* 获取文件的后缀名 */
		String end = fName.substring(dotIndex, fName.length()).toLowerCase();
		if (end == "") {
			return type;
		}
		//在MIME和文件类型的匹配表中找到对应的MIME类型。  
		for (int i = 0; i < mMIME_MapTable.length; i++) { //MIME_MapTable??在这里你一定有疑问，这个MIME_MapTable是什么？  
			if (end.equals(mMIME_MapTable[i][0])) {
				type = mMIME_MapTable[i][1];
			}
		}
		return type;
	}

	private final String[][] mMIME_MapTable = {
			//{后缀名， MIME类型}  
			{ ".3gp", "video/3gpp" },
			{ ".apk", "application/vnd.android.package-archive" },
			{ ".asf", "video/x-ms-asf" },
			{ ".avi", "video/x-msvideo" },
			{ ".bin", "application/octet-stream" },
			{ ".bmp", "image/bmp" },
			{ ".c", "text/plain" },
			{ ".class", "application/octet-stream" },
			{ ".conf", "text/plain" },
			{ ".cpp", "text/plain" },
			{ ".doc", "application/msword" },
			{ ".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document" },
			{ ".xls", "application/vnd.ms-excel" },
			{ ".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" },
			{ ".exe", "application/octet-stream" },
			{ ".gif", "image/gif" },
			{ ".gtar", "application/x-gtar" },
			{ ".gz", "application/x-gzip" },
			{ ".h", "text/plain" },
			{ ".htm", "text/html" },
			{ ".html", "text/html" },
			{ ".jar", "application/java-archive" },
			{ ".java", "text/plain" },
			{ ".jpeg", "image/jpeg" },
			{ ".jpg", "image/jpeg" },
			{ ".js", "application/x-javascript" },
			{ ".log", "text/plain" },
			{ ".m3u", "audio/x-mpegurl" },
			{ ".m4a", "audio/mp4a-latm" },
			{ ".m4b", "audio/mp4a-latm" },
			{ ".m4p", "audio/mp4a-latm" },
			{ ".m4u", "video/vnd.mpegurl" },
			{ ".m4v", "video/x-m4v" },
			{ ".mov", "video/quicktime" },
			{ ".mp2", "audio/x-mpeg" },
			{ ".mp3", "audio/x-mpeg" },
			{ ".mp4", "video/mp4" },
			{ ".mpc", "application/vnd.mpohun.certificate" },
			{ ".mpe", "video/mpeg" },
			{ ".mpeg", "video/mpeg" },
			{ ".mpg", "video/mpeg" },
			{ ".mpg4", "video/mp4" },
			{ ".mpga", "audio/mpeg" },
			{ ".msg", "application/vnd.ms-outlook" },
			{ ".ogg", "audio/ogg" },
			{ ".pdf", "application/pdf" },
			{ ".png", "image/png" },
			{ ".pps", "application/vnd.ms-powerpoint" },
			{ ".ppt", "application/vnd.ms-powerpoint" },
			{ ".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation" },
			{ ".prop", "text/plain" }, { ".rc", "text/plain" },
			{ ".rmvb", "audio/x-pn-realaudio" }, { ".rtf", "application/rtf" },
			{ ".sh", "text/plain" }, { ".tar", "application/x-tar" },
			{ ".tgz", "application/x-compressed" }, { ".txt", "text/plain" },
			{ ".wav", "audio/x-wav" }, { ".wma", "audio/x-ms-wma" }, { ".wmv", "audio/x-ms-wmv" },
			{ ".wps", "application/vnd.ms-works" }, { ".xml", "text/plain" },
			{ ".z", "application/x-compress" }, { ".zip", "application/x-zip-compressed" },
			{ "", "*/*" } };
}
