package cn.cnnic.android.whois.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import cn.cnnic.android.whois.entity.DomainMark;
import cn.cnnic.android.whois.entity.TldEntity;
import cn.cnnic.android.whois.utils.LanguageUtil;

public class UserPreferenceService {
	
	private Context context;
	private String config="tld-recommend.xml";
	private String markConfig="domain-mark.xml";
	
	public UserPreferenceService(Context context){
		this.context = context;
	}
	/**
	 * 读取tld配置的xml中的内容
	 * @param config
	 * @return
	 * @throws Exception
	 */
	public ArrayList<TldEntity> getUserPreference(boolean isShow) throws Exception{

		ArrayList<TldEntity> preList = null;
		TldEntity tld = null;
		XmlPullParser pullParser = Xml.newPullParser();
		File xml = new File(context.getFilesDir(),config);
		FileInputStream inputStream = new FileInputStream(xml);
		pullParser.setInput(inputStream, "UTF-8");
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
				if("show".equals(pullParser.getName())){
					Integer show = Integer.parseInt(pullParser.nextText());
					tld.setShow(show);
				}
				if("server".equals(pullParser.getName())){
					String server = pullParser.nextText();
					tld.setTldServer(server);
				}
				break;
			case XmlPullParser.END_TAG:
				if("tld".equals(pullParser.getName())){
					if(isShow){
						if(tld.getShow() == 1){
							preList.add(tld);
						}
					}else{
						preList.add(tld);
					}
					tld = null;
				}
				break;
			}
			event = pullParser.next();
		}
		return preList;
	}
	

	/**
	 * 将用户的tld选择写入xml文件
	 * @param out
	 * @param preList
	 * @throws Exception 
	 */
	public void saveUserPreference(List<TldEntity> preList) throws Exception{
		FileOutputStream outStream = null;
		File xmlFile = new File(context.getFilesDir(),config);
		outStream = new FileOutputStream(xmlFile);
		XmlSerializer serializer = Xml.newSerializer();
		serializer.setOutput(outStream,"UTF-8");
		serializer.startDocument("UTF-8", true);
		serializer.startTag(null, "tlds");
		for(TldEntity tld : preList){
			serializer.startTag(null, "tld");
			
			serializer.startTag(null, "name");
			serializer.text(tld.getTldName());
			serializer.endTag(null,"name");
			
			serializer.startTag(null, "server");
			serializer.text(tld.getTldServer());
			serializer.endTag(null,"server");
			
			serializer.startTag(null, "show");
			serializer.text(String.valueOf(tld.getShow()));
			serializer.endTag(null,"show");
			
			serializer.endTag(null,"tld");
		}
		serializer.endTag(null,"tlds");
		serializer.endDocument();
		outStream.flush();
		outStream.close();
	}
	
	public void updateTldList(TldEntity tldNew , String method) throws Exception{
		List<TldEntity> tlds = this.getUserPreference(false);
		if("add".equals(method)){
			tlds.add(tldNew);
			this.saveUserPreference(tlds);
			return;
		}
		for(int index=0;index<tlds.size();index++){
			if(tlds.get(index).getTldName().equals(tldNew.getTldName())){
				if("update".equals(method)){
					tlds.set(index, tldNew);
					this.saveUserPreference(tlds);
					break;
				}else if("delete".equals(method)){
					tlds.remove(index);
					this.saveUserPreference(tlds);
					break;
				}else{
					break;
				}
			}
		}
		
	}
	
	/**
	 * 读取tld配置的xml中的内容
	 * @param config
	 * @return
	 * @throws Exception
	 */
	public ArrayList<DomainMark> getUsersDomainMark() throws Exception{

		ArrayList<DomainMark> markList = null;
		DomainMark domainMark = null;
		XmlPullParser pullParser = Xml.newPullParser();
		File xml = new File(context.getFilesDir(),markConfig);
		FileInputStream inputStream = new FileInputStream(xml);
		pullParser.setInput(inputStream, "UTF-8");
		int event = pullParser.getEventType();
		while(event != XmlPullParser.END_DOCUMENT){
			switch(event){
			case XmlPullParser.START_DOCUMENT:
				markList = new ArrayList<DomainMark>();
				break;
			
			case XmlPullParser.START_TAG:
				if("domain".equals(pullParser.getName())){
					domainMark = new DomainMark();
				}
				if("name".equals(pullParser.getName())){
					String domainName = pullParser.nextText();
					domainMark.setDomainName(domainName);
				}
				if("whois".equals(pullParser.getName())){
					String whois = pullParser.nextText();
					domainMark.setWhoisServer(whois);
				}
				break;
			case XmlPullParser.END_TAG:
				if("domain".equals(pullParser.getName())){
					markList.add(domainMark);
					domainMark = null;
				}
				break;
			}
			event = pullParser.next();
		}
		return markList;
	}
	
	/**
	 * 将用户的域名收藏选择写入xml文件
	 *  
	 */
	public void saveUserMark(List<DomainMark> markList) throws Exception{
		FileOutputStream outStream = null;
		File xmlFile = new File(context.getFilesDir(),"domain-mark.xml");
		outStream = new FileOutputStream(xmlFile);
		XmlSerializer serializer = Xml.newSerializer();
		serializer.setOutput(outStream,"UTF-8");
		serializer.startDocument("UTF-8", true);
		serializer.startTag(null, "domains");
		for(DomainMark domainMark : markList){
			serializer.startTag(null, "domain");
			
			serializer.startTag(null, "name");
			serializer.text(domainMark.getDomainName());
			serializer.endTag(null,"name");
			
			serializer.startTag(null, "whois");
			serializer.text(domainMark.getWhoisServer());
			serializer.endTag(null,"whois");
			
			serializer.endTag(null,"domain");
		}
		serializer.endTag(null,"domains");
		serializer.endDocument();
		outStream.flush();
		outStream.close();
	}
	
	public boolean isMarked(String domainName) throws Exception{
		List<DomainMark> markList = getUsersDomainMark();
		for(DomainMark domainMark : markList){
			if(domainMark.getDomainName().equals(domainName)){
				return true;
			}
		}
		return false;
	}
	
	public void updateMarkList(String domainName , String method) throws Exception{
		List<TldEntity> tlds = this.getUserPreference(false);
		List<DomainMark> markList = this.getUsersDomainMark();
		DomainMark domainMark = new DomainMark(domainName);
		if("add".equals(method)){
			for(TldEntity tld:tlds){
				if(LanguageUtil.getDomainTld(domainName).equals(tld.getTldName())){
					domainMark.setWhoisServer(tld.getTldServer());
					markList.add(domainMark);
					saveUserMark(markList);
					return;
				}
			}
		}
	}
} 
