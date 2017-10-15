package org.mifosplatform.portfolio.planservice.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.planservice.data.PlanServiceData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class PlanServiceReadPlatformServiceImpl implements PlanServiceReadPlatformService{
	
	private final JdbcTemplate jdbcTemplate;
	
	private final PlatformSecurityContext context;
	
	@Autowired
	public PlanServiceReadPlatformServiceImpl(final PlatformSecurityContext context,final TenantAwareRoutingDataSource dataSource){
		this.context=context;
		this.jdbcTemplate=new JdbcTemplate(dataSource);
		
	}

	@Override
	public Collection<PlanServiceData> retrieveClientPlanService(Long clientId,String serviceType, String category) {

		planServiceMapper mapper = new planServiceMapper();
		String sql = "select " + mapper.schema();
		final StringBuilder sqlBuilder = new StringBuilder(200);
		sqlBuilder.append(sql);
		if(category!=null){
			sqlBuilder.append(" AND sd.category='"+category+"' ");
		}
		sqlBuilder.append(" GROUP BY s.id");
		return this.jdbcTemplate.query(sqlBuilder.toString(), mapper, new Object[] {clientId,serviceType});

	}

	protected static final class planServiceMapper implements RowMapper<PlanServiceData> {

		@Override
		public PlanServiceData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum)
				throws SQLException {
			Long serviceId=rs.getLong("serviceId");
			Long clientId=rs.getLong("clientId");
			String serviceName = rs.getString("serviceName");
			String channelName = rs.getString("channelName");
			String logo=rs.getString("logo");
			String url=rs.getString("serviceIdentification");
			String category=rs.getString("category");
			String subCategory=rs.getString("subCategory");

       
			return new PlanServiceData(serviceId,clientId,serviceName,logo,url,channelName,category,subCategory);

		}


		public String schema() {
			return " s.id AS serviceId,o.client_id AS clientId,s.service_code AS channelName,s.service_description AS serviceName,sd.image AS logo," +
					"sd.service_identification AS serviceIdentification,sd.category as category,sd.sub_category as subCategory FROM b_orders o," +
					"b_plan_detail p,b_service s,b_prov_service_details sd WHERE o.client_id = ? AND p.plan_id = o.plan_id AND s.service_code = p.service_code" +
					" AND s.service_type = ? AND s.id = sd.service_id AND o.order_status = 1 ";
		}
	}
	
	@Override
	public Collection<PlanServiceData> retrieveClientPlanService(Long clientId,String serviceType, Boolean isCategoryOnly) {

		planServiceMapperForCategories mapper = new planServiceMapperForCategories();
		String sql = "select " + mapper.schema();
		return this.jdbcTemplate.query(sql, mapper, new Object[] {clientId,serviceType});
	}
	
	protected static final class planServiceMapperForCategories implements RowMapper<PlanServiceData> {

		@Override
		public PlanServiceData mapRow(final ResultSet rs, final int rowNum)
				throws SQLException {
			
			String category=rs.getString("category");
			return new PlanServiceData(null, null, null, null, null, null, category, null);
			
		}


		public String schema() {
			return " distinct sd.category as category FROM b_orders o," +
					"b_plan_detail p,b_service s,b_prov_service_details sd WHERE o.client_id = ? AND p.plan_id = o.plan_id AND s.service_code = p.service_code" +
					" AND s.service_type = ? AND s.id = sd.service_id AND o.order_status = 1  GROUP BY s.id";
		}
	}
}
