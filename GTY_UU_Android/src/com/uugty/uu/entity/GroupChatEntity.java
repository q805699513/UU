package com.uugty.uu.entity;

import java.io.Serializable;
import java.util.List;

public class GroupChatEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String STATUS;
	private String MSG;
	private List<GroupChat> LIST;
	
	public class GroupChat {
		private String groupID;//后台群组Id
		private String groupName;//群组名称
		private String groupContent;//群组内容
		private String groupCreateDate;//群组创建时间
		private String groupOwnerID; //群组拥有者id
		private String groupStatus;//群组状态
		private String groupImages;//群组图片
		private String groupLocation; //群组创建地址
		private String groupEasemobID;//环信群组ID
		private String groupUserCount; //群组人数
		//群组成员。。
		public String getGroupID() {
			return groupID;
		}
		public void setGroupID(String groupID) {
			this.groupID = groupID;
		}
		public String getGroupName() {
			return groupName;
		}
		public void setGroupName(String groupName) {
			this.groupName = groupName;
		}
		public String getGroupContent() {
			return groupContent;
		}
		public void setGroupContent(String groupContent) {
			this.groupContent = groupContent;
		}
		public String getGroupCreateDate() {
			return groupCreateDate;
		}
		public void setGroupCreateDate(String groupCreateDate) {
			this.groupCreateDate = groupCreateDate;
		}
		public String getGroupOwnerID() {
			return groupOwnerID;
		}
		public void setGroupOwnerID(String groupOwnerID) {
			this.groupOwnerID = groupOwnerID;
		}
		public String getGroupStatus() {
			return groupStatus;
		}
		public void setGroupStatus(String groupStatus) {
			this.groupStatus = groupStatus;
		}
		public String getGroupImages() {
			return groupImages;
		}
		public void setGroupImages(String groupImages) {
			this.groupImages = groupImages;
		}
		public String getGroupLocation() {
			return groupLocation;
		}
		public void setGroupLocation(String groupLocation) {
			this.groupLocation = groupLocation;
		}
		public String getGroupEasemobID() {
			return groupEasemobID;
		}
		public void setGroupEasemobID(String groupEasemobID) {
			this.groupEasemobID = groupEasemobID;
		}
		public String getGroupUserCount() {
			return groupUserCount;
		}
		public void setGroupUserCount(String groupUserCount) {
			this.groupUserCount = groupUserCount;
		}
		
	}

	public String getSTATUS() {
		return STATUS;
	}

	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}

	public String getMSG() {
		return MSG;
	}

	public void setMSG(String mSG) {
		MSG = mSG;
	}


	public List<GroupChat> getLIST() {
		return LIST;
	}

	public void setLIST(List<GroupChat> lIST) {
		LIST = lIST;
	}
	
	
}
