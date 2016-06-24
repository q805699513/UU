package com.uugty.uu.entity;

public class EvaluationDetailEntity {

	private String STATUS;
	private String MSG;
	private EvaluationDetail OBJECT;

	public class EvaluationDetail {
		private String orderId;// 订单id
		private String commentId;// 评论id
		private String commentUserId;// 评论的用户id
		private String commentedUserId;// 被评论的用户id
		private String commentType;// 1 用户评论，2 订单评论
		private String commentContent;// 评论的内容
		private String serviceIndex;// 服务指数
		private String freshIndex;// 新鲜指数
		private String ratioIndex;// 信价比指数
		private String commentImages;// 评价上传的图片
		private String commentDate;// 评价的日期
		private String totalIndex;// 总评分

		public String getOrderId() {
			return orderId;
		}

		public void setOrderId(String orderId) {
			this.orderId = orderId;
		}

		public String getCommentId() {
			return commentId;
		}

		public void setCommentId(String commentId) {
			this.commentId = commentId;
		}

		public String getCommentUserId() {
			return commentUserId;
		}

		public void setCommentUserId(String commentUserId) {
			this.commentUserId = commentUserId;
		}

		public String getCommentedUserId() {
			return commentedUserId;
		}

		public void setCommentedUserId(String commentedUserId) {
			this.commentedUserId = commentedUserId;
		}

		public String getCommentType() {
			return commentType;
		}

		public void setCommentType(String commentType) {
			this.commentType = commentType;
		}

		public String getCommentContent() {
			return commentContent;
		}

		public void setCommentContent(String commentContent) {
			this.commentContent = commentContent;
		}

		public String getServiceIndex() {
			return serviceIndex;
		}

		public void setServiceIndex(String serviceIndex) {
			this.serviceIndex = serviceIndex;
		}

		public String getFreshIndex() {
			return freshIndex;
		}

		public void setFreshIndex(String freshIndex) {
			this.freshIndex = freshIndex;
		}

		public String getRatioIndex() {
			return ratioIndex;
		}

		public void setRatioIndex(String ratioIndex) {
			this.ratioIndex = ratioIndex;
		}

		public String getCommentImages() {
			return commentImages;
		}

		public void setCommentImages(String commentImages) {
			this.commentImages = commentImages;
		}

		public String getCommentDate() {
			return commentDate;
		}

		public void setCommentDate(String commentDate) {
			this.commentDate = commentDate;
		}

		public String getTotalIndex() {
			return totalIndex;
		}

		public void setTotalIndex(String totalIndex) {
			this.totalIndex = totalIndex;
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

	public EvaluationDetail getOBJECT() {
		return OBJECT;
	}

	public void setOBJECT(EvaluationDetail oBJECT) {
		OBJECT = oBJECT;
	}

}
