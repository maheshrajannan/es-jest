package com.tryout.jest;

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

import com.tryout.jest.domain.Build;

public class RunMe {
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
                RunMe.deleteTestIndex(jestClient);
                RunMe.createTestIndex(jestClient);
                RunMe.indexSomeData(jestClient);
                RunMe.readAllData(jestClient);
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
        searchSourceBuilder.query(QueryBuilders.termQuery("userName", "mrajann"));

        Search search = new Search.Builder(searchSourceBuilder.toString())
                .addIndex(HISTORY_INDEX_NAME).addType(BUILDS_TYPE_NAME).build();
        System.out.println(searchSourceBuilder.toString());
        JestResult result = jestClient.execute(search);
        List<Build> builds = result.getSourceAsObjectList(Build.class);
        for (Build build : builds) {
            System.out.println(build);
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
        final Build build1 = new Build("mrajann", "Build1: do u see this - "
                + (new Date()));
        Index index = new Index.Builder(build1).index(HISTORY_INDEX_NAME)
                .type(BUILDS_TYPE_NAME).build();
        jestClient.execute(index);

        // Asynch index
        final Build build2 = new Build("mrajann", "Build2: do u see this - "
                + (new Date()));
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
        final Build build3 = new Build("mrajann", "Build3: do u see this - "
        		+ (new Date()));
        final Build build4 = new Build("mrajann", "Build4: do u see this - "
        		+ (new Date()));
        Bulk bulk = new Bulk.Builder()
                .addAction(
                        new Index.Builder(build3).index(HISTORY_INDEX_NAME)
                                .type(BUILDS_TYPE_NAME).build())
                .addAction(
                        new Index.Builder(build4).index(HISTORY_INDEX_NAME)
                                .type(BUILDS_TYPE_NAME).build()).build();
        JestResult result = jestClient.execute(bulk);

        Thread.sleep(2000);

        System.out.println(result.toString());
    }
}
