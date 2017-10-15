package org.mifosplatform.logistics.item.data;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.billing.chargecode.data.ChargesData;
import org.mifosplatform.billing.discountmaster.data.DiscountMasterData;
import org.mifosplatform.infrastructure.configuration.data.ConfigurationPropertyData;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.logistics.itemdetails.data.InventoryGrnData;
import org.mifosplatform.logistics.supplier.data.SupplierData;
import org.mifosplatform.organisation.feemaster.data.FeeMasterData;
import org.mifosplatform.organisation.office.data.OfficeData;
import org.mifosplatform.organisation.region.data.RegionData;
import org.mifosplatform.organisation.staff.data.StaffData;

public class ItemData {
	
	private Long id;
	private String itemCode;
	private String units;
	private String chargeCode;
	private BigDecimal unitPrice;
	private BigDecimal totalPrice;
	private List<ItemData> itemDatas;
	private ItemData itemData;
	private String quantity;
	private List<EnumOptionData> itemClassData;
	private List<EnumOptionData> unitData;
	private List<ChargesData> chargesData;
	private String itemDescription;
	private int warranty;
	private String itemClass;
	private List<DiscountMasterData> discountMasterDatas;
	private Long itemMasterId;
	private LocalDate changedDate;
	private List<ItemData> auditDetails;
	private Long usedItems;
	private Long availableItems;
	private Long totalItems;
	private List<RegionData> regionDatas;
	private Long regionId;
	private String price;
	private List<ItemData> itemPricesDatas;
	private Long reorderLevel;
	private List<FeeMasterData> feeMasterData;
	private Collection<InventoryGrnData> grnData;
	private ConfigurationPropertyData configurationData;
	private List<SupplierData> supplierData;
	private String isProvisioning;
	
	private Long supplierId;
	private String supplierCode;
	private String isPairing;
	private Long pairedItemId;
	
	public ItemData(Long id, String itemCode, String itemDesc,String itemClass,String units,   String chargeCode, int warranty, BigDecimal unitPrice,
			Long usedItems,Long availableItems,Long totalItems, Long reorderLevel, Long supplierId,String supplierCode,String isProvisioning) {
		
		this.id=id;
		this.itemCode=itemCode;
		this.units=units;
		this.unitPrice=unitPrice;
		this.chargeCode=chargeCode;
		this.itemDescription=itemDesc;
		this.warranty=warranty;
		this.itemClass=itemClass;
		this.usedItems=usedItems;
		this.availableItems=availableItems;
		this.totalItems=totalItems;
		this.reorderLevel = reorderLevel;
		this.supplierId = supplierId;
		this.supplierCode = supplierCode;
		this.isProvisioning = isProvisioning;
		
	}

	public ItemData(List<ItemData> itemCodeData, ItemData itemData, BigDecimal totalPrice,String quantity, List<DiscountMasterData> discountdata,
			           List<ChargesData> chargesDatas, List<FeeMasterData> feeMasterData) {

		this.itemDatas=itemCodeData;
		this.id=itemData.getId();
		this.itemCode=itemData.getItemCode();
		this.chargeCode=itemData.getChargeCode();
		this.units=itemData.getUnits();
		this.unitPrice=itemData.getUnitPrice();
		this.totalPrice=totalPrice;
		this.quantity=quantity;
		this.chargesData=chargesDatas;
		this.discountMasterDatas=discountdata;
		this.feeMasterData = feeMasterData;
	
	}

	public ItemData(List<EnumOptionData> itemClassdata,List<EnumOptionData> unitTypeData, List<ChargesData> chargeDatas, 
			List<RegionData> regionDatas,ConfigurationPropertyData configurationData, List<SupplierData> supplierData) {
     this.itemClassData=itemClassdata;
     this.unitData=unitTypeData;
     this.chargesData=chargeDatas;
     this.regionDatas = regionDatas;
     this.configurationData = configurationData;
     this.supplierData = supplierData;
	}
	
	public ItemData(Long id, String itemCode, String itemDescription) {
		this.id = id;
		this.itemCode = itemCode;
		this.itemDescription = itemDescription;
	}

	public ItemData(ItemData itemData, List<EnumOptionData> itemClassdata,
			List<EnumOptionData> unitTypeData, List<ChargesData> chargeDatas,List<ItemData> auditDetails) {
		this.id=itemData.getId();
		this.itemCode=itemData.getItemCode();
		this.units=itemData.getUnits();
		this.unitPrice=itemData.getUnitPrice();
		this.chargeCode=itemData.getChargeCode();
		this.itemDescription=itemData.getItemDescription();
		this.warranty=itemData.getWarranty();
		this.itemClass=itemData.getItemClass();
		this.supplierId = itemData.getSupplierId();
		this.supplierCode = itemData.getSupplierCode();
		this.chargesData=chargeDatas;
		this.unitData=unitTypeData;
		this.itemClassData=itemClassdata;
		this.auditDetails=auditDetails;
		this.reorderLevel = itemData.getReorderLevel();
		this.isProvisioning = itemData.getIsProvisioning();
	}

