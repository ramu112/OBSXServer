package org.mifosplatform.provisioning.provisioning.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.mifosplatform.infrastructure.codes.data.CodeValueData;
import org.mifosplatform.infrastructure.codes.service.CodeValueReadPlatformService;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.logistics.itemdetails.domain.ItemDetails;
import org.mifosplatform.logistics.itemdetails.domain.ItemDetailsRepository;
import org.mifosplatform.logistics.itemdetails.exception.ActivePlansFoundException;
import org.mifosplatform.logistics.onetimesale.data.OneTimeSaleData;
import org.mifosplatform.logistics.onetimesale.service.OneTimeSaleReadPlatformService;
import org.mifosplatform.organisation.hardwareplanmapping.data.HardwarePlanData;
import org.mifosplatform.organisation.hardwareplanmapping.service.HardwarePlanReadPlatformService;
import org.mifosplatform.organisation.ippool.domain.IpPoolManagementDetail;
import org.mifosplatform.organisation.ippool.domain.IpPoolManagementJpaRepository;
import org.mifosplatform.organisation.ippool.exception.IpNotAvailableException;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.mifosplatform.organisation.mcodevalues.service.MCodeReadPlatformService;
import org.mifosplatform.organisation.office.domain.Office;
import org.mifosplatform.organisation.office.domain.OfficeAdditionalInfo;
import org.mifosplatform.organisation.office.domain.OfficeRepository;
import org.mifosplatform.portfolio.allocation.domain.HardwareAssociationRepository;
import org.mifosplatform.portfolio.association.domain.HardwareAssociation;
import org.mifosplatform.portfolio.association.exception.PairingNotExistException;
import org.mifosplatform.portfolio.clientservice.domain.ClientService;
import org.mifosplatform.portfolio.clientservice.domain.ClientServiceRepository;
import org.mifosplatform.portfolio.order.domain.Order;
import org.mifosplatform.portfolio.order.domain.OrderLine;
import org.mifosplatform.portfolio.order.domain.OrderRepository;
import org.mifosplatform.portfolio.order.domain.UserActionStatusTypeEnum;
import org.mifosplatform.portfolio.order.service.OrderReadPlatformService;
import org.mifosplatform.portfolio.plan.domain.Plan;
import org.mifosplatform.portfolio.plan.domain.PlanRepository;
import org.mifosplatform.portfolio.planmapping.domain.PlanMapping;
import org.mifosplatform.portfolio.planmapping.domain.PlanMappingRepository;
import org.mifosplatform.portfolio.planmapping.execption.PlanMappingNotExist;
import org.mifosplatform.portfolio.service.data.ServiceDetailData;
import org.mifosplatform.portfolio.service.domain.ServiceMaster;
import org.mifosplatform.portfolio.service.domain.ServiceMasterRepository;
import org.mifosplatform.portfolio.service.service.ServiceMasterReadPlatformService;
import org.mifosplatform.provisioning.preparerequest.data.PrepareRequestData;
import org.mifosplatform.provisioning.preparerequest.service.PrepareRequestReadplatformService;
import org.mifosplatform.provisioning.processrequest.domain.ProcessRequest;
import org.mifosplatform.provisioning.processrequest.domain.ProcessRequestDetails;
import org.mifosplatform.provisioning.processrequest.domain.ProcessRequestRepository;
import org.mifosplatform.provisioning.processrequest.service.ProcessRequestReadplatformService;
import org.mifosplatform.provisioning.processrequest.service.ProcessRequestWriteplatformService;
import org.mifosplatform.provisioning.provisioning.api.ProvisioningApiConstants;
import org.mifosplatform.provisioning.provisioning.data.ProvisionAdapter;
import org.mifosplatform.provisioning.provisioning.domain.ProvisioningCommand;
import org.mifosplatform.provisioning.provisioning.domain.ProvisioningCommandParameters;
import org.mifosplatform.provisioning.provisioning.domain.ProvisioningCommandRepository;
import org.mifosplatform.provisioning.provisioning.domain.ProvisioningRequest;
import org.mifosplatform.provisioning.provisioning.domain.ProvisioningRequestDetail;
import org.mifosplatform.provisioning.provisioning.domain.ProvisioningRequestRepository;
import org.mifosplatform.provisioning.provisioning.domain.ServiceParameters;
import org.mifosplatform.provisioning.provisioning.domain.ServiceParametersRepository;
import org.mifosplatform.provisioning.provisioning.exceptions.ProvisioningRequestNotFoundException;
import org.mifosplatform.provisioning.provisioning.serialization.ProvisioningCommandFromApiJsonDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import net.sf.json.JSONObject;

