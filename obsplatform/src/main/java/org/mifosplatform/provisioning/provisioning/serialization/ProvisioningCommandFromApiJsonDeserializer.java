package org.mifosplatform.provisioning.provisioning.serialization;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.data.DataValidatorBuilder;
import org.mifosplatform.infrastructure.core.exception.InvalidJsonException;
import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.logistics.onetimesale.data.OneTimeSaleData;
import org.mifosplatform.organisation.hardwareplanmapping.data.HardwarePlanData;
import org.mifosplatform.portfolio.plan.domain.Plan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

/**
 * Deserializer for code JSON to validate API request.
 */
@Component
public final class ProvisioningCommandFromApiJsonDeserializer {

	/**
	 * The parameters supported for this command.
	 */
	private final Set<String> provisioningsupportedParameters = new HashSet<String>(
			Arrays.asList("id", "provisioningSystem", "commandName", "status",
					"commandParameters", "commandParam", "paramType",
					"paramDefault", "groupName", "ipAddress", "serviceName",
					"vLan", "planName", "orderId", "clientId", "macId",
					"serviceParameters", "paramName", "paramValue", "orderId",
					"deviceId", "clientName", "ipType", "ipRange", "subnet"));

	private final FromJsonHelper fromApiJsonHelper;
	
	@Autowired
	public ProvisioningCommandFromApiJsonDeserializer(final FromJsonHelper fromApiJsonHelper){
		
		this.fromApiJsonHelper = fromApiJsonHelper;
	}

	public void validateForProvisioning(String json) {

		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}

