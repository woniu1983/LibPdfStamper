package cn.woniu.lib.pdf.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
	private static final String PROJECT_NAME = "[LibPDFStamper]";
	private static final String DEBUG = "[Debug]";
	private static final String ERROR = "[Error]";
	private static final String INFO = "[Info]";
	private static final String TEST = "[Test]";

	public static final int TEST_LEVEL 		= 0;
	public static final int DEBUG_LEVEL 	= 1;
	public static final int INFO_LEVEL 		= 2;
	public static final int ERROR_LEVEL 	= 3;
	private static int mLevel = INFO_LEVEL;
	
	public static void setLevel (int level) {
		if (level < TEST_LEVEL) {
			mLevel = TEST_LEVEL;
		} else if (level > ERROR_LEVEL) {
			mLevel = ERROR_LEVEL;
		} else {
			mLevel = level;
		}
	}
	
	public static void Debug(String msg)
	{
		if (mLevel > DEBUG_LEVEL) {
			return;
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		write(makeLog(format.format(new Date()),PROJECT_NAME,DEBUG, msg));
	}
	public static void Error(String msg)
	{
		if (mLevel > ERROR_LEVEL) {
			return;
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		write(makeLog(format.format(new Date()),PROJECT_NAME,ERROR, msg));
	}
	public static void Info(String msg)
	{
		if (mLevel > INFO_LEVEL) {
			return;
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		write(makeLog(format.format(new Date()),PROJECT_NAME,INFO, msg));
	}
	public static void Test(String msg)
	{
		if (mLevel > TEST_LEVEL) {
			return;
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		write(makeLog(format.format(new Date()),PROJECT_NAME,TEST, msg));
	}
	
	private static void write(String msg)
	{
		System.out.println(msg);
	}
	
	private static String makeLog(String date, String projName, String mode, String msg)
	{
		StringBuilder buffer = new StringBuilder();
		buffer.append("[");
		buffer.append(date);
		buffer.append("]");
		buffer.append(PROJECT_NAME);
		buffer.append(mode);
		buffer.append("[ThreadID:");
		buffer.append(Thread.currentThread().getId());
		buffer.append("]");
		buffer.append(msg);
		return buffer.toString();
	}
	
	public static boolean isTestLevel() {
		return (mLevel <= TEST_LEVEL) ? true : false;
	}
}
