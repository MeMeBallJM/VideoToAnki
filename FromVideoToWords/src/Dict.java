import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

// Genaric dictionary type
public abstract class Dict {

    public abstract CompletableFuture<ArrayList<String>> translate(String word);

    public abstract ArrayList<String> examples(String word);

    public abstract ArrayList<String[]> cases(String word);

    public abstract ArrayList<String[]> conjugation(String word);

    public abstract CompletableFuture<Void> pronunciation(String word, HttpClient client, String dst);

    // Grabs an image from the internet using the first image found by google
    public CompletableFuture<CompletableFuture<HttpResponse<Path>>> image(String word, HttpClient client, String dst) {

        String link = String.format("https://www.google.com/search?q=%s&tbm=isch", word);

        try {
            CompletableFuture<HttpResponse<String>> responseFuture = Client.httpGet(link, client);

            return responseFuture.thenApply(response -> {

                Document doc = Jsoup.parse(response.body());

                Elements imageLinks = doc.select("img");

                for (Element imageLinkAttr : imageLinks) {
                    String imageLink = imageLinkAttr.attr("src");

                    if (!imageLink.startsWith("https")) {
                        continue;
                    }

                    try {
                        return Client.httpGetFile(imageLink, client, dst);
                    } catch (Exception error) {
                        return CompletableFuture.completedFuture(null);
                    }

                }

                return null;

            });

        } catch (Exception error) {
            return CompletableFuture.completedFuture(null);
        }
    }

}
