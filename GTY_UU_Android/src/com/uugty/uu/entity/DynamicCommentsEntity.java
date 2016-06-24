package com.uugty.uu.entity;

import java.io.Serializable;
import java.util.List;

public class DynamicCommentsEntity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String MSG;
	private String STATUS;
	private String OBJECT;
	private List<DynamicComments> LIST;
	public class DynamicComments implements Serializable{
		private String commentId;//主键
		
		private String commentContent;//评论内容
		
		private String commentCreateDate;//评论时间
		
		private String saidId;//朋友圈动态id
		
		private String commentUserId;//评论人
		
		private String saidCommentTimes; //评论次数
		
		private String userAvatar;// 评论人的头像
		
		private String userName;// 评论人的姓名
		
		private String replayedUserName; //回复人的姓名
		
		private String parentId;//回复的父评论
		
		private String userIdValidate;

		public String getCommentId() {
			return commentId;
		}

		public void setCommentId(String commentId) {
			this.commentId = commentId;
		}

		public String getCommentContent() {
			return commentContent;
		}

		public void setCommentContent(String commentContent) {
			this.commentContent = commentContent;
		}

		public String getCommentCreateDate() {
			return commentCreateDate;
		}

		public void setCommentCreateDate(String commentCreateDate) {
			this.commentCreateDate = commentCreateDate;
		}

		public String getSaidId() {
			return saidId;
		}

		public void setSaidId(String saidId) {
			this.saidId = saidId;
		}

		public String getCommentUserId() {
			return commentUserId;
		}

		public void setCommentUserId(String commentUserId) {
			this.commentUserId = commentUserId;
		}

		public String getSaidCommentTimes() {
			return saidCommentTimes;
		}

		public void setSaidCommentTimes(String saidCommentTimes) {
			this.saidCommentTimes = saidCommentTimes;
		}

		public String getUserAvatar() {
			return userAvatar;
		}

		public void setUserAvatar(String userAvatar) {
			this.userAvatar = userAvatar;
		}

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}

		public String getReplayedUserName() {
			return replayedUserName;
		}

		public void setReplayedUserName(String replayedUserName) {
			this.replayedUserName = replayedUserName;
		}

		public String getParentId() {
			return parentId;
		}

		public void setParentId(String parentId) {
			this.parentId = parentId;
		}

		public String getUserIdValidate() {
			return userIdValidate;
		}

		public void setUserIdValidate(String userIdValidate) {
			this.userIdValidate = userIdValidate;
		}

		public DynamicComments() {
			super();
		}
		
		
	}
	public String getMSG() {
		return MSG;
	}
	public void setMSG(String mSG) {
		MSG = mSG;
	}
	public String getSTATUS() {
		return STATUS;
	}
	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}
	public String getOBJECT() {
		return OBJECT;
	}
	public void setOBJECT(String oBJECT) {
		OBJECT = oBJECT;
	}
	public List<DynamicComments> getLIST() {
		return LIST;
	}
	public void setLIST(List<DynamicComments> lIST) {
		LIST = lIST;
	}
	
	
}
