package com.dudu.android.launcher.bean;

import java.io.Serializable;

public class Cmd implements Serializable {

	private static final long serialVersionUID = 1L;
	private String option;
	private String type;
	private String choise;
	private String address;

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getOption() {
		return option;
	}

	public void setOption(String option) {
		this.option = option;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getChoise() {
		return choise;
	}

	public void setChoise(String choise) {
		this.choise = choise;
	}

}
