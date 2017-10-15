package org.mifosplatform.portfolio.service.api;

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

import org.mifosplatform.billing.emun.data.EnumValuesData;
import org.mifosplatform.billing.emun.service.EnumReadplaformService;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.mcodevalues.api.CodeNameConstants;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.mifosplatform.organisation.mcodevalues.service.MCodeReadPlatformService;
import org.mifosplatform.portfolio.plan.service.PlanReadPlatformService;
import org.mifosplatform.portfolio.service.data.ServiceDetailData;
import org.mifosplatform.portfolio.service.data.ServiceMasterOptionsData;
import org.mifosplatform.portfolio.service.service.ServiceMasterReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/servicemasters")
@Component
@Scope("singleton")
public class ServiceMasterApiResource {
	private  final Set<String> RESPONSE_DATA_PARAMETERS=new HashSet<String>(Arrays.asList("id","serviceType","serviceCode","serviceDescription","serviceTypes",
			"serviceUnitTypes","serviceUnitTypes","isOptional","status","serviceStatus","isAutoProvision","serviceCategory"));
        private final static String RESOURCENAMEFORPERMISSIONS = "SERVICE";
	    private final PlatformSecurityContext context;
	    private final DefaultToApiJsonSerializer<ServiceMasterOptionsData> toApiJsonSerializer;
	    private final ApiRequestParameterHelper apiRequestParameterHelper;
	    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	    private final ServiceMasterReadPlatformService serviceMasterReadPlatformService;
		private final PlanReadPlatformService planReadPlatformService;
		private final EnumReadplaformService enumReadplaformService;
		private final MCodeReadPlatformService mCodeReadPlatformService;
		
		 @Autowired
	    public ServiceMasterApiResource(final PlatformSecurityContext context,final DefaultToApiJsonSerializer<ServiceMasterOptionsData> toApiJsonSerializer, 
	    		final ApiRequestParameterHelper apiRequestParameterHelper,final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
	    		final ServiceMasterReadPlatformService serviceMasterReadPlatformService,final PlanReadPlatformService planReadPlatformService,
	    		final EnumReadplaformService enumReadplaformService,final MCodeReadPlatformService mCodeReadPlatformService) {

			    this.context = context;
		        this.toApiJsonSerializer = toApiJsonSerializer;
		        this.enumReadplaformService=enumReadplaformService;
		        this.planReadPlatformService=planReadPlatformService;
		        this.apiRequestParameterHelper = apiRequestParameterHelper;
		        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
		        this.serviceMasterReadPlatformService=serviceMasterReadPlatformService;
		        this.mCodeReadPlatformService = mCodeReadPlatformService;
		        
		        
		    }		
		
	/**
	 * using this method posting  service data 
	 */	 
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String createNewService(final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().createService().withJson(apiRequestBodyAsJson).build();
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
	}
	
	/**
	 * using this method getting  all services data 
	 */	
	 	@GET
		@Consumes({ MediaType.APPLICATION_JSON })
		@Produces({ MediaType.APPLICATION_JSON })
		public String retrieveAllService(@Context final UriInfo uriInfo,@QueryParam("sqlSearch") final String sqlSearch, @QueryParam("limit") final Integer limit,
				@QueryParam("offset") final Integer offset) {
	 		
		   context.authenticatedUser().validateHasReadPermission(RESOURCENAMEFORPERMISSIONS);
			//final Collection<ServiceMasterOptionsData> masterOptionsDatas = this.serviceMasterReadPlatformService.retrieveServices();
		   final SearchSqlQuery searchCodes =SearchSqlQuery.forSearch(sqlSearch, offset,limit );
		   final Page<ServiceMasterOptionsData> masterOptionsDatas = this.serviceMasterReadPlatformService.retrieveServices(searchCodes);
		   return this.toApiJsonSerializer.serialize(masterOptionsDatas);
		}

	     /**
		 * using this method getting  template data for service
		 */	
	    @GET
	    @Path("template")
	    @Consumes({MediaType.APPLICATION_JSON})
	    @Produces({MediaType.APPLICATION_JSON})
	    public String retrieveTempleteInfo(@Context final UriInfo uriInfo) {
		 context.authenticatedUser().validateHasReadPermission(RESOURCENAMEFORPERMISSIONS);
		 final ServiceMasterOptionsData masterOptionsData=handleTemplateData();
		 final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
			return this.toApiJsonSerializer.serialize(settings, masterOptionsData, RESPONSE_DATA_PARAMETERS);
		}

	 private ServiceMasterOptionsData handleTemplateData() {

		 final Collection<EnumValuesData> serviceType = this.enumReadplaformService.getEnumValues("service_type");
		 final List<EnumOptionData> status = this.planReadPlatformService.retrieveNewStatus();
		 final List<EnumOptionData> serviceUnitType = this.serviceMasterReadPlatformService.retrieveServiceUnitType();
		 final Collection<MCodeData> serviceParamsData = this.mCodeReadPlatformService.getCodeValue(CodeNameConstants.CODE_SERVICE_PARAMS);
		 return new ServiceMasterOptionsData(serviceType,serviceUnitType,status,serviceParamsData);
		
	}

	 /**
	 * this method for getting  service data using by id
	 */	
	    @GET
		@Path("{serviceId}")
		@Consumes({MediaType.APPLICATION_JSON})
		@Produces({MediaType.APPLICATION_JSON})
		public String retrieveSingleServiceDetails(@PathParam("serviceId") final Long serviceId, @Context final UriInfo uriInfo) {
			context.authenticatedUser().validateHasReadPermission(RESOURCENAMEFORPERMISSIONS);
			final ServiceMasterOptionsData productData = this.serviceMasterReadPlatformService.retrieveIndividualService(serviceId);
			final Collection<ServiceDetailData> serviceDetailDatas = this.serviceMasterReadPlatformService.retrieveServiceDetails(serviceId);
			 final ServiceMasterOptionsData masterOptionsData=handleTemplateData();
			 productData.setStatus(masterOptionsData.getStatus());
			 productData.setServiceTypes(masterOptionsData.getServiceTypes());
			 productData.setServiceUnitTypes(masterOptionsData.getServiceUnitTypes());
			 productData.setServiceParamsData(masterOptionsData.getServiceParamsData());
			 productData.setServiceDetailData(serviceDetailDatas);
			 
	        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
	        return this.toApiJsonSerializer.serialize(settings, productData, RESPONSE_DATA_PARAMETERS);
		}
	
	 /**
	 * using this method editing single service data 
	 */	
	    @PUT
		@Path("{serviceId}")
		@Consumes({MediaType.APPLICATION_JSON})
		@Produces({MediaType.APPLICATION_JSON})
		public String updateServiceData(@PathParam("serviceId") final Long serviceId, final String apiRequestBodyAsJson){

		 final CommandWrapper commandRequest = new CommandWrapperBuilder().updateService(serviceId).withJson(apiRequestBodyAsJson).build();
		 final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		  return this.toApiJsonSerializer.serialize(result);
		}
	 
	 /**
	 * using this method deleting single service data 
	 */	
 	    @DELETE
		@Path("{serviceId}")
		@Consumes({MediaType.APPLICATION_JSON})
		@Produces({MediaType.APPLICATION_JSON})
		public String deleteServiceData(@PathParam("serviceId") final Long serviceId) {
		 final CommandWrapper commandRequest = new CommandWrapperBuilder().deleteService(serviceId).build();
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);

		}
	 
}
