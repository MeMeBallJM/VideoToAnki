import java.net.URI;
import java.net.http.HttpResponse.*;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.net.http.*;

public class Client {

        public static String httpGetSync(String uri, HttpClient client) throws Exception {
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(new URI(uri))
                                .header("User-Agent", "firefox")
                                .build();

                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

                return response.body();
        }

        public static CompletableFuture<HttpResponse<String>> httpGet(String uri, HttpClient client) throws Exception {
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(new URI(uri))
                                .header("User-Agent", "firefox")
                                .build();

                CompletableFuture<HttpResponse<String>> response = client.sendAsync(request, BodyHandlers.ofString());

                return response;
        }

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
