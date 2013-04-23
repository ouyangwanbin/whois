package cn.cnnic.android.whois.entity;

import java.io.Serializable;

public class Registrar implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2568460921942134995L;
	private String registrarName;
	private String address;
	private String setDefault;
	public String getRegistrarName() {
		return registrarName;
	}
	public void setRegistrarName(String registrarName) {
		this.registrarName = registrarName;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getSetDefault() {
		return setDefault;
	}
	public void setSetDefault(String setDefault) {
		this.setDefault = setDefault;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof Registrar){
			Registrar obj = (Registrar)o;
			if(this.getRegistrarName().equals(obj.getRegistrarName())){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	
	
}
