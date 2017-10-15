package org.mifosplatform.organisation.partneragreement.service;

import java.util.List;

import org.mifosplatform.organisation.partneragreement.data.AgreementData;

public interface PartnersAgreementReadPlatformService {

	AgreementData retrieveAgreementData(Long partnerId);

	Long checkAgreement(Long officeId);

	List<AgreementData> retrieveAgreementDetails(Long agreementId);



}
