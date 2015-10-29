package com.dudu.android.launcher.bean;

import java.io.Serializable;

public class CmdSlots implements Serializable {

	private static final long serialVersionUID = 1L;

	private Cmd cmd;

	public Cmd getCmd() {
		return cmd;
	}

	public void setCmd(Cmd cmd) {
		this.cmd = cmd;
	}

}
