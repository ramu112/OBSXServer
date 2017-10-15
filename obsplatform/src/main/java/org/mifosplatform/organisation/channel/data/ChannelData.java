package org.mifosplatform.organisation.channel.data;

import java.util.List;

import org.mifosplatform.organisation.broadcaster.data.BroadcasterData;

public class ChannelData {

	private Long   id;
	private String channelName;
	private String channelCategory;
	private String   channelType;
	private Boolean   isLocalChannel;
    private Boolean isHdChannel;
	private Long   channelSequence;
	private Long   broadcasterId;
	private String   broadcasterName;

	//for template purpose
	private List<BroadcasterData> broadcasterDatas;
	
	public ChannelData() {
	}

	public ChannelData(Long id, String channelName, String channelCategory, String channelType, Boolean isLocalChannel,
			Boolean isHdChannel, Long channelSequence, Long broadcasterId, String broadcasterName) {
		
		this.id = id;
		this.channelName = channelName;
		this.channelCategory = channelCategory;
		this.channelType = channelType;
		this.isLocalChannel = isLocalChannel;
		this.isHdChannel = isHdChannel;
		this.channelSequence = channelSequence;
		this.broadcasterId = broadcasterId;
		this.broadcasterName = broadcasterName;
	}
	

	
	public ChannelData(final List<BroadcasterData> broadcasterDatas) {
		this.broadcasterDatas = broadcasterDatas;
	}



	public Long getId() {
		return id;
	}
	
	
	public String getchannelName(){
		return channelName;
	}
	

	public String getchannelCategory(){
		return channelCategory;
	}

	public String getchannelType(){
		return channelType;
	}
	

	public Boolean getisLocalChannel(){
		return isLocalChannel;
	}
	
	public Boolean getisHdChannel(){
		return isHdChannel;
	}
	
	public Long getchannelSequence(){
		return channelSequence;
	}
    
	public Long getbroadcasterId(){
		return broadcasterId;
	}
	
	public String getbroadcasterName(){
		return broadcasterName;
	}


	public List<BroadcasterData> getBroadcasterDatas() {
		return broadcasterDatas;
	}



	public void setBroadcasterDatas(List<BroadcasterData> broadcasterDatas) {
		this.broadcasterDatas = broadcasterDatas;
	}
	
	
}
