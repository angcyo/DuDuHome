package com.dudu.android.launcher.bean;

import java.io.Serializable;

public class Rsphead implements Serializable {
	private static final long serialVersionUID = 1L;
	private int rc;
	private String service;
	private String text;

	public int getRc() {
		return rc;
	}

	public void setRc(int rc) {
		this.rc = rc;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