	public ItemData(List<ItemData> itemCodes) {
		this.itemDatas = itemCodes;
	}

	public ItemData(Long id, Long itemMasterId, String itemCode,
			BigDecimal unitPrice, Date changedDate, Long regionId) {
		
		this.id=id;
		this.itemMasterId=itemMasterId;
		this.itemCode=itemCode;
		this.unitPrice=unitPrice;
		this.changedDate=new LocalDate(changedDate);
		this.regionId = regionId;
	}
	
	public ItemData(final Long id, final String itemCode, final String itemDescription, final String chargeCode, 
			        final BigDecimal unitPrice,final String isPairing,final Long pairedItemId) {
		
		this.id=id;
		this.itemCode=itemCode;
		this.itemDescription=itemDescription;
		this.chargeCode = chargeCode;
		this.unitPrice=unitPrice;
		this.isPairing = isPairing;
		this.pairedItemId = pairedItemId;
	}

	public ItemData(Long id, Long itemId, Long regionId, String price) {
		
		this.id = id;
		this.itemMasterId = itemId;
		this.regionId = regionId;
		this.price = price;
		
	}

	public String getChargeCode() {
		return chargeCode;
	}

	public List<ItemData> getItemDatas() {
		return itemDatas;
	}

	public String getItemClass() {
		return itemClass;
	}

	public ItemData getItemData() {
		return itemData;
	}

	public String getQuantity() {
		return quantity;
	}

	public List<EnumOptionData> getItemClassData() {
		return itemClassData;
	}

	public List<EnumOptionData> getUnitData() {
		return unitData;
	}

	public List<ChargesData> getChargesData() {
		return chargesData;
	}

	public String getItemDescription() {
		return itemDescription;
	}

	public int getWarranty() {
		return warranty;
	}

	public Long getId() {
		return id;
	}

	public String getItemCode() {
		return itemCode;
	}

	public String getUnits() {
		return units;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void set(BigDecimal totalPrice) {
	this.totalPrice=totalPrice;
		
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public List<ItemData> getItemCodeData() {
		return 	itemDatas;
	}

	public List<RegionData> getRegionDatas() {
		return regionDatas;
	}

	public void setRegionDatas(List<RegionData> regionDatas) {
		this.regionDatas = regionDatas;
	}

	public List<ItemData> getItemPricesDatas() {
		return itemPricesDatas;
	}

	public void setItemPricesDatas(List<ItemData> itemPricesDatas) {
		this.itemPricesDatas = itemPricesDatas;
	}

	public Long getReorderLevel() {
		return reorderLevel;
	}

	public void setReorderLevel(Long reorderLevel) {
		this.reorderLevel = reorderLevel;
	}

	public void setUnitPrice(BigDecimal itemprice) {
	    this.unitPrice = itemprice;

	}

	public List<FeeMasterData> getFeeMasterData() {
		return feeMasterData;
	}

	public void setFeeMasterData(List<FeeMasterData> feeMasterData) {
		this.feeMasterData = feeMasterData;
	}

	public Collection<InventoryGrnData> getGrnData() {
		return grnData;
	}

	public void setGrnData(Collection<InventoryGrnData> grnData) {
		this.grnData = grnData;
	}

	public Long getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Long supplierId) {
		this.supplierId = supplierId;
	}

	public String getSupplierCode() {
		return supplierCode;
	}

	public void setSupplierCode(String supplierCode) {
		this.supplierCode = supplierCode;
	}

	public ConfigurationPropertyData getConfigurationData() {
		return configurationData;
	}

	public void setConfigurationData(ConfigurationPropertyData configurationData) {
		this.configurationData = configurationData;
	}

	public List<SupplierData> getSupplierData() {
		return supplierData;
	}

	public void setSupplierData(List<SupplierData> supplierData) {
		this.supplierData = supplierData;
	}

	public String getIsProvisioning() {
		return isProvisioning;
	}

	public void setIsProvisioning(String isProvisioning) {
		this.isProvisioning = isProvisioning;
	}

	public String getIsPairing() {
		return isPairing;
	}

	public void setIsPairing(String isPairing) {
		this.isPairing = isPairing;
	}

	public Long getPairedItemId() {
		return pairedItemId;
	}

	public void setPairedItemId(Long pairedItemId) {
		this.pairedItemId = pairedItemId;
	}

	
}
