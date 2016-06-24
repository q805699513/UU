package com.uugty.uu.entity;


public class AppStartImageEntity {
	private String STATUS;
	private String MSG;
	private AppStartImage OBJECT;
	
	public class AppStartImage {
		private String bootImageUrl;//这个可能为空,图片跳转地址
		private String bootImage;//图片名

		public String getBootImageUrl() {
			return bootImageUrl;
		}

		public void setBootImageUrl(String bootImageUrl) {
			this.bootImageUrl = bootImageUrl;
		}

		public String getBootImage() {
			return bootImage;
		}

		public void setBootImage(String bootImage) {
			this.bootImage = bootImage;
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

	public AppStartImage getOBJECT() {
		return OBJECT;
	}

	public void setOBJECT(AppStartImage oBJECT) {
		OBJECT = oBJECT;
	}
	
	
}
