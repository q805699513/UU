package com.uugty.uu.entity;

import java.io.Serializable;

public class ChannelItem implements Serializable {

	private static final long serialVersionUID = 593649593966770756L;

	private int id;// 顺序
	private String text;
	private int selected;

	public ChannelItem(String text){
		this.text = text;
	}
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getSelected() {
		return selected;
	}

	public void setSelected(int selected) {
		this.selected = selected;
	}

}
