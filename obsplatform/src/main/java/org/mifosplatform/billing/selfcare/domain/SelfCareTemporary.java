package org.mifosplatform.billing.selfcare.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.domain.AbstractAuditableCustom;
import org.mifosplatform.useradministration.domain.AppUser;

@Entity
@Table(name="b_client_register")
public class SelfCareTemporary extends AbstractAuditableCustom<AppUser, Long>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name="username")
	private String userName;
	
	@Column(name="generate_key")
	private String generatedKey;
	
	@Column(name="status")
	private String status;
	
	@Column(name="payment_status")
	private String paymentStatus;
	
	@Column(name="payment_data")
	private String paymentData;
	
	
	
	public SelfCareTemporary(){
		
	}
	
	public SelfCareTemporary(String userName, String generatedKey){
		
		this.userName = userName;
		this.generatedKey = generatedKey;
		this.status="INACTIVE";
		
	}
	public static SelfCareTemporary fromJson(JsonCommand command) {
		String userName = command.stringValueOfParameterNamed("userName");
		SelfCareTemporary selfCareTemporary = new SelfCareTemporary();
		selfCareTemporary.setUserName(userName);
		selfCareTemporary.setStatus("INACTIVE");
		selfCareTemporary.paymentStatus = "INACTIVE";
		selfCareTemporary.paymentData = "NULL";
		return selfCareTemporary;
		
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getGeneratedKey() {
		return generatedKey;
	}

	public void setGeneratedKey(String generatedKey) {
		this.generatedKey = generatedKey;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public String getPaymentData() {
		return paymentData;
	}

	public void setPaymentData(String paymentData) {
		this.paymentData = paymentData;
	}

	public void delete() {
         this.generatedKey = "del_"+getId()+"_"+this.getGeneratedKey();
         this.userName = "del_"+getId()+"_"+this.userName;
		
	}
	
	
	

}
