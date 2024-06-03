import java.net.URI;
import java.net.http.HttpResponse.*;
import java.net.http.*;
import java.util.regex.*;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashSet;

public class App {

    static final String programName = "card";

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
            String reqUrl = String.format("https://www.youtube.com/playlist?list=%s", videoIds(link));
            String response = Client.httpGet(reqUrl, client);

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
        HashSet<String> videoIds = playlistVideoIds(prompt.link(), client);

        WordCompiler wordCompiler = new WordCompiler(videoIds, client);

        ArrayList<String> wordList = wordCompiler.wordList();

        Dict russianDict = new RussianDict();

        for (String word : wordList) {

            System.out.println(word);
            ArrayList<String> translations = russianDict.translate(word);

            for (String translation : translations) {
                System.out.println(translation);
            }
        }

    }
}
