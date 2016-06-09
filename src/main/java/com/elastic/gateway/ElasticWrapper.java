package com.elastic.gateway;

import java.io.IOException;
import java.util.List;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import com.elastic.gateway.domain.Indexable;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.DeleteIndex;

/**
 * TODO: lot of null checks.
 * 
 * @author maheshrajannan
 *
 */
public class ElasticWrapper {
	/**
	 * At instance level for a good reason.
	 */
	private JestClient jestClient = null;
	/**
	 * say "buildhistory" in small caps.
	 */
	private String history;
	/**
	 * say "build".
	 */
	private String typeName;

	/**
	 * "http://localhost:9200"
	 */
	private String elasticServerUrl;

	@SuppressWarnings("unused")
	private ElasticWrapper() {
		
	}
	/**
	 * TODO: lot of null checks.
	 * 
	 * @param typeName
	 * @param history
	 *            all lower case like "buildhistory"
	 * @param elasticServerUrl
	 *            "http://localhost:9200"
	 * @throws IOException
	 */
	public ElasticWrapper(String typeName, String history,
			String elasticServerUrl) throws IOException {
		this.history = history;
		this.typeName = typeName;
		this.elasticServerUrl = elasticServerUrl;
		HttpClientConfig clientConfig = new HttpClientConfig.Builder(
				elasticServerUrl).multiThreaded(true).build();
		JestClientFactory factory = new JestClientFactory();
		factory.setHttpClientConfig(clientConfig);
		jestClient = factory.getObject();
		// create new index (if u have this in elasticsearch.yml and prefer
		// those defaults, then leave this out
		Settings.Builder settings = Settings.settingsBuilder();
		//TODO: move these to properties files.
		settings.put("number_of_shards", 3);
		settings.put("number_of_replicas", 0);
		jestClient.execute(new CreateIndex.Builder(history)
				.settings(settings.build().getAsMap()).build());
	}

	/**
	 * 
	 * @param type
	 * @param searchTerm
	 * @param searchValue
	 * @return
	 * @throws Exception
	 */
	public <T> List<T> readAllData(Class<T> typeClass, String searchTerm,
			String searchValue) throws Exception {

		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder
				.query(QueryBuilders.termQuery(searchTerm, searchValue));

		Search search = new Search.Builder(searchSourceBuilder.toString())
				.addIndex(history).addType(typeName).build();
		System.out.println(searchSourceBuilder.toString());
		JestResult jestResult = jestClient.execute(search);
		List<T> results = jestResult.getSourceAsObjectList(typeClass);
		for (T result : results) {
			System.out.println(result);
		}
		return results;
	}

	/**
	 * @param jestClient
	 * @throws Exception
	 */
	public void deleteTestIndex() throws Exception {
		DeleteIndex deleteIndex = new DeleteIndex.Builder(this.history).build();
		JestResult jestResult = jestClient.execute(deleteIndex);
		System.out.println("Successfully delted " + this.history + " result is " + jestResult);
	}

	/**
	 * @param <T>
	 * @param jestClient
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public <T extends Indexable> T syncIndex(T data) throws Exception {
		Index index = new Index.Builder(data).index(this.history)
				.type(this.typeName).build();
		DocumentResult result = jestClient.execute(index);
		System.out.println("Indexed " + data + " as " + result);
		data.setId(result.getId());
		System.out.println("After Setting id " + data);
		return data;
	}

}
