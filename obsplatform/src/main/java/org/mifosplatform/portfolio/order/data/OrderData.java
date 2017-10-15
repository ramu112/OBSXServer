package org.mifosplatform.portfolio.order.data;

import java.util.Collection;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.billing.payterms.data.PaytermData;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.mifosplatform.portfolio.association.data.AssociationData;
import org.mifosplatform.portfolio.clientservice.data.ClientServiceData;
import org.mifosplatform.portfolio.contract.data.SubscriptionData;
import org.mifosplatform.portfolio.plan.data.PlanCodeData;

public class OrderData {
	private Long id;
	private Long pdid;
	private Long orderPriceId;
	private Long clientId;
	private String service_code;
	private String planCode;
	private String planDescription;
	private String chargeCode;
	private double price;
	private String variant;
	private String status;
	private Long period;
	private LocalDate startDate;
	private LocalDate currentDate;
	private LocalDate endDate;
	private String billingFrequency;
	private List<PlanCodeData> plandata;
	private List<PaytermData> paytermdata;
	private List<SubscriptionData> subscriptiondata;
	private List<OrderPriceData> orderPriceData;
	private LocalDate activeDate;
	private String contractPeriod;
//	private boolean flag;
	private Collection<MCodeData> disconnectDetails;
	private List<OrderHistoryData> orderHistory;
	private String isPrepaid;
	private String allowtopup;
	private List<OrderData> clientOrders;
	private String userAction;
	private String orderNo;
	private OrderData orderData;
	private String provisioningSys;
	private List<OrderLineData> orderServices;
	private List<OrderDiscountData> orderDiscountDatas;
	private LocalDate invoiceTilldate;
	private Collection<MCodeData> reasons;
	private Collection<MCodeData> extensionPeriodDatas;
	private String groupName;
	private Long planStatus;
	@SuppressWarnings("unused")
	private List<OrderAddonsData> orderAddonsDatas;
	@SuppressWarnings("unused")
	private String autoRenew;
	private List<AssociationData> HardwareDatas;
	List<ClientServiceData> clientServiceData;
	private Long clientServiceId;

	public OrderData(List<PlanCodeData> allowedtypes,List<PaytermData> paytermData,
			List<SubscriptionData> contractPeriod, OrderData data,List<ClientServiceData> clientServiceData) {

		if (data != null) {
			
			this.id = data.getId();
			this.pdid = data.getPdid();
			this.planCode = data.getPlan_code();
			this.status = null;
			this.period = data.getPeriod();
			this.orderPriceId = data.getOrderPriceId();
			this.service_code = null;
			this.startDate = data.getStartDate();
		}
		this.startDate=DateUtils.getLocalDateOfTenant();
		this.variant = null;
		this.chargeCode = null;
		this.paytermdata = paytermData;
		this.plandata = allowedtypes;
		this.subscriptiondata = contractPeriod;
		this.clientServiceData = clientServiceData;

	}

	public OrderData(Long id, Long planId, String plancode, String status,LocalDate startDate, LocalDate endDate,
			double price,String contractPeriod, String isprepaid, String allowtopup,String userAction,
			String provisioningSys, String orderNo, LocalDate invoiceTillDate, LocalDate activaDate, String groupName,String autoRenew, Long clientServiceId) {
		this.id = id;
		this.pdid = planId;
		this.planCode = plancode;
		this.status = status;
		this.period = null;
		this.startDate = startDate;
		this.currentDate = DateUtils.getLocalDateOfTenant();
		this.endDate = endDate;
		this.orderPriceId = null;
		this.service_code = null;
		this.price = price;
		this.variant = null;
		this.chargeCode = null;
		this.paytermdata = null;
		this.plandata = null;
		this.subscriptiondata = null;
		this.contractPeriod = contractPeriod;
		this.isPrepaid=isprepaid;
		this.allowtopup=allowtopup;
		this.userAction=userAction;
        this.provisioningSys=provisioningSys;
        this.orderNo=orderNo;
        this.invoiceTilldate=invoiceTillDate;
		this.activeDate=activaDate;
		this.groupName=groupName;
		this.autoRenew=autoRenew;
		this.clientServiceId=clientServiceId;
		
	}

	public OrderData(List<OrderPriceData> priceDatas, List<OrderHistoryData> historyDatas, OrderData orderDetailsData,
			  List<OrderLineData> services, List<OrderDiscountData> discountDatas, List<OrderAddonsData> orderAddonsDatas) {
		this.orderPriceData = priceDatas;
		this.orderHistory=historyDatas;
		this.orderData=orderDetailsData;
		this.orderServices=services;
		this.orderDiscountDatas=discountDatas;
		this.orderAddonsDatas=orderAddonsDatas;
       
	}

