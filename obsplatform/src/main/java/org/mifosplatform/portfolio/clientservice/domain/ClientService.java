package org.mifosplatform.portfolio.clientservice.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.domain.AbstractAuditableCustom;
import org.mifosplatform.provisioning.provisioning.domain.ServiceParameters;
import org.mifosplatform.useradministration.domain.AppUser;

@Entity
@Table(name = "b_client_service")
public class ClientService extends AbstractAuditableCustom<AppUser, Long>{

	@Column(name = "client_id")
	private Long clientId;
	
	@Column(name = "Service_id")
	private Long serviceId;
	
	@Column(name = "status")
	private String status;

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "clientService", orphanRemoval = true)
	private List<ServiceParameters> serviceParameters = new ArrayList<ServiceParameters>();
	
	public ClientService(){}

	public ClientService(Long clientId, Long serviceId, String status) {
		this.clientId = clientId;
		this.serviceId = serviceId;
		this.status = status;
	}

	public Long getClientId() {
		return clientId;
	}

	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}

	public Long getServiceId() {
		return serviceId;
	}

	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public static ClientService createNew(JsonCommand command) {
		return null;
	}

	public static ClientService createNew(Long clientId, Long serviceId, String status) {
		return new ClientService(clientId,  serviceId, status);
	}
	

	public void addDetails(ServiceParameters sp) {
		sp.update(this);
		this.serviceParameters.add(sp);

	}

	public List<ServiceParameters> getServiceParameters() {
		return serviceParameters;
	}

	public void setServiceParameters(List<ServiceParameters> serviceParameters) {
		this.serviceParameters = serviceParameters;
	}

	
}