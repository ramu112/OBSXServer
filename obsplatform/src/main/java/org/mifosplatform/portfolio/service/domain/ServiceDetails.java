package org.mifosplatform.portfolio.service.domain;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "b_service_detail")
public class ServiceDetails extends AbstractPersistable<Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "param_name")
	private String paramName;
	
	@Column(name = "param_type")
	private String paramType;
	
	@Column(name = "param_value")
	private String paramValue;

	@Column(name = "is_deleted")
	private String isDeleted = "N";

	@ManyToOne
	@JoinColumn(name = "service_id")
	private ServiceMaster serviceMaster;

	public ServiceDetails() {
	}

	public ServiceDetails(final String paramName,final String paramType,final String paramValue) {

		this.paramName = paramName;
		this.paramType = paramType;
		this.paramValue = paramValue;

	}


	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public String getParamType() {
		return paramType;
	}

	public void setParamType(String paramType) {
		this.paramType = paramType;
	}

	public String getParamValue() {
		return paramValue;
	}

	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}

	public ServiceMaster getServiceMaster() {
		return serviceMaster;
	}

	public void setServiceMaster(ServiceMaster serviceMaster) {
		this.serviceMaster = serviceMaster;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void update(ServiceMaster serviceMaster) {
		this.serviceMaster = serviceMaster;
	}

	public void delete() {

		this.isDeleted = "Y";

	}

	public String getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(String isDeleted) {
		this.isDeleted = isDeleted;
	}

}