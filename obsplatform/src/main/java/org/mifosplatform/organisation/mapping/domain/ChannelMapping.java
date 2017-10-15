package org.mifosplatform.organisation.mapping.domain;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name="b_prd_ch_mapping")
public class ChannelMapping extends AbstractPersistable<Long>{
	
	@Column(name="service_id", nullable=false)
	private int serviceId;
	
	@Column(name="channel_id", nullable=false)
	private int channelId;
	
	@Column(name="is_deleted")
	private char isDeleted;

	public ChannelMapping() {}
	
	public ChannelMapping(int serviceId, int channelId) {
		this.serviceId = serviceId;
		this.channelId = channelId;
		this.isDeleted = 'N';
	}
	

	public int getServiceId() {
		return serviceId;
	}

	public void setServiceId(int serviceId) {
		this.serviceId = serviceId;
	}
	

	public int getChannelId() {
		return channelId;
	}

	public void setChannelId(int channelId) {
		this.channelId = channelId;
	}
	

	public static ChannelMapping formJson(JsonCommand command) {
		
		int serviceId = command.integerValueOfParameterNamed("serviceId");
		int channelId = command.integerValueOfParameterNamed("channelId");
		
		return new ChannelMapping(serviceId, channelId);
	}

	public Map<String, Object> update(JsonCommand command) {
		

		
final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(1);
		
		final String serviceIdNamedParamName = "serviceId";
		final String channelIdNamedParamName = "channelId";
		
		
		
		
		if(command.isChangeInIntegerParameterNamed(serviceIdNamedParamName, this.serviceId)){
			final Integer newValue = command.integerValueOfParameterNamed(serviceIdNamedParamName);
			actualChanges.put(serviceIdNamedParamName, newValue);
			this.serviceId = newValue;
		}
		

		if(command.isChangeInIntegerParameterNamed(channelIdNamedParamName, this.channelId)){
			final Integer newValue = command.integerValueOfParameterNamed(channelIdNamedParamName);
			actualChanges.put(channelIdNamedParamName, newValue);
			this.channelId = newValue;
		}
		
		
		
		return actualChanges;
	
	
	
	}
	

}
