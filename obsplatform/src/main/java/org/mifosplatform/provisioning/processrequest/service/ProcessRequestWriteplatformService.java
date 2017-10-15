package org.mifosplatform.provisioning.processrequest.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.provisioning.provisioning.domain.ProvisioningRequest;

public interface ProcessRequestWriteplatformService {

//	void ProcessingRequestDetails();

    CommandProcessingResult addProcessRequest(JsonCommand command);

	void notifyProcessingDetails(ProvisioningRequest provisioningRequest, char status);

	//void postProvisioningdetails(Client client, EventOrder eventOrder,String requestType, String provsystem, String response);

}
