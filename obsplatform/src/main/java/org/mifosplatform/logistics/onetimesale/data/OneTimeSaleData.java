package org.mifosplatform.logistics.onetimesale.data;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.billing.chargecode.data.ChargesData;
import org.mifosplatform.billing.discountmaster.data.DiscountMasterData;
import org.mifosplatform.cms.eventorder.data.EventOrderData;
import org.mifosplatform.logistics.item.data.ItemData;
import org.mifosplatform.organisation.office.data.OfficeData;
import org.mifosplatform.portfolio.clientservice.data.ClientServiceData;
import org.mifosplatform.portfolio.contract.data.SubscriptionData;
import org.mifosplatform.portfolio.plan.data.ServiceData;

public class OneTimeSaleData {
	
	private List<ChargesData> chargesDatas;
	private List<ItemData> itemDatas;
	private Long itemId;
	private Long id;
	private String units;
	private String itemCode;
	private String chargeCode;
	private String quantity;
	private BigDecimal unitPrice;
	private BigDecimal totalPrice;
	private LocalDate saleDate;
	private Long clientId;
	private String isInvoiced;
	private OneTimeSaleData salesData;
	private boolean flag = false;
	private String hardwareAllocated;
	private String itemClass;
	private List<AllocationDetailsData> allocationData;
	private List<DiscountMasterData> discountMasterDatas;
	private Long discountId;
	private List<OneTimeSaleData> oneTimeSaleData;
	private List<EventOrderData> eventOrdersData;
	private String serialNo;
	private Collection<OfficeData> officesData;
	private Collection<SubscriptionData> contractPeriods;
	private Integer taxInclusive;
	private String chargeType;
	private String saleType;
	private LocalDate warrantyDate;
	private String propertyCode;
	private Long itemDetailId;
	private String provserialnumber;
	private String quality;
	private String pairedItemCode;
	private String serviceCode;
	private String serviceDescription;
	private List<ClientServiceData> clientServiceData;
	private Long clientServiceId;
	
	public OneTimeSaleData(final List<ItemData> itemData, final List<DiscountMasterData> discountData,
			final Collection<OfficeData> officesData, final Collection<SubscriptionData> contractPeriods, final List<ClientServiceData> clientServiceData) {
		
		this.itemDatas=itemData;
		this.discountMasterDatas=discountData;
		this.officesData=officesData;
		this.contractPeriods=contractPeriods;
		this.clientServiceData = clientServiceData;
	}

	/*hardware allocated and flag is added by rahman */

	public OneTimeSaleData(final Long id,final LocalDate saleDate,final String itemCode,
			final String chargeCode,final String quantity,final BigDecimal totalPrice,final String hardwareAllocated,final String itemClass,final String serialNo,
			final String units,final String saleType,final LocalDate warrantyDate,final String propertyCode,final Long itemDetailId,final String provserialnumber,
			final String quality,final String serviceCode,final String serviceDescription,final String pairedItemCode) {
		
		this.id=id;
		this.saleDate=saleDate;
		this.itemCode=itemCode;
		this.chargeCode=chargeCode;
		this.quantity=quantity;
		this.totalPrice=totalPrice;
		this.hardwareAllocated = hardwareAllocated;
		this.flag = hardwareAllocated.equalsIgnoreCase("ALLOCATED")?true:false;
		this.itemClass = itemClass;
		this.serialNo=serialNo;
		this.units = units;
		this.saleType = saleType;
		this.warrantyDate = warrantyDate;
		this.propertyCode = propertyCode;
		this.itemDetailId = itemDetailId;
		this.provserialnumber = provserialnumber;
		this.quality = quality;
		this.serviceCode = serviceCode;
		this.serviceDescription = serviceDescription;
		this.pairedItemCode = pairedItemCode;
	}


	public OneTimeSaleData(final Long oneTimeSaleId,final Long clientId,final String units,final String chargeCode,
			final String chargeType,final BigDecimal unitPrice,final String quantity,final BigDecimal totalPrice, 
			final String isInvoiced,final Long itemId,final Long discountId,final Integer taxInclusive) {
		
		this.id = oneTimeSaleId;
		this.clientId = clientId;
		this.units=units;
		this.chargeCode = chargeCode;
		this.chargeType = chargeType;
		this.unitPrice= unitPrice;
		this.quantity = quantity;
		this.totalPrice = totalPrice;
		this.isInvoiced = isInvoiced;
		this.itemId = itemId;
		this.discountId =discountId;
		this.taxInclusive = taxInclusive;
		
	}


	public OneTimeSaleData() {
		
	}

	public OneTimeSaleData(final List<OneTimeSaleData> salesData,
			          final List<EventOrderData> eventOrderDatas) {
             
		this.oneTimeSaleData=salesData;
		this.eventOrdersData=eventOrderDatas;
	
	}

	/**
	 * @return the chargesDatas
	 */
	public List<ChargesData> getChargesDatas() {
		return chargesDatas;
	}

	public void setChargesDatas(List<ChargesData> chargesDatas) {
		this.chargesDatas = chargesDatas;
	}

	/**
	 * @return the itemDatas
	 */
	public List<ItemData> getItemDatas() {
		return itemDatas;
	}

