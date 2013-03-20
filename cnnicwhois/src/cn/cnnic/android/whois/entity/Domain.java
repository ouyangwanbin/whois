package cn.cnnic.android.whois.entity;

public class Domain {
	private String domainName;
	private String whoisResult;
	private String domainInfo;
	
	public String getDomainInfo() {
		return domainInfo;
	}
	public void setDomainInfo(String domainInfo) {
		this.domainInfo = domainInfo;
	}
	public String getDomainName() {
		return domainName;
	}
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	public String getWhoisResult() {
		return whoisResult;
	}
	public void setWhoisResult(String whoisResult) {
		this.whoisResult = whoisResult;
	}
}
