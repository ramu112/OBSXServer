package org.mifosplatform.logistics.item.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.mifosplatform.cms.mediadetails.domain.MediaassetLocation;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.logistics.item.exception.ItemNotFoundException;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "b_item_master", uniqueConstraints = { @UniqueConstraint(columnNames = { "item_code" }, name = "item_code") })
public class ItemMaster extends AbstractPersistable<Long>{


	@Column(name = "item_code")
	private String itemCode;

	@Column(name = "unit_price")
	private BigDecimal unitPrice;
	
	@Column(name = "item_description")
	private String itemDescription;

	@Column(name = "item_class")
	private String itemClass;
	
	@Column(name = "units")
	private String units;
	
	@Column(name = "charge_code")
	private String chargeCode;

	
	@Column(name = "warranty")
	private Long warranty;
	
	@Column(name="reorder_level")
	private Long reorderLevel;
	
	@Column(name="supplier_id", nullable = true)
	private Long supplierId;
	
	@Column(name = "is_provisioning", nullable = false, length = 100)
	private char isProvisioning;
	
	@Column(name = "is_deleted", nullable = false)
	private char deleted = 'n';
	
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "itemMaster", orphanRemoval = true)
	private List<ItemPrice> itemPrices = new ArrayList<ItemPrice>();
	
	public ItemMaster(){}
	
	public ItemMaster(String itemCode, String itemDescription,
			String itemClass, BigDecimal unitPrice, String units,
			Long warranty, String chargeCode,Long reorderLevel, Long supplierId, boolean isProvisioning) {
             this.itemCode=itemCode;
             this.itemDescription=itemDescription;
             this.itemClass=itemClass;
             this.chargeCode=chargeCode;
             this.units=units;
             this.warranty=warranty;
             this.unitPrice=unitPrice;
             this.reorderLevel=reorderLevel; 
             this.supplierId = supplierId;
             this.isProvisioning = isProvisioning?'Y':'N';

	}

	public String getItemCode() {
		return itemCode;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public String getItemDescription() {
		return itemDescription;
	}

	public String getItemClass() {
		return itemClass;
	}

	public String getUnits() {
		return units;
	}

	public String getChargeCode() {
		return chargeCode;
	}

	
	public Long getWarranty() {
		return warranty;
	}

	public char getDeleted() {
		return deleted;
	}
	public Long getReorderLevel() {
		return reorderLevel;
	}

	public void setReorderLevel(long reorderLevel) {
		this.reorderLevel = reorderLevel;
	}

	public Map<String, Object> update(JsonCommand command){
		if("Y".equals(deleted)){
			throw new ItemNotFoundException(command.entityId().toString());
		}
		
		final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(1);
		
		final String itemCodeParamName = "itemCode";
		if(command.isChangeInStringParameterNamed(itemCodeParamName, this.itemCode)){
			final String newValue = command.stringValueOfParameterNamed(itemCodeParamName);
			actualChanges.put(itemCodeParamName, newValue);
			this.itemCode = StringUtils.defaultIfEmpty(newValue,null);
		}
		final String itemDescriptionParamName = "itemDescription";
		if(command.isChangeInStringParameterNamed(itemDescriptionParamName, this.itemDescription)){
			final String newValue = command.stringValueOfParameterNamed(itemDescriptionParamName);
			actualChanges.put(itemDescriptionParamName, newValue);
			this.itemDescription = StringUtils.defaultIfEmpty(newValue, null);
		}
		
		final String itemClassParamName = "itemClass";
		if(command.isChangeInStringParameterNamed(itemClassParamName,this.itemClass)){
			final String newValue = command.stringValueOfParameterNamed(itemClassParamName);
			actualChanges.put(itemClassParamName, newValue);
			this.itemClass =StringUtils.defaultIfEmpty(newValue,null);
		}
		
		final String chargeCodeParamName = "chargeCode";
		if(command.isChangeInStringParameterNamed(chargeCodeParamName,this.chargeCode)){
			final String newValue = command.stringValueOfParameterNamed(chargeCodeParamName);
			actualChanges.put(chargeCodeParamName, newValue);
			this.chargeCode = StringUtils.defaultIfEmpty(newValue,null);
		}
		
		final String unitsParamName = "units";
		if(command.isChangeInStringParameterNamed(unitsParamName,this.units)){
			final String newValue = command.stringValueOfParameterNamed(unitsParamName);
			actualChanges.put(unitsParamName, newValue);
			this.units = StringUtils.defaultIfEmpty(newValue,null); 
		}
		
		final String warrantyParamName = "warranty";
		if(command.isChangeInLongParameterNamed(warrantyParamName, this.warranty)){
			final Long newValue = command.longValueOfParameterNamed(warrantyParamName);
			actualChanges.put(warrantyParamName, newValue);
			this.warranty = newValue;
		}
		
		final String unitPriceParamName = "unitPrice";
		if(command.isChangeInBigDecimalParameterNamed(unitPriceParamName, this.unitPrice)){
			final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(unitPriceParamName);
			actualChanges.put(unitPriceParamName,newValue);
			this.unitPrice = newValue;
		}
		
		final String supplierIdParamName = "supplierId";
		if(command.isChangeInLongParameterNamed(supplierIdParamName, this.supplierId)){
			final Long newValue = command.longValueOfParameterNamed(supplierIdParamName);
			actualChanges.put(supplierIdParamName,newValue);
			this.supplierId = newValue;
		}
		
		final boolean isProvisioning = command.booleanPrimitiveValueOfParameterNamed("isProvisioning");
        this.isProvisioning = isProvisioning?'Y':'N';
		
		return actualChanges;
	
	}
	
	

	public void delete() {
		this.deleted='Y';
		
	}

	public static ItemMaster fromJson(JsonCommand command) {
		final String itemCode=command.stringValueOfParameterNamed("itemCode");
		final String itemDescription=command.stringValueOfParameterNamed("itemDescription");
		final String itemClass=command.stringValueOfParameterNamed("itemClass");
		final BigDecimal unitPrice=command.bigDecimalValueOfParameterNamed("unitPrice");
		final String units=command.stringValueOfParameterNamed("units");
		final Long warranty=command.longValueOfParameterNamed("warranty");
		final String chargeCode=command.stringValueOfParameterNamed("chargeCode");
		final Long reorderLevel=command.longValueOfParameterNamed("reorderLevel");
		final Long supplierId=command.longValueOfParameterNamed("supplierId");
		final boolean isProvisioning = command.booleanPrimitiveValueOfParameterNamed("isProvisioning");
		
		return new ItemMaster(itemCode, itemDescription, itemClass, unitPrice, units, warranty, chargeCode, reorderLevel,supplierId,isProvisioning);
	}
	
	public void addItemPrices(final ItemPrice itemPrice) {
		itemPrice.update(this);
        this.itemPrices.add(itemPrice);
	}

	public List<ItemPrice> getItemPrices() {
		return itemPrices;
	}

	public void setItemPrices(List<ItemPrice> itemPrices) {
		this.itemPrices = itemPrices;
	}
	
	

}
