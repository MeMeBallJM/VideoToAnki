import java.io.FileWriter;
import java.io.InputStream;
import java.net.http.*;
import java.util.regex.*;
import java.util.ArrayList;
import java.util.HashSet;

public class App {

    private static String videoId(String link) {
        String pattern = "(?:watch\\?v=|youtu\\.be\\/|\\?list=)([A-z|0-9|-]*).*";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(link);
        matcher.find();

        String stub = matcher.group(1);

        return stub;
    }

    private static HashSet<String> playlistVideoIds(String link, HttpClient client) throws Exception {

        HashSet<String> stubs = new HashSet<String>();

        if (!link.matches(".*playlist.*")) {
            stubs.add(videoId(link));
        } else {
            String reqUrl = String.format("https://www.youtube.com/playlist?list=%s", videoId(link));
            String response = Client.httpGetSync(reqUrl, client);

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

        HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build();
        HashSet<String> videoIds = playlistVideoIds(prompt.link(), client);

        WordCompiler wordCompiler = new WordCompiler(videoIds, client);

        ArrayList<String> wordList = wordCompiler.wordList();

        Dict russianDict = new RussianDict();
        AnkiFormatter.audioAll(wordList, russianDict, client);
        String output = AnkiFormatter.formatAll(wordList, russianDict);

        try {
            FileWriter file = new FileWriter("output.txt");
            file.write(output);
            file.close();
        } catch (Exception e) {
            // TODO: handle exception
        }

    }
}
