package org.mifosplatform.crm.ticketmaster.service;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.crm.ticketmaster.data.ClientTicketData;
import org.mifosplatform.crm.ticketmaster.data.TicketMasterData;
import org.mifosplatform.crm.ticketmaster.data.UsersData;
import org.mifosplatform.crm.ticketmaster.domain.PriorityType;
import org.mifosplatform.crm.ticketmaster.domain.PriorityTypeEnum;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.core.service.PaginationHelper;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.useradministration.domain.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class TicketMasterReadPlatformServiceImpl  implements TicketMasterReadPlatformService{
	
	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;
	private final PaginationHelper<ClientTicketData> paginationHelper = new PaginationHelper<ClientTicketData>();

	@Autowired
	public TicketMasterReadPlatformServiceImpl(final PlatformSecurityContext context,
			final TenantAwareRoutingDataSource dataSource) {
		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<UsersData> retrieveUsers() {
		context.authenticatedUser();

		final UserMapper mapper = new UserMapper();

		final String sql = "select " + mapper.schema();

		return this.jdbcTemplate.query(sql, mapper, new Object[] {});
	}

	private static final class UserMapper implements
			RowMapper<UsersData> {

		public String schema() {
			return "u.id as id,u.username as username from m_appuser u where u.is_deleted=0";

		}

		@Override
		public UsersData mapRow(ResultSet resultSet, int rowNum)
				throws SQLException {

			final Long id = resultSet.getLong("id");
			final String username = resultSet.getString("username");

			final UsersData data = new UsersData(id, username);

			return data;

		}

	}
	
	@Override
	public Page<ClientTicketData> retrieveAssignedTicketsForNewClient(SearchSqlQuery searchTicketMaster, String statusType) {
		final AppUser user = this.context.authenticatedUser();
		
		final String hierarchy = user.getOffice().getHierarchy();
        
        
        final String hierarchySearchString = hierarchy + "%";
		
		final UserTicketsMapperForNewClient mapper = new UserTicketsMapperForNewClient();
				
		StringBuilder sqlBuilder = new StringBuilder(200);
        sqlBuilder.append("select ");
        sqlBuilder.append(mapper.userTicketSchema());
        sqlBuilder.append(" where tckt.id IS NOT NULL and o.hierarchy like ? ");
        
        String sqlSearch = searchTicketMaster.getSqlSearch();
        String extraCriteria = "";
	    if (sqlSearch != null) {
	    	sqlSearch = sqlSearch.trim();
	    	extraCriteria = " and ((select display_name from m_client where id = tckt.client_id) like '%"+sqlSearch+"%' OR" 
	    			+ " (select mcv.code_value from m_code_value mcv where mcv.id = tckt.problem_code) like '%"+sqlSearch+"%' OR"
	    			+ " tckt.status like '%"+sqlSearch+"%' OR"
	    			+ " (select user.username from m_appuser user where tckt.assigned_to = user.id) like '%"+sqlSearch+"%')";
	    }
	    if(statusType != null){
	    	extraCriteria =" and tckt.status='"+statusType+"'";
	    }
	    sqlBuilder.append(extraCriteria);
	    
        if (searchTicketMaster.isLimited()) {
            sqlBuilder.append(" limit ").append(searchTicketMaster.getLimit());
        }

        if (searchTicketMaster.isOffset()) {
            sqlBuilder.append(" offset ").append(searchTicketMaster.getOffset());
        }
		
		return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()", sqlBuilder.toString(),
	            new Object[] {hierarchySearchString}, mapper);
		
	}
	
	@Override
	public List<TicketMasterData> retrieveClientTicketDetails(final Long clientId) {
		try {
				final ClientTicketMapper mapper = new ClientTicketMapper();

				final String sql = "select " + mapper.clientOrderLookupSchema() + " and tckt.client_id= ? order by tckt.id DESC ";

				return jdbcTemplate.query(sql, mapper, new Object[] { clientId});
			} catch (EmptyResultDataAccessException e) {
				return null;
			  }

	}

	private static final class ClientTicketMapper implements RowMapper<TicketMasterData> {

		public String clientOrderLookupSchema() {
				
		return "tckt.id as id, tckt.priority as priority, tckt.ticket_date as ticketDate, tckt.assigned_to as userId,tckt.source_of_ticket as sourceOfTicket, "
					+" tckt.problem_code as problemCode, tckt.status_code as statusCode, tckt.due_date as dueDate," +
					"(SELECT code_value FROM m_code_value mcv WHERE  tckt.status_code = mcv.id) AS ticketstatus," +
					" tckt.description as description,tckt.resolution_description as resolutionDescription, "
			        + " (select code_value from m_code_value mcv where tckt.problem_code=mcv.id)as problemDescription," 
					+ " tckt.status as status, "
			        + " (select m_appuser.username from m_appuser "
                    +		" inner join b_ticket_details td on td.assigned_to = m_appuser.id"
                    + " where td.id = (select max(id) from b_ticket_details where b_ticket_details.ticket_id = tckt.id)) as assignedTo,"
			        + " (select comments FROM b_ticket_details details where details.ticket_id =tckt.id and "
			        + " details.id=(select max(id) from b_ticket_details where b_ticket_details.ticket_id = tckt.id)) as lastComment"
			        + " from b_ticket_master tckt, m_appuser user where tckt.assigned_to = user.id"; 
		}

		@Override
		public TicketMasterData mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {

			final Long id = resultSet.getLong("id");
			final String priority = resultSet.getString("priority");
			final String status = resultSet.getString("status");
			final String LastComment = resultSet.getString("LastComment");
			final String problemDescription = resultSet.getString("problemDescription");
			final String assignedTo = resultSet.getString("assignedTo");
			final String usersId = resultSet.getString("userId");
			final LocalDate ticketDate = JdbcSupport.getLocalDate(resultSet, "ticketDate");
			final int userId = new Integer(usersId);
			final String sourceOfTicket = resultSet.getString("sourceOfTicket");
			final Date dueDate = resultSet.getTimestamp("dueDate");
			final String description = resultSet.getString("description");
			final String resolutionDescription = resultSet.getString("resolutionDescription");
			final String ticketstatus = resultSet.getString("ticketstatus");
			final Integer problemCode = resultSet.getInt("problemCode");
			final Integer statusCode = resultSet.getInt("statusCode");
			
			return new TicketMasterData(id, priority, status, userId, ticketDate, LastComment, problemDescription, assignedTo, sourceOfTicket,
					dueDate, description, resolutionDescription, problemCode, statusCode,ticketstatus);
		}
	}

	@Override
	public TicketMasterData retrieveSingleTicketDetails(final Long clientId, final Long ticketId) {
		try {
				final ClientTicketMapper mapper = new ClientTicketMapper();
				final String sql = "select " + mapper.clientOrderLookupSchema() + " and tckt.client_id= ? and tckt.id=?";
				return jdbcTemplate.queryForObject(sql, mapper, new Object[] {clientId, ticketId});
			} catch (EmptyResultDataAccessException e) {
					return null;
			}

	}

	@Override
	public List<EnumOptionData> retrievePriorityData() {
		EnumOptionData low = PriorityTypeEnum.priorityType(PriorityType.LOW);
		EnumOptionData medium = PriorityTypeEnum.priorityType(PriorityType.MEDIUM);
		EnumOptionData high = PriorityTypeEnum.priorityType(PriorityType.HIGH);
		List<EnumOptionData> priorityType = Arrays.asList(low, medium, high);
		return priorityType;
	}

	@Override
	public List<TicketMasterData> retrieveClientTicketHistory(final Long ticketId) {
		
		context.authenticatedUser();
		final TicketDataMapper mapper = new TicketDataMapper();
		String sql = "select " + mapper.schema() + " where t.ticket_id=tm.id and t.ticket_id=? order by t.id DESC";
		return this.jdbcTemplate.query(sql, mapper, new Object[] { ticketId});
	}
	
	private static final class TicketDataMapper implements
					RowMapper<TicketMasterData> {

		public String schema() {
				return " t.id AS id,t.created_date AS createDate,user.username AS assignedTo,t.comments as description," +
						" t.attachments AS attachments  FROM b_ticket_master tm , b_ticket_details t  "
						+" inner join m_appuser user on user.id = t.assigned_to ";

		}

		@Override
		public TicketMasterData mapRow(ResultSet resultSet, int rowNum)
						throws SQLException {

			final Long id = resultSet.getLong("id");
			final LocalDate createdDate = JdbcSupport.getLocalDate(resultSet, "createDate");
			final String assignedTo = resultSet.getString("assignedTo");
			final String description = resultSet.getString("description");
			final String attachments = resultSet.getString("attachments");
			String fileName=null;
			if(attachments!=null){
				File file=new File(attachments);
				fileName=file.getName();
			}
			final TicketMasterData data = new TicketMasterData(id, createdDate, assignedTo, description, fileName);

			return data;
		}

	}
				
	private static final class UserTicketsMapperForNewClient implements RowMapper<ClientTicketData> {
				
		public String userTicketSchema() {
					
			return " SQL_CALC_FOUND_ROWS tckt.id AS id,tckt.client_id AS clientId,mct.display_name as clientName,tckt.priority AS priority,"+
							"tckt.status AS status,tckt.ticket_date AS ticketDate,"+
							"(SELECT user.username FROM m_appuser user WHERE tckt.createdby_id = user.id) AS created_user,"+
							"tckt.assigned_to AS userId,"+
							"(SELECT comments FROM b_ticket_details x WHERE tckt.id = x.ticket_id AND x.id = (SELECT max(id) FROM b_ticket_details y WHERE tckt.id = y.ticket_id)) AS LastComment,"+
							"(SELECT mcv.code_value FROM m_code_value mcv WHERE mcv.id = tckt.problem_code) AS problemDescription,"+
							"(SELECT user.username FROM m_appuser user WHERE tckt.assigned_to = user.id) AS assignedTo,"+
							"CONCAT(TIMESTAMPDIFF(day, tckt.ticket_date, Now()), ' d ', MOD(TIMESTAMPDIFF(hour, tckt.ticket_date, Now()), 24), ' hr ',"+
							"MOD(TIMESTAMPDIFF(minute, tckt.ticket_date, Now()), 60), ' min ') AS timeElapsed,"+
							"IFNull((SELECT user.username FROM m_appuser user WHERE tckt.lastmodifiedby_id = user.id),'Null') AS closedby_user "+
							"FROM b_ticket_master tckt left join m_client mct on mct.id = tckt.client_id "+
							"left join m_office o on o.id = mct.office_id ";
      
		}

		@Override
		public ClientTicketData mapRow(ResultSet resultSet, int rowNum) throws SQLException {
					
			final Long id = resultSet.getLong("id");
			final String priority = resultSet.getString("priority");
			final String status = resultSet.getString("status");
			final Long userId = resultSet.getLong("userId");
			final LocalDate ticketDate = JdbcSupport.getLocalDate(resultSet, "ticketDate");
			final String lastComment = resultSet.getString("LastComment");
			final String problemDescription = resultSet.getString("problemDescription");
			final String assignedTo = resultSet.getString("assignedTo");
			final Long clientId = resultSet.getLong("clientId");
			final String timeElapsed = resultSet.getString("timeElapsed");
			final String clientName = resultSet.getString("clientName");
			final String createUser = resultSet.getString("created_user");
			final String closedByuser = resultSet.getString("closedby_user");
					
			return new ClientTicketData(id, priority, status, userId, ticketDate, lastComment, problemDescription,
					assignedTo, clientId, timeElapsed, clientName, createUser, closedByuser);
		}
				
	}
			
	@Override
	public TicketMasterData retrieveTicket(final Long clientId, final Long ticketId) {

		try {
				final ClientTicketMapper mapper = new ClientTicketMapper();
				final String sql = "select " + mapper.clientOrderLookupSchema() + " and tckt.client_id= ? and tckt.id=?";
				
				return jdbcTemplate.queryForObject(sql, mapper, new Object[] { clientId, ticketId});
			} catch (EmptyResultDataAccessException e) {
					return null;
			}
	}
	
}