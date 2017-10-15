package org.mifosplatform.crm.clientprospect.data;

public class ProspectDetailAssignedToData {

	private Long assignId;
	private String assignedTo;

	public ProspectDetailAssignedToData() {
	}

	public ProspectDetailAssignedToData(final Long assignedId,
			final String assignedTo) {

		this.assignId = assignedId;
		this.assignedTo = assignedTo;
	}

	public Long getAssignId() {
		return assignId;
	}

	public void setAssignId(Long assignId) {
		this.assignId = assignId;
	}

	public String getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(String assignedTo) {
		this.assignedTo = assignedTo;
	}

}
