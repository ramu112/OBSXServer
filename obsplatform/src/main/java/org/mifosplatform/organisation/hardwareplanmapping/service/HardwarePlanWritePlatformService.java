package org.mifosplatform.organisation.hardwareplanmapping.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface HardwarePlanWritePlatformService {

	CommandProcessingResult createHardwarePlan(JsonCommand command);

	CommandProcessingResult updatePlanMapping(Long entityId, JsonCommand command);

}
