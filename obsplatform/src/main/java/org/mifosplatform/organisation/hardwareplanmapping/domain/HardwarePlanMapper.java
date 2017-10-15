package org.mifosplatform.organisation.hardwareplanmapping.domain;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.apache.commons.lang.StringUtils;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;

@SuppressWarnings("serial")
@Entity
@Table(name = "b_hw_plan_mapping")
public class HardwarePlanMapper extends AbstractPersistable<Long> {

	@Column(name = "item_code")
	private String itemCode;

	@Column(name = "plan_code")
	private String planCode;
	
	@Column(name = "provisioning_id")
	private Long provisioningId;

	public HardwarePlanMapper() {
		// TODO Auto-generated constructor stub
	}

	public HardwarePlanMapper(final String planCode, final String itemCode,final Long provisioningId) {

		this.itemCode = itemCode;
		this.planCode = planCode;
		this.provisioningId = provisioningId;

	}

	public Map<String, Object> update(final JsonCommand command) {
		
		final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(1);
		
		final String firstnameParamName = "planCode";
		if (command.isChangeInStringParameterNamed(firstnameParamName, this.planCode)) {
			final String newValue = command.stringValueOfParameterNamed(firstnameParamName);
			actualChanges.put(firstnameParamName, newValue);
			this.planCode = StringUtils.defaultIfEmpty(newValue, null);
		}

		final String itemCodeParamName = "itemCode";
		if (command.isChangeInStringParameterNamed(itemCodeParamName, this.itemCode)) {
			final String newValue = command.stringValueOfParameterNamed(itemCodeParamName);
			actualChanges.put(firstnameParamName, newValue);
			this.itemCode = StringUtils.defaultIfEmpty(newValue, null);
		}
		
		final String provisioningIdParamName = "provisioningId";
		if (command.isChangeInLongParameterNamed(provisioningIdParamName, this.provisioningId)) {
			final Long newValue = command.longValueOfParameterNamed(provisioningIdParamName);
			actualChanges.put(firstnameParamName, newValue);
			this.provisioningId = newValue;
		}

		return actualChanges;

	}

	public static HardwarePlanMapper fromJson(final JsonCommand command) {
		
		final String planCode = command.stringValueOfParameterNamed("planCode");
		final String itemCode = command.stringValueOfParameterNamed("itemCode");
		final Long provisioningId = command.longValueOfParameterNamed("provisioningId");
		
		return new HardwarePlanMapper(planCode, itemCode,provisioningId);
	}

	public String getCode() {
		return planCode;
	}

	public String getPlanCode() {
		return planCode;
	}

	public String getItemCode() {
		return itemCode;
	}

}