@Service
public class ProvisioningWritePlatformServiceImpl implements ProvisioningWritePlatformService {

	private final FromJsonHelper fromJsonHelper;
	private final PlatformSecurityContext context;
	private final OrderRepository orderRepository;
	private final FromJsonHelper fromApiJsonHelper;
	private final ServiceMasterRepository serviceMasterRepository;
	private final ProcessRequestRepository processRequestRepository;
	private final OrderReadPlatformService orderReadPlatformService;
	private final HardwareAssociationRepository associationRepository;
	private final ServiceParametersRepository serviceParametersRepository;
	private final ProvisioningCommandRepository provisioningCommandRepository;
	private final IpPoolManagementJpaRepository ipPoolManagementJpaRepository;
	private final ProvisionHelper provisionHelper;
	private final PlanRepository planRepository;
	private final OfficeRepository officeRepository;
	private final PlanMappingRepository planMappingRepository;
	private final PrepareRequestReadplatformService prepareRequestReadplatformService;
	private final ItemDetailsRepository inventoryItemDetailsRepository;
	private final ProvisioningCommandFromApiJsonDeserializer fromApiJsonDeserializer;
	private final ProcessRequestReadplatformService processRequestReadplatformService;
	private final ProcessRequestWriteplatformService processRequestWriteplatformService;
	private final ProvisioningRequestRepository provisioningRequestRepository;
	private final ClientServiceRepository clientServiceRepository;
	private final MCodeReadPlatformService mCodeReadPlatformService;
	private final ServiceMasterReadPlatformService serviceMasterReadPlatformService;
	private final CodeValueReadPlatformService codeValueReadPlatformService;
	private final HardwarePlanReadPlatformService hardwarePlanReadPlatformService;
	private final OneTimeSaleReadPlatformService oneTimeSaleReadPlatformService;

	@Autowired
	public ProvisioningWritePlatformServiceImpl(final PlatformSecurityContext context,final ItemDetailsRepository inventoryItemDetailsRepository,
			final ProvisioningCommandFromApiJsonDeserializer fromApiJsonDeserializer,final FromJsonHelper fromApiJsonHelper,
			final OrderReadPlatformService orderReadPlatformService,final ProvisioningCommandRepository provisioningCommandRepository,
			final ServiceParametersRepository parametersRepository,final ProcessRequestRepository processRequestRepository,
			final OrderRepository orderRepository,final FromJsonHelper fromJsonHelper,final HardwareAssociationRepository associationRepository,
			final ServiceMasterRepository serviceMasterRepository,final ProvisionHelper provisionHelper,final OfficeRepository officeRepository,
			final ProcessRequestReadplatformService processRequestReadplatformService,final IpPoolManagementJpaRepository ipPoolManagementJpaRepository,
			final ProcessRequestWriteplatformService processRequestWriteplatformService,final PlanRepository planRepository,
			final PrepareRequestReadplatformService prepareRequestReadplatformService,final PlanMappingRepository planMappingRepository,
			final ProvisioningRequestRepository provisioningRequestRepository, final ClientServiceRepository clientServiceRepository,
			final MCodeReadPlatformService mCodeReadPlatformService, final ServiceMasterReadPlatformService serviceMasterReadPlatformService,
			final CodeValueReadPlatformService codeValueReadPlatformService, final HardwarePlanReadPlatformService hardwarePlanReadPlatformService,
			final OneTimeSaleReadPlatformService oneTimeSaleReadPlatformService) {

		this.context = context;
		this.fromJsonHelper = fromJsonHelper;
		this.orderRepository = orderRepository;
		this.fromApiJsonHelper = fromApiJsonHelper;
		this.associationRepository = associationRepository;
		this.planRepository=planRepository;
		this.provisionHelper=provisionHelper;
		this.fromApiJsonDeserializer = fromApiJsonDeserializer;
		this.serviceMasterRepository = serviceMasterRepository;
		this.serviceParametersRepository = parametersRepository;
		this.processRequestRepository = processRequestRepository;
		this.planMappingRepository = planMappingRepository;
		this.prepareRequestReadplatformService=prepareRequestReadplatformService;
		this.officeRepository = officeRepository;
		this.orderReadPlatformService = orderReadPlatformService;
		this.provisioningCommandRepository = provisioningCommandRepository;
		this.ipPoolManagementJpaRepository = ipPoolManagementJpaRepository;
		this.inventoryItemDetailsRepository = inventoryItemDetailsRepository;
		this.processRequestReadplatformService = processRequestReadplatformService;
		this.processRequestWriteplatformService = processRequestWriteplatformService;
		this.provisioningRequestRepository = provisioningRequestRepository;
		this.clientServiceRepository = clientServiceRepository;
		this.mCodeReadPlatformService = mCodeReadPlatformService;
		this.serviceMasterReadPlatformService = serviceMasterReadPlatformService;
		this.codeValueReadPlatformService = codeValueReadPlatformService;
		this.hardwarePlanReadPlatformService = hardwarePlanReadPlatformService;
		this.oneTimeSaleReadPlatformService = oneTimeSaleReadPlatformService;

	}

