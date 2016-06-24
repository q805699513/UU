package com.uugty.uu.entity;

import java.util.List;


public class RoadLine {
	private String describeArea;// 地点
	private String describeTime;// 时间
	private String describeImage;// 图片路径
	private List<RoadMarker> describeMarks;
	
	public RoadLine(String describeImage,String describeTime,String describeArea){
		this.describeImage=describeImage;
		this.describeTime = describeTime;
		this.describeArea =describeArea;
				
	}
	public String getDescribeArea() {
		return describeArea;
	}

	public void setDescribeArea(String describeArea) {
		this.describeArea = describeArea;
	}

	public String getDescribeTime() {
		return describeTime;
	}

	public void setDescribeTime(String describeTime) {
		this.describeTime = describeTime;
	}

	public String getDescribeImage() {
		return describeImage;
	}

	public void setDescribeImage(String describeImage) {
		this.describeImage = describeImage;
	}

	public List<RoadMarker> getDescribeMarks() {
		return describeMarks;
	}

	public void setDescribeMarks(List<RoadMarker> describeMarks) {
		this.describeMarks = describeMarks;
	}

	
}