package org.mifosplatform.provisioning.entitlements.service;

import java.util.List;

import org.json.JSONObject;
import org.mifosplatform.billing.selfcare.domain.SelfCare;
import org.mifosplatform.billing.selfcare.service.SelfCareRepository;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.organisation.message.domain.BillingMessage;
import org.mifosplatform.organisation.message.domain.BillingMessageRepository;
import org.mifosplatform.organisation.message.domain.BillingMessageTemplate;
import org.mifosplatform.organisation.message.domain.BillingMessageTemplateConstants;
import org.mifosplatform.organisation.message.domain.BillingMessageTemplateRepository;
import org.mifosplatform.organisation.message.exception.BillingMessageTemplateNotFoundException;
import org.mifosplatform.organisation.office.domain.Office;
import org.mifosplatform.organisation.office.domain.OfficeRepository;
import org.mifosplatform.portfolio.client.domain.Client;
import org.mifosplatform.portfolio.client.domain.ClientRepository;
import org.mifosplatform.portfolio.client.exception.ClientNotFoundException;
import org.mifosplatform.provisioning.processrequest.domain.ProcessRequest;
import org.mifosplatform.provisioning.processrequest.domain.ProcessRequestDetails;
import org.mifosplatform.provisioning.processrequest.domain.ProcessRequestRepository;
import org.mifosplatform.provisioning.processrequest.service.ProcessRequestWriteplatformService;
import org.mifosplatform.provisioning.provisioning.api.ProvisioningApiConstants;
import org.mifosplatform.provisioning.provisioning.domain.ProvisioningRequest;
import org.mifosplatform.provisioning.provisioning.domain.ProvisioningRequestDetail;
import org.mifosplatform.provisioning.provisioning.domain.ProvisioningRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class EntitlementWritePlatformServiceImpl implements EntitlementWritePlatformService {

	private final ProcessRequestRepository entitlementRepository;
	private final ProcessRequestWriteplatformService processRequestWriteplatformService;
	private final ClientRepository clientRepository;
	private final SelfCareRepository selfCareRepository;
	private final BillingMessageTemplateRepository billingMessageTemplateRepository;
	private final BillingMessageRepository messageDataRepository;
	private final OfficeRepository officeRepository;
	private final ProvisioningRequestRepository provisioningRequestRepository;

	@Autowired
	public EntitlementWritePlatformServiceImpl(final ProcessRequestWriteplatformService processRequestWriteplatformService,
			final ProcessRequestRepository entitlementRepository, final ClientRepository clientRepository,
			final SelfCareRepository selfCareRepository,final BillingMessageTemplateRepository billingMessageTemplateRepository,
			final BillingMessageRepository messageDataRepository,final OfficeRepository officeRepository,
			final ProvisioningRequestRepository provisioningRequestRepository) {

		this.processRequestWriteplatformService = processRequestWriteplatformService;
		this.entitlementRepository = entitlementRepository;
		this.clientRepository = clientRepository;
		this.officeRepository = officeRepository;
		this.selfCareRepository = selfCareRepository;
		this.billingMessageTemplateRepository = billingMessageTemplateRepository;
		this.messageDataRepository = messageDataRepository;
		this.provisioningRequestRepository = provisioningRequestRepository;
	}

	/* In This create(JsonCommand command) method, 
	 * The provSystem,clientId,Authpin parameters are sends only for beenius integration. 
	 * For sending Beenius Generated Authpin to Client Email Address.
	 */
	
	@Override
	public CommandProcessingResult create(JsonCommand command) {
		
		String authPin = null;
		String message = null;	
		String provSystem = command.stringValueOfParameterNamed("provSystem");
		String requestType = command.stringValueOfParameterNamed("requestType");
		String  agentResourceId = null;
		String zebraSubscriberId = null;
	try {	
		
		if(command.hasParameter("agentResourceId")){
			
			agentResourceId = command.stringValueOfParameterNamed("agentResourceId");
		}
		
		if(command.hasParameter("zebraSubscriberId")){
			zebraSubscriberId = command.stringValueOfParameterNamed("zebraSubscriberId");
		}
		
		if(provSystem != null && requestType !=null && provSystem.equalsIgnoreCase(ProvisioningApiConstants.PROV_BEENIUS) 
				&& requestType.equalsIgnoreCase(ProvisioningApiConstants.REQUEST_CLIENT_ACTIVATION)){
			
			authPin = command.stringValueOfParameterNamed("authPin");
			Long clientId = command.longValueOfParameterNamed("clientId");	
			
			if(clientId !=null && authPin !=null && authPin.length()>0 && clientId>0){
				
				Client client = this.clientRepository.findOne(clientId);
				SelfCare selfcare = this.selfCareRepository.findOneByClientId(clientId);
				
				if(client == null){
					throw new ClientNotFoundException(clientId);
				}
				
				if(selfcare == null){
					throw new PlatformDataIntegrityException("client does not exist", "client not registered","clientId", "client is null ");
				}
				
				selfcare.setAuthPin(authPin);
				this.selfCareRepository.save(selfcare);
				String Name = client.getLastname();
				
				BillingMessageTemplate messageDetails=this.billingMessageTemplateRepository.findByTemplateDescription(BillingMessageTemplateConstants.MESSAGE_TEMPLATE_PROVISION_CREDENTIALS);
				if(messageDetails!=null){
				String subject=messageDetails.getSubject();
				String body=messageDetails.getBody();
				String header=messageDetails.getHeader()+","+"\n"+"\n";
				String footer=messageDetails.getFooter();
				
				header = header.replace("<PARAM1>", Name);
				body = body.replace("<PARAM2>", client.getAccountNumber());
				body = body.replace("<PARAM3>", authPin);
			
				BillingMessage billingMessage = new BillingMessage(header, body, footer, BillingMessageTemplateConstants.MESSAGE_TEMPLATE_EMAIL_FROM, client.getEmail(),
						subject, BillingMessageTemplateConstants.MESSAGE_TEMPLATE_STATUS, messageDetails, BillingMessageTemplateConstants.MESSAGE_TEMPLATE_MESSAGE_TYPE, null);
			
				this.messageDataRepository.save(billingMessage);
						
			}else{
				throw new BillingMessageTemplateNotFoundException(BillingMessageTemplateConstants.MESSAGE_TEMPLATE_PROVISION_CREDENTIALS);
			}
		 }
		}
		
		if(zebraSubscriberId != null && requestType !=null && requestType.equalsIgnoreCase(ProvisioningApiConstants.REQUEST_CLIENT_ACTIVATION) &&
				(!zebraSubscriberId.isEmpty())){
			
			Long clientId = command.longValueOfParameterNamed("clientId");	
			
			if(clientId !=null && zebraSubscriberId !=null && zebraSubscriberId.length()>0 && clientId>0){
				
				Client client = this.clientRepository.findOne(clientId);
				SelfCare selfcare = this.selfCareRepository.findOneByClientId(clientId);
				
				if(client == null){
					throw new ClientNotFoundException(clientId);
				}
				
				if(selfcare == null){
					throw new PlatformDataIntegrityException("client does not exist", "client not registered","clientId", "client is null ");
				}
				
				selfcare.setZebraSubscriberId(new Long(zebraSubscriberId));
				this.selfCareRepository.save(selfcare);
			  }

			}
		
		if(zebraSubscriberId != null && requestType !=null && requestType.equalsIgnoreCase(ProvisioningApiConstants.REQUEST_ACTIVATION) &&
				(!zebraSubscriberId.isEmpty())){
			
			Long clientId = command.longValueOfParameterNamed("clientId");	
			
			if(clientId !=null && zebraSubscriberId !=null && zebraSubscriberId.length()>0 && clientId>0){
				
				Client client = this.clientRepository.findOne(clientId);
				SelfCare selfcare = this.selfCareRepository.findOneByClientId(clientId);
				
				if(client == null){
					throw new ClientNotFoundException(clientId);
				}
				
				if(selfcare == null){
					throw new PlatformDataIntegrityException("client does not exist", "client not registered","clientId", "client is null ");
				}
				
				selfcare.setZebraSubscriberId(new Long(zebraSubscriberId));
				this.selfCareRepository.save(selfcare);
								
			}
			
		}
		
		ProvisioningRequest processRequest = this.provisioningRequestRepository.findOne(command.entityId());
		if(requestType !=null && requestType.equalsIgnoreCase(ProvisioningApiConstants.REQUEST_CREATE_AGENT)){
			
			if(agentResourceId !=null ){
				//List<ProcessRequestDetails> details = processRequest.getProcessRequestDetails();
				List<ProvisioningRequestDetail> details = processRequest.getProvisioningRequestDetail();
				/*for (ProvisioningRequestDetail processRequestDetails : details) {
					Long id = command.longValueOfParameterNamed("prdetailsId");
					if (processRequestDetails.getId().longValue() == id.longValue()) {
			    		JSONObject object = new JSONObject(processRequestDetails.getSentMessage());
			    		Long officeId = object.getLong("agentId");
			    		 Office office = this.officeRepository.findOne(officeId);
			    		 office.setExternalId(agentResourceId);
			    		 this.officeRepository.saveAndFlush(office);
			    		 break;
			    	 }
			     }*/
	    	}	
		}
		
		
		String receiveMessage = command.stringValueOfParameterNamed("receiveMessage");
		char status;
		if (receiveMessage.contains("failure :")) {
			status = 'F';
		} else {
			status = 'Y';
		}
			
		//List<ProcessRequestDetails> details = processRequest.getProcessRequestDetails();
		List<ProvisioningRequestDetail> details = processRequest.getProvisioningRequestDetail();

		for (ProvisioningRequestDetail processRequestDetails : details) {
			Long id = command.longValueOfParameterNamed("prdetailsId");
			if (processRequestDetails.getId().longValue() == id.longValue()) {
				processRequestDetails.updateStatus(command);

				/*	if(provSystem != null && requestType !=null && authPin !=null && provSystem.equalsIgnoreCase("Beenius") && requestType.equalsIgnoreCase("ACTIVATION") ){
					processRequestDetails.setReceiveMessage(processRequestDetails.getReceiveMessage() +
							", generated authpin=" + authPin + ", Email output=" + message);
				}else{
					processRequestDetails.setReceiveMessage(processRequestDetails.getReceiveMessage());
				}*/
				
			}
		}

		/*if (processRequest.getRequestType().equalsIgnoreCase("DEVICE_SWAP") && !checkProcessDetailsUpdated(details)) {
			status = 'F';
		}*/
		processRequest.setStatus(status);

		 this.provisioningRequestRepository.saveAndFlush(processRequest);
		this.processRequestWriteplatformService.notifyProcessingDetails(processRequest, status);
		
		return new CommandProcessingResult(processRequest.getId());
		
	}catch(Exception e){
		
		ProcessRequest processRequest = this.entitlementRepository.findOne(command.entityId());
		
        List<ProcessRequestDetails> details = processRequest.getProcessRequestDetails();

		 for (ProcessRequestDetails processRequestDetails : details) {
			Long id = command.longValueOfParameterNamed("prdetailsId");
			if (processRequestDetails.getId().longValue() == id.longValue()) {
			   String receiveMessage = command.stringValueOfParameterNamed("receiveMessage");	
		       processRequestDetails.setReceiveMessage(receiveMessage);
			}
		}
		 processRequest.setProcessStatus('F');
		 this.entitlementRepository.saveAndFlush(processRequest);
		return new CommandProcessingResult(Long.valueOf(-1L));
	}
}
	/*private boolean checkProcessDetailsUpdated(List<ProcessRequestDetails> details) {
		boolean flag = true;
		if (details.get(0).getReceiveMessage().contains("failure : Exce")) {
			flag = false;
		}
		return flag;
	}*/

}