	@Override
	public CommandProcessingResult createProvisioning(JsonCommand command) {

		try {
			String paramDefault = null;
			this.context.authenticatedUser();
			this.fromApiJsonDeserializer.validateForProvisioning(command.json());
			final ProvisioningCommand provisioningCommand = ProvisioningCommand.from(command);
			final JsonElement element = fromApiJsonHelper.parse(command.json());
			final JsonArray commandArray = fromApiJsonHelper.extractJsonArrayNamed("commandParameters", element);
			
			if (commandArray != null) {
				for (JsonElement jsonelement : commandArray) {
					final String commandParam = fromApiJsonHelper.extractStringNamed("commandParam", jsonelement);
					final String paramType = fromApiJsonHelper.extractStringNamed("paramType", jsonelement);

					if (fromApiJsonHelper.parameterExists("paramDefault", jsonelement)) {
						paramDefault = fromApiJsonHelper.extractStringNamed("paramDefault", jsonelement);
					}
					ProvisioningCommandParameters data = new ProvisioningCommandParameters(commandParam, paramType, paramDefault);
					provisioningCommand.addCommandParameters(data);
				}
			}

			this.provisioningCommandRepository.save(provisioningCommand);

			return new CommandProcessingResult(provisioningCommand.getId());

		} catch (DataIntegrityViolationException dve) {
			handleCodeDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}

	}

	private void handleCodeDataIntegrityIssues(JsonCommand command,
			DataIntegrityViolationException dve) {
	}

