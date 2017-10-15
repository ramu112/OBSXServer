package org.mifosplatform.portfolio.order.api;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.json.JSONException;
import org.json.JSONObject;
import org.mifosplatform.billing.payterms.data.PaytermData;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.finance.billingorder.exceptions.BillingOrderNoRecordsFoundException;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.mcodevalues.api.CodeNameConstants;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.mifosplatform.organisation.mcodevalues.service.MCodeReadPlatformService;
import org.mifosplatform.portfolio.association.data.AssociationData;
import org.mifosplatform.portfolio.association.service.HardwareAssociationReadplatformService;
import org.mifosplatform.portfolio.clientservice.data.ClientServiceData;
import org.mifosplatform.portfolio.clientservice.service.ClientServiceReadPlatformService;
import org.mifosplatform.portfolio.contract.data.SubscriptionData;
import org.mifosplatform.portfolio.order.data.OrderAddonsData;
import org.mifosplatform.portfolio.order.data.OrderData;
import org.mifosplatform.portfolio.order.data.OrderDiscountData;
import org.mifosplatform.portfolio.order.data.OrderHistoryData;
import org.mifosplatform.portfolio.order.data.OrderLineData;
import org.mifosplatform.portfolio.order.data.OrderPriceData;
import org.mifosplatform.portfolio.order.service.OrderAddOnsReadPlaformService;
import org.mifosplatform.portfolio.order.service.OrderReadPlatformService;
import org.mifosplatform.portfolio.order.service.OrderWritePlatformService;
import org.mifosplatform.portfolio.plan.data.PlanCodeData;
import org.mifosplatform.portfolio.plan.domain.Plan;
import org.mifosplatform.portfolio.plan.service.PlanReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Path("/orders")
@Component
@Scope("singleton")
public class OrdersApiResource {
	private  final Set<String> RESPONSE_DATA_PARAMETERS=new HashSet<String>(Arrays.asList("id","cancelledStatus","status","contractPeriod","nextBillDate","flag",
	           "currentDate","plan_code","units","service_code","allowedtypes","data","servicedata","billing_frequency", "start_date", "contract_period",
	           "billingCycle","startDate","invoiceTillDate","orderHistory","userAction","ispaymentEnable","paymodes","orderServices","orderDiscountDatas",
	           "discountstartDate","discountEndDate","userName","isAutoProvision"));
	
	  private final String resourceNameForPermissions = "ORDER";
	  private final PlatformSecurityContext context;
	  private final PlanReadPlatformService planReadPlatformService;
	  private final OrderReadPlatformService orderReadPlatformService;
	  private final MCodeReadPlatformService mCodeReadPlatformService;
	  private final ApiRequestParameterHelper apiRequestParameterHelper;
	  private final OrderAddOnsReadPlaformService orderAddOnsReadPlaformService;
	  private final DefaultToApiJsonSerializer<OrderData> toApiJsonSerializer;
	  private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	  private final OrderWritePlatformService orderWritePlatformService;
	  private final HardwareAssociationReadplatformService associationReadplatformService;
	  private final ClientServiceReadPlatformService clientServiceReadPlatformService;
	  

	  @Autowired
	   public OrdersApiResource(final PlatformSecurityContext context,final DefaultToApiJsonSerializer<OrderData> toApiJsonSerializer, 
	   final ApiRequestParameterHelper apiRequestParameterHelper,final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
	   final OrderReadPlatformService orderReadPlatformService,final PlanReadPlatformService planReadPlatformService, 
	   final MCodeReadPlatformService mCodeReadPlatformService,final OrderAddOnsReadPlaformService orderAddOnsReadPlaformService,
	   final OrderWritePlatformService orderWritePlatformService,
	   final HardwareAssociationReadplatformService associationReadplatformService, final ClientServiceReadPlatformService clientServiceReadPlatformService) {


		        this.context = context;
		        this.toApiJsonSerializer = toApiJsonSerializer;
		        this.planReadPlatformService=planReadPlatformService;
		        this.mCodeReadPlatformService=mCodeReadPlatformService;
		        this.orderReadPlatformService=orderReadPlatformService;
		        this.orderAddOnsReadPlaformService=orderAddOnsReadPlaformService;
		        this.apiRequestParameterHelper = apiRequestParameterHelper;
		        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
		        this.orderWritePlatformService = orderWritePlatformService;
		        this.associationReadplatformService=associationReadplatformService;
		        this.clientServiceReadPlatformService = clientServiceReadPlatformService;
		    }	
	  
