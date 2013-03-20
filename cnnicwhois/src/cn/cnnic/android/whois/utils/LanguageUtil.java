package cn.cnnic.android.whois.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Title: LanguageUtil.java</p>
 * <p>Description:鐠囶叀鈻堟径鍕倞缁拷/p>
 * @author Jia Shuo
 */
public class LanguageUtil
{
	
	/**
	 * punyCode杞腑鏂�	 * @param str
	 * @return
	 * @throws PunyException
	 */
	public static String punycode2Chinese(String str)
		throws PunyException
	{
		String dest_name = null;
		if (str!=null)
		{
			str = str.toLowerCase();
			if (str.startsWith("xn--"))
			{
				dest_name = PunycodeUtil.punycode2chinese(str);
			}else
			{
				dest_name = str;
			}
		}else
		{
			dest_name = str;
		}
		dest_name = dest_name.toLowerCase();		
		return dest_name;
	}
	
	/**
	 * 
	 *  涓枃杞琍uniCode
	 * @param str
	 * @return
	 * @throws PunyException
	 */
	public static String chinese2Punycode(String str)
		throws PunyException
	{
		String dest_name = PunycodeUtil.chinese2punycode(str);
		System.out.println(str + " = " + dest_name);
		return dest_name;		
	}
	
	/**
	 * 
	 * 鍒ゆ柇鏄惁鏄痯unicode
	 * @param str
	 * @return
	 */
	public static boolean isPunycode(String str)
	{
		if (str != null)
		{
			str = str.toLowerCase();
			if (str.startsWith("xn--"))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public static String getDomainTld(String domainName){
		if(domainName != null && domainName.trim().length()>0){
			domainName = domainName.substring(domainName.indexOf("."), domainName.length());
		}
		return domainName;
	}
	
	public static String getDomainNameWithoutTld(String domainName){
		if(domainName != null && domainName.trim().length()>0 && domainName.indexOf(".") > 0){
			domainName = domainName.substring(0, domainName.indexOf("."));
		}
		return domainName;
	}
	
	public static boolean isChineseDomain(String domainName){
		Pattern p = Pattern.compile("[\u4e00-\u9fa5]+");
		Matcher m = p.matcher(domainName);
		if(domainName != null && domainName.trim().length()>0){
			if(m.find() || isPunycode(domainName)){
				return true;
			}else{
				return false;
			}
		}
		return false;
	}
}



