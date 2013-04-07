package cn.cnnic.android.whois.test;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.test.AndroidTestCase;
import android.util.Log;
import cn.cnnic.android.whois.entity.Domain;
import cn.cnnic.android.whois.entity.TldEntity;
import cn.cnnic.android.whois.service.UserPreferenceService;
import cn.cnnic.android.whois.service.WhoisService;

public class WhoisTest extends AndroidTestCase {
	
	public static final String WHOIS = "whoisLog";
	
	public void testWhoisTest(){
		WhoisService whoisService = new WhoisService();
		whoisService.setDomainNameWithoutTld("baidu");
		TldEntity tldEntity = new TldEntity(".中国" , "cwhois.cnnic.cn" , 1);
		whoisService.setTldEntity(tldEntity);
		try {
			
//			String s = "百度.cn";
			Domain result  = whoisService.execute();
			Log.i(WHOIS, result.getWhoisResult());
		} catch (Exception e) {
			Log.i(WHOIS, e.toString());
			e.printStackTrace();
		}
	}
	
	public void testUserPreTest(){
		UserPreferenceService us = new UserPreferenceService();
		try{
			List<TldEntity> preList = us.getUserPreference("tld-all.xml",true);
			ExecutorService pool = Executors.newFixedThreadPool(preList.size());
			ArrayList<WhoisService> threadList = new ArrayList<WhoisService>();
			int index = 0;
			for(TldEntity tldEntity : preList){
				WhoisService whoisService = new WhoisService();
				whoisService.setDomainNameWithoutTld("新浪");
				whoisService.setEncoding("UTF-8");
				whoisService.setTldEntity(tldEntity);
				threadList.add(index, whoisService);
				pool.execute(whoisService);
				index++;
			}
			Thread.sleep(2000);
			for(WhoisService whoisService : threadList){
				Log.i(WHOIS,whoisService.getTldEntity().getTldName()+" : "+whoisService.getWhoisResult());
			}
			pool.shutdown();
		}catch(Exception e){
			Log.i(WHOIS, e.toString());
			e.printStackTrace();
		}
	}
	
	public void testSaveUserPreTest(){
		UserPreferenceService us = new UserPreferenceService();
		try{
			List<TldEntity> preList = us.getUserPreference("tld-test.xml",true);
			preList.add(new TldEntity(".cn","whois.cnnic.cn",1));
			preList.add(new TldEntity(".com","verisign.com",1));
			preList.add(new TldEntity(".net","verisign.net",1));
			
			File xmlFile = new File(getContext().getFilesDir(),"tld-test.xml");
			FileOutputStream outStream = new FileOutputStream(xmlFile);
			us.saveUserPreference(outStream,preList);
		}catch(Exception e){
			
		}
	}
}
