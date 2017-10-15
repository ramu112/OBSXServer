package org.mifosplatform.portfolio.allocation.service;

import java.util.List;

import org.mifosplatform.logistics.onetimesale.data.AllocationDetailsData;

public interface AllocationReadPlatformService {

	AllocationDetailsData getTheHardwareItemDetails(Long clientId);

	List<AllocationDetailsData> retrieveHardWareDetailsByItemCode(Long clientId, String itemCode);

	List<String> retrieveHardWareDetails(Long clientId);

	AllocationDetailsData getDisconnectedHardwareItemDetails(Long orderId,Long clientId);


}
