package org.mifosplatform.billing.taxmapping.api;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
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

import org.mifosplatform.billing.chargecode.data.ChargeCodeData;
import org.mifosplatform.billing.taxmapping.data.TaxMapData;
import org.mifosplatform.billing.taxmapping.service.TaxMapReadPlatformService;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.mcodevalues.api.CodeNameConstants;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.mifosplatform.organisation.mcodevalues.service.MCodeReadPlatformService;
import org.mifosplatform.organisation.priceregion.data.PriceRegionData;
import org.mifosplatform.organisation.priceregion.service.RegionalPriceReadplatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author hugo
 *
 *this api class use to create,update taxes for different charge codes
 */
@Path("taxmap")
@Component
@Scope("singleton")
public class TaxMapApiResource {

	private final Set<String> RESPONSE_TAXMAPPING_PARAMETERS = new HashSet<String>(Arrays.asList("id", "chargeCode", "taxCode", "startDate", "type",
					"rate", "taxRegionId", "taxRegion", "priceRegionData"));

	private String resourceNameForPermissions = "TAXMAPPING";
	private final ApiRequestParameterHelper apiRequestParameterHelper;
	private final PlatformSecurityContext context;
	private final DefaultToApiJsonSerializer<TaxMapData> apiJsonSerializer;
	private final PortfolioCommandSourceWritePlatformService commandSourceWritePlatformService;
	private final TaxMapReadPlatformService taxMapReadPlatformService;
	private final RegionalPriceReadplatformService regionalPriceReadplatformService;
	private final MCodeReadPlatformService mCodeReadPlatformService;

	@Autowired
	public TaxMapApiResource(final ApiRequestParameterHelper apiRequestParameterHelper,final PlatformSecurityContext context,
			final DefaultToApiJsonSerializer<TaxMapData> apiJsonSerializer,final PortfolioCommandSourceWritePlatformService commandSourceWritePlatformService,
			final TaxMapReadPlatformService taxMapReadPlatformService,final RegionalPriceReadplatformService regionalPriceReadplatformService,
			final MCodeReadPlatformService mCodeReadPlatformService) {
		
		this.context = context;
		this.apiJsonSerializer = apiJsonSerializer;
		this.apiRequestParameterHelper = apiRequestParameterHelper;
		this.commandSourceWritePlatformService = commandSourceWritePlatformService;
		this.taxMapReadPlatformService = taxMapReadPlatformService;
		this.regionalPriceReadplatformService = regionalPriceReadplatformService;
		this.mCodeReadPlatformService = mCodeReadPlatformService;
	}

	@GET
	@Path("template")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retriveTaxMapTemplate(@QueryParam("chargeCode") final String chargeCode,@Context final UriInfo uriInfo) {
		
		context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		final Collection<MCodeData> taxTypeData = this.mCodeReadPlatformService.getCodeValue(CodeNameConstants.CODE_TYPE);
		final List<PriceRegionData> priceRegionData = this.regionalPriceReadplatformService.getPriceRegionsDetails();
		final TaxMapData taxMapData=new TaxMapData(taxTypeData,priceRegionData,chargeCode);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.apiJsonSerializer.serialize(settings, taxMapData,RESPONSE_TAXMAPPING_PARAMETERS);
	}

	@POST
	@Path("{chargCode}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String createTaxMap(@PathParam("chargeCode") final String chargeCode,final String jsonRequestBody) {

	final CommandWrapper command = new CommandWrapperBuilder().createTaxMap(chargeCode).withJson(jsonRequestBody).build();
	final CommandProcessingResult result = commandSourceWritePlatformService.logCommandSource(command);
	return apiJsonSerializer.serialize(result);
	}
	
	@GET
	@Path("{chargCode}/chargetax")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retriveTaxDetailsForChargeCode(@PathParam("chargCode") final String chargeCode,	@Context final UriInfo uriInfo) {
		
		context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		final List<TaxMapData> taxMapData = taxMapReadPlatformService.retriveTaxMapData(chargeCode);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.apiJsonSerializer.serialize(settings, taxMapData,RESPONSE_TAXMAPPING_PARAMETERS);
	}


	@GET
	@Path("{taxMapId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrievedSingleTaxMap(@PathParam("taxMapId") final Long taxMapId,@Context final UriInfo uriInfo) {
		
		context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		TaxMapData taxMapData = taxMapReadPlatformService.retrievedSingleTaxMapData(taxMapId);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		if(settings.isTemplate()){
		final List<ChargeCodeData> chargeCodeData = this.taxMapReadPlatformService.retrivedChargeCodeTemplateData();
		final Collection<MCodeData> taxTypeData = this.mCodeReadPlatformService.getCodeValue(CodeNameConstants.CODE_TYPE);
		final List<PriceRegionData> priceRegionData = this.regionalPriceReadplatformService.getPriceRegionsDetails();
		taxMapData.setChargeCodesForTax(chargeCodeData);
		taxMapData.setTaxTypeData(taxTypeData);
		taxMapData.setPriceRegionData(priceRegionData);
		}
		return this.apiJsonSerializer.serialize(settings, taxMapData,RESPONSE_TAXMAPPING_PARAMETERS);

	}

	@PUT
	@Path("{taxMapId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String updateTaxMapData(@PathParam("taxMapId") final Long taxMapId,final String jsonRequestBody) {

		context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		final CommandWrapper commandRequest = new CommandWrapperBuilder().updateTaxMap(taxMapId).withJson(jsonRequestBody).build();
		final CommandProcessingResult result = commandSourceWritePlatformService.logCommandSource(commandRequest);
		return apiJsonSerializer.serialize(result);
	}
}
