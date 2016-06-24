package com.uugty.uu.entity;

import java.util.ArrayList;
import java.util.List;

public class RouteMarkEntity {

	private String describeImage;
	private String PicPath;
	private String describeTime;
	private String describeArea;
	private List<RoadMarker> describeMarks;

	public RouteMarkEntity(String imageName,String path,String time,String area){
		this.describeImage= imageName;
		this.PicPath = path;
		this.describeTime = time;
		this.describeArea= area;
	}
	public RouteMarkEntity(){
		this.describeImage="";
		this.PicPath = "";
		this.describeTime = "";
		this.describeArea= "";
		this.describeMarks = new ArrayList<RoadMarker>();
	}
	public String getDescribeImage() {
		return describeImage;
	}

	public void setDescribeImage(String describeImage) {
		this.describeImage = describeImage;
	}

	public String getPicPath() {
		return PicPath;
	}

	public void setPicPath(String picPath) {
		PicPath = picPath;
	}

	public String getDescribeTime() {
		return describeTime;
	}

	public void setDescribeTime(String describeTime) {
		this.describeTime = describeTime;
	}

	public String getDescribeArea() {
		return describeArea;
	}

	public void setDescribeArea(String describeArea) {
		this.describeArea = describeArea;
	}

	public List<RoadMarker> getDescribeMarks() {
		return describeMarks;
	}

	public void setDescribeMarks(ArrayList<RoadMarker> describeMarks) {
		this.describeMarks = describeMarks;
	}

}
