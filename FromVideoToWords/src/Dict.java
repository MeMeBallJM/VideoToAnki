import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public abstract class Dict {

    public abstract CompletableFuture<ArrayList<String>> translate(String word);

    public abstract ArrayList<String> examples(String word);

    public abstract ArrayList<String[]> cases(String word);

    public abstract ArrayList<String[]> conjugation(String word);

    public abstract CompletableFuture<Void> pronunciation(String word, HttpClient client);

}