	@Override
	public CommandProcessingResult updateProvisioning(final JsonCommand command) {

		try {
			String paramDefault = null;
			this.context.authenticatedUser();
			this.fromApiJsonDeserializer.validateForProvisioning(command.json());
			final ProvisioningCommand provisioningCommand = this.provisioningCommandRepository.findOne(command.entityId());
			provisioningCommand.getCommandparameters().clear();
			provisioningCommand.updateProvisioning(command);

			final JsonElement element = fromApiJsonHelper.parse(command.json());
			final JsonArray commandArray = fromApiJsonHelper.extractJsonArrayNamed("commandParameters", element);
			
			if (commandArray != null) {
				for (JsonElement jsonelement : commandArray) {
					String commandParam = fromApiJsonHelper.extractStringNamed("commandParam", jsonelement);
					String paramType = fromApiJsonHelper.extractStringNamed("paramType", jsonelement);

					if (fromApiJsonHelper.parameterExists("paramDefault", jsonelement)) {
						paramDefault = fromApiJsonHelper.extractStringNamed("paramDefault", jsonelement);
					}
					ProvisioningCommandParameters data = new ProvisioningCommandParameters(commandParam, paramType, paramDefault);
					provisioningCommand.addCommandParameters(data);
				}
			}
			
			this.provisioningCommandRepository.save(provisioningCommand);
			
			return new CommandProcessingResult(provisioningCommand.getId());
			
		} catch (DataIntegrityViolationException dve) {
			handleCodeDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
	}

	@Override
	public CommandProcessingResult deleteProvisioningSystem(JsonCommand command) {
		try {
			this.context.authenticatedUser();
			final ProvisioningCommand provisioningCommand = this.provisioningCommandRepository.findOne(command.entityId());

			if (provisioningCommand.getIsDeleted() != 'Y') {
				provisioningCommand.setIsDeleted('Y');
			}

			this.provisioningCommandRepository.save(provisioningCommand);

			return new CommandProcessingResult(provisioningCommand.getId());

		} catch (DataIntegrityViolationException dve) {
			handleCodeDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
	}

	@Transactional
	@Override
	public CommandProcessingResult createNewProvisioningSystem(final JsonCommand command, final Long entityId) {

		try {
			
			this.context.authenticatedUser();
			this.fromApiJsonDeserializer.validateForAddProvisioning(command.json());
			final Long orderId = command.longValueOfParameterNamed("orderId");
			final Long clientId = command.longValueOfParameterNamed("clientId");
			final String planName = command.stringValueOfParameterNamed("planName");
			final String macId = command.stringValueOfParameterNamed("macId");
			final String ipType = command.stringValueOfParameterNamed("ipType");
			final String iprange = command.stringValueOfParameterNamed("ipRange");
			final Long subnet = command.longValueOfParameterNamed("subnet");
			
			final ItemDetails inventoryItemDetails = this.inventoryItemDetailsRepository.getInventoryItemDetailBySerialNum(macId);
			final HardwareAssociation hardwareAssociation = this.associationRepository.findOneByOrderId(orderId);
			
			if (hardwareAssociation == null || inventoryItemDetails == null) {
				throw new PairingNotExistException(orderId);
			}

			final JsonElement element = fromJsonHelper.parse(command.json());
			JSONObject jsonData=this.provisionHelper.provisionAssemblerForm(element,clientId,macId,planName,orderId,iprange,ipType,subnet);
			
			ProcessRequest processRequest = new ProcessRequest(Long.valueOf(0), clientId, orderId,
					ProvisioningApiConstants.PROV_PACKETSPAN, UserActionStatusTypeEnum.ACTIVATION.toString(), 'N', 'N');
			final Order order = this.orderRepository.findOne(orderId);
			List<OrderLine> orderLines = order.getServices();
			
			for (OrderLine orderLine : orderLines) {

				ServiceMaster service = this.serviceMasterRepository.findOne(orderLine.getServiceId());
				jsonData.put(ProvisioningApiConstants.PROV_DATA_SERVICETYPE, service.getServiceType());
				ProcessRequestDetails processRequestDetails = new ProcessRequestDetails(orderLine.getId(), orderLine.getServiceId(),jsonData.toString(), 
						"Recieved", inventoryItemDetails.getProvisioningSerialNumber(),order.getStartDate(), order.getEndDate(), null, null,'N',
						UserActionStatusTypeEnum.ACTIVATION.toString(), service.getServiceType());
				
				processRequest.add(processRequestDetails);
			}
			
			this.processRequestRepository.saveAndFlush(processRequest);
			return new CommandProcessingResult(Long.valueOf(processRequest.getId()), clientId);
			
		} catch (DataIntegrityViolationException dve) {
			handleCodeDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		} catch (net.sf.json.JSONException e) {
			return new CommandProcessingResult(Long.valueOf(-1));
		}
	}

	@Transactional
	@Override
	public CommandProcessingResult updateProvisioningDetails(final Long entityId) {

		try {
			this.context.authenticatedUser();
			final ProcessRequest processRequest = this.processRequestRepository.findOne(entityId);
		
			if (processRequest != null) {
				processRequest.update();
				this.processRequestRepository.saveAndFlush(processRequest);
			}
			return new CommandProcessingResult(entityId, processRequest.getClientId());
			
		} catch (DataIntegrityViolationException dve) {
			handleCodeDataIntegrityIssues(null, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
	}

	
	@Override
	public void updateHardwareDetails(final Long clientId,final String serialNumber,final String oldSerialnumber,final String provSerilaNum,final String oldHardware) {
		
		final Long activeorders = this.orderReadPlatformService.retrieveClientActiveOrderDetails(clientId, oldSerialnumber,null);
		
		if (activeorders != 0) {
			throw new ActivePlansFoundException(oldSerialnumber);
		}
		// Update in Association table if Exist
		final List<HardwareAssociation> hardwareAssociations = this.associationRepository.findOneByserialNo(oldSerialnumber);
		
		if (!hardwareAssociations.isEmpty()) {
			for (HardwareAssociation hardwareAssociation : hardwareAssociations) {
				hardwareAssociation.updateserailNum(serialNumber);
				this.associationRepository.saveAndFlush(hardwareAssociation);
			}
		}
		// Update ProcessRequest
		final Long ProcessReqId = this.processRequestReadplatformService.retrievelatestReqId(clientId, oldHardware);
		
		if (ProcessReqId != null && !ProcessReqId.equals(Long.valueOf(0))) {
			ProcessRequest processRequest = this.processRequestRepository.findOne(ProcessReqId);
			List<ProcessRequestDetails> processRequestDetails = processRequest.getProcessRequestDetails();
			for (ProcessRequestDetails details : processRequestDetails) {
				details.update(provSerilaNum);
			}
			this.processRequestRepository.saveAndFlush(processRequest);
		}
	}

	@Override
	public CommandProcessingResult postOrderDetailsForProvisioning(final Order order,final String planName,final String requestType, 
			final Long prepareId,final String groupname,final String serialNo,final Long orderId,final String provisioningSys,Long addonId) {


	//try {
		Long commandProcessId=null;
		String serialNumber = null;
		HardwareAssociation hardwareAssociation = this.associationRepository.findOneByOrderId(order.getId());
		Plan plan=this.planRepository.findOne(order.getPlanId());
		
		PlanMapping planMapping= this.planMappingRepository.findOneByPlanId(order.getPlanId());
		
		
		
		if (planMapping == null && plan.getProvisionSystem().equalsIgnoreCase("None")) {
			throw new PlanMappingNotExist(plan.getPlanCode());
		}
		
		if (hardwareAssociation == null && plan.isHardwareReq() == 'Y') {
			throw new PairingNotExistException(order.getId());
		}else if (hardwareAssociation != null) {
			serialNumber = hardwareAssociation.getSerialNo();
		}
		List<ServiceParameters> parameters = this.serviceParametersRepository.findDataByOrderId(orderId);
			
		if (!parameters.isEmpty()) {	
			ItemDetails inventoryItemDetails =null;	
			if("ALLOT".equalsIgnoreCase(hardwareAssociation.getAllocationType())){	
				inventoryItemDetails = this.inventoryItemDetailsRepository.getInventoryItemDetailBySerialNum(serialNumber);	 
				if (inventoryItemDetails == null) { 
					throw new PairingNotExistException(order.getId());		 
				}	
			}

		   ProcessRequest processRequest = new ProcessRequest(prepareId, order.getClientId(), order.getId(),
				   ProvisioningApiConstants.PROV_PACKETSPAN, requestType, 'N', 'N');
		  List<OrderLine> orderLines = order.getServices();
		  JSONObject jsonData=this.provisionHelper.buildJsonForOrderProvision(order.getClientId(),planName,requestType,
				  groupname,serialNo,orderId, inventoryItemDetails.getSerialNumber(),order.getId(),parameters);

		  for (OrderLine orderLine : orderLines) {
			  
			  ServiceMaster service = this.serviceMasterRepository.findOne(orderLine.getServiceId());
			  jsonData.put(ProvisioningApiConstants.PROV_DATA_SERVICETYPE, service.getServiceType());
			  ProcessRequestDetails processRequestDetails = new ProcessRequestDetails(orderLine.getId(), orderLine.getServiceId(),
					  jsonData.toString(), "Recieved", inventoryItemDetails.getProvisioningSerialNumber(),order.getStartDate(),
					       order.getEndDate(), null, null, 'N', requestType, service.getServiceType());
			  processRequest.add(processRequestDetails);	
		  }
		  this.processRequestRepository.save(processRequest);
		  commandProcessId=processRequest.getId();


			}else{
				PrepareRequestData prepareRequestData=new  PrepareRequestData(Long.valueOf(0),order.getClientId(), orderId, requestType, serialNumber,
						 null, provisioningSys, planName, String.valueOf(plan.isHardwareReq()),addonId);

			CommandProcessingResult commandProcessingResult =this.prepareRequestReadplatformService.processingClientDetails(prepareRequestData);
			commandProcessId=commandProcessingResult.resourceId();
				
				
		}
			return new CommandProcessingResult(commandProcessId);
		//} catch (DataIntegrityViolationException dve) {
		//	handleCodeDataIntegrityIssues(null, dve);
			//return new CommandProcessingResult(Long.valueOf(-1));
	//	} 

	}

	@Transactional
	@Override
	public CommandProcessingResult confirmProvisioningDetails(Long entityId) {

		try {
			this.context.authenticatedUser();
			
			//final ProcessRequest processRequest = this.processRequestRepository.findOne(entityId);

			final ProvisioningRequest processRequest = this.provisioningRequestRepository.findOne(entityId);
			if (processRequest == null) {
				throw new ProvisioningRequestNotFoundException(entityId);
			}
			processRequest.setStatus('C');
			//processRequest.setNotify();
			//final List<ProcessRequestDetails> details = processRequest.getProcessRequestDetails();
			final List<ProvisioningRequestDetail> details = processRequest.getProvisioningRequestDetail();
			
			
			for (ProvisioningRequestDetail processRequestDetails : details) {
				//processRequestDetails.setRecievedMessage("Manually Confirmed");
				processRequestDetails.setResponseMessage("Manually Confirmed");
			}

			this.provisioningRequestRepository.save(processRequest);
			this.processRequestWriteplatformService.notifyProcessingDetails(processRequest, 'Y');

			return new CommandProcessingResult(entityId, processRequest.getClientId());

		} catch (DataIntegrityViolationException dve) {
			handleCodeDataIntegrityIssues(null, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
	}

	@Transactional
	@Override
	public CommandProcessingResult updateIpDetails(final Long orderId, final JsonCommand command) {
		
		IpPoolManagementDetail ipPoolManagement = null;
		Long clientId = null;
		try {
			this.context.authenticatedUser();
			// this.fromApiJsonDeserializer.validateForUpDateIpDetails(command.json());
			clientId = command.longValueOfParameterNamed("clientId");
			final JsonElement element = fromJsonHelper.parse(command.json());
			
			final String[] removeIpsArray = fromApiJsonHelper.extractArrayNamed("removeIps", element);
			final String[] newIpsArray = fromApiJsonHelper.extractArrayNamed("newIps", element);
			// find duplicate ips in String Array
			List<String> tmpList = Arrays.asList(newIpsArray);
			Set<String> uniqueList = new HashSet<String>(tmpList);
			if (uniqueList.size() < tmpList.size()) {
				throw new IpNotAvailableException(orderId);
			}

			final JSONArray array = new JSONArray();
			List<ServiceParameters> parameters = this.serviceParametersRepository.findDataByOrderId(orderId);
			
			if(parameters != null){
			for (ServiceParameters serviceData : parameters) {
				
				if (ProvisioningApiConstants.PROV_DATA_IPADDRESS.equalsIgnoreCase(serviceData.getParameterName())) {

					for (String newIp : newIpsArray) {
						array.put(newIp);
					}
					serviceData.setParameterValue(array.toString());

					if (removeIpsArray.length >= 1) {
						for (int i = 0; i < removeIpsArray.length; i++) {
							ipPoolManagement = this.ipPoolManagementJpaRepository.findByIpAddress(removeIpsArray[i]);
							if (ipPoolManagement == null) {
								throw new IpNotAvailableException(removeIpsArray[i]);
							}
							ipPoolManagement.setStatus('F');
							ipPoolManagement.setClientId(null);
							ipPoolManagement.setSubnet(null);
							this.ipPoolManagementJpaRepository.save(ipPoolManagement);
						}

					}

					if (newIpsArray.length >= 1) {

						for (int i = 0; i < newIpsArray.length; i++) {
							ipPoolManagement = this.ipPoolManagementJpaRepository.findByIpAddress(newIpsArray[i]);
							if (ipPoolManagement == null) {
								throw new IpNotAvailableException(newIpsArray[i]);
							}
							ipPoolManagement.setStatus('A');
							ipPoolManagement.setClientId(clientId);
							// ipPoolManagement.setSubnet(null);
							this.ipPoolManagementJpaRepository.save(ipPoolManagement);
						}
					}
					this.serviceParametersRepository.save(serviceData);
				}
			}
			}else{
				
			}
		} catch (DataIntegrityViolationException dve) {
			handleCodeDataIntegrityIssues(null, dve);
		}
	
		return new CommandProcessingResult(orderId, clientId);

	}

	/**
	 * this Method <code> runAdapterCommands </code> used for
	 * run System commands.
	 * 
	 * @author ashokreddy
	 */
	@Override
	public String runAdapterCommands(String apiRequestBodyAsJson) {

		try {
			org.json.JSONObject object = new org.json.JSONObject(apiRequestBodyAsJson);
			String command = object.getString("command");
			return ProvisioningWritePlatformServiceImpl.runScript(command);
			
		} catch (JSONException e) {
			return e.getLocalizedMessage();
		}
	}

	public static String runScript(String command) {

		try {
			System.out.println("Processing the command ...");
			Process process = Runtime.getRuntime().exec(command);
			process.waitFor();
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

			String s, output = "";
			while ((s = br.readLine()) != null) {
				System.out.println(s);
				output = output + s + ",";
			}

			while ((s = stdError.readLine()) != null) {
				System.out.println(s);
				output = output + s + ",";
			}

			System.out.println("Command Processing Completed ...");

			return output;

		} catch (IOException e) {
			e.printStackTrace();
			return e.getLocalizedMessage();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return e.getLocalizedMessage();
		}
	}

	@Override
	public List<ProvisionAdapter> gettingLogInformation(String apiRequestBodyAsJson) {
		try {
			org.json.JSONObject object = new org.json.JSONObject(apiRequestBodyAsJson);
			Long days = object.getLong("days");
			String dateFormat = object.getString("dateFormat");
			final String startDate = object.getString("startDate");

			DateFormat dateformater = new SimpleDateFormat(dateFormat);
			String todayDate = dateformater.format(DateUtils.getDateOfTenant());

			String logFileLocation = object.getString("logFileLocation");
			String datearray[] = calculateDate(days, startDate, dateFormat);
			List<ProvisionAdapter> logLocation = new ArrayList<ProvisionAdapter>();

			for (String date : datearray) {
				if (todayDate.equalsIgnoreCase(date)) {
					logLocation.add(new ProvisionAdapter(date, logFileLocation));
				} else {
					String filedata = logFileLocation + "." + date;
					File f = new File(filedata);
					if (f.exists() && !f.isDirectory()) {
						logLocation.add(new ProvisionAdapter(date, filedata));
					}
				}

			}
			
			return logLocation;

		} catch (JSONException e) {
			return null;
		} catch (ParseException e) {
			return null;
		}

	}

	private String[] calculateDate(final Long days,final String startDate,final String dateFormat1) throws ParseException {

		String datearray[] = new String[days.intValue()];
		DateFormat dateFormat = new SimpleDateFormat(dateFormat1);
		Date date = dateFormat.parse(startDate);

		for (int day = 0; day < days; day++) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.add(Calendar.DATE, -day);
			Date todate1 = cal.getTime();
			String fromdate = dateFormat.format(todate1);
			datearray[day] = fromdate;
		}

		return datearray;
	}

	@Override
	public CommandProcessingResult postDetailsForProvisioning(Long clientId,Long resourceId, String requestType,String provisioningSystem,String hardwareId) {
		

		  Long defaultValue=Long.valueOf(0);
		  JSONObject jsonObject = new JSONObject();
		  if(requestType.equalsIgnoreCase(ProvisioningApiConstants.REQUEST_CREATE_AGENT)){
		   Office office=this.officeRepository.findOne(resourceId);
		   if(office !=null){
		      OfficeAdditionalInfo additionalInfo=office.getOfficeAdditionalInfo();
		           if(additionalInfo != null){
		               jsonObject.put("agentName", office.getName());
		               jsonObject.put("agentId", office.getId());
		               jsonObject.put("agentDescription", additionalInfo.getContactName());
		           }
		           resourceId=Long.valueOf(0L);
		      }
		  }
		  
		  ProcessRequest processRequest=new ProcessRequest(defaultValue,clientId,resourceId, provisioningSystem, requestType,'N','N');
		   ProcessRequestDetails processRequestDetails=new ProcessRequestDetails(defaultValue,defaultValue,jsonObject.toString(),"Recieved",
		     hardwareId,DateUtils.getDateOfTenant(),DateUtils.getDateOfTenant(),null,null,'N',requestType,null);
		   processRequest.add(processRequestDetails);
		   this.processRequestRepository.save(processRequest);
		  return new CommandProcessingResult(processRequest.getId());
   }

	@SuppressWarnings("unchecked")
	@Override
	public CommandProcessingResult createProvisioningRequest(Order order, JsonCommand command,boolean checkValidate) {
		
		String provisioningSystemId = null;String parameterId = null;
		JSONArray deviceDetails = null;
		try{
			String requestType = command.stringValueOfParameterNamed("requestType");
			Long oldOrderId = command.longValueOfParameterNamed("oldOrderId");
			ClientService clientService = this.clientServiceRepository.findOne(order.getClientServiceId());
			Map<String,Object> hardwareAndDeviceDetails = this.retriveHardwareMappingAndSalesDetails(order,clientService);
			if(checkValidate){this.fromApiJsonDeserializer.validateForHardwareAndDevice(hardwareAndDeviceDetails);}
			deviceDetails = this.retrivedeviceDetailsforProvisioningRequest((List<HardwarePlanData>)hardwareAndDeviceDetails.get("hardwareMappingDetails"),(List<OneTimeSaleData>)hardwareAndDeviceDetails.get("deviceDetails"));
			Collection<MCodeData> mcodeDatas = this.mCodeReadPlatformService.getCodeValue("SP");
			for(MCodeData mcodeData:mcodeDatas){
				if(mcodeData.getmCodeValue().equalsIgnoreCase("Network_node")){
					parameterId = mcodeData.getId().toString(); break;
				}
			}
			final List<ServiceParameters> serviceParameters = clientService.getServiceParameters();
			for(ServiceParameters serviceParameter:serviceParameters){
				if(parameterId.equalsIgnoreCase(serviceParameter.getParameterName())){
					provisioningSystemId = serviceParameter.getParameterValue();
					break;
				}
			}
			
			
			ProvisioningRequest provisioningRequest = new ProvisioningRequest(order.getClientId(),order.getId(),requestType,provisioningSystemId,'N',
					DateUtils.getLocalDateOfTenant().toDate(),null);
			provisioningRequest.addDetails(new ProvisioningRequestDetail(this.retriveRequestMessage(order,clientService,provisioningSystemId,deviceDetails,oldOrderId),null,null,null));
			this.provisioningRequestRepository.saveAndFlush(provisioningRequest);
			
			return new CommandProcessingResult(provisioningRequest.getId());
		}catch (DataIntegrityViolationException dve) {
			this.handleCodeDataIntegrityIssues(null, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
	}
	
	private Map<String, Object> retriveHardwareMappingAndSalesDetails(Order order, ClientService clientService) {
		Map<String, Object>  hardwareAndSalesDetails = new HashMap<String, Object>();
		List<OneTimeSaleData> neededSlesDatas = new ArrayList<OneTimeSaleData>();
		List<HardwarePlanData> requiredhardwarePlanDatas = new ArrayList<HardwarePlanData>();
		Plan planData = this.planRepository.findOne(order.getPlanId());
		List<HardwarePlanData> hardwarePlanDatas = this.hardwarePlanReadPlatformService.retrieveHardwareMappingsOfAPlan(planData.getPlanCode());
		for(HardwarePlanData hardwarePlanData:hardwarePlanDatas){
			for(ServiceParameters serviceParameter:clientService.getServiceParameters()){
				if(serviceParameter.getParameterValue().equalsIgnoreCase(hardwarePlanData.getProvisioningId().toString()))
					requiredhardwarePlanDatas.add(hardwarePlanData);
			}
		}
		List<OneTimeSaleData> salesDatas = this.oneTimeSaleReadPlatformService.retrieveClientOneTimeSalesData(order.getClientId());List<OneTimeSaleData> allocatedsalesDatas = new ArrayList<OneTimeSaleData>();
		for(OneTimeSaleData salesData:salesDatas){
			if(!salesData.getHardwareAllocated().equalsIgnoreCase("UNALLOCATED")){
				allocatedsalesDatas.add(salesData);
			}
		}
		for(OneTimeSaleData allocatedsalesData:allocatedsalesDatas){
			if(clientService.getId().toString().equalsIgnoreCase(allocatedsalesData.getClientServiceId().toString())){
				neededSlesDatas.add(allocatedsalesData);
			}
		}
		hardwareAndSalesDetails.put("hardwareMappingDetails", requiredhardwarePlanDatas);hardwareAndSalesDetails.put("deviceDetails",neededSlesDatas);
		hardwareAndSalesDetails.put("planData", planData);
		return hardwareAndSalesDetails;
	}

	private JSONArray retrivedeviceDetailsforProvisioningRequest(List<HardwarePlanData> requiredhardwarePlanDatas,List<OneTimeSaleData> salesDatas){
		JSONArray deviceDetails = new JSONArray();JSONObject object = null;
		for(HardwarePlanData hardwarePlanData:requiredhardwarePlanDatas){
			for(OneTimeSaleData salesData:salesDatas){
				if(hardwarePlanData.getItemCode().equalsIgnoreCase(salesData.getItemCode())){
					object = new JSONObject();
					object.put("ItemType", salesData.getItemCode());
					object.put("ProvisioningSerialNo",salesData.getProvserialnumber());
					deviceDetails.put(object);
					break;
				}
			}
		}
		return deviceDetails;
		
	}
	
	private String retriveRequestMessage(final Order order,ClientService clientService, String provisioningSystemId,JSONArray deviceDetails,Long oldOrderId){
		JSONObject object = new JSONObject();
		object.put("serviceId",clientService.getServiceId());
		object.put("clientServiceId",clientService.getId());
		object.put("Network_node",provisioningSystemId);
		object.put("ItemList", String.valueOf(deviceDetails));
		object.put("AddProductList", String.valueOf(this.retrivePlanProductCodes(order.getPlanId(),provisioningSystemId)));
		if(oldOrderId !=null){
			object.put("RemoveProductList", String.valueOf(this.retrivePlanProductCodes(this.orderRepository.findOne(oldOrderId).getPlanId(),provisioningSystemId)));
		}
			
		return object.toString();
		
	}

	private JSONArray retrivePlanProductCodes(Long planId,String provisioningSystemId) {
		JSONArray array = new JSONArray();
		JSONObject object = null;
		CodeValueData codeValueData = this.codeValueReadPlatformService.retrieveCodeValue(Long.valueOf(provisioningSystemId));
		List<ServiceDetailData> serviceDetailDatas = this.serviceMasterReadPlatformService.retriveServiceDetailsOfPlan(planId);
		for(ServiceDetailData serviceDetailData:serviceDetailDatas){
			if(codeValueData.getName().equalsIgnoreCase(serviceDetailData.getCodeParamName())){
				object = new JSONObject();
				object.put("neProductId",serviceDetailData.getParamValue());
				array.put(object);
			}
		}
		return array;
	}
}
