package org.mifosplatform.finance.creditdistribution.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.mifosplatform.billing.invoice.data.InvoiceData;
import org.mifosplatform.billing.invoice.service.InvoiceReadPlatformService;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.finance.creditdistribution.data.CreditDistributionData;
import org.mifosplatform.finance.creditdistribution.service.CreditDistributionReadPlatformService;
import org.mifosplatform.finance.payments.data.PaymentData;
import org.mifosplatform.finance.payments.service.PaymentReadPlatformService;
import org.mifosplatform.infrastructure.codes.data.CodeData;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/creditdistributions")
@Component
@Scope("singleton")
public class CreditDistributionApiResource {

	/**
	 * The set of parameters that are supported in response for {@link CodeData}
	 */
	private static final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("id", "clientId", "paymentDate", "paymentId",
					"amount", "invoiceAmount","invoiceDatas","paymentDatas","availAmount"));
	private final String resourceNameForPermissions = "CREDITDISTRIBUTION";
	private final PlatformSecurityContext context;
	private final DefaultToApiJsonSerializer<CreditDistributionData> toApiJsonSerializer;
	private final ApiRequestParameterHelper apiRequestParameterHelper;
	private final PortfolioCommandSourceWritePlatformService writePlatformService;
	private final InvoiceReadPlatformService invoiceReadPlatformService;
	private final PaymentReadPlatformService paymentReadPlatformservice;
	private final CreditDistributionReadPlatformService creditDistributionReadPlatformService;
	private final DefaultToApiJsonSerializer<CreditDistributionData> apiJsonSerializer;
	@Autowired
	public CreditDistributionApiResource(final PlatformSecurityContext context,final DefaultToApiJsonSerializer<CreditDistributionData> toApiJsonSerializer,
			final ApiRequestParameterHelper apiRequestParameterHelper,final PortfolioCommandSourceWritePlatformService writePlatformService,
			final InvoiceReadPlatformService invoiceReadPlatformService,final PaymentReadPlatformService paymentReadPlatformservice,
			final CreditDistributionReadPlatformService creditDistributionReadPlatformService,DefaultToApiJsonSerializer<CreditDistributionData> apiJsonSerializer) {
		
		this.context = context;
		this.toApiJsonSerializer = toApiJsonSerializer;
		this.apiRequestParameterHelper = apiRequestParameterHelper;
		this.writePlatformService = writePlatformService;
		this.invoiceReadPlatformService=invoiceReadPlatformService;
		this.paymentReadPlatformservice=paymentReadPlatformservice;
		this.creditDistributionReadPlatformService=creditDistributionReadPlatformService;
		this.apiJsonSerializer=apiJsonSerializer;
	}

	@POST
	@Path("{clientId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String addCreditDistribution(@PathParam("clientId") final Long clientId,	final String apiRequestBodyAsJson) {
		final CommandWrapper commandRequest = new CommandWrapperBuilder().createCreditDistribution(clientId).withJson(apiRequestBodyAsJson).build();
		final CommandProcessingResult result = this.writePlatformService.logCommandSource(commandRequest);
		return this.toApiJsonSerializer.serialize(result);
	}
	
	@GET
	@Path("template/{clientId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrievetemplatedata(@PathParam("clientId") final Long clientId,@Context final UriInfo uriInfo) {
 		 context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		List<InvoiceData> invoiceDatas= this.invoiceReadPlatformService.retrieveDueAmountInvoiceDetails(clientId);
		List<PaymentData> paymentDatas=this.paymentReadPlatformservice.retrieveClientPaymentDetails(clientId);
		CreditDistributionData creditDistributionData=new CreditDistributionData(invoiceDatas,paymentDatas);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.toApiJsonSerializer.serialize(settings,creditDistributionData, RESPONSE_DATA_PARAMETERS);
	}
	@GET
	@Path("{clientId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveCreditDistributiondata(@PathParam("clientId") final Long clientId,@Context final UriInfo uriInfo) {
 		 context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		Page<CreditDistributionData> creditDistributionDatas= this.creditDistributionReadPlatformService.getClientDistributionData(clientId);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.apiJsonSerializer.serialize(creditDistributionDatas);
	}
	
}
