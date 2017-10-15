package org.mifosplatform.cms.media.data;

public class MediaassetAttributeData {

	private final String attributeType;
	private final Long attributeName;
	private final String attributeValue;
	private final String attributeNickname;
	private final String attributeImage;
	private final Long id;
	
	public MediaassetAttributeData(final String mediaAttributeType, final Long attributeName, final String mediaattributeValue,
			final String mediaattributeNickname, final String mediaattributeImage, final Long id) {
		
         this.attributeName = attributeName;
         this.attributeType = mediaAttributeType;
         this.attributeValue = mediaattributeValue;
         this.attributeNickname = mediaattributeNickname;
         this.attributeImage = mediaattributeImage;
         this.id = id;
	
	}

	public Long getId() {
		return id;
	}

	public String getAttributeType() {
		return attributeType;
	}

	public Long getAttributeName() {
		return attributeName;
	}

	public String getAttributeValue() {
		return attributeValue;
	}

	public String getAttributeNickname() {
		return attributeNickname;
	}

	public String getAttributeImage() {
		return attributeImage;
	}
}
