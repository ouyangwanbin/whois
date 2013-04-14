package cn.cnnic.android.whois.service;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.content.Context;

public class FileService {
	private Context context;
	private String config="tld-recommend.xml";
	private String configMark="domain-mark.xml";
	public FileService(Context context){
		this.context = context;
	}
	
	public void init() throws Exception{
		String files[] = context.fileList();
		if(0 == files.length){
			FileOutputStream outStream = context.openFileOutput(config, Context.MODE_PRIVATE);
			InputStream inputStream = context.getClassLoader().getResourceAsStream(config);
			ByteArrayOutputStream swapStream = new ByteArrayOutputStream(); 
			byte[] buff = new byte[100]; //buff用于存放循环读取的临时数据 
			int rc = 0; 
			while ((rc = inputStream.read(buff, 0, 100)) > 0) { 
				swapStream.write(buff, 0, rc); 
			} 
			byte[] in_b = swapStream.toByteArray();
			outStream.write(in_b);
			outStream.close();
			
			FileOutputStream outStreamMark = context.openFileOutput(configMark, Context.MODE_PRIVATE);
			InputStream inputStreamMark = context.getClassLoader().getResourceAsStream(configMark);
			ByteArrayOutputStream swapStreamMark = new ByteArrayOutputStream(); 
			byte[] buffMark = new byte[100]; //buff用于存放循环读取的临时数据 
			int rcMark = 0; 
			while ((rcMark = inputStreamMark.read(buffMark, 0, 100)) > 0) { 
				swapStreamMark.write(buffMark, 0, rcMark); 
			} 
			byte[] in_bMark = swapStreamMark.toByteArray();
			outStreamMark.write(in_bMark);
			outStreamMark.close();
		}
	}
}
