package com.uugty.uu.entity;


public class GroupChatSimpleEntity {

	private String STATUS;
	private String MSG;
	private GroupChat OBJECT;

	public class GroupChat {
		private String groupName;
		private String groupImages;

		public String getGroupName() {
			return groupName;
		}

		public void setGroupName(String groupName) {
			this.groupName = groupName;
		}

		public String getGroupImages() {
			return groupImages;
		}

		public void setGroupImages(String groupImages) {
			this.groupImages = groupImages;
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

	public GroupChat getOBJECT() {
		return OBJECT;
	}

	public void setOBJECT(GroupChat oBJECT) {
		OBJECT = oBJECT;
	}

}
