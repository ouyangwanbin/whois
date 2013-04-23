package cn.cnnic.android.whois.entity;

import java.io.Serializable;

public class DomainMark implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2568460921942134995L;
	private String domainName;
	private String whoisServer;

	public DomainMark(){
		
	}
	public DomainMark(String domainName){
		this.domainName = domainName;
	}
	
	public String getDomainName(){
		return this.domainName;
	}
	
	public void setDomainName(String domainName){
		this.domainName = domainName;
	}
	
	public String getWhoisServer() {
		return whoisServer;
	}
	public void setWhoisServer(String whoisServer) {
		this.whoisServer = whoisServer;
	}
	@Override
	public boolean equals(Object o) {
		if(o instanceof DomainMark){
			DomainMark obj = (DomainMark)o;
			if(this.getDomainName().equals(obj.getDomainName())){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	
	
}
