package com.uugty.uu.entity;

import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;

//地图标签使用对象
public class MarkerEntity {

	// 经纬度
	private LatLng latLng;
	// mark上的话
	private String text;
	// 用户名
	private String userName;
	// mark
	private Marker marker;
	// 用户图像
	private String userHead;
	//用户身份
	private String userStatus;

	public MarkerEntity(LatLng latLng, String text, String userName,
			String userHead,String status) {
		this.latLng = latLng;
		this.text = text;
		this.userName = userName;
		this.userHead = userHead;
		this.userStatus=status;
	}

	public LatLng getLatLng() {
		return latLng;
	}

	public void setLatLng(LatLng latLng) {
		this.latLng = latLng;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Marker getMarker() {
		return marker;
	}

	public void setMarker(Marker marker) {
		this.marker = marker;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserHead() {
		return userHead;
	}

	public void setUserHead(String userHead) {
		this.userHead = userHead;
	}

	public String getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}

	
}
