package cn.cnnic.android.whois.entity;

import java.io.Serializable;

/**
 * 
 * @author ouyangwanbin
 *
 */
public class TldEntity implements Serializable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8232910839787927190L;
	private String tldName;
	private String tldServer;
	private Integer show = 0;
	
	public Integer getShow() {
		return show;
	}



	public void setShow(Integer show) {
		this.show = show;
	}



	public TldEntity(String tldName, String tldServer , Integer show) {
		this.tldName = tldName;
		this.tldServer = tldServer;
		this.show = show;
	}
	
	
	
	public TldEntity(){
		
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
