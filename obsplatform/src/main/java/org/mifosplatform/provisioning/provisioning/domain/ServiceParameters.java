package org.mifosplatform.provisioning.provisioning.domain;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import net.sf.json.JSONArray;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.domain.AbstractAuditableCustom;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.portfolio.clientservice.domain.ClientService;
import org.mifosplatform.useradministration.domain.AppUser;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;


@SuppressWarnings("serial")
@Entity
@Table(name="b_service_parameters")
public class ServiceParameters extends AbstractAuditableCustom<AppUser,Long>{
	
	
	@Column(name = "client_id")
	private Long clientId;

	@Column(name = "order_id")
	private Long orderId;

	@Column(name = "plan_name")
	private String planName;

	@Column(name = "parameter_name")
	private String parameterName;

	@Column(name = "parameter_value")
	private String parameterValue;
	
	@Column(name = "status")
	private String status;
	
	@ManyToOne
	@JoinColumn(name = "clientservice_id")
	private ClientService clientService;
	
	public ServiceParameters(){
		
	}
	
	public static ClientService createNew(Long clientId, Long serviceId, String status) {
		return new ClientService(clientId,  serviceId, status);
	}
	
	public void update(ClientService clientService) {
		this.clientService = clientService;
	}
	
	public ServiceParameters(Long clientId, String parameterName, String parameterValue, String status) {
		this.clientId = clientId;
		this.parameterName = parameterName;
		this.parameterValue = parameterValue;
		this.status = status;
	}


	public ServiceParameters(final Long clientId, final Long orderId, 
			final String planName, final String paramName,
			final String paramValue, final String status) {
	
		       this.clientId=clientId;
		       this.orderId=orderId;
		       this.planName=planName;
		       this.parameterName=paramName;
		       this.parameterValue=paramValue;
		       this.status=status;
	}


	public static ServiceParameters fromJson(final JsonElement j, final FromJsonHelper fromJsonHelper,
			final Long clientId, final Long orderId, final String planName,
			final String status, final String ipRange, final Long subnet) {

		 final String paramName = fromJsonHelper.extractStringNamed("paramName",j);
		 String service;
		 if(paramName.equalsIgnoreCase("IP_ADDRESS")){
			  if(ipRange.equalsIgnoreCase("subnet")){
				  service= fromJsonHelper.extractStringNamed("paramValue",j);
				  
				  service=service+"/"+subnet;
			  }else{
				  
			String[] ipaddresses = fromJsonHelper.extractArrayNamed("paramValue",j);
			JSONArray array=new JSONArray();
			for(String ipAddress:ipaddresses){
				array.add(ipAddress);
			}
			
			service=array.toString();
			  }
		 }else{
		  service= fromJsonHelper.extractStringNamed("paramValue",j);
		 }
		 
		 return new ServiceParameters(clientId,orderId,planName,paramName,service,status);
	
	}


	public Long getClientId() {
		return clientId;
	}


	public Long getOrderId() {
		return orderId;
	}


	public String getPlanName() {
		return planName;
	}


	public String getParameterName() {
		return parameterName;
	}


	public String getParameterValue() {
		return parameterValue;
	}


	public String getStatus() {
		return status;
	}


	
	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}


	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}


	public void setPlanName(String planName) {
		this.planName = planName;
	}


	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}


	public void setParameterValue(String parameterValue) {
		this.parameterValue = parameterValue;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public Map<String, Object> updateServiceParam(final JsonArray serviceParameters,
			final FromJsonHelper fromApiJsonHelper, final JsonCommand command) {

		final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(
				1);

		for (JsonElement element : serviceParameters) {
			String paramName = fromApiJsonHelper.extractStringNamed(
					"paramName", element);
			if (this.parameterName.equalsIgnoreCase(paramName)) {

				String service;
				if (paramName.equalsIgnoreCase("IP_ADDRESS")) {
					String[] ipaddresses = fromApiJsonHelper.extractArrayNamed(
							"paramValue", element);
					JSONArray array = new JSONArray();
					for (String ipAddress : ipaddresses) {
						array.add(ipAddress);
					}
					service = array.toString();
				} else
					service = fromApiJsonHelper.extractStringNamed(
							"paramValue", element);

				if (!service.equalsIgnoreCase(this.parameterValue)) {

					this.parameterValue = service;
					actualChanges.put(parameterName, service);
					return actualChanges;

				}

			}
		}
		return actualChanges;

	}
}
