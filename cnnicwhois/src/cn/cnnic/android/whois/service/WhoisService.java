package cn.cnnic.android.whois.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.net.Socket;

import android.os.Handler;
import android.util.Log;
import cn.cnnic.android.whois.entity.Domain;
import cn.cnnic.android.whois.entity.TldEntity;
import cn.cnnic.android.whois.utils.DomainTool;
import cn.cnnic.android.whois.utils.LanguageUtil;

public class WhoisService implements Runnable{
	public static final String WHOIS = "whoisLog";
	private static final int TIME_OUT = 5000;// 10秒
	private int bufferSize = 1024;
	private int port = 43;
	private static String NEW_LINE = "\r\n";
	private TldEntity tldEntity;
	private String domainNameWithoutTld;
	private String encoding;
	private String whoisResult;
	private Handler mHandler;
	private Domain domain = new Domain();

	
	/** 执行一个whois的命令 */
	public Domain execute() throws Exception {
		Socket socket = null;
		BufferedInputStream in = null;
		BufferedOutputStream out = null;
		String command = null;
		String whois = domainNameWithoutTld.trim()+tldEntity.getTldName();
		domain.setDomainName(whois);
		whois = LanguageUtil.chinese2Punycode(whois);
		if(LanguageUtil.isChineseDomain(domainNameWithoutTld) && ".cn".equals(tldEntity.getTldName())){
			//独立处理中文.cn的情况
			command = whois+NEW_LINE;
			tldEntity.setTldServer("cwhois.cnnic.cn");
		}else if(LanguageUtil.isChineseDomain(domainNameWithoutTld) || LanguageUtil.isChineseDomain(tldEntity.getTldName())){
			//如果是中文域名
			command = whois+NEW_LINE;
		}else{
			//英文域名
			command = "domain "+whois+NEW_LINE;
		}
		String domainServer = tldEntity.getTldServer();
		Log.i("whoisLog","domainServer is :" + domainServer + " ; and whois is : " + whois);
		socket = new Socket(domainServer, port);
		socket.setSoTimeout(TIME_OUT);
		in = new BufferedInputStream(socket.getInputStream(), bufferSize);
		out = new BufferedOutputStream(socket.getOutputStream(), bufferSize);	
		out.write(command.getBytes(), 0, command.getBytes().length);
		out.flush();
		byte[] resultData = DomainTool.read(in);
		String result = new String(resultData, encoding);
		if(DomainTool.isRegistered(result)){
			domain.setDomainInfo("registered");
		}else{
			domain.setDomainInfo("unregistered");
		}
		domain.setWhoisResult(result);
		socket.close();
		return domain;
	}
	@Override
	public void run() {
		try {
			mHandler.obtainMessage(0, execute()).sendToTarget();
		} catch (Exception e) {
			domain.setDomainName(domainNameWithoutTld+tldEntity.getTldName());
			domain.setDomainInfo("error");
			mHandler.obtainMessage(0,domain);
			e.printStackTrace();
		}
	}
	public TldEntity getTldEntity() {
		return tldEntity;
	}
	public void setTldEntity(TldEntity tldEntity) {
		this.tldEntity = tldEntity;
	}

	public String getDomainNameWithoutTld() {
		return domainNameWithoutTld;
	}
	public void setDomainNameWithoutTld(String domainNameWithoutTld) {
		this.domainNameWithoutTld = domainNameWithoutTld;
	}
	public String getEncoding() {
		return encoding;
	}
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	public String getWhoisResult() {
		return whoisResult;
	}
	public void setWhoisResult(String whoisResult) {
		this.whoisResult = whoisResult;
	}
	public Handler getmHandler() {
		return mHandler;
	}
	public void setmHandler(Handler mHandler) {
		this.mHandler = mHandler;
	}
}