	@POST
	@Path("{clientId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String createOrder(@PathParam("clientId") final Long clientId, final String apiRequestBodyAsJson) throws JSONException {
		JSONObject object = new JSONObject(apiRequestBodyAsJson);
		if(object.has("planCode")){
			Plan plan = this.orderWritePlatformService.findOneWithNotFoundDetection(object.getLong("planCode"));
			object.put("planDescription", plan.getDescription());
		}
 	    final CommandWrapper commandRequest = new CommandWrapperBuilder().createOrder(clientId).withJson(object.toString()).build();
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
	}
	
	@GET
	@Path("template")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retrieveOrderTemplate(@QueryParam("planId")Long planId,@QueryParam("clientId")Long clientId,@Context final UriInfo uriInfo) {
	context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
	OrderData orderData = handleTemplateRelatedData(planId,clientId);
	final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
    return this.toApiJsonSerializer.serialize(settings, orderData, RESPONSE_DATA_PARAMETERS);
	}
	
	private OrderData handleTemplateRelatedData(Long planId, Long clientId) {
		
		final List<PlanCodeData> planDatas = this.orderReadPlatformService.retrieveAllPlatformData(planId,clientId);
		final List<PaytermData> data=new ArrayList<PaytermData>();
		final List<SubscriptionData> contractPeriod=this.planReadPlatformService.retrieveSubscriptionData(null,null);
		/*final List<ClientServiceData> clientServiceData = this.clientServiceReadPlatformService.retriveClientServices(clientId);*/
		
		return new OrderData(planDatas,data,contractPeriod,null,/*clientServiceData*/null);
	}
	
	@GET
	@Path("{planCode}/template")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String getBillingFrequency(@PathParam("planCode") final Long planCode,@QueryParam("clientId") final Long clientId,@Context final UriInfo uriInfo) {
	context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
	OrderData orderData = handleTemplateRelatedData(new Long(0),null);
	List<PaytermData> datas  = this.orderReadPlatformService.getChargeCodes(planCode,clientId);
	if(datas.size()==0){
		throw new BillingOrderNoRecordsFoundException(planCode);
	}
	orderData.setPaytermData(datas);
	if(datas.get(0).getDuration()!=null){
	orderData.setDuration(datas.get(0).getDuration());
	orderData.setplanType(datas.get(0).getPlanType());
	}
	final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
    return this.toApiJsonSerializer.serialize(settings, orderData, RESPONSE_DATA_PARAMETERS);
	}

	@DELETE
	@Path("{orderId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String deleteOrder(@PathParam("orderId") final Long orderId) {
		final CommandWrapper commandRequest = new CommandWrapperBuilder().deleteOrder(orderId).build();
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
	}

	@GET
	@Path("{clientId}/orders")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retrieveOrderDetails(@PathParam("clientId") final Long clientId, @Context final UriInfo uriInfo) {
    context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
    final List<OrderData> clientOrders = this.orderReadPlatformService.retrieveClientOrderDetails(clientId);
     final List<AssociationData> HardwareDatas = this.associationReadplatformService.retrieveHardwareData(clientId);
     OrderData orderData=new OrderData(clientId,clientOrders,HardwareDatas);
    final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
    return this.toApiJsonSerializer.serialize(settings, orderData, RESPONSE_DATA_PARAMETERS);
	    }
	  
	 @GET
	 @Path("{orderId}/orderprice")
	 @Consumes({MediaType.APPLICATION_JSON})
	 @Produces({MediaType.APPLICATION_JSON})
	 public String retrieveOrderPriceDetails(@PathParam("orderId") final Long orderId,@Context final UriInfo uriInfo) {
		 
	        context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
	        final List<OrderPriceData> priceDatas = this.orderReadPlatformService.retrieveOrderPriceDetails(orderId,null);
	        final List<OrderLineData> services = this.orderReadPlatformService.retrieveOrderServiceDetails(orderId);
	        final List<OrderDiscountData> discountDatas= this.orderReadPlatformService.retrieveOrderDiscountDetails(orderId);
	        OrderData orderDetailsData = this.orderReadPlatformService.retrieveOrderDetails(orderId);
	        final List<OrderAddonsData> orderAddonsDatas = this.orderAddOnsReadPlaformService.retrieveAllOrderAddons(orderId);
	        final List<OrderHistoryData> historyDatas = this.orderReadPlatformService.retrieveOrderHistoryDetails(orderDetailsData.getOrderNo());
	        orderDetailsData=new OrderData(priceDatas,historyDatas,orderDetailsData,services,discountDatas,orderAddonsDatas);
	        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
	        return this.toApiJsonSerializer.serialize(settings, orderDetailsData, RESPONSE_DATA_PARAMETERS);
	    }

