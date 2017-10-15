/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.clientservice.service;

import java.util.ArrayList;
import java.util.List;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.clientservice.domain.ClientService;
import org.mifosplatform.portfolio.clientservice.domain.ClientServiceRepository;
import org.mifosplatform.portfolio.clientservice.serialization.ClientServiceDataValidator;
import org.mifosplatform.portfolio.order.data.OrderStatusEnumaration;
import org.mifosplatform.portfolio.order.domain.Order;
import org.mifosplatform.portfolio.order.domain.OrderRepository;
import org.mifosplatform.portfolio.order.domain.StatusTypeEnum;
import org.mifosplatform.portfolio.order.domain.UserActionStatusTypeEnum;
import org.mifosplatform.portfolio.order.service.OrderWritePlatformService;
import org.mifosplatform.provisioning.preparerequest.service.PrepareRequestReadplatformService;
import org.mifosplatform.provisioning.provisioning.data.ServiceParameterData;
import org.mifosplatform.provisioning.provisioning.domain.ServiceParameters;
import org.mifosplatform.provisioning.provisioning.service.ProvisioningWritePlatformService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class ClientServiceWritePlatformServiceJpaRepositoryImpl implements ClientServiceWriteplatformService {

    private final static Logger logger = LoggerFactory.getLogger(ClientServiceWritePlatformServiceJpaRepositoryImpl.class);

    private final PlatformSecurityContext context;
    private final ClientServiceRepository clientServiceRepository;
    private final ClientServiceDataValidator fromApiJsonDeserializer;
    private final ClientServiceReadPlatformService clientServiceReadPlatformService;
    private final FromJsonHelper fromApiJsonHelper;
    private final OrderRepository orderRepository;
    private final ProvisioningWritePlatformService provisioningWritePlatformService;
    private final OrderWritePlatformService orderWritePlatformService;
    
    
   

    @Autowired
    public ClientServiceWritePlatformServiceJpaRepositoryImpl(final PlatformSecurityContext context,
            final ClientServiceRepository clientServiceRepository, final ClientServiceDataValidator fromApiJsonDeserializer, 
            final PrepareRequestReadplatformService prepareRequestReadplatformService,
            final ClientServiceReadPlatformService clientServiceReadPlatformService,
            final FromJsonHelper fromApiJsonHelper,final OrderRepository orderRepository,
            final ProvisioningWritePlatformService provisioningWritePlatformService, final OrderWritePlatformService orderWritePlatformService) {
    	
        this.context = context;
        this.fromApiJsonDeserializer = fromApiJsonDeserializer;
        this.clientServiceRepository = clientServiceRepository;
        this.clientServiceReadPlatformService = clientServiceReadPlatformService;
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.orderRepository = orderRepository;
        this.provisioningWritePlatformService = provisioningWritePlatformService;
        this.orderWritePlatformService = orderWritePlatformService;
    }
    
    @Transactional
    @Override
    public CommandProcessingResult createClient(final JsonCommand command) {

        try {
            this.context.authenticatedUser();
            this.fromApiJsonDeserializer.validateForCreate(command.json());
            
            final Long clientId = command.longValueOfParameterNamed("clientId");
            final Long serviceId = command.longValueOfParameterNamed("serviceId");
            final String status = "NEW";
            ClientService clientService = ClientService.createNew(clientId, serviceId, status);
            //details
            final JsonArray detailsArray = command.arrayOfParameterNamed("clientServiceDetails").getAsJsonArray();
            clientService = this.detailFun(detailsArray,clientService,clientId);
            
            this.clientServiceRepository.saveAndFlush(clientService);
            
            return new CommandProcessingResultBuilder() 
                    .withCommandId(command.commandId()) 
                    .withOfficeId(null) 
                    .withClientId(clientService.getId())
                    .withResourceIdAsString(clientService.getId().toString())
                    .withGroupId(null) 
                    .withEntityId(clientService.getId()) 
                    .build();
        } catch (DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }
    
    
    @Override
	public CommandProcessingResult createClientServiceActivation(Long clientServiceId, JsonCommand command) {
		try{
			Long clientId = command.longValueOfParameterNamed("clientId");
			List<Order> orders = new ArrayList<Order>();
			if(this.isThisClientServiceHasProvisioning(clientServiceId)){
				orders = this.orderRepository.findOrdersByClientService(clientServiceId, clientId);
				 this.fromApiJsonDeserializer.validateForOrders(orders);
				for(Order order:orders){
					if(order.getStatus().toString().equalsIgnoreCase(OrderStatusEnumaration.OrderStatusType(StatusTypeEnum.PENDING).getId().toString()) ||
							order.getStatus().toString().equalsIgnoreCase(OrderStatusEnumaration.OrderStatusType(StatusTypeEnum.NEW).getId().toString())){
						JsonObject provisioningObject = new JsonObject();
						provisioningObject.addProperty("requestType", UserActionStatusTypeEnum.ACTIVATION.toString());
						JsonCommand com = new JsonCommand(null, provisioningObject.toString(),provisioningObject, fromApiJsonHelper, null, null, null, null, null, null, null, null, null, null, null,null);
						order = this.handleOrderStatusifNew(order);
						this.provisioningWritePlatformService.createProvisioningRequest(order, com,true);
					}
				}
				
			}
			
			this.updateClientServiceStatus(clientServiceId);
			return new CommandProcessingResultBuilder().withEntityId(clientServiceId).build();
	
		}catch (DataIntegrityViolationException dve) {
            return CommandProcessingResult.empty();
        }
	}
	
    private Order handleOrderStatusifNew(Order order){
    	if(order.getStatus().toString().equalsIgnoreCase(OrderStatusEnumaration.OrderStatusType(StatusTypeEnum.NEW).getId().toString())){
    		order.setStatus(OrderStatusEnumaration.OrderStatusType(StatusTypeEnum.PENDING).getId());
    		this.orderRepository.saveAndFlush(order);
    	}
    	return order;
    }
    
	@Override
	public CommandProcessingResult suspendClientService(Long clientServiceId, JsonCommand command) {
		Long clientId = command.longValueOfParameterNamed("clientId");
		List<Order> orders = this.orderRepository.findOrdersByClientService(clientServiceId, clientId);
		for(Order order:orders){
			if(order.getStatus().toString().equalsIgnoreCase(StatusTypeEnum.ACTIVE.getValue().toString())){
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("suspensionDate", command.stringValueOfParameterNamed("suspensionDate"));
				jsonObject.addProperty("dateFormat", command.stringValueOfParameterNamed("dateFormat"));
				jsonObject.addProperty("locale", command.stringValueOfParameterNamed("locale"));
				jsonObject.addProperty("suspensionReason", "Due to client service suspention");
				
				JsonCommand com = new JsonCommand(null, jsonObject.toString(),jsonObject, fromApiJsonHelper, null, null, null, null, null, null, null, null, null, null, null,null);
				this.orderWritePlatformService.orderSuspention(com, order.getId());
			}
		}
		this.updateClientServiceStatus(clientServiceId);
		return new CommandProcessingResultBuilder().withEntityId(clientServiceId).build();
	}
    

	@Override
	public CommandProcessingResult reactiveClientService(Long clientServiceId, JsonCommand command) {
		Long clientId = command.longValueOfParameterNamed("clientId");
		List<Order> orders = this.orderRepository.findOrdersByClientService(clientServiceId, clientId);
		for(Order order:orders){
			if(order.getStatus().toString().equalsIgnoreCase(StatusTypeEnum.SUSPENDED.getValue().toString())){
				this.orderWritePlatformService.reactiveOrder(null, order.getId());
			}
		}
		this.updateClientServiceStatus(clientServiceId);
		return new CommandProcessingResultBuilder().withEntityId(clientServiceId).build();
	}
	
	@Override
	public CommandProcessingResult terminateClientService(Long clientServiceId, JsonCommand command) {
		Long clientId = command.longValueOfParameterNamed("clientId");
		List<Order> orders = this.orderRepository.findOrdersByClientService(clientServiceId, clientId);
		for(Order order:orders){
			this.orderWritePlatformService.orderTermination(null, order.getId());
		}
		this.updateClientServiceStatus(clientServiceId);
		return new CommandProcessingResultBuilder().withEntityId(clientServiceId).build();
	}
	
	
    private ClientService detailFun(JsonArray detailsArray, ClientService clientService,Long clientId) {
    	String[]  details = new String[detailsArray.size()];
    	ServiceParameters serviceParameters = null;
		if(detailsArray.size() > 0){
			for(int i = 0; i < detailsArray.size(); i++){
				details[i] = detailsArray.get(i).toString();
		}
	
		for (final String detail : details) {
			final JsonElement element = this.fromApiJsonHelper.parse(detail);
			final String parameterId = this.fromApiJsonHelper.extractStringNamed("parameterId", element);
			final String parameterValue = this.fromApiJsonHelper.extractStringNamed("parameterValue", element);
			final String status = this.fromApiJsonHelper.extractStringNamed("status", element);
			serviceParameters = new ServiceParameters(clientId, parameterId, parameterValue, status);
			clientService.addDetails(serviceParameters);
			
		}	 
	}	
	return clientService;
    }

	private void logAsErrorUnexpectedDataIntegrityException(final DataIntegrityViolationException dve) {
        logger.error(dve.getMessage(), dve);
    }
    
    /*
     * Guaranteed to throw an exception no matter what the data integrity issue
     * is.
     */
    private void handleDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {

    	final Throwable realCause = dve.getMostSpecificCause();
        if (realCause.getMessage().contains("external_id")) {

            final String externalId = command.stringValueOfParameterNamed("externalId");
            throw new PlatformDataIntegrityException("error.msg.client.duplicate.externalId", "Client with externalId `" + externalId
                    + "` already exists", "externalId", externalId);
            
        } else if (realCause.getMessage().contains("account_no_UNIQUE")) {
            final String accountNo = command.stringValueOfParameterNamed("accountNo");
            throw new PlatformDataIntegrityException("error.msg.client.duplicate.accountNo", "Client with accountNo `" + accountNo
                    + "` already exists", "accountNo", accountNo);
        }else if (realCause.getMessage().contains("username")) {
            final String username = command.stringValueOfParameterNamed("username");
            throw new PlatformDataIntegrityException("error.msg.client.duplicate.username", "Client with username" + username
                    + "` already exists", "username", username);
        }else if (realCause.getMessage().contains("email_key")) {
            final String email = command.stringValueOfParameterNamed("email");
            throw new PlatformDataIntegrityException("error.msg.client.duplicate.email", "Client with email `" + email
                    + "` already exists", "email", email);
            
        }else if (realCause.getMessage().contains("login_key")) {
            final String login = command.stringValueOfParameterNamed("login");
            throw new PlatformDataIntegrityException("error.msg.client.duplicate.login", "Client with login `" + login
                    + "` already exists", "login", login);
        }

        logAsErrorUnexpectedDataIntegrityException(dve);
        throw new PlatformDataIntegrityException("error.msg.client.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource.");
    }

	
	
	
	private void updateClientServiceStatus(Long clientServiceId) {
		ClientService clientService = this.clientServiceRepository.findOne(clientServiceId);
		clientService.setStatus("PROCESSING");
		this.clientServiceRepository.saveAndFlush(clientService);
	}

	private boolean isThisClientServiceHasProvisioning(Long clientServiceId){
		boolean isThisClientServiceHasProvisioning = false;
		List<ServiceParameterData> serviceParameterDatas = this.clientServiceReadPlatformService.retriveClientServiceDetails(clientServiceId);
		for(ServiceParameterData serviceParameterData:serviceParameterDatas){
			if(!"None".equalsIgnoreCase(serviceParameterData.getType())){
				isThisClientServiceHasProvisioning = true;
			}break;
		}
		return isThisClientServiceHasProvisioning;
	}


}