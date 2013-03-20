package cn.cnnic.android.whois.service;

import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;
import cn.cnnic.android.whois.entity.TldEntity;

public class UserPreferenceService {
	
	public ArrayList<TldEntity> getUserPreference(String config) throws Exception{
		ArrayList<TldEntity> preList = null;
		TldEntity tld = null;
		InputStream xml =  this.getClass().getClassLoader().getResourceAsStream(config);
		XmlPullParser pullParser = Xml.newPullParser();
		pullParser.setInput(xml, "UTF-8");
		int event = pullParser.getEventType();
		while(event != XmlPullParser.END_DOCUMENT){
			switch(event){
			case XmlPullParser.START_DOCUMENT:
				preList = new ArrayList<TldEntity>();
				break;
			
			case XmlPullParser.START_TAG:
				if("tld".equals(pullParser.getName())){
					tld = new TldEntity();
				}
				if("name".equals(pullParser.getName())){
					String name = pullParser.nextText();
					tld.setTldName(name);
				}
				
				if("server".equals(pullParser.getName())){
					String server = pullParser.nextText();
					tld.setTldServer(server);
				}
				
				if("notfind".equals(pullParser.getName())){
					String notfind = pullParser.nextText();
					tld.setNotfind(notfind);
				}
				break;
			case XmlPullParser.END_TAG:
				if("tld".equals(pullParser.getName())){
					preList.add(tld);
					tld = null;
				}
				break;
			}
			event = pullParser.next();
		}
		return preList;
	}
} 
