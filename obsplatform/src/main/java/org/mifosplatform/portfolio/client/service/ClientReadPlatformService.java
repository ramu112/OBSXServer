/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.client.service;

import java.util.Collection;
import java.util.List;

import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.portfolio.client.data.ClientAccountSummaryCollectionData;
import org.mifosplatform.portfolio.client.data.ClientAccountSummaryData;
import org.mifosplatform.portfolio.client.data.ClientAdditionalData;
import org.mifosplatform.portfolio.client.data.ClientData;
import org.mifosplatform.portfolio.group.service.SearchParameters;

public interface ClientReadPlatformService {

    ClientData retrieveTemplate();

    Page<ClientData> retrieveAll(SearchParameters searchParameters);

    ClientData retrieveOne(Long clientId);

    Collection<ClientData> retrieveAllForLookup(String extraCriteria);

    Collection<ClientData> retrieveAllForLookupByOfficeId(Long officeId);

    ClientAccountSummaryCollectionData retrieveClientAccountDetails(Long clientId);

    Collection<ClientAccountSummaryData> retrieveClientLoanAccountsByLoanOfficerId(Long clientId, Long loanOfficerId);

    ClientData retrieveClientByIdentifier(Long identifierTypeId, String identifierKey);

    Collection<ClientData> retrieveClientMembersOfGroup(Long groupId);
    
    Collection<ClientCategoryData> retrieveClientCategories();
    
    Collection<GroupData> retrieveGroupData();

	ClientData retrieveAllClosureReasons(String clientClosureReason);

	ClientCategoryData retrieveClientBillModes(Long clientId);

	List<ClientCategoryData> retrievingParentClients(String query);

	List<ClientCategoryData> retrievedParentAndChildData(Long parentClientId,Long clientId);

	Boolean countChildClients(Long entityId);

	ClientAdditionalData retrieveClientAdditionalData(Long clientId);

	ClientData retrieveClientWalletAmount(Long clientId,String type);

	Page<ClientData> retrieveAllClients(SearchParameters searchParameters);
	
	ClientData retrieveSearchClientId(String columnName,String columnValue);

}