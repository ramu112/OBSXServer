package org.mifosplatform.portfolio.association.data;

import java.util.Collection;
import java.util.List;

import org.mifosplatform.billing.emun.data.EnumValuesData;

public class AssociationData {
         private Long orderId;
         private String planCode;
         private String itemCode;
         private String serialNum;
         private Long id;
         private Long planId;
         private List<AssociationData> hardwareData;
         private List<AssociationData> planData;
		private Long clientId;
		private String allocationType;
		private String provisionNumber;
		private Long saleId;
		private Long itemId;
		private String propertyCode;
		private Collection<EnumValuesData> enumValuesDatas;
		
	
	public AssociationData(Long orderId,Long id, String planCode, String itemCode,String serialNum,Long planId) {
		
		this.orderId=orderId;
		this.planCode=planCode;
		this.itemCode=itemCode;
		this.serialNum=serialNum;
		this.planId=planId;
		this.id=id;

	}

	public AssociationData(List<AssociationData> hardwareDatas, List<AssociationData> planDatas) {
		// TODO Auto-generated constructor stub
		this.hardwareData=hardwareDatas;
		this.planData=planDatas;
		
	}

	public AssociationData(Long orderId, String planCode, String provisionNumber,Long id, Long planId, Long clientId, 
			String serialNum, String itemCode, Long saleId, Long itemId,String allocationType) {
		this.orderId=orderId;
		this.planCode=planCode;
		this.serialNum=serialNum;
		this.allocationType=allocationType;
		this.id=id;
		this.planId=planId;
		this.clientId=clientId;
		this.provisionNumber=provisionNumber;
		this.itemCode=itemCode;
		this.saleId=saleId;
		this.itemId=itemId;
	}
	
	public AssociationData(Long planId, String planCode, Long id) {
		// TODO Auto-generated constructor stub
		this.planId=planId;
		this.planCode=planCode;
		this.orderId=id;
	}

	public AssociationData(String serialNum, String provisionNumber,String allocationType, String propertyCode,Long orderId) {
	    this.serialNum=serialNum;
	    this.provisionNumber=provisionNumber;
	    this.allocationType=allocationType;
	    this.propertyCode =propertyCode;
	    this.orderId =orderId;
	}

	public void addHardwareDatas(List<AssociationData> hardwareDatas){
		this.hardwareData=hardwareDatas;
	}
	public void addPlanDatas(List<AssociationData> planDatas){
		this.planData=planDatas;
	}
	
	public void addEnumValuesDatas(Collection<EnumValuesData> enumValuesDatas) {

		this.enumValuesDatas = enumValuesDatas;
	}

	public Long getOrderId() {
		return orderId;
	}

	public String getPlanCode() {
		return planCode;
	}

	public String getItemCode() {
		return itemCode;
	}

	public String getSerialNum() {
		return serialNum;
	}

	public Long getId() {
		return id;
	}

	public Long getPlanId() {
		return planId;
	}


	public String getProvisionNumber() {
		return provisionNumber;
	}

	public List<AssociationData> getHardwareData() {
		return hardwareData;
	}

	public List<AssociationData> getPlanData() {
		return planData;
	}

	public Long getClientId() {
		return clientId;
	}

	public String getAllocationType() {
		return allocationType;
	}

	public Long getSaleId() {
		return saleId;
	}

	public Long getItemId() {
		return itemId;
	}

	public String getPropertyCode() {
		return propertyCode;
	}

	public void setPropertyCode(String propertyCode) {
		this.propertyCode = propertyCode;
	}

	
	
	
	

}