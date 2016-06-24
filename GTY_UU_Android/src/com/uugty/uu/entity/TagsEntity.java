package com.uugty.uu.entity;



public class TagsEntity {
	private String tagId;// id
	private String tagName;// 内容
	
	public TagsEntity(String describeImage,String describeTime){
		this.setTagId(describeImage);
		this.setTagName(describeTime);
				
	}

	public String getTagId() {
		return tagId;
	}

	public void setTagId(String tagId) {
		this.tagId = tagId;
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	
}