package org.mifosplatform.organisation.hardwareplanmapping.data;

import java.util.List;

import org.mifosplatform.finance.payments.data.McodeData;
import org.mifosplatform.logistics.item.data.ItemData;
import org.mifosplatform.portfolio.plan.data.PlanCodeData;

public class HardwarePlanData {

	private Long id;
	private String planCode;
	private String itemCode;
	private Long provisioningId;
	private String provisioningValue;

	private List<ItemData> itemDatas;
	private List<PlanCodeData> planDatas;
	private List<McodeData> provisioning;

	public HardwarePlanData(final List<ItemData> itemsdata,
			final List<PlanCodeData> plansData,final List<McodeData> provisioning) {

		this.itemDatas = itemsdata;
		this.planDatas = plansData;
		this.provisioning = provisioning;

	}

	public HardwarePlanData(final Long id,final String planCode, final String itemCode,final Long provisioningId,final String provisioningValue) {

		this.id = id;
		this.planCode = planCode;
		this.itemCode = itemCode;
		this.provisioningId = provisioningId;
		this.provisioningValue = provisioningValue;

	}

	public Long getId() {
		return id;
	}

	public String getplanCode() {
		return planCode;
	}

	public String getPlanCode() {
		return planCode;
	}

	public String getItemCode() {
		return itemCode;
	}

	public List<ItemData> getItemDatas() {
		return itemDatas;
	}

	public List<PlanCodeData> getPlanDatas() {
		return planDatas;
	}

	public void addData(List<ItemData> data) {
		this.itemDatas = data;

	}
	

	public void addPlan(List<PlanCodeData> planData) {

		this.planDatas = planData;
	}

	public List<McodeData> getProvisioning() {
		return provisioning;
	}

	public void setProvisioning(List<McodeData> provisioning) {
		this.provisioning = provisioning;
	}

	public Long getProvisioningId() {
		return provisioningId;
	}

	public void setProvisioningId(Long provisioningId) {
		this.provisioningId = provisioningId;
	}

	public String getProvisioningValue() {
		return provisioningValue;
	}

	public void setProvisioningValue(String provisioningValue) {
		this.provisioningValue = provisioningValue;
	}

	public void addProvisioning(List<McodeData> provisioning) {
		this.provisioning = provisioning;
		
	}
	

}
