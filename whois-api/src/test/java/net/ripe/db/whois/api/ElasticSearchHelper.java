package net.ripe.db.whois.api;

import net.ripe.db.whois.common.dao.jdbc.DatabaseHelper;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ElasticSearchHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseHelper.class);

    private String hostname;
    private int port;

    @Value("${elastic.host:localhost}")
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    @Value("${elastic.port:9200}")
    public void setPort(int port) {
        this.port = port;
    }

    public void setupElasticIndexes(final String indexName, final String metaDetaIndex) throws Exception {

        try(final RestHighLevelClient esClient = getEsClient()) {
            if(!isElasticRunning(esClient)) {
                return;
            }

            CreateIndexRequest whoisRequest = new CreateIndexRequest(indexName);
            esClient.indices().create(whoisRequest, RequestOptions.DEFAULT);

            CreateIndexRequest whoisMetaDataRequest = new CreateIndexRequest(metaDetaIndex);
            esClient.indices().create(whoisMetaDataRequest, RequestOptions.DEFAULT);
        }
    }

    public void resetElasticIndexes(final String indexName, final String metaDetaIndex) throws Exception {
        try(final RestHighLevelClient esClient = getEsClient()) {

            if(!isElasticRunning(esClient)) {
                return;
            }

            try {
                DeleteIndexRequest whoisRequest = new DeleteIndexRequest(indexName);
                esClient.indices().delete(whoisRequest, RequestOptions.DEFAULT);
            } catch (Exception ignored) {
            }

            try {
                DeleteIndexRequest metadataRequest = new DeleteIndexRequest(metaDetaIndex);
                esClient.indices().delete(metadataRequest, RequestOptions.DEFAULT);
            } catch (Exception ignored) {
            }
        }
    }

    @NotNull
    private RestHighLevelClient getEsClient() {
        return new RestHighLevelClient(RestClient.builder(new HttpHost(hostname, port)));
    }

    private boolean isElasticRunning(final RestHighLevelClient esClient) {
        try {
            return esClient.ping(RequestOptions.DEFAULT);
        } catch (Exception e) {
            LOGGER.warn("ElasticSearch is not running");
            return false;
        }
    }

}
