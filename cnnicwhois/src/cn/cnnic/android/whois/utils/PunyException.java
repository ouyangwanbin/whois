package cn.cnnic.android.whois.utils;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: KNET </p>
 * @author Orsen Leo
 * @version 1.0
 */

@SuppressWarnings("serial")
public class PunyException extends java.lang.Exception implements java.io.Serializable{

  public PunyException() {
    super();
  }

  public PunyException(String msg) {
        super(msg);
  }
}