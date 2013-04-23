package cn.cnnic.android.whois.service;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.content.Context;

public class FileService {
	private Context context;
	private String config="tld-recommend.xml";
	private String configMark="domain-mark.xml";
	private String registrarMark="registrar.xml";
	public FileService(Context context){
		this.context = context;
	}
	
	public void init() throws Exception{
		String files[] = context.fileList();
		if(!isContainIn(files, config)){
			writeFile(config);
		}
		
		if(!isContainIn(files, configMark)){
			writeFile(configMark);
		}
		
		if(!isContainIn(files, registrarMark)){
			writeFile(registrarMark);
		}
	}
	
	private boolean isContainIn(String[] files, String file){
		for(String f : files){
			if(file.equals(f)){
				return true;
			}
		}
		return false;
	}
	
	private void writeFile(String file) throws Exception{
		FileOutputStream outStream = context.openFileOutput(file, Context.MODE_PRIVATE);
		InputStream inputStream = context.getClassLoader().getResourceAsStream(file);
		ByteArrayOutputStream swapStream = new ByteArrayOutputStream(); 
		byte[] buff = new byte[100]; //buff用于存放循环读取的临时数据 
		int rc = 0; 
		while ((rc = inputStream.read(buff, 0, 100)) > 0) { 
			swapStream.write(buff, 0, rc); 
		} 
		byte[] in_b = swapStream.toByteArray();
		outStream.write(in_b);
		outStream.close();
	}
}
