/**
 * 
 */
package com.elastic.gateway;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.elastic.gateway.domain.CompletedBuild;

/**
 * @author maheshrajannan
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ElasticWrapperTest {
	private ElasticWrapper elasticWrapper = null;
	long startedMillis = System.currentTimeMillis() - 1000;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		elasticWrapper = new ElasticWrapper("builds", "buildhistory",
				"http://localhost:9200");
		startedMillis = System.currentTimeMillis() - 1000;
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	public void asserts(CompletedBuild build2, long startedMillis) {
		
		assertNotNull("Empty build object ", build2);

		assertNotNull("Empty build property id ", build2.getId());
		assertTrue("Empty build property id ",
				StringUtils.isNotEmpty(build2.getId()));

		assertNotNull("Empty build property buildNumber ",
				build2.getBuildNumber());
		assertTrue("Empty build property buildNumber "+build2,
				build2.getBuildNumber() > 0);

		assertNotNull("Empty build property projectName ",
				build2.getProjectName());
		assertTrue("Empty build property projectName ",
				StringUtils.isNotEmpty(build2.getProjectName()));

		assertNotNull("Empty build property startedAt ", build2.getStartedAt());

		assertNotNull("Empty build property startedBy", build2.getStartedBy());
		assertEquals("Empty build property startedBy ", build2.getStartedBy(),
				"mrajann");

		assertNotNull("Empty build property startedNode",
				build2.getStartedNode());
		assertEquals("Empty build property startedBy ", build2.getStartedNode(),
				"Jenkins Server");

		assertNotNull("Empty build property status", build2.getStatus());
		assertEquals("Empty build property status ", build2.getStatus(),
				"SUCCESS");

		assertNotNull("Empty build property timeTaken", build2.getTimeTaken());
		assertTrue("Empty build property timeTaken",
				(build2.getTimeTaken()-1000) < 100 );

	}

	/**
	 * Test method for
	 * {@link com.elastic.gateway.ElasticWrapper#syncIndex(com.elastic.gateway.domain.Indexable)}
	 * .
	 * 
	 * @throws Exception
	 */
	@Test
	public void test1SyncIndex() throws Exception {
		startedMillis = System.currentTimeMillis() - 1000;
		// Blocking index
		CompletedBuild build1 = CompletedBuild.getInstance("SampleProject",
				new Date(startedMillis), "mrajann");
		CompletedBuild build2 = elasticWrapper.syncIndex(build1);
		// TODO: configure log4j correctly.
		System.out.println("build2" + build2);
		asserts(build2, startedMillis);
	}

	/**
	 * Test method for
	 * {@link com.elastic.gateway.ElasticWrapper#readAllData(java.lang.Class, java.lang.String, java.lang.String)}
	 * .
	 * 
	 * @throws Exception
	 */
	@Test
	public void test2ReadAllData() throws Exception {
		//INFO: It takes approx 30 seconds to index.
		Thread.sleep(1000);
		List<CompletedBuild> completedBuilds = elasticWrapper
				.readAllData(CompletedBuild.class, "startedBy", "mrajann");
		assertNotNull("Empty result", completedBuilds);
		assertTrue("Empty result", completedBuilds.size() > 0);
		for (CompletedBuild completedBuild : completedBuilds) {
			asserts(completedBuild, startedMillis);
		}
	}

	/**
	 * Test method for
	 * {@link com.elastic.gateway.ElasticWrapper#deleteTestIndex(io.searchbox.client.JestClient)}
	 * .
	 * 
	 * @throws Exception
	 */
	@Test
	public void test3DeleteTestIndex() throws Exception {
		elasticWrapper.deleteTestIndex();
	}

}
