package com.dudu.android.launcher.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.text.DecimalFormat;

import com.dudu.android.launcher.LauncherApplication;

import android.content.Context;
import android.os.Environment;

/**
 * 文件操作工具类
 * @author 赵圣琪
 *
 */
public class FileUtils {

	/**
	 * 获取录像存储目录
	 * 
	 * @return
	 */
	public static File getVideoStorageDir() {
		File dir = new File(getStorageDir(), "/video");
		if (!dir.exists()) {
			dir.mkdirs();
		}

		FileUtils.makeNoMediaFile(dir);
		return dir;
	}
	
	
	/**
	 * 获取临时目录
	 *
	 * @return
	 */
	public static File getStorageDir() {
		File dir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
		if (sdCardExist) {
			dir = new File(Environment.getExternalStorageDirectory(),
					getMainDirName());
		} else {
			dir = new File(LauncherApplication.mApplication.getCacheDir(),
					getMainDirName());
		}

		if (!dir.exists()) {
			dir.mkdirs();
		}
		
		return dir;
	}
	
	public static String getMainDirName() {
		return "/dudu";
	}
	
	
	/**
	 * 读取asset目录下文件。
	 * 
	 * @return content
	 */
	public static String readFile(Context mContext, String file, String code) {
		int len = 0;
		byte[] buf = null;
		String result = "";
		try {
			InputStream in = mContext.getAssets().open(file);
			len = in.available();
			buf = new byte[len];
			in.read(buf, 0, len);

			result = new String(buf, code);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 删除指定文件
	 * 
	 * @param path
	 */
	public static void delectCardFile(String path) {
		File file = new File(path);
		if (file.exists()) {
			file.delete();
		}
	}

	/**
	 * 目录下面的所有文件夹跟文件
	 * 
	 * @param path
	 */
	public static void delectCardFiles(String path) {
		File file = new File(path);
		if (file.exists()) {
			delectAllFiles(file);
		}
	}

	private static void delectAllFiles(File root) {
		File files[] = root.listFiles();
		if (files != null)
			for (File f : files) {
				if (f.isDirectory()) { // 判断是否为文件夹
					delectAllFiles(f);
				} else {
					if (f.exists()) { // 判断是否存在
						try {
							f.delete();
						} catch (Exception e) {
						}
					}
				}
			}
	}

	/**
	 * 判断文件 是不是 type结尾
	 * 
	 * @param file
	 * @param type
	 * @return
	 */
	public static boolean isFileType(File file, String type) {
		String name = file.getName();
		if (!"".equals(name) && name != null) {
			String fileEnd = name.substring(name.lastIndexOf("."));
			if (type.equals(fileEnd)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断sdcard是否存在
	 * 
	 * @return
	 */
	public static boolean isSdCard() {
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
		if (sdCardExist) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 返回sdCard路径
	 * 
	 * @return
	 */
	public static String getSdPath() {
		if (Environment.getExternalStorageState().equalsIgnoreCase(
				Environment.MEDIA_MOUNTED)) {
			return Environment.getExternalStorageDirectory().toString();
		}
		return null;
	}

	public static String fileByte2Mb(float size) {
		double mbSize = size / 1024 / 1024;
		DecimalFormat df = new DecimalFormat("#.##");
		return df.format(mbSize);
	}

	public static void makeNoMediaFile(File dir) {
		try {
			File f = new File(dir, ".nomedia");
			if (!f.exists()) {
				f.createNewFile();
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 拼接路径 concatPath("/mnt/sdcard", "/DCIM/Camera") => /mnt/sdcard/DCIM/Camera
	 * concatPath("/mnt/sdcard", "DCIM/Camera") => /mnt/sdcard/DCIM/Camera
	 * concatPath("/mnt/sdcard/", "/DCIM/Camera") => /mnt/sdcard/DCIM/Camera
	 */
	public static String concatPath(String... paths) {
		StringBuilder result = new StringBuilder();
		if (paths != null) {
			for (String path : paths) {
				if (path != null && path.length() > 0) {
					int len = result.length();
					boolean suffixSeparator = len > 0
							&& result.charAt(len - 1) == File.separatorChar;// 后缀是否是'/'
					boolean prefixSeparator = path.charAt(0) == File.separatorChar;// 前缀是否是'/'
					if (suffixSeparator && prefixSeparator) {
						result.append(path.substring(1));
					} else if (!suffixSeparator && !prefixSeparator) {// 补前缀
						result.append(File.separatorChar);
						result.append(path);
					} else {
						result.append(path);
					}
				}
			}
		}
		return result.toString();
	}

	/**
	 * 检测文件是否可用
	 */
	public static boolean checkFile(File f) {
		if (f != null && f.exists() && f.canRead()
				&& (f.isDirectory() || (f.isFile() && f.length() > 0))) {
			return true;
		}
		return false;
	}

	/**
	 * 检测文件是否可用
	 */
	public static boolean checkFile(String path) {
		if (StringUtils.isNotEmpty(path)) {
			File f = new File(path);
			if (f != null && f.exists() && f.canRead()
					&& (f.isDirectory() || (f.isFile() && f.length() > 0)))
				return true;
		}
		return false;
	}

	/**
	 * 获取sdcard路径
	 */
	public static String getExternalStorageDirectory() {
		String path = Environment.getExternalStorageDirectory().getPath();
		return path;
	}

	public static long getFileSize(String fn) {
		File f = null;
		long size = 0;

		try {
			f = new File(fn);
			size = f.length();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			f = null;
		}
		return size < 0 ? null : size;
	}

	public static long getFileSize(File fn) {
		return fn == null ? 0 : fn.length();
	}

	public static long getDirSize(File file) {
		if (file.isFile())
			return file.length();
		final File[] children = file.listFiles();
		long total = 0;
		if (children != null)
			for (final File child : children)
				total += getDirSize(child);
		return total;
	}

	public static long getDirSize(String filePath) {
		File file = new File(filePath);
		if (file.isFile())
			return file.length();
		final File[] children = file.listFiles();
		long total = 0;
		if (children != null)
			for (final File child : children)
				total += getDirSize(child);
		return total;
	}

	public static String getFileType(String fn, String defaultType) {
		FileNameMap fNameMap = URLConnection.getFileNameMap();
		String type = fNameMap.getContentTypeFor(fn);
		return type == null ? defaultType : type;
	}

	public static String getFileType(String fn) {
		return getFileType(fn, "application/octet-stream");
	}

	public static String getFileExtension(String filename) {
		String extension = "";
		if (filename != null) {
			int dotPos = filename.lastIndexOf(".");
			if (dotPos >= 0 && dotPos < filename.length() - 1) {
				extension = filename.substring(dotPos + 1);
			}
		}
		return extension.toLowerCase();
	}

	public static boolean deleteFile(File f) {
		if (f != null && f.exists() && !f.isDirectory()) {
			return f.delete();
		}
		return false;
	}

	public static void deleteDir(File f) {
		if (f != null && f.exists() && f.isDirectory()) {
			for (File file : f.listFiles()) {
				if (file.isDirectory())
					deleteDir(file);
				file.delete();
			}
			f.delete();
		}
	}

	public static void deleteDir(String f) {
		if (f != null && f.length() > 0) {
			deleteDir(new File(f));
		}
	}

	public static boolean deleteFile(String f) {
		if (f != null && f.length() > 0) {
			return deleteFile(new File(f));
		}
		return false;
	}

	/**
	 * read file
	 * 
	 * @param file
	 * @param charsetName
	 *            The name of a supported {@link java.nio.charset.Charset
	 *            </code>charset<code>}
	 * @return if file not exist, return null, else return content of file
	 * @throws RuntimeException
	 *             if an error occurs while operator BufferedReader
	 */
	public static String readFile(File file, String charsetName) {
		StringBuilder fileContent = new StringBuilder("");
		if (file == null || !file.isFile()) {
			return fileContent.toString();
		}

		BufferedReader reader = null;
		try {
			InputStreamReader is = new InputStreamReader(new FileInputStream(
					file), charsetName);
			reader = new BufferedReader(is);
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (!fileContent.toString().equals("")) {
					fileContent.append("\r\n");
				}
				fileContent.append(line);
			}
			reader.close();
		} catch (IOException e) {
			throw new RuntimeException("IOException occurred. ", e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					throw new RuntimeException("IOException occurred. ", e);
				}
			}
		}
		return fileContent.toString();
	}

	public static String readFile(String filePath, String charsetName) {
		return readFile(new File(filePath), charsetName);
	}

	public static String readFile(File file) {
		return readFile(file, "utf-8");
	}

	/**
	 * 文件拷贝
	 * 
	 * @param from
	 * @param to
	 * @return
	 */
	public static boolean fileCopy(String from, String to) {
		boolean result = false;

		int size = 1 * 1024;

		FileInputStream in = null;
		FileOutputStream out = null;
		try {
			in = new FileInputStream(from);
			out = new FileOutputStream(to);
			byte[] buffer = new byte[size];
			int bytesRead = -1;
			while ((bytesRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
			}
			out.flush();
			result = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
			}
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
			}
		}
		return result;
	}

	/**
	 * 清除WebView缓存
	 */
	public static void clearWebViewCache(Context context) {

		// 清理Webview缓存数据库
		try {
			context.deleteDatabase("webview.db");
			context.deleteDatabase("webviewCache.db");
		} catch (Exception e) {
			e.printStackTrace();
		}

		// WebView 缓存文件
		File appCacheDir = new File(context.getFilesDir().getAbsolutePath()
				+ "/webcache");
		// Log.e(TAG, "appCacheDir path="+appCacheDir.getAbsolutePath());

		File webviewCacheDir = new File(context.getCacheDir().getAbsolutePath()
				+ "/webviewCache");
		// Log.e(TAG,
		// "webviewCacheDir path="+webviewCacheDir.getAbsolutePath());

		// 删除webview 缓存目录
		if (webviewCacheDir.exists()) {
			deleteFile2(webviewCacheDir);
		}
		// 删除webview 缓存 缓存目录
		if (appCacheDir.exists()) {
			deleteFile2(appCacheDir);
		}
	}

	/**
	 * 递归删除 文件/文件夹
	 * 
	 * @param file
	 */
	private static void deleteFile2(File file) {

		// Log.i(TAG, "delete file path=" + file.getAbsolutePath());

		if (file.exists()) {
			if (file.isFile()) {
				file.delete();
			} else if (file.isDirectory()) {
				File files[] = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					deleteFile2(files[i]);
				}
			}
			file.delete();
		} else {
			// Log.e(TAG, "delete file no exists " + file.getAbsolutePath());
		}
	}
	
}
