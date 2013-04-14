package cn.cnnic.android.whois.test;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.test.AndroidTestCase;
import android.util.Log;
import cn.cnnic.android.whois.entity.Domain;
import cn.cnnic.android.whois.entity.DomainMark;
import cn.cnnic.android.whois.entity.TldEntity;
import cn.cnnic.android.whois.service.UserPreferenceService;
import cn.cnnic.android.whois.service.WhoisService;

public class WhoisTest extends AndroidTestCase {
	
	public static final String WHOIS = "whoisLog";
	
	public void testWhoisTest() throws Exception{
		Pattern pattern = Pattern.compile("Expiration Date.*|Expiry date.*|expires.*|renewal[\\s]*:.*|Expire Date.*|\\[\\$BM-8z4\\|8B\\(B\\][\\s]*.*|Expired.*",Pattern.CASE_INSENSITIVE);
		UserPreferenceService us = new UserPreferenceService(getContext());
		//List<TldEntity> preList = us.getUserPreference(false);
		//for(TldEntity tld : preList){
			WhoisService whoisService = new WhoisService();
			whoisService.setDomainNameWithoutTld("google");
			whoisService.setEncoding("UTF-8");
			TldEntity tld = new TldEntity(".am" , "whois.amnic.net" , 1);
			whoisService.setTldEntity(tld);
			try {
				Domain result  = whoisService.execute();
				if("registered".equals(result.getDomainInfo())){
					Matcher matcher = pattern.matcher(result.getWhoisResult());
					//Log.i(WHOIS, "processing : " + tld.getTldName());
					if(matcher.find()){
						Log.i(WHOIS, "match : " + matcher.group());
					}else{
//						Log.i(WHOIS, "unmatch : " + tld.getTldName());
					}
				}
			} catch (Exception e) {
				Log.i(WHOIS, e.toString());
				e.printStackTrace();
			}
			Thread.sleep(2000);
		//}
		
	}
	
	public void testUserPreTest(){
		UserPreferenceService us = new UserPreferenceService(getContext());
		try{
			List<TldEntity> preList = us.getUserPreference(true);
			for(TldEntity tld : preList){
				if(tld.getTldName().equals(".com")){
					Log.i(WHOIS,".com show is : " + tld.getShow());
				}
			}
//			ExecutorService pool = Executors.newFixedThreadPool(preList.size());
//			ArrayList<WhoisService> threadList = new ArrayList<WhoisService>();
//			int index = 0;
//			for(TldEntity tldEntity : preList){
//				WhoisService whoisService = new WhoisService();
//				whoisService.setDomainNameWithoutTld("ÐÂÀË");
//				whoisService.setEncoding("UTF-8");
//				whoisService.setTldEntity(tldEntity);
//				threadList.add(index, whoisService);
//				pool.execute(whoisService);
//				index++;
//			}
//			Thread.sleep(2000);
//			for(WhoisService whoisService : threadList){
//				Log.i(WHOIS,whoisService.getTldEntity().getTldName()+" : "+whoisService.getWhoisResult());
//			}
//			pool.shutdown();
		}catch(Exception e){
			Log.i(WHOIS, e.toString());
			e.printStackTrace();
		}
	}
	
//	public void testSaveUserPreTest(){
//		UserPreferenceService us = new UserPreferenceService();
//		try{
//			List<TldEntity> preList = us.getUserPreference("tld-recommend.xml",false);
//			for(TldEntity tld : preList){
//				if(tld.getTldName().equals(".com")){
//					Log.i("whoisLog", ".com show is : " + tld.getShow());
//				}
//			}
//			File xmlFile = new File(getContext().getFilesDir(),"tld-test.xml");
//			FileOutputStream outStream = new FileOutputStream(xmlFile);
//			us.saveUserPreference(outStream,preList);
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//	}
	
	public void testGetUserDomainMarkTest(){
		UserPreferenceService us = new UserPreferenceService(getContext());
			try{
				List<DomainMark> markList = us.getUsersDomainMark();
				for(DomainMark domainMark : markList){
					Log.i("whoisLog", domainMark.getDomainName());
				}
			}catch(Exception e){
				e.printStackTrace();
			}
	}
	
	public void testSaveDomainMarkTest(){
		UserPreferenceService us = new UserPreferenceService(getContext());
		try{
			List<DomainMark> markList = us.getUsersDomainMark();
			DomainMark domainMark = new DomainMark("google.com");
			domainMark.setWhoisServer("whois.cnnic.cn");
			markList.add(domainMark);
			us.saveUserMark(markList);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void testUpMarkTest(){
		UserPreferenceService us = new UserPreferenceService(getContext());
		try{
			us.updateMarkList("baidu.net.cn", "add");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
