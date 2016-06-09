/**
 * 
 */
package com.elastic.gateway;

import java.util.Date;

import com.elastic.gateway.domain.CompletedBuild;

/**
 * @author maheshrajannan
 *
 */
public class ElasticWrapperTestData {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		ElasticWrapper elasticWrapper = null;
		elasticWrapper = new ElasticWrapper("builds", "buildhistory",
				"http://localhost:9200");
		// Blocking index
		int i = 0;
		while (i<10) {
			CompletedBuild build1 = CompletedBuild.getInstance("SampleProject",
					new Date(System.currentTimeMillis()), "mrajann");
			build1.setBuildNumber(i++);
			CompletedBuild build2 = elasticWrapper.syncIndex(build1);			
			// TODO: configure log4j correctly.
			System.out.println("build2" + build2);
		}
	}

}
