package org.mifosplatform.provisioning.provisioning.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.mifosplatform.billing.discountmaster.domain.DiscountDetails;
import org.mifosplatform.infrastructure.core.domain.AbstractAuditableCustom;
import org.mifosplatform.useradministration.domain.AppUser;

@Entity
@Table(name = "b_provisioning_request")
public class ProvisioningRequest extends AbstractAuditableCustom<AppUser, Long>{

	private static final long serialVersionUID = 1L;
	
	@Column(name = "client_id")
	private Long clientId;
	
	@Column(name = "order_id")
	private Long orderId;
	
	@Column(name = "request_type")
	private String requestType;
	
	@Column(name = "provisioning_system")
	private String provisioningSystem;
	
	@Column(name = "status")
	private char status;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "start_date")
	private Date startDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "end_date")
	private Date endDate;
	
	
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "provisioningRequest", orphanRemoval = true)
	private List<ProvisioningRequestDetail> provisioningRequestDetail = new ArrayList<ProvisioningRequestDetail>();
	
	/*@Column(name = "request_message")
	private String requestMessage;
	
	@Column(name = "response_message")
	private String responseMessage;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "response_date")
	private Date responseDate;
	
	@Column(name = "response_status")
	private String responseStatus;*/

	
	
	public ProvisioningRequest() {
	}



	public ProvisioningRequest(Long clientId, Long orderId, String requestType, String provisioningSystem,char status,
			Date startDate, Date endDate) {

		this.clientId = clientId;
		this.orderId = orderId;
		this.requestType = requestType;
		this.provisioningSystem = provisioningSystem;
		this.status = status;
		this.startDate = startDate;
		this.endDate = endDate;
	}



	public Long getClientId() {
		return clientId;
	}



	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}



	public Long getOrderId() {
		return orderId;
	}



	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}



	public String getRequestType() {
		return requestType;
	}



	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}



	public String getProvisioningSystem() {
		return provisioningSystem;
	}



	public void setProvisioningSystem(String provisioningSystem) {
		this.provisioningSystem = provisioningSystem;
	}



	public char getStatus() {
		return status;
	}



	public void setStatus(char status) {
		this.status = status;
	}



	public Date getStartDate() {
		return startDate;
	}



	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}



	public Date getEndDate() {
		return endDate;
	}



	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	
	
	public List<ProvisioningRequestDetail> getProvisioningRequestDetail() {
		return provisioningRequestDetail;
	}



	public void setProvisioningRequestDetail(List<ProvisioningRequestDetail> provisioningRequestDetail) {
		this.provisioningRequestDetail = provisioningRequestDetail;
	}



	public void addDetails(ProvisioningRequestDetail provisioningRequestDetail) {
		provisioningRequestDetail.setProvisioningRequest(this);
		this.provisioningRequestDetail.add(provisioningRequestDetail);

	}
	
}
