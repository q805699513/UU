package com.uugty.uu.entity;

import java.io.Serializable;
import java.util.List;


public class HomeTagEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String MSG;
	private String STATUS;
	private MyTags OBJECT;
	
	
	public class MyTags implements Serializable{
		private List<Tags> typeandTags;

		public List<Tags> getTypeandTags() {
			return typeandTags;
		}

		public void setTypeandTags(List<Tags> typeandTags) {
			this.typeandTags = typeandTags;
		}
	}

	public class Tags implements Serializable {
		
		private static final long serialVersionUID = 1L;
		private List<PlayAndBuy> tagList;
		private String typeId;
		private String typeName;
		
		public class PlayAndBuy implements Serializable{
			
			private static final long serialVersionUID = 1L;
			private String tagId;//标签id
			private String tagName;//标签名称
			private String tagPicture;//标签图片
			
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
			public String getTagPicture() {
				return tagPicture;
			}
			public void setTagPicture(String tagPicture) {
				this.tagPicture = tagPicture;
			}
		}
		
		public List<PlayAndBuy> getLIST() {
			return tagList;
		}

		public void setLIST(List<PlayAndBuy> lIST) {
			tagList = lIST;
		}

		public String getTypeId() {
			return typeId;
		}

		public void setTypeId(String typeId) {
			this.typeId = typeId;
		}

		public String getTypeName() {
			return typeName;
		}

		public void setTypeName(String typeName) {
			this.typeName = typeName;
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

	public MyTags getOBJECT() {
		return OBJECT;
	}

	public void setOBJECT(MyTags oBJECT) {
		OBJECT = oBJECT;
	}

//	public List<typeandTags> getLIST() {
//		return LIST;
//	}
//
//	public void setLIST(List<typeandTags> lIST) {
//		LIST = lIST;
//	}
	
}
