package org.mifosplatform.finance.billingorder.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.finance.billingorder.service.InvoiceClient;
import org.mifosplatform.infrastructure.codes.data.CodeData;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;


@Path("/billingorder")
@Component
@Scope("singleton")
public class BillingOrderApiResourse {

    private final DefaultToApiJsonSerializer<CodeData> toApiJsonSerializer;
    private final InvoiceClient invoiceClient;
    private final FromJsonHelper fromApiJsonHelper;
    
	@Autowired
	BillingOrderApiResourse(final DefaultToApiJsonSerializer<CodeData> toApiJsonSerializer,final InvoiceClient invoiceClient,
			final FromJsonHelper fromApiJsonHelper){
		
        this.toApiJsonSerializer = toApiJsonSerializer;
		this.invoiceClient=invoiceClient;
		this.fromApiJsonHelper=fromApiJsonHelper;
	}
	
	
	@POST
	@Path("{clientId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveBillingProducts(@PathParam("clientId") final Long clientId,final String apiRequestBodyAsJson) {
		 final CommandWrapper wrapper = new CommandWrapperBuilder().createInvoice(clientId).withJson(apiRequestBodyAsJson).build();
		 final String json = wrapper.getJson();
		 CommandProcessingResult result = null;
			
				final JsonElement parsedCommand = this.fromApiJsonHelper.parse(json);
				final JsonCommand command = JsonCommand.from(json, parsedCommand,
						this.fromApiJsonHelper, wrapper.getEntityName(),
						wrapper.getEntityId(), wrapper.getSubentityId(),
						wrapper.getGroupId(), wrapper.getClientId(),
						wrapper.getLoanId(), wrapper.getSavingsId(),
						wrapper.getCodeId(), wrapper.getSupportedEntityType(),
						wrapper.getSupportedEntityId(), wrapper.getTransactionId(),null);
		result=this.invoiceClient.createInvoiceBill(command); 
		return this.toApiJsonSerializer.serialize(result);
	}
	
	
}
