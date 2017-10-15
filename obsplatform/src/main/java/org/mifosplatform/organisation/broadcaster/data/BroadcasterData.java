package org.mifosplatform.organisation.broadcaster.data;

public class BroadcasterData {
	    
	private Long id;
	private String broadcasterCode;
	private String broadcasterName;
	private Long contactMobile;
	private Long contactNumber;
    private String contactName;
	private String email;
	private String address;
	private Long pin;

	
	
	
	public BroadcasterData(Long id, String broadcasterCode,String broadcasterName) {
		
		this.id = id;
		this.broadcasterCode = broadcasterCode;
		this.broadcasterName = broadcasterName;
	}



	public BroadcasterData(Long id, String broadcasterCode, String broadcasterName, Long contactMobile, Long contactNumber,
			String contactName, String email, String address, Long pin) {
		this.id = id;
		this.broadcasterCode = broadcasterCode;
        this.broadcasterName = broadcasterName;
        this.contactMobile = contactMobile;
        this.contactNumber = contactNumber;
		this.contactName = contactName;
		this.email = email;
		this.address = address;
		this.pin = pin;
	}
		
		
		
		public Long getId() {
			return id;
		}
		
		
		public String getbroadcasterCode(){
			return broadcasterCode;
		}
		
		
		
		public String getbroadcasterName(){
			return broadcasterName;
		}
		
		
		
		
		public Long getcontactMobile() {
			return contactMobile;
		}
		
		
		
		public Long getcontactNumber(){
			return contactNumber;
		}
		
		
		
		public String getcontactName(){
			return contactName;
		}
		
		
		public String getemail(){
			return email;
		}
		
		
		
		public String getaddress(){
			return address;
		}
		
		
		
		public Long getpin(){
			return pin;
		}
			
	}


