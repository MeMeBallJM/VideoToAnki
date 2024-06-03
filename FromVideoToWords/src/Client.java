
import java.net.URI;
import java.net.http.HttpResponse.*;
import java.net.http.*;

public class Client {

    public static String httpGet(String uri, HttpClient client) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(uri))
                .build();

        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

        return response.body().toString();
    }

}