	 	@PUT
		@Path("{orderId}/orderprice")
		@Consumes({ MediaType.APPLICATION_JSON })
		@Produces({ MediaType.APPLICATION_JSON })
		public String updateOrderPrice(@PathParam("orderId") final Long orderId,final String apiRequestBodyAsJson) {
		 final CommandWrapper commandRequest = new CommandWrapperBuilder().updateOrderPrice(orderId).withJson(apiRequestBodyAsJson).build();
		 final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		  return this.toApiJsonSerializer.serialize(result);

		}

	 
	@PUT
	@Path("{orderId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String updateOrder(@PathParam("orderId") final Long orderId, final String apiRequestBodyAsJson) {
		final CommandWrapper commandRequest = new CommandWrapperBuilder().disconnectOrder(orderId).withJson(apiRequestBodyAsJson).build();
		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		return this.toApiJsonSerializer.serialize(result);
	}
		
	@GET
    @Path("renewalorder")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String retrieveRenewalOrderDetails(@QueryParam("orderId")final Long orderId,@QueryParam("planType")final String planType, @Context final UriInfo uriInfo) {
        context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
    	List<SubscriptionData> contractPeriods=this.planReadPlatformService.retrieveSubscriptionData(orderId,planType);
    	for(int i=0;i<contractPeriods.size();i++){
    		if(contractPeriods.get(i).getContractdata().equalsIgnoreCase("Perpetual")){
    			contractPeriods.remove(contractPeriods.get(i));
    			
    		}
    		
    	}
    	OrderData orderData=new OrderData(null,contractPeriods);
        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, orderData, RESPONSE_DATA_PARAMETERS);
      }
	
	
	    @POST
		@Path("renewal/{orderId}")
		@Consumes({ MediaType.APPLICATION_JSON })
		@Produces({ MediaType.APPLICATION_JSON })
		public String renewalOrder(@PathParam("orderId") final Long orderId, final String apiRequestBodyAsJson) {
	    	
        final CommandWrapper commandRequest = new CommandWrapperBuilder().renewalOrder(orderId).withJson(apiRequestBodyAsJson).build();
		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		return this.toApiJsonSerializer.serialize(result);
		}
	
	 
	 @GET
	 @Path("disconnect")
	 @Consumes({MediaType.APPLICATION_JSON})
	 @Produces({MediaType.APPLICATION_JSON})
	 public String retrieveOrderDisconnectDetails(@Context final UriInfo uriInfo) {
		 
		 context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
	     final Collection<MCodeData> disconnectDetails = this.mCodeReadPlatformService.getCodeValue(CodeNameConstants.CODE_DISCONNECT_REASON);
	     OrderData orderData = new OrderData(disconnectDetails);
	     final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
	     return this.toApiJsonSerializer.serialize(settings, orderData, RESPONSE_DATA_PARAMETERS);
	 }
	 
	 
	    @PUT
		@Path("reconnect/{orderId}")
		@Consumes({ MediaType.APPLICATION_JSON })
		@Produces({ MediaType.APPLICATION_JSON })
		public String reconnectOrder(@PathParam("orderId") final Long orderId) {
		 
		    context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
			final CommandWrapper commandRequest = new CommandWrapperBuilder().reconnectOrder(orderId).build();
	        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
	        return this.toApiJsonSerializer.serialize(result);
		}

	    @GET
	    @Path("{clientId}/activeplans")
		@Consumes({MediaType.APPLICATION_JSON})
		@Produces({MediaType.APPLICATION_JSON})
		public String retrieveActivePlans(@PathParam("clientId") final Long clientId,@QueryParam("planType") final String planType, @Context final UriInfo uriInfo) {
	    	
		context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		List<OrderData> datas=this.orderReadPlatformService.getActivePlans(clientId,planType);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
	    return this.toApiJsonSerializer.serialize(settings, datas, RESPONSE_DATA_PARAMETERS);
	    
	 }
	 
	 /**
	   * this method is using for posting data while command centre
	   * @param uriInfo
	   * @return
	   */
	 	@POST
		@Path("retrackOsdmessage/{orderId}")
		@Consumes({ MediaType.APPLICATION_JSON })
		@Produces({ MediaType.APPLICATION_JSON })
		public String retrackmessage(@PathParam("orderId") final Long orderId,final String apiRequestBodyAsJson) {
			final CommandWrapper commandRequest = new CommandWrapperBuilder().retrackOsdmessage(orderId).withJson(apiRequestBodyAsJson).build();
			final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
			return this.toApiJsonSerializer.serialize(result);

	 }
	 
	@PUT
		@Path("changePlan/{orderId}")
		@Consumes({ MediaType.APPLICATION_JSON })
		@Produces({ MediaType.APPLICATION_JSON })
		public String changePlan(@PathParam("orderId") final Long orderId,final String apiRequestBodyAsJson) {
        final CommandWrapper commandRequest = new CommandWrapperBuilder().changePlan(orderId).withJson(apiRequestBodyAsJson).build();
		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		return this.toApiJsonSerializer.serialize(result);
		}	 
	   
	@PUT
	  @Path("applypromo/{orderId}")
	  @Consumes({ MediaType.APPLICATION_JSON })
	  @Produces({ MediaType.APPLICATION_JSON })
	  public String applyPromoCodeToOrder(@PathParam("orderId") final Long orderId,final String apiRequestBodyAsJson) {
	  final CommandWrapper commandRequest = new CommandWrapperBuilder().applyPromo(orderId).withJson(apiRequestBodyAsJson).build();
	  final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
	  return this.toApiJsonSerializer.serialize(result);
	}	   

	
	@POST
	@Path("scheduling/{clientId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String schedulingOrderCreation(@PathParam("clientId") final Long clientId, final String apiRequestBodyAsJson) {
 	    final CommandWrapper commandRequest = new CommandWrapperBuilder().createSchedulingOrder(clientId).withJson(apiRequestBodyAsJson).build();
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
	}
	
	@DELETE
	@Path("scheduling/{orderId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String deleteScheduleOrder(@PathParam("orderId") final Long orderId) {
		final CommandWrapper commandRequest = new CommandWrapperBuilder().deleteSchedulOrder(orderId).build();
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
	}
	
	  @PUT
	  @Path("extension/{orderId}")
	  @Consumes({ MediaType.APPLICATION_JSON })
	  @Produces({ MediaType.APPLICATION_JSON })
	  public String extendOrder(@PathParam("orderId") final Long orderId,final String apiRequestBodyAsJson) {
	  final CommandWrapper commandRequest = new CommandWrapperBuilder().extensionOrder(orderId).withJson(apiRequestBodyAsJson).build();
	  final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
	  return this.toApiJsonSerializer.serialize(result);
	}	   
	

	@GET
    @Path("extension")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String getOfExtension(@Context final UriInfo uriInfo) {
        context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
        Collection<MCodeData> extensionPeriodDatas=this.mCodeReadPlatformService.getCodeValue(CodeNameConstants.CODE_EXTENSION_PERIOD);
		Collection<MCodeData> extensionReasonDatas=this.mCodeReadPlatformService.getCodeValue(CodeNameConstants.CODE_EXTENSION_REASON);
        OrderData extensionData=new OrderData(extensionPeriodDatas,extensionReasonDatas);
        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, extensionData, RESPONSE_DATA_PARAMETERS);
    }
	
	  @PUT
	  @Path("terminate/{orderId}")
	  @Consumes({ MediaType.APPLICATION_JSON })
	  @Produces({ MediaType.APPLICATION_JSON })
	  public String terminateOrder(@PathParam("orderId") final Long orderId) {
	  final CommandWrapper commandRequest = new CommandWrapperBuilder().terminateOrder(orderId).build();
	  final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
	  return this.toApiJsonSerializer.serialize(result);
	}	   
	
	  /**
	   * this method is using for getting template information in suspension
	   * @param uriInfo
	   * @return
	   */
	  @GET
	  @Path("suspend")
	  @Consumes({MediaType.APPLICATION_JSON})
	  @Produces({MediaType.APPLICATION_JSON})
	  public String getSuspentationReasons(@Context final UriInfo uriInfo) {
		  
	        context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
			final Collection<MCodeData> reasonDatas=this.mCodeReadPlatformService.getCodeValue(CodeNameConstants.CODE_SUSPENSION_REASON);
	        final OrderData orderData=new OrderData(null,reasonDatas);
	        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
	        return this.toApiJsonSerializer.serialize(settings, orderData, RESPONSE_DATA_PARAMETERS);
	    }
	
   /**
   * this method is using for update order status while suspension
   * @param uriInfo
   * @return
   */
	@PUT
	@Path("suspend/{orderId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String suspendOrder(@PathParam("orderId") final Long orderId,final String apiRequestBodyAsJson) {
    final CommandWrapper commandRequest = new CommandWrapperBuilder().orderSuspend(orderId).withJson(apiRequestBodyAsJson).build();
	final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
	return this.toApiJsonSerializer.serialize(result);
	}
	
	@PUT
	@Path("reactive/{orderId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String reactiveOrder(@PathParam("orderId") final Long orderId, final String apiRequestBodyAsJson) {
		final CommandWrapper commandRequest = new CommandWrapperBuilder().orderReactive(orderId).withJson(apiRequestBodyAsJson).build();
		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		return this.toApiJsonSerializer.serialize(result);
		
	}
	
	@PUT
	@Path("scheduling/{orderId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String schedulingOrderUpdation(@PathParam("orderId") final Long orderId, final String apiRequestBodyAsJson) {
 	    final CommandWrapper commandRequest = new CommandWrapperBuilder().updateSchedulingOrder(orderId).withJson(apiRequestBodyAsJson).build();
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
	}
}
