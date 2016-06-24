package com.uugty.uu.entity;

import java.io.Serializable;
import java.util.List;

public class DisCoveryEntity implements Serializable{
	private static final long serialVersionUID = 1L;
	private String MSG;
	private String STATUS;
	private MainDate OBJECT;
	public class MainDate{
		private String mainImage;
		private List<TagTypeDetail> tagTypeDetails;
		public String getMainImage() {
			return mainImage;
		}
		public void setMainImage(String mainImage) {
			this.mainImage = mainImage;
		}
		public List<TagTypeDetail> getTagTypeDetails() {
			return tagTypeDetails;
		}
		public void setTagTypeDetails(List<TagTypeDetail> tagTypeDetails) {
			this.tagTypeDetails = tagTypeDetails;
		}
		public MainDate(String mainImage, List<TagTypeDetail> tagTypeDetails) {
			super();
			this.mainImage = mainImage;
			this.tagTypeDetails = tagTypeDetails;
		}
		
		
	}
	
	public class TagTypeDetail{
		private String typeId; //标签类别id
		private String typeName; //标签类别名字
		private String typeContent; //标签类别内容
		private String typePicture; //标签类别图片
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
		public String getTypeContent() {
			return typeContent;
		}
		public void setTypeContent(String typeContent) {
			this.typeContent = typeContent;
		}
		public String getTypePicture() {
			return typePicture;
		}
		public void setTypePicture(String typePicture) {
			this.typePicture = typePicture;
		}
		public TagTypeDetail(String typeId, String typeName,
				String typeContent, String typePicture) {
			super();
			this.typeId = typeId;
			this.typeName = typeName;
			this.typeContent = typeContent;
			this.typePicture = typePicture;
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

	public MainDate getOBJECT() {
		return OBJECT;
	}

	public void setOBJECT(MainDate oBJECT) {
		OBJECT = oBJECT;
	}

	public DisCoveryEntity(String mSG, String sTATUS, MainDate oBJECT) {
		super();
		MSG = mSG;
		STATUS = sTATUS;
		OBJECT = oBJECT;
	}
	
}
