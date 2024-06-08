package com.skyapi.weatherforecast.location;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.skyapi.weatherforecast.common.Location;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@DataJpaTest
@AutoConfigureTestDatabase(replace=Replace.NONE)
public class LocationCriteriaQueryTests {
	
	@Autowired
	private EntityManager entityManager;
	
	@Test
	public void testCriteriaQuery() {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Location> query = builder.createQuery(Location.class);
		
		Root<Location> root = query.from(Location.class);
		
		// Add WHERE
		Predicate predicate = builder.equal(root.get("countryCode"), "US");
		query.where(predicate);
		
		// ADD ORDER BY
		query.orderBy(builder.asc(root.get("cityName")));
		
		TypedQuery<Location> typedQuery = entityManager.createQuery(query);
		
		//ADD PAGINATION
		typedQuery.setFirstResult(0);
		typedQuery.setMaxResults(3);
	
		List<Location> resultList = typedQuery.getResultList();
		entityManager.close();
	
		assertThat(resultList).isNotEmpty();
		
		resultList.forEach(System.out::println);
	
	}
	
	

}
