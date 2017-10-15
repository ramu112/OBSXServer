package org.mifosplatform.billing.planprice.data;
import java.math.BigDecimal;
import java.util.List;



import org.mifosplatform.billing.discountmaster.data.DiscountMasterData;
import org.mifosplatform.billing.chargecode.data.ChargeCodeData;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.organisation.priceregion.data.PriceRegionData;
import org.mifosplatform.portfolio.contract.data.SubscriptionData;
import org.mifosplatform.portfolio.plan.data.ServiceData;


public class PricingData {

	private List<ServiceData> serviceData,pricingData;
	private List<ChargeCodeData> chargeData;
	private List<EnumOptionData> chargevariant;
	private List<DiscountMasterData> discountdata;
	private String planCode;
	private Long planId;
	private Long serviceId;
	private Long chargeId;
	private BigDecimal price;
	private Long discountId;
	private int chargeVariantId;
	private Long id;
	private List<PriceRegionData> priceRegionData;
	private Long priceregion;
	private Long priceId;
	private String serviceCode;
	private String chargeCode;
	private Long contractId;
	private String contractPeriod;
	private List<SubscriptionData> contractPeriods;
	private String isPrepaid;

	public PricingData(final List<ServiceData> serviceData,	final List<ChargeCodeData> chargeData,
	final List<EnumOptionData> chargevariant,final List<DiscountMasterData> data,final String planCode,final Long planId,final PricingData pricingData, 
	final List<PriceRegionData> priceRegionData,final List<SubscriptionData> contractPeriods,final String isPrepaid)
	{

		if(pricingData!= null)
		{
		this.chargeId=pricingData.getChargeId();
		this.serviceId=pricingData.getServiceId();
		this.price=pricingData.getPrice();
		this.discountId=pricingData.getDiscountId();
		this.chargeVariantId=pricingData.getChargeVariantId();
		this.priceregion=pricingData.getPriceregion();
		this.planCode=pricingData.getPlanCode();
		this.priceId=pricingData.getPriceId();
		this.serviceCode=pricingData.getServiceCode();
		this.chargeCode=pricingData.getChargeCode();
		this.contractPeriod=pricingData.getContractPeriod();
		
		}
		this.chargeData=chargeData;
		this.serviceData=serviceData;
		this.chargevariant=chargevariant;
		this.discountdata=data;
		if(planCode!=null){
		this.planCode=planCode;
		}
		this.planId=planId;
		this.isPrepaid=isPrepaid;
		this.priceRegionData=priceRegionData;
		this.contractPeriods=contractPeriods;

	}

	public PricingData(final List<ServiceData> serviceData) {
		this.chargeData=null;
		this.pricingData=serviceData;
		this.chargevariant=null;
		this.discountdata=null;
		this.planCode=null;
	}

	public PricingData(final Long id,final String serviceCode,final String chargeCode,final BigDecimal price,final Long discountId,final int chargeVariantId, 
			final Long priceregion,final String planCode,final Long priceId,final String contractperiod) {
	    this.serviceData=null;
		this.chargeData=null;
		this.chargevariant=null;
		this.discountdata=null;
		this.planId=id;
		this.serviceCode=serviceCode;
		this.chargeCode=chargeCode;
		this.price=price;
		this.contractPeriod=contractperiod;
		this.chargeVariantId=chargeVariantId;
		this.discountId=discountId;
		this.priceregion=priceregion;
		this.planCode=planCode;
		this.priceId=priceId;

	}

	

	public PricingData(Long planId, String planCode, String isPrepaid,List<ServiceData> pricingData) {
		this.planId=planId;
		this.planCode=planCode;
		this.isPrepaid=isPrepaid;
		this.pricingData=pricingData;
	}

	public List<ServiceData> getServiceData() {
		return serviceData;
	}
	public Long getContractId() {
		return contractId;
	}

	public String getContractPeriod() {
		return contractPeriod;
	}

	public List<SubscriptionData> getContractPeriods() {
		return contractPeriods;
	}

	public String getIsPrepaid() {
		return isPrepaid;
	}

	public String getServiceCode() {
		return serviceCode;
	}

	public String getChargeCode() {
		return chargeCode;
	}

	public Long getPriceId() {
		return priceId;
	}

	public List<ChargeCodeData> getChargeData() {
		return chargeData;
	}

	public List<EnumOptionData> getChargevariant() {
		return chargevariant;
	}

	public List<DiscountMasterData> getDiscountdata() {
		return discountdata;
	}

	public Long getPlanId() {
		return planId;
	}

	public String getPlanCode() {
		return planCode;
	}

	public Long getServiceId() {
		return serviceId;
	}

	public Long getChargeId() {
		return chargeId;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public Long getDiscountId() {
		return discountId;
	}

	public int getChargeVariantId() {
		return chargeVariantId;
	}

	public Long getId() {
		return id;
	}

	public List<PriceRegionData> getPriceRegionData() {
		return priceRegionData;
	}

	public Long getPriceregion() {
		return priceregion;
	}



}
