import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Result;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {

    RestClient restClient = RestClient
            .builder(HttpHost.create("http://localhost:9200"))
            .build();
    ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
    ElasticsearchClient client = new ElasticsearchClient(transport);

    // Use the client...
    Person person = new Person(20, "Mark Doe", new Date(1471466076564L));
    IndexResponse response = client.index(i -> i
            .index("person")
            .id(person.fullName())
            .document(person));

        System.out.println(response);

    }

}
