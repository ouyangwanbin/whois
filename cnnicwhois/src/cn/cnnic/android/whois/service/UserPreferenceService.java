package cn.cnnic.android.whois.service;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import android.util.Xml;
import cn.cnnic.android.whois.entity.TldEntity;

public class UserPreferenceService {
	
	/**
	 * 读取xml中的内容
	 * @param config
	 * @return
	 * @throws Exception
	 */
	public ArrayList<TldEntity> getUserPreference(String config , boolean isShow) throws Exception{
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
	 * 将用户的选择写入xml文件
	 * @param out
	 * @param preList
	 * @throws Exception 
	 */
	public void saveUserPreference(OutputStream out , List<TldEntity> preList) throws Exception{
		XmlSerializer serializer = Xml.newSerializer();
		serializer.setOutput(out,"UTF-8");
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
		out.flush();
		out.close();
	}
	
} 