	public OrderData(final Collection<MCodeData> disconnectDetails, final List<SubscriptionData> subscriptionDatas) {
		this.disconnectDetails = disconnectDetails;
		this.subscriptiondata = subscriptionDatas;
	}

	public OrderData(Long clientId, List<OrderData> clientOrders) {
		this.clientId=clientId;
		this.clientOrders=clientOrders;
	}
	public OrderData(Long clientId, List<OrderData> clientOrders,List<AssociationData> HardwareDatas) {
		this.clientId=clientId;
		this.clientOrders=clientOrders;
		this.setHardwareDatas(HardwareDatas);
	}

	public OrderData(Long orderId, String planCode, String planDescription,
			String billingFreq, String contractPeriod, Double price, LocalDate endDate) {
                 
		            this.id=orderId;
		            this.planCode=planCode;
		            this.planDescription=planDescription;
		            this.billingFrequency=billingFreq;
		            this.contractPeriod=contractPeriod;
		            this.price=price;
		            this.endDate=endDate;
		           
	}
	
	public OrderData(Collection<MCodeData> extensionPeriodDatas,
			Collection<MCodeData> extensionReasonDatas) {
		
		this.extensionPeriodDatas=extensionPeriodDatas;
		this.reasons=extensionReasonDatas;
	}
	
	public OrderData(Long planId,Long planStatus) {
		this.planStatus = planStatus;
		this.pdid = planId;
	}

	public OrderData(Collection<MCodeData> disconnectDetails) {
		this.disconnectDetails=disconnectDetails;
	}
	
	
	public OrderData(Long orderId) {
		this.id=orderId;
	}

	public Long getPlanStatus() {
		return planStatus;
	}

	public void setPlanStatus(Long planStatus) {
		this.planStatus = planStatus;
	}

	public Long getId() {
		return id;
	}

	public Long getPdid() {
		return pdid;
	}

	public Long getClientId() {
		return clientId;
	}

	public String getBillingFrequency() {
		return billingFrequency;
	}

	
	public String getPlanCode() {
		return planCode;
	}

	public String getPlanDescription() {
		return planDescription;
	}

	public LocalDate getCurrentDate() {
		return currentDate;
	}

	public LocalDate getActiveDate() {
		return activeDate;
	}

	public String getContractPeriod() {
		return contractPeriod;
	}

	public Collection<MCodeData> getDisconnectDetails() {
		return disconnectDetails;
	}

	public List<OrderHistoryData> getOrderHistory() {
		return orderHistory;
	}

	public String getIsPrepaid() {
		return isPrepaid;
	}

	public String getAllowtopup() {
		return allowtopup;
	}

	public List<OrderData> getClientOrders() {
		return clientOrders;
	}

	public String getUserAction() {
		return userAction;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public OrderData getOrderData() {
		return orderData;
	}

	public String getProvisioningSys() {
		return provisioningSys;
	}




	public List<OrderLineData> getOrderServices() {
		return orderServices;
	}

	public List<OrderDiscountData> getOrderDiscountDatas() {
		return orderDiscountDatas;
	}

	public LocalDate getInvoiceTilldate() {
		return invoiceTilldate;
	}

	public Collection<MCodeData> getReasons() {
		return reasons;
	}

	public Collection<MCodeData> getExtensionPeriodDatas() {
		return extensionPeriodDatas;
	}

	public String getGroupName() {
		return groupName;
	}

	public List<OrderPriceData> getOrderPriceData() {
		return orderPriceData;
	}

	public Long getOrderPriceId() {
		return orderPriceId;
	}

	public String getService_code() {
		return service_code;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public String getStatus() {
		return status;
	}

	public Long getPeriod() {
		return period;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public String getPlan_code() {
		return planCode;
	}

	public double getPrice() {
		return price;
	}

	public String getVariant() {
		return variant;
	}

	public String getChargeCode() {
		return chargeCode;
	}

	public List<PlanCodeData> getPlandata() {
		return plandata;
	}

	public List<PaytermData> getPaytermdata() {
		return paytermdata;
	}

	public List<SubscriptionData> getSubscriptiondata() {
		return subscriptiondata;
	}

	public void setPaytermData(List<PaytermData> data) {
		this.paytermdata = data;
	}

	public void setDisconnectDetails(Collection<MCodeData> disconnectDetails) {
	this.disconnectDetails = disconnectDetails;
		
	}

	public void setDuration(String duration) {
		this.contractPeriod=duration;
		
	}

	public void setplanType(String planType) {
	this.isPrepaid=planType;
		
	}

	public List<AssociationData> getHardwareDatas() {
		return HardwareDatas;
	}

	public void setHardwareDatas(List<AssociationData> hardwareDatas) {
		HardwareDatas = hardwareDatas;
	}

	public Long getClientServiceId() {
		return clientServiceId;
	}

	public void setClientServiceId(Long clientServiceId) {
		this.clientServiceId = clientServiceId;
	}
	
	

}
