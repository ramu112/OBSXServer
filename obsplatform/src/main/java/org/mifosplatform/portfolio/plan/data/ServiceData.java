package org.mifosplatform.portfolio.plan.data;

import java.math.BigDecimal;

public class ServiceData {

	private final Long id;
	private final String serviceCode;
	private final String planDescription;
	private final String planCode;
	private final Long discountId;
	private final BigDecimal price;
	private final String chargeCode;
	private final String chargeVariant;
	private final Long planId;
	private final String serviceDescription;
	private final String priceregion;
	private final Long contractId;
	private final String duration;
	private final String billingFrequency;
	private final String isPrepaid;
	private final String serviceType;
	private final String chargeDescription;
	private String image;

	public ServiceData(final Long id, final String planCode,final  String serviceCode,final String planDescription,final  String chargeCode,
			final String chargingVariant,final BigDecimal price,final String priceregion,final Long contractId,final String duration,
			final String billingFrequency, final Long discountId, String isPrepaid) {

		this.id = id;
		this.serviceCode = serviceCode;
		this.planDescription = planDescription;
		this.planCode = planCode;
		this.chargeCode = chargeCode;
		this.chargeVariant = chargingVariant;
		this.price = price;
		this.priceregion=priceregion;
		this.contractId=contractId;
		this.duration=duration;
		this.billingFrequency=billingFrequency;
		this.planId=null;
		this.discountId = discountId;
		this.serviceDescription=null;
		this.serviceType=null;
		this.isPrepaid=isPrepaid;
		this.chargeDescription=null;
		
		

	}

	public ServiceData(final Long id,final Long planId,final String planCode,final String chargeCode,final  String serviceCode,
			final String serviceDescription,final String chargeDescription, final String priceRegion,final String serviceType,final String isPrepaid) {
		
		this.id = id;
		this.planId = planId;
		this.discountId = null;
		this.serviceCode = serviceCode;
		this.planDescription = null;
		this.planCode = planCode;
		this.chargeCode = chargeCode;
		this.chargeDescription=chargeDescription;
		this.chargeVariant = null;
		this.price = null;
		this.serviceDescription = serviceDescription;
		this.priceregion=priceRegion;
		this.serviceType=serviceType;
		this.isPrepaid=isPrepaid;
		this.contractId=null;
		this.duration=null;
		this.billingFrequency=null;

	}
	
	public ServiceData(final Long id,final  String serviceCode,final String serviceDescription,final String image) {
		
		this.id = id;
		this.serviceCode = serviceCode;
		this.serviceDescription = serviceDescription;
		this.image=image;
		this.planId = null;
		this.discountId = null;
		this.planDescription = null;
		this.planCode = null;
		this.chargeCode = null;
		this.chargeDescription=null;
		this.chargeVariant = null;
		this.price = null;
		this.priceregion=null;
		this.serviceType=null;
		this.isPrepaid=null;
		this.contractId=null;
		this.duration=null;
		this.billingFrequency=null;

	}

	public Long getId() {
		return id;
	}

	public String getServiceCode() {
		return serviceCode;
	}

	public String getServiceDescription() {
		return planDescription;
	}

	public Long getDiscountId() {
		return discountId;
	}

	public String getPlanCode() {
		return planCode;
	}
	
	public String getPriceregion() {
		return priceregion;
	}

	
	public Long getPlanId() {
		return planId;
	}

	public String getPlanDescription() {
		return planDescription;
	}


	public BigDecimal getPrice() {
		return price;
	}

	public String getChargeCode() {
		return chargeCode;
	}

	public String getChargeVariant() {
		return chargeVariant;
	}

	

	public Long getContractId() {
		return contractId;
	}

	public String getDuration() {
		return duration;
	}

	public String getBillingFrequency() {
		return billingFrequency;
	}

	public String getIsPrepaid() {
		return isPrepaid;
	}

	public String getChargeDescription() {
		return chargeDescription;
	}

	public String getServiceType() {
		return serviceType;
	}

	



}
