import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;

import java.io.IOException;

/**
 * SeqNo and primary_term
 * When updating a document, Elasticsearch checks:
 *** If the seq_no matches the current document’s sequence number.
 *** If the primary_term matches the current primary shard’s term.
 * If both match, the update is applied.
 * If either mismatches, Elasticsearch rejects the update with a 409 Conflict error
 */
public class ConcurrentUpdates {

    public static void main(String[] args) throws IOException {

        String serverUrl = "http://localhost:9200";

        RestClient restClient = RestClient.builder(HttpHost.create(serverUrl)).build();

        ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());

        ElasticsearchClient esClient = new ElasticsearchClient(transport);

        String searchText = "Shining";

        SearchResponse<Book> response = esClient.search(s -> s
                        .index("books")
                        //  .storedFields("_seq_no", "_primary_term") // Retrieve versioning info

                        .query(q -> q
                                .match(t -> t
                                        .field("title")
                                        .query(searchText)
                                )
                        ).seqNoPrimaryTerm(true),
                Book.class
        );

        System.out.println(response.hits().hits().size());

        Hit<Book> bookHit = response.hits().hits().stream().findFirst().get();

        /**
         * seqNo: monotonically increasing number assigned to a document every time it is changed (indexed, updated, or deleted)
         * Unique only within the same shard.
         * Helps track the order of operations for a document.
         * Used to detect conflicts in OCC.
         */
        Long seqNo = bookHit.seqNo();
        /**
         * A number that changes only when a primary shard is reassigned (e.g., node failure, shard relocation, or cluster restart).
         * Ensures updates are applied to the latest primary shard, avoiding stale updates.
         */
        Long primaryTerm = bookHit.primaryTerm();
        Book originalBook = bookHit.source();

        System.out.println("seqNo = " + seqNo + "; primaryTerm=" + primaryTerm );

        Book bookUpdate1 = new Book(originalBook.id(), originalBook.title(), originalBook.author() + "oi");

        Book bookUpdate2 = new Book(originalBook.id(), originalBook.title()+"hello", originalBook.author());

        System.out.println("Trying to update the first book = SeqNo = " + seqNo + " primary="+primaryTerm);
        esClient.update(u -> u
                        .index("books")
                        .id(bookUpdate1.id().toString())
                        .ifPrimaryTerm(primaryTerm)
                        .ifSeqNo(seqNo)
                        .doc(bookUpdate1),
                Book.class
        );

        System.out.println("Trying to update the first book = SeqNo = " + seqNo + " primary="+primaryTerm);
        esClient.update(u -> u
                        .index("books")
                        .id(bookUpdate2.id().toString())
                        .ifPrimaryTerm(primaryTerm)
                        .ifSeqNo(seqNo)
                        .doc(bookUpdate2),
                Book.class
        );

        esClient.close();

    }
}