		final Type typeOfMap = new TypeToken<Map<String, Object>>() {
		}.getType();
		fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, provisioningsupportedParameters);

		final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
		final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(
				dataValidationErrors).resource("order");

		final JsonElement element = fromApiJsonHelper.parse(json);
		final String provisioningSystem = fromApiJsonHelper.extractStringNamed("provisioningSystem", element);
		baseDataValidator.reset().parameter("provisioningSystem").value(provisioningSystem).notBlank();

		final String commandName = fromApiJsonHelper.extractStringNamed("commandName", element);
		baseDataValidator.reset().parameter("commandName").value(commandName).notBlank();

		final JsonArray commandArray = fromApiJsonHelper.extractJsonArrayNamed("commandParameters", element);

		if (commandArray != null) {
			for (JsonElement jsonelement : commandArray) {

				final String commandParam = fromApiJsonHelper.extractStringNamed("commandParam", jsonelement);
				baseDataValidator.reset().parameter("commandParam").value(commandParam).notBlank();

				final String paramType = fromApiJsonHelper.extractStringNamed("paramType", jsonelement);
				baseDataValidator.reset().parameter("paramType").value(paramType).notBlank();
			}
		}

		throwExceptionIfValidationWarningsExist(dataValidationErrors);
	}

	private void throwExceptionIfValidationWarningsExist(
			final List<ApiParameterError> dataValidationErrors) {
		if (!dataValidationErrors.isEmpty()) {
			throw new PlatformApiDataValidationException(
					"validation.msg.validation.errors.exist",
					"Validation errors exist.", dataValidationErrors);
		}
	}

	public void validateForAddProvisioning(String json) {

		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}

		final Type typeOfMap = new TypeToken<Map<String, Object>>() {
		}.getType();
		final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
		fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,provisioningsupportedParameters);
		final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(
				dataValidationErrors).resource("provisioning");
		final JsonElement element = fromApiJsonHelper.parse(json);
		final String serviceName = fromApiJsonHelper.extractStringNamed("serviceName", element);
		final String ipRange = fromApiJsonHelper.extractStringNamed("ipRange",element);
		baseDataValidator.reset().parameter("serviceName").value(serviceName).notBlank();
		final JsonArray serviceParametersArray = fromApiJsonHelper.extractJsonArrayNamed("serviceParameters", element);
		// baseDataValidator.reset().parameter("mediaassetAttributes").value(mediaassetAttributesArray).
		String[] serviceParameters = null;
		serviceParameters = new String[serviceParametersArray.size()];
		int mediaassetAttributeSize = serviceParametersArray.size();
		baseDataValidator.reset().parameter(null).value(mediaassetAttributeSize).integerGreaterThanZero();
		for (int i = 0; i < serviceParametersArray.size(); i++) {
			serviceParameters[i] = serviceParametersArray.get(i).toString();
		}
		// For Media Attributes
		for (String serviceParameter : serviceParameters) {

			final JsonElement attributeElement = fromApiJsonHelper.parse(serviceParameter);
			final String paramName = fromApiJsonHelper.extractStringNamed("paramName", attributeElement);
			baseDataValidator.reset().parameter("paramName").value(paramName).notBlank();

			if (paramName.equalsIgnoreCase("IP_ADDRESS")) {

				if (ipRange.equalsIgnoreCase("subnet")) {
					final String parmaValue = fromApiJsonHelper.extractStringNamed("paramValue", attributeElement);
					baseDataValidator.reset().parameter(paramName).value(parmaValue).notBlank();
				} else {
					final String[] parmaValue = fromApiJsonHelper.extractArrayNamed("paramValue", attributeElement);
					baseDataValidator.reset().parameter(paramName).value(parmaValue).arrayNotEmpty();
				}
			} else {
				final String parmaValue = fromApiJsonHelper.extractStringNamed(
						"paramValue", attributeElement);
				baseDataValidator.reset().parameter(paramName)
						.value(parmaValue).notBlank();
			}
		}

		throwExceptionIfValidationWarningsExist(dataValidationErrors);
	}

	public void validateForHardwareAndDevice(Map<String,Object> hardwareAndDeviceDetails){
		@SuppressWarnings("unchecked")
		List<HardwarePlanData> hardwareMappingDatas = (List<HardwarePlanData>) hardwareAndDeviceDetails.get("hardwareMappingDetails");
		@SuppressWarnings("unchecked")
		List<OneTimeSaleData> deviceDatas = (List<OneTimeSaleData>) hardwareAndDeviceDetails.get("deviceDetails");
		Plan plan = (Plan)hardwareAndDeviceDetails.get("planData");
		if(hardwareMappingDatas.isEmpty()){
			throw new PlatformDataIntegrityException("hardware.mappings.not.defined", 
					"Please provide Hardware mappings for this plan "+plan.getPlanCode());
		}else{
			if(deviceDatas.isEmpty()){
				throw new PlatformDataIntegrityException("devices.not.found", 
						"Devices Not found");
			}else{
				for(HardwarePlanData hardwareMappingData:hardwareMappingDatas){
					boolean isFound = false;
					for(OneTimeSaleData deviceData:deviceDatas){
						if(hardwareMappingData.getItemCode().equalsIgnoreCase(deviceData.getItemCode())){
							isFound = true;
							if(deviceData.getPairedItemCode() != null){
								this.isavailablePairedItem(deviceDatas,deviceData);
							}
							break;
						}
					}
					if(!isFound){
						throw new PlatformDataIntegrityException("device.not.found", 
								"Devices Not found");
					}
				}
			}
		}
	}
	

	private void isavailablePairedItem(List<OneTimeSaleData> salesDatas, OneTimeSaleData saleData){
		boolean found = false;
		for(OneTimeSaleData salesData:salesDatas){
			if(!salesData.getId().toString().equalsIgnoreCase(saleData.getId().toString()) && saleData.getPairedItemCode().equalsIgnoreCase(salesData.getItemCode())){
				found = true;
				break;
			}
		}
		if(!found){
			throw new PlatformDataIntegrityException("paired.item.not.found", 
					"there is no paired device for "+saleData.getSerialNo());
		}
	}
	
	@SuppressWarnings("unused")
	private void ValidationOfIsPairedDeviceConfiguredInHardwardMapping(OneTimeSaleData salesData,List<HardwarePlanData> requiredhardwarePlanDatas){
		boolean found = false;
		for(HardwarePlanData hardwarePlanData:requiredhardwarePlanDatas){
			if(hardwarePlanData.getItemCode().equalsIgnoreCase(salesData.getItemCode())){
				found = true;break;
			}
		}	
		if(!found){
			throw new PlatformDataIntegrityException("client.need.hardware.mapping.for.paired.device", 
				"client must have "+salesData.getItemCode()+" type device hard ware mapping");
			}
	}
	
	/*
	 * public void validateForUpDateIpDetails(String json) {
	 * 
	 * if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }
	 * 
	 * final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
	 * fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
	 * ipsupportedParameters);
	 * 
	 * final List<ApiParameterError> dataValidationErrors = new
	 * ArrayList<ApiParameterError>(); final DataValidatorBuilder
	 * baseDataValidator = new
	 * DataValidatorBuilder(dataValidationErrors).resource("ipDetails");
	 * 
	 * final JsonElement element = fromApiJsonHelper.parse(json); final
	 * JsonArray
	 * deletedIpsArray=fromApiJsonHelper.extractJsonArrayNamed("removeIps"
	 * ,element); final JsonArray
	 * newIpsArray=fromApiJsonHelper.extractJsonArrayNamed("newIps",element);
	 * int ipsSize=newIpsArray.size(); //
	 * baseDataValidator.reset().parameter(null
	 * ).value(ipsSize).integerGreaterThanZero();
	 * throwExceptionIfValidationWarningsExist(dataValidationErrors);
	 * 
	 * }
	 */

}