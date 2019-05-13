package com.yyhd.tools.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class MessageDigestUtil {

	private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	public static String getApkSignatureMD5(Context context, String pkgfile) {
		try {
			PackageInfo info = context.getPackageManager().getPackageArchiveInfo(pkgfile, PackageManager.GET_SIGNATURES);
			MessageDigest digest = MessageDigest.getInstance("MD5");
			for (Signature s : info.signatures) {
				digest.update(s.toByteArray());
			}
			byte[] b = digest.digest();
			StringBuilder sb = new StringBuilder(b.length * 2);
			for (int i = 0; i < b.length; i++) {
				sb.append(HEX_DIGITS[(b[i] & 0xf0) >> 4]);
				sb.append(HEX_DIGITS[b[i] & 0x0f]);
			}
			return sb.toString();
		} catch (Throwable e) {
			// java.lang.OutOfMemoryError
		}
		return null;
	}

	private static Certificate[] loadCertificates(JarFile jarFile) {
		JarEntry jarEntry = jarFile.getJarEntry("AndroidManifest.xml");
		try {
			InputStream is = jarFile.getInputStream(jarEntry);
			byte[] readBuffer = new byte[8192];
			while (is.read(readBuffer, 0, readBuffer.length) != -1) {
				// JarEntry.getCertificates() 需要读出所有的数据后才可用
			}
			is.close();
			return (jarEntry != null ? jarEntry.getCertificates() : null);
		} catch (Throwable e) {
			// java.lang.OutOfMemoryError
		}
		return null;
	}

	/**
	 * @param archivefile
	 * @return 快速读取AndroidManifest.xml来获取签名，非apk全部文件枚举扫描
	 */
	public static String getApkSignatureMD5(String archivefile) {
		JarFile jarFile = null;
		try {
			jarFile = new JarFile(archivefile);
			Certificate[] certs = loadCertificates(jarFile);
			if (certs == null || certs.length < 1) {
				return null;
			}

			MessageDigest digest = MessageDigest.getInstance("MD5");
			final int N = certs.length;
			for (int i = 0; i < N; i++) {
				// Log.i("Public key", certs[i].getPublicKey().toString());
				digest.update(certs[i].getEncoded());
			}

			byte[] b = digest.digest();
			StringBuilder sb = new StringBuilder(b.length * 2);
			for (int i = 0; i < b.length; i++) {
				sb.append(HEX_DIGITS[(b[i] & 0xf0) >> 4]);
				sb.append(HEX_DIGITS[b[i] & 0x0f]);
			}
			return sb.toString();
		} catch (Throwable e) {
			return null;
		} finally {
			try {
				jarFile.close();
			} catch (Exception e) {
			}
		}
	}


	/**
	 * 此方法专门为适配豌豆荚软件升级API计算APK签名的MD5值而创建,跟常规的计算方式可能有一定差异，慎用！
	 * 
	 * @param context
	 * @param packageName
	 * @return 返回小写形式的APK签名的MD5码
	 */
	public static String getWDJApkSignatureMD5ByPackageName(Context context, String packageName) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
			MessageDigest digest = MessageDigest.getInstance("MD5");
			if (info.signatures == null || info.signatures.length == 0) {
				return null;
			}
			Signature signature = info.signatures[0];
			String signatureHex = bufferToHex(signature.toByteArray()).toUpperCase();
			digest.update(signatureHex.getBytes());
			byte[] b = digest.digest();
			StringBuilder sb = new StringBuilder(b.length * 2);
			for (int i = 0; i < b.length; i++) {
				sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
				sb.append(HEX_DIGITS[b[i] & 0x0f]);
			}
			return sb.toString().toLowerCase();
		} catch (Throwable e) {
			// java.lang.OutOfMemoryError
		}
		return "";
	}

	/**
	 * 传入已安装的Apk的package那么，返回其前签名的MD5，此方法的MD5的生成过程是一般标准的形式
	 * 
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static String getStandardApkSignatureMD5ByPackageName(Context context, String packageName) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
			MessageDigest digest = MessageDigest.getInstance("MD5");
			if (info.signatures == null || info.signatures.length == 0) {
				return null;
			}
			for (Signature s : info.signatures) {
				digest.update(s.toByteArray());
			}
			byte[] b = digest.digest();
			StringBuilder sb = new StringBuilder(b.length * 2);
			for (int i = 0; i < b.length; i++) {
				sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
				sb.append(HEX_DIGITS[b[i] & 0x0f]);
			}
			return sb.toString();
		} catch (Throwable e) {
			// java.lang.OutOfMemoryError
		}
		return "";
	}

	public static String getMD5String(String s) {
		return getMD5String(s.getBytes());
	}

	public static String getMD5String(byte[] bytes) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(bytes);
			return bufferToHex(digest.digest());
		} catch (Throwable e) {
			return null;
		}
	}

	public static String getFileMD5String(File file) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			InputStream fis;
			fis = new FileInputStream(file);
			byte[] buffer = new byte[8192];
			int numRead = 0;
			while ((numRead = fis.read(buffer)) > 0) {
				digest.update(buffer, 0, numRead);
			}
			fis.close();
			return bufferToHex(digest.digest());
		} catch (Throwable e) {
			return null;
		}
	}

	public static String getFileMD5LowCaseString(File file) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			InputStream fis;
			fis = new FileInputStream(file);
			byte[] buffer = new byte[8192];
			int numRead = 0;
			while ((numRead = fis.read(buffer)) > 0) {
				digest.update(buffer, 0, numRead);
			}
			fis.close();
			return bufferToHex(digest.digest()).toLowerCase();
		} catch (Throwable e) {
			return null;
		}
	}

	public static String getPublicKey(byte[] signature) {
		try {
			CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
			X509Certificate
          cert = (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(signature));

			String publickey = cert.getPublicKey().toString();
			publickey = publickey.substring(publickey.indexOf("modulus: ") + 9, publickey.indexOf("\n", publickey.indexOf("modulus:")));
			return publickey;
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String bufferToHex(byte bytes[]) {
		return bufferToHex(bytes, 0, bytes.length);
	}

	private static String bufferToHex(byte bytes[], int m, int n) {
		StringBuffer stringbuffer = new StringBuffer(2 * n);
		int k = m + n;
		for (int l = m; l < k; l++) {
			appendHexPair(bytes[l], stringbuffer);
		}
		return stringbuffer.toString();
	}

	private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
		char c0 = HEX_DIGITS[(bt & 0xf0) >> 4];
		char c1 = HEX_DIGITS[bt & 0xf];// 取字节中低4位的数字转换
		stringbuffer.append(c0);
		stringbuffer.append(c1);
	}

}
