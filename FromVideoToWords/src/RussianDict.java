import java.io.File;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RussianDict extends Dict {

    HttpClient client = HttpClient.newHttpClient();

    @Override
    public CompletableFuture<ArrayList<String>> translate(String word) {
        try {

            String link = String.format("https://en.openrussian.org/ru/%s", word);
            CompletableFuture<HttpResponse<String>> responseFuture = Client.httpGet(link, client);

            return responseFuture.thenApply(response -> {

                ArrayList<String> translations = new ArrayList<>();

                String body = response.body();
                String pattern = "<p class=\"tl\">([a-zA-Z ]+)";
                Pattern regex = Pattern.compile(pattern);
                Matcher matcher = regex.matcher(body);

                while (matcher.find()) {
                    translations.add(matcher.group(1));
                }

                return translations;

            });

        } catch (Exception error) {
            return CompletableFuture.completedFuture(null);
        }

    }

    @Override
    public CompletableFuture<Void> pronunciation(String word, HttpClient client) {
        String link = String.format("https://api.openrussian.org/read/ru/%s", word);

        String fileName = String.format(
                "/Users/joshualevymorton/Desktop/testing_media/%s.mp3", word);

        try {
            return Client.httpGetAudio(link, client, fileName).thenApply(result -> null);
        } catch (Exception error) {
            return new CompletableFuture<>();
        }
    }

    @Override
    public ArrayList<String> examples(String word) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ArrayList<String[]> conjugation(String word) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ArrayList<String[]> cases(String word) {
        // TODO Auto-generated method stub
        return null;
    }

}