package org.mifosplatform.crm.ticketmaster.domain;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.joda.time.LocalDate;
import org.mifosplatform.crm.ticketmaster.command.TicketMasterCommand;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.service.DateUtils;

@Entity
@Table(name = "b_ticket_master")
public class TicketMaster {
	
	@Id
	@GeneratedValue
	@Column(name = "id")
	private Long id;

	@Column(name = "client_id", length = 65536)
	private Long clientId;

	@Column(name = "priority")
	private String priority;

	@Column(name = "problem_code")
	private Integer problemCode;
	
	@Column(name = "description")
	private String description;

	@Column(name = "ticket_date")
	private Date ticketDate;

	@Column(name = "status")
	private String status;
	
	@Column(name = "status_code")
	private Integer statusCode;

	@Column(name = "resolution_description")
	private String resolutionDescription;
	
	@Column(name = "assigned_to")
	private Integer assignedTo;

	@Column(name = "source")
	private String source;
	
	@Column(name = "closed_date")
	private Date closedDate;
	
	@Column(name = "created_date")
	private Date createdDate;
	
	@Column(name = "createdby_id") 
	private Long createdbyId;

	@Column(name="source_of_ticket", length=50 )
	private String sourceOfTicket;
	
	@Column(name = "due_date")
	private Date dueDate;
	
	@Column(name = "lastmodifiedby_id")
	private Long lastModifyId;
	
	@Column(name = "lastmodified_date")
	private Date lastModifydate;

	public TicketMaster() {
		
	}
	
	public static TicketMaster fromJson(final JsonCommand command) throws ParseException {
	
		final String priority = command.stringValueOfParameterNamed("priority");
		final Integer problemCode = command.integerValueOfParameterNamed("problemCode");
		final String description = command.stringValueOfParameterNamed("description");
		final Integer assignedTo = command.integerValueOfParameterNamed("assignedTo");
		
		final LocalDate startDate = command.localDateValueOfParameterNamed("ticketDate");
		final String startDateString = startDate.toString() + command.stringValueOfParameterNamed("ticketTime");
		final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		final Date ticketDate = df.parse(startDateString);
	
		final String statusCode = command.stringValueOfParameterNamed("problemDescription");
		final Long clientId = command.getClientId();
		final String sourceOfTicket = command.stringValueOfParameterNamed("sourceOfTicket");
		final String dueDate = command.stringValueOfParameterNamed("dueTime");
		final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date dueTime;
		if(dueDate.equalsIgnoreCase("")){
				dueTime=null;
		}else{
			dueTime = dateFormat.parse(dueDate);
		}
	
		return new TicketMaster(clientId, priority,ticketDate, problemCode,description,statusCode, null, 
					assignedTo, null, null, null, sourceOfTicket, dueTime);
	}

	public TicketMaster(final Integer statusCode, final Integer assignedTo) {
		
		this.clientId = null;
		this.priority = null;
		this.ticketDate = null;
		this.problemCode = null;
		this.description = null;
		this.status = null;
		this.statusCode = statusCode;
		this.source = null;
		this.resolutionDescription = null;
		this.assignedTo = assignedTo;	
		this.createdDate = null;
		this.createdbyId = null;
	}

	public TicketMaster(final Long clientId, final String priority, final Date ticketDate, final Integer problemCode,
			final String description, final String status, final String resolutionDescription, 
			final Integer assignedTo, final Integer statusCode, final Date createdDate, final Integer createdbyId,
			final String sourceOfTicket, final Date dueTime) {
		
		this.clientId = clientId;
		this.priority = priority;
		this.ticketDate = ticketDate;
		this.problemCode = problemCode;
		this.description = description;
		this.status = "OPEN";
		this.statusCode = statusCode;
		this.source = "Manual";
		this.resolutionDescription = resolutionDescription;
		this.assignedTo = assignedTo;	
		this.createdDate = DateUtils.getDateOfTenant();
		this.createdbyId = null;
		this.sourceOfTicket = sourceOfTicket;
		this.dueDate = dueTime;
		
	}

	public String getSource() {
		return source;
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

	public Integer getProblemCode() {
		return problemCode;
	}

	public String getDescription() {
		return description;
	}

	public Date getTicketDate() {
		return ticketDate;
	}

	public String getStatus() {
		return status;
	}
	
	public Integer getStatusCode() {
		return statusCode;
	}

	public String getResolutionDescription() {
		return resolutionDescription;
	}

	public Integer getAssignedTo() {
		return assignedTo;
	}
	
	public Date getCreatedDate() {
		return createdDate;
	}

	public void updateTicket(final TicketMasterCommand command) {
		this.status = command.getStatus();
		this.assignedTo = command.getAssignedTo();
		this.priority = command.getPriority();
		this.problemCode = command.getProblemCodeId();
	}

	public void closeTicket(final JsonCommand command, final Long userId) {
		
		this.status = "CLOSED";
	    this.statusCode = Integer.parseInt(command.stringValueOfParameterNamed("status"));
		this.resolutionDescription = command.stringValueOfParameterNamed("resolutionDescription");
		this.closedDate = DateUtils.getDateOfTenant();
		this.lastModifyId = userId;
		this.lastModifydate = DateUtils.getDateOfTenant();
		
	}
	
	public Date getClosedDate() {
		return closedDate;
	}

	/**
	 * @return the createdbyId
	 */
	public Long getCreatedbyId() {
		return createdbyId;
	}

	/**
	 * @param createdbyId the createdbyId to set
	 */
	public void setCreatedbyId(final Long createdbyId) {
		this.createdbyId = createdbyId;
	}
	
}