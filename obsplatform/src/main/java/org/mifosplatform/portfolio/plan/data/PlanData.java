package org.mifosplatform.portfolio.plan.data;

import java.util.Collection;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.mifosplatform.portfolio.contract.data.SubscriptionData;

public class PlanData {

	private  Long id;
	private  Long billRule;
	private  String planCode;
	private  String planDescription;
	private  LocalDate startDate;
	private  LocalDate endDate;
	private  Long status;
	private  EnumOptionData planstatus;
	private  String serviceDescription;
	private  Collection<ServiceData> services;
	private  Collection<ServiceData> selectedServices;
	private List<String> contractPeriods;
	private List<SubscriptionData> subscriptiondata;
	private List<BillRuleData> billRuleDatas;
	private List<EnumOptionData> planStatus,volumeTypes;
	private  String contractPeriod;
	private PlanData datas;
	private long statusname;
	private Collection<MCodeData> provisionSysData;
	private String provisionSystem;
	private String isPrepaid;
	private String allowTopup;
	private String isHwReq;
	private String volume;
	private String units;
	private String unitType;
	private Long contractId;
	private Boolean isActive=false;
	//private Integer planCount = 0;
	private List<PlanData> data = null;
	private int planCount;
	private boolean ordersFlag;
	
	private Collection<MCodeData> planTypeData;
	private Long planType;
	private String planTypeName;
	
	public PlanData(Collection<ServiceData> data, List<BillRuleData> billData,List<SubscriptionData> contractPeriod, List<EnumOptionData> status,
			PlanData datas, Collection<ServiceData> selectedservice,Collection<MCodeData> provisionSysData, List<EnumOptionData> volumeType,
			Collection<MCodeData> planTypeData) {
	
		if(datas!=null){
		this.id = datas.getId();
		this.planCode = datas.getplanCode();
		this.subscriptiondata = contractPeriod;
		this.startDate = datas.getStartDate();
		this.status = datas.getStatus();
		this.billRule = datas.getBillRule();
		this.endDate = datas.getEndDate();
		this.planDescription = datas.getplanDescription();
		this.provisionSystem=datas.getProvisionSystem();
		this.isPrepaid=datas.getIsPrepaid();
		this.allowTopup=datas.getAllowTopup();
		this.volume=datas.getVolume();
		this.units=datas.getUnits();
		this.unitType=datas.getUnitType();
		this.contractPeriod=datas.getPeriod();
		this.isHwReq=datas.getIsHwReq();
		this.planType=datas.getPlanType();
		this.planTypeName=datas.getPlanTypeName();
		
		}
		this.services = data;
        this.provisionSysData=provisionSysData;
		this.selectedServices = selectedservice;
		this.billRuleDatas = billData;
		this.subscriptiondata=contractPeriod;
		this.planStatus = status;
		this.serviceDescription = null;
		
		this.datas = datas;
		//this.datas = null;
		this.volumeTypes=volumeType;
		this.planTypeData = planTypeData;

	}

	
	public PlanData(Long id, String planCode, LocalDate startDate,LocalDate endDate, Long bill_rule, String contractPeriod,
			long status, String planDescription,String provisionSys,EnumOptionData enumstatus, String isPrepaid,
			String allowTopup, String volume, String units, String unitType, Collection<ServiceData> services, Long contractId, String isHwReq,Long count, Long planType,
			String planTypeName) {

		this.id = id;
		this.planCode = planCode;
		this.serviceDescription = null;
		this.startDate = startDate;
		this.status = status;
		this.billRule = bill_rule;
		this.endDate = endDate;
		this.planDescription = planDescription;
		//this.services = null;
		this.billRuleDatas = null;
		this.contractPeriod = contractPeriod;
        this.provisionSystem=provisionSys;  
		this.selectedServices = null;
		this.planstatus = enumstatus;
		this.isPrepaid=isPrepaid;
		this.allowTopup=allowTopup;
		this.volume=volume;
		this.units=units;
		this.unitType=unitType;
		this.isHwReq=isHwReq;
		this.services=services;
		this.contractId=contractId;
		this.ordersFlag=(count>0)?true:false;
		this.planType=planType;
		this.planTypeName=planTypeName;
	}

	

