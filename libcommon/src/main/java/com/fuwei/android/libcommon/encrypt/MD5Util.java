package com.fuwei.android.libcommon.encrypt;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

public class MD5Util {
	private native void jniused();

	public static String hexdigest(String string) {
		String s = null;
		char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
			'a', 'b', 'c', 'd', 'e', 'f'};
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(string.getBytes());
			byte tmp[] = md.digest();
			char str[] = new char[16 * 2];
			int k = 0;
			for (int i = 0; i < 16; i++) {
				byte byte0 = tmp[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			s = new String(str);
		} catch (Exception e) {
		}
		return s;
	}

	public static String getFileMD5(String fileName) {
		try {
			FileInputStream fileStream = new FileInputStream(fileName);
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte[] buffer = new byte[1024];
			int length;
			while ((length = fileStream.read(buffer)) != -1) {
				md5.update(buffer, 0, length);
			}
			fileStream.close();
			byte[] md5Bytes = md5.digest();
			StringBuffer hexValue = new StringBuffer();
			for (int i = 0; i < md5Bytes.length; i++) {
				int val = ((int) md5Bytes[i]) & 0xff;
				if (val < 16) {
					hexValue.append("0");
				}
				hexValue.append(Integer.toHexString(val));
			}
			return hexValue.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getFileMD5(File file) {
		try {
			FileInputStream fileStream = new FileInputStream(file);
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte[] buffer = new byte[1024];
			int length;
			while ((length = fileStream.read(buffer)) != -1) {
				md5.update(buffer, 0, length);
			}
			fileStream.close();
			byte[] md5Bytes = md5.digest();
			StringBuffer hexValue = new StringBuffer();
			for (int i = 0; i < md5Bytes.length; i++) {
				int val = ((int) md5Bytes[i]) & 0xff;
				if (val < 16) {
					hexValue.append("0");
				}
				hexValue.append(Integer.toHexString(val));
			}
			return hexValue.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * ��ȡ�ַ�MD5
	 *
	 * @param str
	 * @return string
	 */
	public static String getStringMD5(String str) {

		char[] charArray = str.toCharArray();

		return getCharArrayMD5(charArray);
	}

	/**
	 * ��ȡ�ַ�MD5
	 *
	 * @param char[]
	 * @return string
	 */
	public static String getCharArrayMD5(char[] charArray) {
		byte[] byteArray = new byte[charArray.length];
		for (int i = 0; i < charArray.length; i++) {
			byteArray[i] = (byte) charArray[i];
		}

		return getByteArrayMD5(byteArray);
	}

	/**
	 * @return string
	 */
	public static String getByteArrayMD5(byte[] byteArray) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(byteArray);
			byte[] md5Bytes = md5.digest();
			StringBuffer hexValue = new StringBuffer();
			for (int i = 0; i < md5Bytes.length; i++) {
				int val = ((int) md5Bytes[i]) & 0xff;
				if (val < 16) {
					hexValue.append("0");
				}
				hexValue.append(Integer.toHexString(val));
			}
			return hexValue.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}