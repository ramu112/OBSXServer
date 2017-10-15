package org.mifosplatform.organisation.channel.service;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.core.service.PaginationHelper;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.organisation.channel.data.ChannelData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class ChannelReadPlatformServiceImpl implements ChannelReadPlatformService {

	private final JdbcTemplate jdbcTemplate;
	private final PaginationHelper<ChannelData> paginationHelper = new PaginationHelper<ChannelData>();
	
	
	@Autowired
	public ChannelReadPlatformServiceImpl( final TenantAwareRoutingDataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	//this is to retrive a particular record 
	@Override
	public ChannelData retrieveChannel(Long channelId) {

		try{
      	ChannelMapper channelMapper = new ChannelMapper();
		String sql = "SELECT "+channelMapper.schema()+" WHERE c.is_deleted = 'N' AND c.id = ?";
		return jdbcTemplate.queryForObject(sql, channelMapper,new Object[]{channelId});
		}catch(EmptyResultDataAccessException ex){
			return null;
		}
	
	}

	@Override
	public Page<ChannelData> retrieveChannel(SearchSqlQuery searchChannel) {
		ChannelMapper channelMapper = new ChannelMapper();
		
		final StringBuilder sqlBuilder = new StringBuilder(200);
        sqlBuilder.append("select ");
        sqlBuilder.append(channelMapper.schema());
        sqlBuilder.append(" where c.id IS NOT NULL and c.is_deleted = 'N'");
        
        String sqlSearch = searchChannel.getSqlSearch();
        String extraCriteria = null;
	    if (sqlSearch != null) {
	    	sqlSearch=sqlSearch.trim();
	    	extraCriteria = " and (id like '%"+sqlSearch+"%' OR" 
	    			+ " channel_name like '%"+sqlSearch+"%' OR"
	    			+ " channel_category like '%"+sqlSearch+"%' OR"
	    			+ " channel_type like '%"+sqlSearch+"%' OR"
	    			+ " is-local_channel like '%"+sqlSearch+"%' OR"
	    			+ " is_hd_channel like '%"+sqlSearch+"%' OR"
	    			+ " channel_sequence like '%"+sqlSearch+"%' OR"
	    			+ " broadcaster_id like '%"+sqlSearch+"%' OR"
	    			+ " brc_name like '%"+sqlSearch+"%')";
	    }
        
        if (null != extraCriteria) {
            sqlBuilder.append(extraCriteria);
        }


        if (searchChannel.isLimited()) {
            sqlBuilder.append(" limit ").append(searchChannel.getLimit());
        }

        if (searchChannel.isOffset()) {
            sqlBuilder.append(" offset ").append(searchChannel.getOffset());
        }
		
		return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()",sqlBuilder.toString(),
		        new Object[] {}, channelMapper);
	}
	
	
	private class ChannelMapper implements RowMapper<ChannelData> {
	    @Override
		public ChannelData mapRow(ResultSet rs, int rowNum) throws SQLException {
			
	    	final Long id = rs.getLong("id");
			final String channelName = rs.getString("channelName");
			final String channelCategory = rs.getString("channelCategory");
		    final String channelType = rs.getString("channelType");
			final Boolean isLocalChannel = rs.getBoolean("isLocalChannel");
		    final Boolean isHdChannel = rs.getBoolean("isHdChannel");
			final Long channelSequence = rs.getLong("channelSequence");
			final Long broadcasterId = rs.getLong("broadcasterId");
			final String broadcasterName = rs.getString("broadcasterName");
			
			return new ChannelData(id, channelName, channelCategory, channelType, isLocalChannel, isHdChannel, channelSequence,broadcasterId,broadcasterName);
		}
	    
		public String schema() {
			
			return " c.id AS id, c.channel_name AS channelName, c.channel_category AS channelCategory, c.channel_type AS channelType, " +
				   " c.is_local_channel AS isLocalChannel, c.is_hd_channel AS isHdChannel, c.channel_sequence AS channelSequence,c.broadcaster_id AS broadcasterId,br.brc_name AS broadcasterName FROM b_channel c "+
				   " left join b_broadcaster br ON c.broadcaster_id = br.id ";
			
		}
	}
	

}
