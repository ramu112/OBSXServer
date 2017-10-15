 package org.mifosplatform.crm.ticketmaster.data;

import org.joda.time.LocalDate;

public class ClientTicketData {
	
	private final Long id;
    private final String priority;
    private final String status;
    private final Long userId;
    private final LocalDate ticketDate;
    private final String lastComment;
    private final String problemDescription;
    private final String userName;
    private final Long clientId;
	private String timeElapsed;
	private Object clientName;
	private final String createUser;
	private final String closedByuser;
	
	public ClientTicketData(final Long id, final String priority, final String status, final Long assignedTo, final LocalDate ticketDate,
			final String lastComment, final String problemDescription, final String userName, final Long clientId) {
		
		this.id = id;
		this.priority = priority;
		this.status = status;
		this.userId = assignedTo;
		this.ticketDate = ticketDate;
		this.lastComment = lastComment;
		this.problemDescription = problemDescription;
		this.userName = userName;
		this.clientId = clientId;
		this.closedByuser = null;
		this.createUser = null;
	
	}
	public ClientTicketData(final Long id, final String priority, final String status, final Long assignedTo, final LocalDate ticketDate,
			final String lastComment, final String problemDescription, final String userName, final Long clientId,
			final String timeElapsed, final String clientName, final String createUser, final String closedByuser) {
	
		this.id = id;
		this.priority = priority;
		this.status = status;
		this.userId = assignedTo;
		this.ticketDate = ticketDate;
		this.lastComment = lastComment;
		this.problemDescription = problemDescription;
		this.userName = userName;
		this.clientId = clientId;
		this.timeElapsed = timeElapsed;
		this.clientName = clientName;
		this.createUser = createUser;
		this.closedByuser = closedByuser;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @return the priority
	 */
	public String getPriority() {
		return priority;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @return the userId
	 */
	public Long getUserId() {
		return userId;
	}

	/**
	 * @return the ticketDate
	 */
	public LocalDate getTicketDate() {
		return ticketDate;
	}

	/**
	 * @return the lastComment
	 */
	public String getLastComment() {
		return lastComment;
	}

	/**
	 * @return the problemDescription
	 */
	public String getProblemDescription() {
		return problemDescription;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @return the clientId
	 */
	public Long getClientId() {
		return clientId;
	}
	
	public String getTimeElapsed() {
		return timeElapsed;
	}
	
	public void setTimeElapsed(final String timeElapsed) {
		this.timeElapsed = timeElapsed;
	}
	
	public Object getClientName() {
		return clientName;
	}
	
	public void setClientName(final Object clientName) {
		this.clientName = clientName;
	}
	
	public String getCreateUser() {
		return createUser;
	}
	
	public String getClosedByuser() {
		return closedByuser;
	}

}