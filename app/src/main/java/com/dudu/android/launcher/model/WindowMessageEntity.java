package com.dudu.android.launcher.model;

import com.dudu.voice.window.MessageType;
import java.io.Serializable;

public class WindowMessageEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	private String content;

	private MessageType type;

	public WindowMessageEntity(String content, MessageType type) {
        this.content = content;
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}

}
