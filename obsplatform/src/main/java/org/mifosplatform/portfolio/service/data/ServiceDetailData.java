package org.mifosplatform.portfolio.service.data;

import java.util.Collection;
import java.util.Date;

import org.mifosplatform.organisation.mcodevalues.data.MCodeData;

public class ServiceDetailData {
	
	private final Long id;
	private final Long paramName;
	private final String paramType;
	private final String paramValue;
	private final String codeParamName;
	private String detailValue;
	private boolean isDetail;
	private Date detailDate;
	private Collection<MCodeData> details;
	
	

	public ServiceDetailData(final Long id, final Long paramName,final String paramType, final String paramValue,final String codeParamName) {
             
		this.id=id;
		this.paramName = paramName;
		this.paramType = paramType;
		this.paramValue = paramValue;
		this.codeParamName = codeParamName;
	
	}

	public Long getId() {
		return id;
	}

	public Long getParamName() {
		return paramName;
	}

	public String getParamType() {
		return paramType;
	}

	public String getParamValue() {
		return paramValue;
	}

	public String getCodeParamName() {
		return codeParamName;
	}

	public void setDetailValue(String detailValue) {
		this.detailValue = detailValue;
	}

	public void setDetail(boolean isDetail) {
		this.isDetail = isDetail;
	}

	

	public String getDetailValue() {
		return detailValue;
	}

	public boolean isDetail() {
		return isDetail;
	}

	public Collection<MCodeData> getDetails() {
		return details;
	}

	public void setDetails(Collection<MCodeData> details) {
		this.details = details;
	}

	public Date getDetailDate() {
		return detailDate;
	}

	public void setDetailDate(Date detailDate) {
		this.detailDate = detailDate;
	}

	
	
	
}
