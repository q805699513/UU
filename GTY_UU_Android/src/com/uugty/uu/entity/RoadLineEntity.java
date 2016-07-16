package com.uugty.uu.entity;

import java.io.Serializable;
import java.util.List;
/**路线发布对象
 * 
 */
//路线发布对象
public class RoadLineEntity implements Serializable{

	
	private static final long serialVersionUID = 1L;
	private String roadlineStatus;//路线审核状态
	private String roadlineId;//路线Id
	private String roadlineTitle; //标题
	private String roadlinePrice; //价格
	private String roadlineContent; //摘要
	private String roadlineInfo;//旅行路线
	private String roadlineFeeContains;//价格包含的内容
	private String roadlineStartArea;//旅行出发地
	private String roadlineSpecialMark;//特别说明
	private String roadlineGoalArea;//目的地
	private String roadlineDays;//路线的天数
	private String roadlineBackground;//背景图片
	private String roadlineCreateDate;//时间
	private List<RoadLine> roadlineDescribes;
	private String userId;//insert数据库增加
	private List<TagsEntity> tags;//路线标签
	private String orderNum;//路线销量
	private String orderCount;//已下架销量

	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getRoadlineCreateDate() {
		return roadlineCreateDate;
	}

	public void setRoadlineCreateDate(String roadlineCreateDate) {
		this.roadlineCreateDate = roadlineCreateDate;
	}

	public String getRoadlineInfo() {
		return roadlineInfo;
	}

	public void setRoadlineInfo(String roadlineInfo) {
		this.roadlineInfo = roadlineInfo;
	}

	public String getRoadlineFeeContains() {
		return roadlineFeeContains;
	}

	public void setRoadlineFeeContains(String roadlineFeeContains) {
		this.roadlineFeeContains = roadlineFeeContains;
	}

	public String getRoadlineStartArea() {
		return roadlineStartArea;
	}

	public void setRoadlineStartArea(String roadlineStartArea) {
		this.roadlineStartArea = roadlineStartArea;
	}

	public String getRoadlineSpecialMark() {
		return roadlineSpecialMark;
	}

	public void setRoadlineSpecialMark(String roadlineSpecialMark) {
		this.roadlineSpecialMark = roadlineSpecialMark;
	}

	public String getRoadlineId() {
		return roadlineId;
	}

	public void setRoadlineId(String roadlineId) {
		this.roadlineId = roadlineId;
	}

	public String getRoadlineTitle() {
		return roadlineTitle;
	}

	public void setRoadlineTitle(String roadlineTitle) {
		this.roadlineTitle = roadlineTitle;
	}

	public String getRoadlinePrice() {
		return roadlinePrice;
	}

	public void setRoadlinePrice(String roadlinePrice) {
		this.roadlinePrice = roadlinePrice;
	}

	public String getRoadlineContent() {
		return roadlineContent;
	}

	public void setRoadlineContent(String roadlineContent) {
		this.roadlineContent = roadlineContent;
	}

	public String getRoadlineGoalArea() {
		return roadlineGoalArea;
	}

	public void setRoadlineGoalArea(String roadlineGoalArea) {
		this.roadlineGoalArea = roadlineGoalArea;
	}

	public List<RoadLine> getRoadlineDescribes() {
		return roadlineDescribes;
	}

	public void setRoadLineDescribes(
			List<RoadLine> roadLineDescribes) {
		this.roadlineDescribes = roadLineDescribes;
	}

	public String getRoadlineDays() {
		return roadlineDays;
	}

	public void setRoadlineDays(String roadlineDays) {
		this.roadlineDays = roadlineDays;
	}

	public String getRoadlineBackground() {
		return roadlineBackground;
	}

	public void setRoadlineBackground(String roadlineBackground) {
		this.roadlineBackground = roadlineBackground;
	}

	public String getRoadlineStatus() {
		return roadlineStatus;
	}

	public void setRoadlineStatus(String roadlineStatus) {
		this.roadlineStatus = roadlineStatus;
	}

	public RoadLineEntity(String roadlineStatus, String roadlineTitle,
			String roadlineBackground) {
		super();
		this.roadlineStatus = roadlineStatus;
		this.roadlineTitle = roadlineTitle;
		this.roadlineBackground = roadlineBackground;
	}

	public RoadLineEntity() {
		super();
	}

	public List<TagsEntity> getRoadlineTags() {
		return tags;
	}

	public void setRoadlineTags(List<TagsEntity> roadlineTags) {
		this.tags = roadlineTags;
	}


	public String getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}

	public String getOrderCount() {
		return orderCount;
	}

	public void setOrderCount(String orderCount) {
		this.orderCount = orderCount;
	}
}
