package com.elastic.gateway;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.JestResultHandler;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Bulk;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.DeleteIndex;

import java.util.Date;
import java.util.List;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import com.elastic.gateway.domain.CompletedBuild;

public class ElasticWrapper {
    private static final String BUILDS_TYPE_NAME = "builds";
    //INFO: this should be all lower case.
    //INFO: when exception you have to use getItems to get the error.
    private static final String HISTORY_INDEX_NAME = "buildhistory";

    public static void main(String[] args) {
        try {
            // Get Jest client
            HttpClientConfig clientConfig = new HttpClientConfig.Builder(
                    "http://localhost:9200").multiThreaded(true).build();
            JestClientFactory factory = new JestClientFactory();
            factory.setHttpClientConfig(clientConfig);
            JestClient jestClient = factory.getObject();

            try {
                // run test index & searching
                ElasticWrapper.deleteTestIndex(jestClient);
                ElasticWrapper.createTestIndex(jestClient);
                ElasticWrapper.indexSomeData(jestClient);
                ElasticWrapper.readAllData(jestClient);
            } finally {
                // shutdown client
                jestClient.shutdownClient();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void createTestIndex(final JestClient jestClient)
            throws Exception {

        // create new index (if u have this in elasticsearch.yml and prefer
        // those defaults, then leave this out
        Settings.Builder settings = Settings.settingsBuilder();
        settings.put("number_of_shards", 3);
        settings.put("number_of_replicas", 0);
        jestClient.execute(new CreateIndex.Builder(HISTORY_INDEX_NAME).settings(
                settings.build().getAsMap()).build());
    }

    private static void readAllData(final JestClient jestClient)
            throws Exception {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //INFO: use the par
        searchSourceBuilder.query(QueryBuilders.termQuery("startedBy", "mrajann"));

        Search search = new Search.Builder(searchSourceBuilder.toString())
                .addIndex(HISTORY_INDEX_NAME).addType(BUILDS_TYPE_NAME).build();
        System.out.println(searchSourceBuilder.toString());
        JestResult result = jestClient.execute(search);
        List<CompletedBuild> completedBuilds = result.getSourceAsObjectList(CompletedBuild.class);
        for (CompletedBuild completedBuild : completedBuilds) {
            System.out.println(completedBuild);
        }
    }

    private static void deleteTestIndex(final JestClient jestClient)
            throws Exception {
        DeleteIndex deleteIndex = new DeleteIndex.Builder(HISTORY_INDEX_NAME)
                .build();
        jestClient.execute(deleteIndex);
    }

    private static void indexSomeData(final JestClient jestClient)
            throws Exception {
        // Blocking index
        final CompletedBuild build1 = CompletedBuild.getInstance(
        		"SampleProject", new Date(System.currentTimeMillis()-1000), "mrajann");
        Index index = new Index.Builder(build1).index(HISTORY_INDEX_NAME)
                .type(BUILDS_TYPE_NAME).build();
        jestClient.execute(index);

        // Asynch index
        Thread.sleep(250);
        final CompletedBuild build2 = CompletedBuild.getInstance(
        		"SampleProject",2, new Date(System.currentTimeMillis()-1000), "mrajann");
        index = new Index.Builder(build2).index(HISTORY_INDEX_NAME)
                .type(BUILDS_TYPE_NAME).build();
        jestClient.executeAsync(index, new JestResultHandler<JestResult>() {
            public void failed(Exception ex) {
            }

            public void completed(JestResult result) {
                build2.setId((String) result.getValue("_id"));
                System.out.println("completed==>>" + build2);
            }
        });

        // bulk index
        Thread.sleep(250);
        final CompletedBuild build3 = CompletedBuild.getInstance(
        		"SampleProject",3, new Date(System.currentTimeMillis()-1000), "mrajann");
        Thread.sleep(250);
        final CompletedBuild build4 = CompletedBuild.getInstance(
        		"SampleProject",4, new Date(System.currentTimeMillis()-1000), "mrajann");
        Bulk bulk = new Bulk.Builder()
                .addAction(
                        new Index.Builder(build3).index(HISTORY_INDEX_NAME)
                                .type(BUILDS_TYPE_NAME).build())
                .addAction(
                        new Index.Builder(build4).index(HISTORY_INDEX_NAME)
                                .type(BUILDS_TYPE_NAME).build()).build();
        JestResult result = jestClient.execute(bulk);

        Thread.sleep(500);

        System.out.println(result.toString());
    }
}
