package org.mifosplatform.portfolio.planmapping.domain;

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
@Table(name = "b_prov_plan_details")
public class PlanMapping extends AbstractPersistable<Long> {

	@Column(name = "plan_id")
	private Long planId;

	@Column(name = "plan_identification", nullable = false, length = 100)
	private String planIdentification;

	@Column(name = "status", nullable = false, length = 100)
	private String status;

	@Column(name = "is_deleted")
	private String isDeleted = "n";
	
	@Column(name="image")
	private String image;

	public PlanMapping() {

	}

	public PlanMapping(final Long planId, final String planIdentification,
			final String status, final String image) {
		this.planId = planId;
		this.planIdentification = planIdentification;
		this.status = status;
		this.image = image;
	}

	public static PlanMapping fromJson(JsonCommand command) {
		
		final Long planId = command.longValueOfParameterNamed("planId");
		final String planIdentification = command.stringValueOfParameterNamed("planIdentification");
		final String status = command.stringValueOfParameterNamed("status");
		final String image = command.stringValueOfParameterNamed("image");
		
		return new PlanMapping(planId, planIdentification, status,image);
	}

	public Long getPlanId() {
		return planId;
	}

	public String getPlanIdentification() {
		return planIdentification;
	}

	public String getStatus() {
		return status;
	}

	public String getIsDeleted() {
		return isDeleted;
	}

	public Map<String, Object> update(JsonCommand command) {

		final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(1);
		final String planParamName = "planId";
		if (command.isChangeInLongParameterNamed(planParamName,this.planId)) {
			final Long newValue = command.longValueOfParameterNamed(planParamName);
			actualChanges.put(planParamName, newValue);
			this.planId = newValue;
		}

		final String planIdentificationParamName = "planIdentification";
		if (command.isChangeInStringParameterNamed(planIdentificationParamName, this.planIdentification)) {
			final String newValue = command.stringValueOfParameterNamed(planIdentificationParamName);
			actualChanges.put(planIdentificationParamName, newValue);
			this.planIdentification = StringUtils.defaultIfEmpty(newValue,null);
		}

		final String statusParamName = "status";
		if (command.isChangeInStringParameterNamed(statusParamName, this.status)) {
			final String newValue = command.stringValueOfParameterNamed(statusParamName);
			actualChanges.put(statusParamName, newValue);
			this.status = StringUtils.defaultIfEmpty(newValue, null);
		}
		
		final String image = "image";
		if (command.isChangeInStringParameterNamed(image, this.image)) {
			final String newValue = command.stringValueOfParameterNamed(image);
			actualChanges.put(image, newValue);
			this.image = StringUtils.defaultIfEmpty(newValue,null);
		}

		return actualChanges;

	}

}

