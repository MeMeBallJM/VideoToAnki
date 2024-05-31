import java.net.URI;
import java.net.http.HttpResponse.*;
import java.net.http.*;
import java.util.regex.*;
import java.util.HashSet;

public class App {

    static final String programName = "card";

    public static String httpGet(String uri, HttpClient client) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(uri))
                .build();

        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

        return response.body().toString();
    }

    public static String videoIds(String link) {
        String pattern = "(?:watch\\?v=|youtu\\.be\\/|\\?list=)([A-z|0-9|-]*).*";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(link);
        matcher.find();

        String stub = matcher.group(1);

        return stub;
    }

    public static HashSet<String> playlistVideoIds(String link, HttpClient client) throws Exception {

        HashSet<String> stubs = new HashSet<String>();

        if (!link.matches(".*playlist.*")) {
            stubs.add(videoIds(link));
        } else {
            System.out.println(link);
            String reqUrl = String.format("https://www.youtube.com/playlist?list=%s", videoIds(link));
            String response = httpGet(reqUrl, client);

            String pattern = "watch\\?v=([A-Za-z0-9_-|0-9]{11})";
            Pattern regex = Pattern.compile(pattern);
            Matcher matcher = regex.matcher(response);
            while (matcher.find()) {
                stubs.add(matcher.group(1));
            }
        }

        return stubs;
    }

    public static void main(String[] args) throws Exception {
        Cli prompt = new Cli(args);

        HttpClient client = HttpClient.newHttpClient();
        HashSet<String> e = playlistVideoIds(prompt.link(), client);
        for (var ee : e) {
            System.out.println(ee);
        }

    }

}
