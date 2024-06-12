import java.net.URI;
import java.net.http.HttpResponse.*;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.net.http.*;

// Class to help make http calls
public class Client {

        // Gets content from page (blocking (very slow))
        public static String httpGetSync(String uri, HttpClient client) throws Exception {
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(new URI(uri))
                                .header("User-Agent", "firefox")
                                .build();

                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

                return response.body();
        }

        // Gets content from page (non-blocking (not very slow, but pain))
        public static CompletableFuture<HttpResponse<String>> httpGet(String uri, HttpClient client) throws Exception {
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(new URI(uri))
                                .header("User-Agent", "firefox")
                                .build();

                CompletableFuture<HttpResponse<String>> response = client.sendAsync(request, BodyHandlers.ofString());

                return response;
        }

        // Gets media (images or audio) from page (non-blocking (not very slow, but
        // pain))
        public static CompletableFuture<HttpResponse<Path>> httpGetFile(String uri, HttpClient client, String dst)
                        throws Exception {
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(new URI(uri))
                                .header("User-Agent", "firefox")
                                .build();

                Path path = Path.of(String.format(dst));
                CompletableFuture<HttpResponse<Path>> response = client.sendAsync(request, BodyHandlers.ofFile(path));

                return response;

        }

}
