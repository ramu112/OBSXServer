package org.mifosplatform.crm.clientprospect.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.crm.clientprospect.domain.ClientProspect;
import org.mifosplatform.crm.clientprospect.domain.ClientProspectJpaRepository;
import org.mifosplatform.crm.clientprospect.domain.ProspectDetail;
import org.mifosplatform.crm.clientprospect.domain.ProspectDetailJpaRepository;
import org.mifosplatform.crm.clientprospect.serialization.ClientProspectCommandFromApiJsonDeserializer;
import org.mifosplatform.infrastructure.codes.exception.CodeNotFoundException;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.useradministration.domain.AppUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClientProspectWritePlatformServiceImp implements
		ClientProspectWritePlatformService {

	private final static Logger LOGGER = (Logger) LoggerFactory
			.getLogger(ClientProspectWritePlatformServiceImp.class);

	private final PlatformSecurityContext context;
	private final ClientProspectJpaRepository clientProspectJpaRepository;
	private final ProspectDetailJpaRepository prospectDetailJpaRepository;
	private final ClientProspectCommandFromApiJsonDeserializer clientProspectCommandFromApiJsonDeserializer;
	private final FromJsonHelper fromApiJsonHelper;
	private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;

	@Autowired
	public ClientProspectWritePlatformServiceImp(
			final PlatformSecurityContext context,
			final ClientProspectJpaRepository clientProspectJpaRepository,
			final ClientProspectCommandFromApiJsonDeserializer clientProspectCommandFromApiJsonDeserializer,
			final FromJsonHelper fromApiJsonHelper,
			final ProspectDetailJpaRepository prospectDetailJpaRepository,
			final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService) {
		this.context = context;
		this.clientProspectJpaRepository = clientProspectJpaRepository;
		this.clientProspectCommandFromApiJsonDeserializer = clientProspectCommandFromApiJsonDeserializer;
		this.fromApiJsonHelper = fromApiJsonHelper;
		this.prospectDetailJpaRepository = prospectDetailJpaRepository;
		this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
	}

	@Transactional
	@Override
	public CommandProcessingResult createProspect(JsonCommand command) {

		try {
			context.authenticatedUser();
			this.clientProspectCommandFromApiJsonDeserializer.validateForCreate(command.json());
			
			final ClientProspect entity = ClientProspect.fromJson(fromApiJsonHelper, command);
			this.clientProspectJpaRepository.save(entity);

			return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(entity.getId()).build();
			
		} catch (DataIntegrityViolationException dve) {
			handleDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		} catch (ParseException pe) {
			throw new PlatformDataIntegrityException(
					"invalid.date.and.time.format",
					"invalid.date.and.time.format",
					"invalid.date.and.time.format");
		}
	}

	@Transactional
	@Override
	public CommandProcessingResult followUpProspect(final JsonCommand command, final Long prospectId) {
		try {
			context.authenticatedUser();
			this.clientProspectCommandFromApiJsonDeserializer.validateForUpdate(command.json());
			
			final ProspectDetail prospectDetail = ProspectDetail.fromJson(command, prospectId);
			prospectDetailJpaRepository.save(prospectDetail);
			
			return new CommandProcessingResultBuilder().withCommandId(command.commandId())
					.withEntityId(prospectDetail.getProspectId()).build();
		
		} catch (DataIntegrityViolationException dve) {
			handleDataIntegrityIssues(command, dve);
			return CommandProcessingResult.empty();
		} catch (ParseException e) {
			throw new PlatformDataIntegrityException(
					"invalid.date.and.time.format",
					"invalid.date.and.time.format",
					"invalid.date.and.time.format");
		}
	}

	@Transactional
	@Override
	public CommandProcessingResult deleteProspect(JsonCommand command) {
		
		context.authenticatedUser();
		final ClientProspect clientProspect = retrieveCodeBy(command.entityId());
		clientProspect.setIsDeleted('Y');
		clientProspect.setStatus("Canceled");
		clientProspect.setStatusRemark(command.stringValueOfParameterNamed("statusRemark"));
		
		this.clientProspectJpaRepository.saveAndFlush(clientProspect);
		
		return new CommandProcessingResultBuilder().withEntityId(
				clientProspect.getId()).build();
	}

	private ClientProspect retrieveCodeBy(final Long prospectId) {
		
		final ClientProspect clientProspect = this.clientProspectJpaRepository.findOne(prospectId);
		
		if (clientProspect == null) {
			throw new CodeNotFoundException(prospectId.toString());
		}
		
		return clientProspect;
	}

	@Override
	public CommandProcessingResult convertToClient(final Long entityId) {

		final AppUser currentUser = context.authenticatedUser();
		final ClientProspect clientProspect = retrieveCodeBy(entityId);

		Long clientId = null;

		final JSONObject newClientJsonObject = new JSONObject();
		
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
			String activationDate = formatter.format(DateUtils.getDateOfTenant());

			final Long officeId = currentUser.getOffice().getId();
			newClientJsonObject.put("dateFormat", "dd MMMM yyyy");
			newClientJsonObject.put("locale", "en");
			newClientJsonObject.put("officeId", officeId);
			newClientJsonObject.put("firstname", clientProspect.getFirstName());
			newClientJsonObject.put("middlename", clientProspect.getMiddleName());
			newClientJsonObject.put("lastname", clientProspect.getLastName());
			newClientJsonObject.put("fullname", "");
			newClientJsonObject.put("externalId", "");
			newClientJsonObject.put("clientCategory", "20");
			// newClientJsonObject.put("active","300");
			newClientJsonObject.put("activationDate", activationDate);
			newClientJsonObject.put("active", "true");
			newClientJsonObject.put("email", clientProspect.getEmail());
			newClientJsonObject.put("phone", clientProspect.getMobileNumber());
			newClientJsonObject.put("flag", false);
			/*
			 * newClientJsonObject.put("login","");
			 * newClientJsonObject.put("password","");
			 */

			newClientJsonObject.put("addressNo", clientProspect.getAddress());
			newClientJsonObject.put("street", clientProspect.getStreetArea());
			newClientJsonObject.put("city", clientProspect.getCityDistrict());
			newClientJsonObject.put("zipCode", clientProspect.getZipCode());
			newClientJsonObject.put("state", clientProspect.getState());
			newClientJsonObject.put("country", clientProspect.getCountry());
			newClientJsonObject.put("flag", "false");

			final CommandWrapper commandNewClient = new CommandWrapperBuilder().createClient()
					.withJson(newClientJsonObject.toString().toString()).build(); //
			
			final CommandProcessingResult clientResult = this.commandsSourceWritePlatformService.logCommandSource(commandNewClient);
			/*
			 * final CommandWrapper commandRequest = new
			 * CommandWrapperBuilder().
			 * createAddress(clientResult.getClientId()).
			 * withJson(newClientAddressObject.toString().toString()).build();
			 * final CommandProcessingResult addressResult =
			 * this.commandsSourceWritePlatformService
			 * .logCommandSource(commandRequest);
			 */

			clientProspect.setStatusRemark(clientResult.getClientId().toString());
			clientId = clientResult.getClientId();

		} catch (JSONException e) {
			e.printStackTrace();
		}

		clientProspect.setStatus("Closed");
		// clientProspect.setIsDeleted('Y');

		// clientProspect.setStatusRemark(command.stringValueOfParameterNamed("statusRemark"));
		
		this.clientProspectJpaRepository.saveAndFlush(clientProspect);
		
		return new CommandProcessingResultBuilder().withEntityId(clientId).build();

	}

	@Override
	public CommandProcessingResult updateProspect(JsonCommand command) {
		
		try {
			context.authenticatedUser();
			this.clientProspectCommandFromApiJsonDeserializer.validateForCreate(command.json());

			final ClientProspect pros = retrieveCodeBy(command.entityId());
			final Map<String, Object> changes = pros.update(command);

			if (!changes.isEmpty()) {
				this.clientProspectJpaRepository.save(pros);
			}

			return new CommandProcessingResultBuilder() //
					.withCommandId(command.commandId()) //
					.withEntityId(pros.getId()) //
					.with(changes) //
					.build();
			
		} catch (DataIntegrityViolationException dve) {
			handleDataIntegrityIssues(command, dve);
		}
		return new CommandProcessingResultBuilder().withEntityId(-1L).build();
	}

	private void handleDataIntegrityIssues(final JsonCommand element,
			final DataIntegrityViolationException dve) {

		Throwable realCause = dve.getMostSpecificCause();
		if (realCause.getMessage().contains("serial_no_constraint")) {
			throw new PlatformDataIntegrityException(
					"validation.error.msg.inventory.item.duplicate.serialNumber",
					"validation.error.msg.inventory.item.duplicate.serialNumber",
					"validation.error.msg.inventory.item.duplicate.serialNumber",
					"");
		}

		LOGGER.error(dve.getMessage(), dve);
	}

}
