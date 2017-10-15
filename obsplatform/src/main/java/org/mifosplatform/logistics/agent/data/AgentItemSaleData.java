package org.mifosplatform.logistics.agent.data;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import org.mifosplatform.billing.chargecode.data.ChargesData;
import org.mifosplatform.logistics.item.data.ItemData;
import org.mifosplatform.organisation.office.data.OfficeData;

public class AgentItemSaleData {

	private final Collection<OfficeData> officeDatas;
	private final List<ItemData> itemDatas;
	private final List<ChargesData> chargeDatas;
	private final Long id;
	private final Long itemId;
	private final Long agentId;
	private final String agentName;
	private final String itemName;
	private final Long orderQunatity;
	private final BigDecimal chargeAmount;
	private final BigDecimal invoiceAmount;
	private final BigDecimal tax;

	public static AgentItemSaleData instance(final AgentItemSaleData itemSaleData,final Collection<OfficeData> officeDatas,
			final List<ItemData> itemDatas, final List<ChargesData> chargesDatas) {

		return new AgentItemSaleData(itemSaleData.id, itemSaleData.itemId,itemSaleData.agentId, itemSaleData.itemName,
				itemSaleData.agentName, itemSaleData.orderQunatity,itemSaleData.chargeAmount, itemSaleData.tax,
				itemSaleData.invoiceAmount, officeDatas, itemDatas,chargesDatas);
	}

	public static AgentItemSaleData withTemplateData(final Collection<OfficeData> officeDatas, final List<ItemData> itemDatas,final List<ChargesData> chargesDatas) {

		return new AgentItemSaleData(null, null, null, null, null, null, null,
				           null, null, officeDatas, itemDatas, chargesDatas);
	}

	public AgentItemSaleData(final Long id, final Long itemId, final Long agentId,
			final String itemName, final String agentName, final Long orderQunatity,
			final BigDecimal chargeAmount, final BigDecimal tax, final BigDecimal invoiceAmount,
			final Collection<OfficeData> officeDatas, final List<ItemData> itemDatas,
			final List<ChargesData> chargesDatas) {

		this.id = id;
		this.itemId = itemId;
		this.itemName = itemName;
		this.agentId = agentId;
		this.agentName = agentName;
		this.orderQunatity = orderQunatity;
		this.chargeAmount = chargeAmount;
		this.invoiceAmount = invoiceAmount;
		this.tax = tax;
		this.officeDatas = officeDatas;
		this.itemDatas = itemDatas;
		this.chargeDatas = chargesDatas;

	}

	public Collection<OfficeData> getOfficeDatas() {
		return officeDatas;
	}

	public List<ItemData> getItemDatas() {
		return itemDatas;
	}

	public Long getId() {
		return id;
	}

	public Long getItemId() {
		return itemId;
	}

	public Long getAgentId() {
		return agentId;
	}

	public String getAgentName() {
		return agentName;
	}

	public String getItemName() {
		return itemName;
	}

	public Long getOrderQunatity() {
		return orderQunatity;
	}

	public BigDecimal getChargeAmount() {
		return chargeAmount;
	}

	public BigDecimal getInvoiceAmount() {
		return invoiceAmount;
	}

	public BigDecimal getTax() {
		return tax;
	}

	public List<ChargesData> getChargeDatas() {
		return chargeDatas;
	}

}
