package org.mifosplatform.portfolio.clientservice.data;

import java.util.List;

import org.mifosplatform.portfolio.plan.data.ServiceData;
import org.mifosplatform.provisioning.provisioning.data.ServiceParameterData;

public class ClientServiceData {
	
	private Long id;
	private Long serviceId;
	private String serviceCode;
	private String serviceDescription;
	private String status;
	
	private List<ServiceData> serviceData;
	List<ServiceParameterData> ServiceParameterData;
	
	public ClientServiceData() {
	}


	public ClientServiceData(Long id, Long serviceId, String serviceCode, String serviceDescription, String status) {
		this.id = id;
		this.serviceId = serviceId;
		this.serviceCode = serviceCode;
		this.serviceDescription = serviceDescription;
		this.status = status;
	}


	public ClientServiceData(final List<org.mifosplatform.provisioning.provisioning.data.ServiceParameterData> serviceParameterData) {
		ServiceParameterData = serviceParameterData;
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public Long getServiceId() {
		return serviceId;
	}


	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}


	public String getServiceCode() {
		return serviceCode;
	}


	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}


	public String getServiceDescription() {
		return serviceDescription;
	}


	public void setServiceDescription(String serviceDescription) {
		this.serviceDescription = serviceDescription;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public List<ServiceData> getServiceData() {
		return serviceData;
	}


	public void setServiceData(List<ServiceData> serviceData) {
		this.serviceData = serviceData;
	}


	public List<ServiceParameterData> getServiceParameterData() {
		return ServiceParameterData;
	}


	public void setServiceParameterData(List<ServiceParameterData> serviceParameterData) {
		ServiceParameterData = serviceParameterData;
	}
}
