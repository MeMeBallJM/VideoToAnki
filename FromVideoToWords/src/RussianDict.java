import java.io.File;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
    public CompletableFuture<Void> pronunciation(String word, HttpClient client, String dst) {
        String link = String.format("https://api.openrussian.org/read/ru/%s", word);

        String fileName = String.format(
                "%s/%s.mp3", dst, word);

        try {
            return Client.httpGetFile(link, client, fileName).thenApply(result -> null);
        } catch (Exception error) {
            return new CompletableFuture<>();
        }
    }

    @Override
    public ArrayList<String> examples(String word) {

        try {

            String link = String.format("https://en.openrussian.org/ru/%s", word);

            Document doc = Jsoup.connect(link).get();

            ArrayList<String> examples = new ArrayList<>();

            ArrayList<String> russianText = new ArrayList<>();
            Elements russianSentences = doc.select("span.ru");
            for (Element russianSentence : russianSentences) {
                russianText.add(russianSentence.text());
            }

            ArrayList<String> englishText = new ArrayList<>();
            Elements englishSentences = doc.select("li.sentence span.tl");
            for (Element englishSentence : englishSentences) {
                englishText.add(englishSentence.text());
            }

            for (int i = 0; i < russianSentences.size(); i++) {
                examples.add(String.format("%s\n%s", russianText.get(i), englishText.get(i)));
            }

            return examples;

        } catch (IOException error) {
            return new ArrayList<>();
        }

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