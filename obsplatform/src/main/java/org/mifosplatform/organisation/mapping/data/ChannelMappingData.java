package org.mifosplatform.organisation.mapping.data;

public class ChannelMappingData {
	
	final private int   id;
	final private int serviceId;
	final private int channelId;
	public ChannelMappingData(int id, int serviceId, int channelId) {
		this.id = id;
		this.serviceId = serviceId;
		this.channelId = channelId;
	}


	public int getId() {
		return id;
	}
	

	public int getserviceId() {
		return serviceId;
	}
	

	public int channelId() {
		return channelId;
	}
	
}
