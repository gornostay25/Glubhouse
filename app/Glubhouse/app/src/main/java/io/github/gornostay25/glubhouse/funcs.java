package io.github.gornostay25.glubhouse;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.zip.ZipInputStream;
import java.io.FileInputStream;
import java.util.zip.ZipEntry;
import java.io.BufferedInputStream;
import java.security.PublicKey;

public class funcs
{
	public static boolean checkAppInstalledByName(Context context, String packageName) {
	    if (packageName == null || "".equals(packageName))  
	        return false;  
	    try {  
	        ApplicationInfo info = context.getPackageManager().getApplicationInfo(  
				packageName, PackageManager.GET_UNINSTALLED_PACKAGES);  

	        Log.d("QPYMAIN",  "checkAppInstalledByName:"+packageName+" found");
	        return true;  
	    } catch (Exception e) {  
	        Log.d("QPYMAIN",  "checkAppInstalledByName:"+packageName+" not found");

	        return false;  
	    }  
	}
	
	
	
//	public static boolean unpackZip(String path, String zipname)
//	{       
//		InputStream is;
//		ZipInputStream zis;
//		try 
//		{
//			String filename;
//			is = new FileInputStream(path + zipname);
//			zis = new ZipInputStream(new BufferedInputStream(is));          
//			ZipEntry ze;
//			byte[] buffer = new byte[1024];
//			int count;
//
//			while ((ze = zis.getNextEntry()) != null) 
//			{
//				filename = ze.getName();
//
//				// Need to create directories if not exists, or
//				// it will generate an Exception...
//				if (ze.isDirectory()) {
//					File fmd = new File(path + filename);
//					fmd.mkdirs();
//					continue;
//				}
//
//				FileOutputStream fout = new FileOutputStream(path + filename);
//
//				while ((count = zis.read(buffer)) != -1) 
//				{
//					fout.write(buffer, 0, count);             
//				}
//
//				fout.close();               
//				zis.closeEntry();
//			}
//
//			zis.close();
//		} 
//		catch(IOException e)
//		{
//			e.printStackTrace();
//			return false;
//		}
//
//		return true;
//	}
	
	
}
