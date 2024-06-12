import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.nio.file.Path;
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

            ArrayList<String> examples = dictionary.examples(word);

            String front = String.format("%s [sound:%s.mp3]", word, word);

            String back = new String();
            for (String translation : translations) {
                back += String.format(", %s", translation);
            }
            back += String.format("<br><br> <img src=\"%s.jpeg\">", word);

            if (examples.size() >= 1) {
                String spilt[] = examples.get(0).split("\n");
                back += String.format("<br><br>%s<br>%s", spilt[0], spilt[1]);
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

    static public void imageAll(ArrayList<String> words, Dict dictionary, HttpClient client, String dst) {

        LoadingBar loadingBar = new LoadingBar("Fetching images", words.size() * 3);

        ArrayList<CompletableFuture<CompletableFuture<HttpResponse<Path>>>> linksFuture = new ArrayList<>();
        ArrayList<CompletableFuture<HttpResponse<Path>>> videosFuture = new ArrayList<>();

        for (String word : words) {
            String out = String.format("%s/%s.jpeg", dst, word);
            // System.out.printf("\n%s\n", out);
            linksFuture.add(dictionary.image(word, client, out));
            loadingBar.tick();
        }

        for (CompletableFuture<CompletableFuture<HttpResponse<Path>>> future : linksFuture) {
            try {
                loadingBar.tick();
                videosFuture.add(future.get(10, TimeUnit.SECONDS));
            } catch (InterruptedException | ExecutionException | CancellationException error) {
                // Do nothing
            } catch (TimeoutException error) {
                future.cancel(true);
            }

        }

        for (CompletableFuture<HttpResponse<Path>> future : videosFuture) {
            try {
                loadingBar.tick();
                future.get(10, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | CancellationException error) {
                // Do nothing
            } catch (TimeoutException error) {
                future.cancel(true);
            }

        }

        loadingBar.complete();

    }

    static public void audioAll(ArrayList<String> words, Dict dictionary, HttpClient client, String dst) {

        ArrayList<CompletableFuture<Void>> futures = new ArrayList<>();

        LoadingBar loadingBar = new LoadingBar("Fetching audio", words.size() * 2);
        for (String word : words) {
            futures.add(dictionary.pronunciation(word, client, dst));
            loadingBar.tick();
        }

        for (CompletableFuture<Void> future : futures) {
            loadingBar.tick();
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