	public Long getPlanType() {
		return planType;
	}


	public List<String> getContractPeriods() {
		return contractPeriods;
	}


	public String getIsHwReq() {
		return isHwReq;
	}


	public Long getContractId() {
		return contractId;
	}


	public List<PlanData> getData() {
		return data;
	}


	public PlanData(List<PlanData> datas) {
		this.data = datas;
	}

	public PlanData(Long id, String planCode, String planDescription, String isPrepaid) {
              this.id=id;
              this.planCode=planCode;
              this.planDescription=planDescription;
              this.isPrepaid = isPrepaid;
	}

	public String getProvisionSystem() {
		return provisionSystem;
	}


	public EnumOptionData getPlanstatus() {
		return planstatus;
	}

	public PlanData getDatas() {
		return datas;
	}

	public Collection<ServiceData> getSelectedServices() {
		return selectedServices;
	}

	public long getStatusname() {
		return statusname;
	}

	public List<EnumOptionData> getPlanStatus() {
		return planStatus;
	}

	public String getPeriod() {
		return contractPeriod;
	}

	public void setContractPeriod(List<String> contractPeriod) {
		this.contractPeriods = contractPeriod;
	}

	public List<BillRuleData> getBillRuleData() {
		return billRuleDatas;
	}

	public Long getId() {
		return id;
	}

	public String getplanCode() {
		return planCode;
	}

	public String getplanDescription() {
		return planDescription;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public Long getStatus() {
		return status;
	}

	public Collection<ServiceData> getServicedata() {
		return services;
	}

	public Long getBillRule() {
		return billRule;
	}

	public List<String> getContractPeriod() {
		return contractPeriods;
	}

	public String getserviceDescription() {
		return serviceDescription;
	}

	public List<SubscriptionData> getSubscriptiondata() {
		return subscriptiondata;
	}


	/**
	 * @return the planCode
	 */
	public String getPlanCode() {
		return planCode;
	}


	/**
	 * @return the planDescription
	 */
	public String getPlanDescription() {
		return planDescription;
	}


	/**
	 * @return the serviceDescription
	 */
	public String getServiceDescription() {
		return serviceDescription;
	}


	/**
	 * @return the services
	 */
	public Collection<ServiceData> getServices() {
		return services;
	}


	/**
	 * @return the billRuleDatas
	 */
	public List<BillRuleData> getBillRuleDatas() {
		return billRuleDatas;
	}


	/**
	 * @return the volumeTypes
	 */
	public List<EnumOptionData> getVolumeTypes() {
		return volumeTypes;
	}


	/**
	 * @return the provisionSysData
	 */
	public Collection<MCodeData> getProvisionSysData() {
		return provisionSysData;
	}


	public Collection<MCodeData> getPlanTypeData() {
		return planTypeData;
	}


	/**
	 * @return the isPrepaid
	 */
	public String getIsPrepaid() {
		return isPrepaid;
	}


	/**
	 * @return the allowTopup
	 */
	public String getAllowTopup() {
		return allowTopup;
	}


	/**
	 * @return the volume
	 */
	public String getVolume() {
		return volume;
	}


	/**
	 * @return the units
	 */
	public String getUnits() {
		return units;
	}


	/**
	 * @return the unitType
	 */
	public String getUnitType() {
		return unitType;
	}


	/**
	 * @return the isActive
	 */
	public Boolean getIsActive() {
		return isActive;
	}


	/**
	 * @param isActive the isActive to set
	 */
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}




	public void setSeriveces(List<ServiceData> services) {
		
		this.selectedServices=services;
	}


	public void setPlanCount(int size) {
		this.planCount=size;
		
	}


	public String getPlanTypeName() {
		return planTypeName;
	}


	public void setPlanTypeName(String planTypeName) {
		this.planTypeName = planTypeName;
	}
	
	

}
