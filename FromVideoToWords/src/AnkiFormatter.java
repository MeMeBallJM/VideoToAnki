import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AnkiFormatter {

    static private CompletableFuture<String> format(String word, Dict dictionary) {

        CompletableFuture<ArrayList<String>> translationsFuture = dictionary.translate(word);

        return translationsFuture.thenApplyAsync(translations -> {

            if (translations == null || translations.size() == 0) {
                return null;
            }

            String front = String.format("%s [sound:%s.mp3]", word, word);

            String back = new String();
            for (String translation : translations) {
                back += String.format(", %s", translation);
            }

            return String.format("%s\t%s", front, back.substring(1));
        });
    }

    static public String formatAll(ArrayList<String> words, Dict dictionary) {

        LoadingBar loadingBar = new LoadingBar("Translating words", words.size());

        ArrayList<CompletableFuture<String>> futures = new ArrayList<>();

        for (String word : words) {
            futures.add(AnkiFormatter.format(word, dictionary));
        }

        ArrayList<String> formatteds = new ArrayList<>();

        for (CompletableFuture<String> future : futures) {

            loadingBar.tick();

            try {
                formatteds.add(future.get(60, TimeUnit.SECONDS));
            } catch (InterruptedException | ExecutionException | CancellationException error) {
                // DO NOTHING
            } catch (TimeoutException error) {
                for (CompletableFuture<String> f : futures) {
                    f.cancel(true);
                }
            }
        }

        String all = new String();

        for (String formatted : formatteds) {

            if (formatted != null) {
                all += formatted + "\n";
            }
        }

        System.out.println();

        return all;
    }

    static public void audioAll(ArrayList<String> words, Dict dictionary, HttpClient client) {

        ArrayList<CompletableFuture<Void>> futures = new ArrayList<>();

        LoadingBar loadingBar = new LoadingBar("Fetching audio", words.size());
        for (String word : words) {
            futures.add(dictionary.pronunciation(word, client));
            loadingBar.tick();
        }

        for (CompletableFuture<Void> future : futures) {
            try {
                future.get(3, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException | CancellationException error) {
                for (CompletableFuture<Void> f : futures) {
                    f.cancel(true);
                }
            }
        }

        System.out.println();
    }
}
