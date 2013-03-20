package cn.cnnic.android.whois.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.cnnic.android.whois.entity.TldEntity;
import cn.cnnic.android.whois.service.UserPreferenceService;
import cn.cnnic.android.whois.service.WhoisService;
import android.test.AndroidTestCase;
import android.util.Log;

public class WhoisTest extends AndroidTestCase {
	
	public static final String WHOIS = "whoisLog";
	
	public void testWhoisTest(){
		WhoisService whoisService = new WhoisService();
		try {
			
			String s = "百度ss";
			Pattern p = Pattern.compile("[\u4e00-\u9fa5]+");
			Matcher m = p.matcher(s.toString());
			if(m.find()){
				Log.i(WHOIS,s + " is ZH");
			}else{
				Log.i(WHOIS,s + " is EN");
			}
			//ExecutorService pool = Executors.newFixedThreadPool(2);
			//String result  = whoisService.execute("ibm.中国", "UTF-8");
//			Log.i(WHOIS, result);
		} catch (Exception e) {
			Log.i(WHOIS, e.toString());
			e.printStackTrace();
		}
	}
	
	public void testUserPreTest(){
		UserPreferenceService us = new UserPreferenceService();
		try{
			List<TldEntity> preList = us.getUserPreference("tld-test.xml");
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
}
