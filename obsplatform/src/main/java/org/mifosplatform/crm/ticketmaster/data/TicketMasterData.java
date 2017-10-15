package org.mifosplatform.crm.ticketmaster.data;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;

public class TicketMasterData {
	
	private List<TicketMasterData> masterData;
	private Collection<MCodeData> statusType;
	private List<EnumOptionData> priorityType;
    private Collection<MCodeData> problemsDatas;
    private List<UsersData> usersData;
    private Collection<MCodeData> sourceData;
    private Long id;
    private String priority;
    private String status;
    private String assignedTo;
    private LocalDate ticketDate;
    private int userId;
    private String lastComment;
    private String problemDescription;
    private String userName;
    private Integer statusCode;
    private Integer problemCode;
    private String statusDescription;
	private LocalDate createdDate;
	private String attachedFile;
	private String sourceOfTicket;
	private Date dueDate;
	private String resolutionDescription;
	private String ticketstatus;
	
  	public TicketMasterData(final List<EnumOptionData> statusType,
			final List<EnumOptionData> priorityType) {
		this.priorityType = priorityType;
		this.problemsDatas = null;
	}

	public TicketMasterData(final Collection<MCodeData> datas, final List<UsersData> userData,
			final List<EnumOptionData> priorityData, final Collection<MCodeData> sourceData) {
		
		this.problemsDatas = datas;
		this.usersData = userData;
		this.ticketDate = DateUtils.getLocalDateOfTenant();
		this.priorityType = priorityData;
		this.sourceData = sourceData;
	}

	public TicketMasterData(final Long id, final String priority, final String status, final Integer assignedTo, 
			final LocalDate ticketDate, final String lastComment, final String problemDescription, final String userName, 
			final String sourceOfTicket, final Date dueDate, final String description, final String resolutionDescription,
			final Integer problemCode, final Integer statusCode, String ticketstatus) {
		
		this.id = id;
		this.priority = priority;
		this.status = status;
		this.userId = assignedTo;
		this.ticketDate = ticketDate;
		this.lastComment = lastComment;
		this.problemDescription = problemDescription;
		this.userName = userName;
		this.sourceOfTicket = sourceOfTicket;
		this.dueDate = dueDate;
		this.statusDescription = description;
		this.resolutionDescription = resolutionDescription;
		this.problemCode = problemCode;
		this.statusCode = statusCode;
		this.ticketstatus = ticketstatus;
		
	}

	public TicketMasterData(final Integer statusCode, final String statusDesc) {
	     this.statusCode = statusCode;
	     this.statusDescription = statusDesc;
	 
	}

	public TicketMasterData(final Long id, final LocalDate createdDate,
			final String assignedTo, final String description, final String fileName) {
		 this.id = id;
		 this.createdDate = createdDate;
		 this.assignedTo = assignedTo;
	     this.attachedFile = fileName;
	     this.statusDescription = description;
	}

	public TicketMasterData(final String description, final List<TicketMasterData> data) {
		this.problemDescription = description;
		this.masterData = data;

	}

	public List<EnumOptionData> getPriorityType() {
		return priorityType;
	}

	public Collection<MCodeData> getProblemsDatas() {
		return problemsDatas;
	}

	public List<UsersData> getUsersDatas() {
		return usersData;
	}

	public List<UsersData> getUsersData() {
		return usersData;
	}

	public Long getId() {
		return id;
	}

	public String getPriority() {
		return priority;
	}

	public String getStatus() {
		return status;
	}

	public String getAssignedTo() {
		return assignedTo;
	}

	public LocalDate getTicketDate() {
		return ticketDate;
	}

	public int getUserId() {
		return userId;
	}

	public String getLastComment() {
		return lastComment;
	}

	public String getProblemDescription() {
		return problemDescription;
	}

	public String getUserName() {
		return userName;
	}

	public Integer getStatusCode() {
		return statusCode;
	}

	public String getStatusDescription() {
		return statusDescription;
	}

	public void setStatusData(final Collection<MCodeData> statusdata) {
		
		this.statusType = statusdata;
	}

	public String getResolutionDescription() {
		return resolutionDescription;
	}

	public void setResolutionDescription(final String resolutionDescription) {
		this.resolutionDescription = resolutionDescription;
	}

	public void setUsersData(final List<UsersData> usersData) {
		this.usersData = usersData;
	}

	public void setPriorityType(List<EnumOptionData> priorityType) {
		this.priorityType = priorityType;
	}

	public void setProblemsDatas(Collection<MCodeData> problemsDatas) {
		this.problemsDatas = problemsDatas;
	}
	
	
	
}