	public void setItemDatas(List<ItemData> itemDatas) {
		this.itemDatas = itemDatas;
	}

	/**
	 * @return the itemId
	 */
	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the itemCode
	 */
	public String getItemCode() {
		return itemCode;
	}

	/**
	 * @param itemCode the itemCode to set
	 */
	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	/**
	 * @return the chargeCode
	 */
	public String getChargeCode() {
		return chargeCode;
	}

	/**
	 * @param chargeCode the chargeCode to set
	 */
	public void setChargeCode(String chargeCode) {
		this.chargeCode = chargeCode;
	}

	/**
	 * @return the quantity
	 */
	public String getQuantity() {
		return quantity;
	}


	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	/**
	 * @return the totalPrice
	 */
	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	
	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}

	/**
	 * @return the saleDate
	 */
	public LocalDate getSaleDate() {
		return saleDate;
	}

	public void setSaleDate(LocalDate saleDate) {
		this.saleDate = saleDate;
	}

	/**
	 * @return the clientId
	 */
	public Long getClientId() {
		return clientId;
	}

	
	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}

	/**
	 * @return the isInvoiced
	 */
	public String getIsInvoiced() {
		return isInvoiced;
	}

	public void setIsInvoiced(String isInvoiced) {
		this.isInvoiced = isInvoiced;
	}

	/**
	 * @return the salesData
	 */
	public OneTimeSaleData getSalesData() {
		return salesData;
	}

	public void setSalesData(OneTimeSaleData salesData) {
		this.salesData = salesData;
	}

	/**
	 * @return the hardwareAllocated
	 */
	public String getHardwareAllocated() {
		return hardwareAllocated;
	}

	public void setHardwareAllocated(String hardwareAllocated) {
		this.hardwareAllocated = hardwareAllocated;
	}

	/**
	 * @return the itemClass
	 */
	public String getItemClass() {
		return itemClass;
	}

	
	public void setItemClass(String itemClass) {
		this.itemClass = itemClass;
	}

	/**
	 * @return the allocationData
	 */
	public List<AllocationDetailsData> getAllocationData() {
		return allocationData;
	}

	public void setAllocationData(List<AllocationDetailsData> allocationData) {
		this.allocationData = allocationData;
	}

	/**
	 * @return the discountMasterDatas
	 */
	public List<DiscountMasterData> getDiscountMasterDatas() {
		return discountMasterDatas;
	}

	public void setDiscountMasterDatas(List<DiscountMasterData> discountMasterDatas) {
		this.discountMasterDatas = discountMasterDatas;
	}

	/**
	 * @return the discountId
	 */
	public Long getDiscountId() {
		return discountId;
	}

	/**
	 * @param discountId the discountId to set
	 */
	public void setDiscountId(Long discountId) {
		this.discountId = discountId;
	}

	/**
	 * @return the oneTimeSaleData
	 */
	public List<OneTimeSaleData> getOneTimeSaleData() {
		return oneTimeSaleData;
	}

	public void setOneTimeSaleData(List<OneTimeSaleData> oneTimeSaleData) {
		this.oneTimeSaleData = oneTimeSaleData;
	}

	/**
	 * @return the eventOrdersData
	 */
	public List<EventOrderData> getEventOrdersData() {
		return eventOrdersData;
	}


	public void setEventOrdersData(List<EventOrderData> eventOrdersData) {
		this.eventOrdersData = eventOrdersData;
	}

	/**
	 * @return the serialNo
	 */
	public String getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}

	/**
	 * @return the officesData
	 */
	public Collection<OfficeData> getOfficesData() {
		return officesData;
	}
	
	public void setOfficesData(Collection<OfficeData> officesData) {
		this.officesData = officesData;
	}

	/**
	 * @return the contractPeriods
	 */
	public Collection<SubscriptionData> getContractPeriods() {
		return contractPeriods;
	}

	public void setContractPeriods(Collection<SubscriptionData> contractPeriods) {
		this.contractPeriods = contractPeriods;
	}

	/**
	 * @return the taxInclusive
	 */
	public Integer getTaxInclusive() {
		return taxInclusive;
	}

	public void setTaxInclusive(Integer taxInclusive) {
		this.taxInclusive = taxInclusive;
	}

	/**
	 * @return the chargeType
	 */
	public String getChargeType() {
		return chargeType;
	}

	public void setChargeType(String chargeType) {
		this.chargeType = chargeType;
	}

	/**
	 * @return the units
	 */
	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	/**
	 * @return the unitPrice
	 */
	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	public String getPairedItemCode() {
		return pairedItemCode;
	}

	public void setPairedItemCode(String pairedItemCode) {
		this.pairedItemCode = pairedItemCode;
	}
	
	
	public List<ClientServiceData> getClientServiceData() {
		return clientServiceData;
	}

	public void setClientServiceData(List<ClientServiceData> clientServiceData) {
		this.clientServiceData = clientServiceData;
	}

	public String getProvserialnumber() {
		return provserialnumber;
	}

	public void setProvserialnumber(String provserialnumber) {
		this.provserialnumber = provserialnumber;
	}

	public Long getClientServiceId() {
		return clientServiceId;
	}

	public void setClientServiceId(Long clientServiceId) {
		this.clientServiceId = clientServiceId;
	}
	
	
}
