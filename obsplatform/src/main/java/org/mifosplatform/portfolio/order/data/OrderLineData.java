package org.mifosplatform.portfolio.order.data;

public class OrderLineData {
	
	private final Long id;
	private final Long orderId;
	private final String servicecode;
	private final String serviceDescription;
	private final String serviceType;
	private final Long serviceId;
	private String isAutoProvision;
	private String image;
	

	public OrderLineData(Long id, Long orderId, String serviceCode,String serviceDescription,
			String serviceType, Long serviceId, String isAutoProvision,String image) {
		
		this.id=id;
		this.orderId=orderId;
		this.servicecode=serviceCode;
		this.serviceDescription=serviceDescription;
		this.serviceType=serviceType;
		this.serviceId=serviceId;
		this.isAutoProvision=isAutoProvision;
		this.image=image;
	}


	public Long getId() {
		return id;
	}


	public Long getOrderId() {
		return orderId;
	}


	public String getServicecode() {
		return servicecode;
	}


	public String getServiceDescription() {
		return serviceDescription;
	}


	public String getServiceType() {
		return serviceType;
	}

	
}
