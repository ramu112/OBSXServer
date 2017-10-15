package org.mifosplatform.crm.ticketmaster.command;

import org.joda.time.LocalDate;

public class TicketMasterCommand {

	private final Long id;
	private final Long clientId;
	private String priority;
	private final LocalDate ticketDate;
	private final String problemCode;
	private final String description;
	private final String status;
	private final String resolutionDescription;
	private final Integer assignedTo;
	private final String comments;
	private final Long ticketId;
	private final Long createdbyId;
	private final Integer statusCode;
	private Integer problemCodeId;
	
	
	public TicketMasterCommand(final Long clientId, final String priority,
								final String description, final String problemCode, final String status,
								final String resolutionDescription, final Integer assignedTo, final LocalDate ticketDate,
								final Long createdbyId, final Integer statusCode){		
		
		this.id = null;
		this.clientId = clientId;
		this.priority = priority;
		this.ticketDate = ticketDate;
		this.problemCode = problemCode;
		this.description = description;
		this.status = status;
		this.resolutionDescription = resolutionDescription;
		this.assignedTo = assignedTo;
		this.comments = null;
		this.ticketId = null;
		this.createdbyId = createdbyId;
		this.statusCode = null;
	}
	public TicketMasterCommand(final Long ticketId, final String comments, final String status,
			final Integer assignedTo, final Long createdbyId, final Integer statusCode,
			final Integer problemCode, final String priority) {
		
		this.id = null;
		this.clientId = null;
		this.priority = null;
		this.ticketDate = null;
		this.problemCode = null;
		this.description = null;
		this.status = status;
		this.resolutionDescription = null;
		this.assignedTo = assignedTo;
		this.comments = comments;
		this.ticketId = ticketId;
		this.createdbyId = createdbyId;
		this.statusCode = statusCode;
		this.problemCodeId = problemCode;
		this.priority = priority;
		
	}
	public TicketMasterCommand(final String status, final String resolutionDescription, final Integer statusCode) {
		this.id = null;
		this.clientId = null;
		this.priority = null;
		this.ticketDate = null;
		this.problemCode = null;
		this.description = null;
		this.assignedTo = null;
		this.comments = null;
		this.ticketId = null;
		this.createdbyId = null;
		this.statusCode = statusCode;
		this.status = "CLOSED";
		this.resolutionDescription = resolutionDescription;
	}
	
	public Long getId() {
		return id;
	}
	public Long getClientId() {
		return clientId;
	}
	public String getPriority() {
		return priority;
	}
	public LocalDate getTicketDate() {
		return ticketDate;
	}
	public String getProblemCode() {
		return problemCode;
	}
	public String getDescription() {
		return description;
	}
	public String getStatus() {
		return status;
	}
	public String getResolutionDescription() {
		return resolutionDescription;
	}
	public Integer getAssignedTo() {
		return assignedTo;
	}
	public String getComments() {
		return comments;
	}
	public Long getTicketId() {
		return ticketId;
	}
	public Long getCreatedbyId() {
		return createdbyId;
	}
	/**
	 * @return the statusCode
	 */
	public Integer getStatusCode() {
		return statusCode;
	}
	public Integer getProblemCodeId() {
		return problemCodeId;
	}
	public void setProblemCodeId(Integer problemCodeId) {
		this.problemCodeId = problemCodeId;
	}
	
	
}
