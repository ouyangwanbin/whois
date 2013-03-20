package cn.cnnic.android.whois.entity;

/**
 * 
 * @author ouyangwanbin
 *
 */
public class TldEntity {
	private String tldName;
	private String tldServer;
	private String notfind="";
	public String getNotfind() {
		return notfind;
	}
	public void setNotfind(String notfind) {
		this.notfind = notfind;
	}
	public String getTldName() {
		return tldName;
	}
	public void setTldName(String tldName) {
		this.tldName = tldName;
	}
	public String getTldServer() {
		return tldServer;
	}
	public void setTldServer(String tldServer) {
		this.tldServer = tldServer;
	}
}
