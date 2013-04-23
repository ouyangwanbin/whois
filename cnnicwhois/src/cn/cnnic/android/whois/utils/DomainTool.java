package cn.cnnic.android.whois.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import android.util.Log;

public class DomainTool {
	
	public static final String[] NOT_FOUND_ARRAY= {"NOT FOUND","Domain not found","no matching record","No records matching","No Data Found","Not Registered","No match","nothing found","Status: AVAILABLE","does not exist in database","Domain status:         available","no existe","Domain Status: Available","Status: free","Status: Not Registered","not been registered","Status: Not Registered","No data was found","- Available","Status:             AVAILABLE","is not registered","Status:               available","No such domain","No Objects Found","is free","not found in database"};
	
	public static byte[] read(InputStream inStream) throws Exception{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while((len = inStream.read(buffer)) != -1){
			outStream.write(buffer,0,len);
		}
		inStream.close();
		return outStream.toByteArray();
	}
	
	public static boolean isRegistered(String whoisResult) throws Exception{
		Log.i("whoisLog",whoisResult);
		for(int index=0;index<NOT_FOUND_ARRAY.length;index++){
			if(whoisResult.toLowerCase().contains(NOT_FOUND_ARRAY[index].toLowerCase())){
				return false;
			}
		}
		return true;
	}
}
