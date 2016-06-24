package com.uugty.uu.entity;

import java.io.Serializable;
import java.util.List;

public class UserCommentEntity implements Serializable{
	private static final long serialVersionUID = 1L;
	private String MSG;
	private String STATUS;
	private UserComment OBJECT;
	private List<UserCommentList> LIST;

	public class UserCommentList {

		private String totalIndex;// 综合评分
		private String commentContent;// 评分的文字内容
		private String commentImages;// 评价上传的图片
		private String userName;// 用户姓名
		private String userAvatar;// 用户头像
		private String commentDate;// 评价时间
		private String replyContent;// 回复文字
		private String replyImages;// 回复的图片
		public String getTotalIndex() {
			return totalIndex;
		}
		public void setTotalIndex(String totalIndex) {
			this.totalIndex = totalIndex;
		}
		public String getCommentContent() {
			return commentContent;
		}
		public void setCommentContent(String commentContent) {
			this.commentContent = commentContent;
		}
		public String getCommentImages() {
			return commentImages;
		}
		public void setCommentImages(String commentImages) {
			this.commentImages = commentImages;
		}
		public String getUserName() {
			return userName;
		}
		public void setUserName(String userName) {
			this.userName = userName;
		}
		public String getUserAvatar() {
			return userAvatar;
		}
		public void setUserAvatar(String userAvatar) {
			this.userAvatar = userAvatar;
		}
		public String getCommentDate() {
			return commentDate;
		}
		public void setCommentDate(String commentDate) {
			this.commentDate = commentDate;
		}
		public String getReplyContent() {
			return replyContent;
		}
		public void setReplyContent(String replyContent) {
			this.replyContent = replyContent;
		}
		public String getReplyImages() {
			return replyImages;
		}
		public void setReplyImages(String replyImages) {
			this.replyImages = replyImages;
		}

	}

	public class UserComment {
		private String commentCount; // 评论总条数
		private String avageServiceIndex; // 服务指数
		private String avageRatioIndex;// 性价比指数
		private String avageFreshIndex;// 新鲜指数
		public String getCommentCount() {
			return commentCount;
		}
		public void setCommentCount(String commentCount) {
			this.commentCount = commentCount;
		}
		public String getAvageServiceIndex() {
			return avageServiceIndex;
		}
		public void setAvageServiceIndex(String avageServiceIndex) {
			this.avageServiceIndex = avageServiceIndex;
		}
		public String getAvageRatioIndex() {
			return avageRatioIndex;
		}
		public void setAvageRatioIndex(String avageRatioIndex) {
			this.avageRatioIndex = avageRatioIndex;
		}
		public String getAvageFreshIndex() {
			return avageFreshIndex;
		}
		public void setAvageFreshIndex(String avageFreshIndex) {
			this.avageFreshIndex = avageFreshIndex;
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

	public UserComment getOBJECT() {
		return OBJECT;
	}

	public void setOBJECT(UserComment oBJECT) {
		OBJECT = oBJECT;
	}

	public List<UserCommentList> getLIST() {
		return LIST;
	}

	public void setLIST(List<UserCommentList> lIST) {
		LIST = lIST;
	}

}
