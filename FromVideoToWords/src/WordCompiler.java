import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Takes a bunch of yt ids and fetching the transcripts from the videos
public class WordCompiler {

    public HashMap<String, Integer> words = new HashMap<>();

    // Takes a set of video ids and gets transcripts
    // @param a set of video ids and a http client
    // @returns a Self
    public WordCompiler(HashSet<String> videoIds, HttpClient client) throws Exception {

        LoadingBar loadingBar = new LoadingBar("Fetching transcripts", videoIds.size());
        for (String videoId : videoIds) {

            String link = String.format("https://youtubetranscript.com/?server_vid2=%s", videoId);
            String response = Client.httpGetSync(link, client);

            createWordList(response, words);

            loadingBar.tick();
        }
        System.out.println();
    }

    // Returns the list of words from the videos in order of commoness
    // @param nothing
    // @returns the list of words
    public ArrayList<String> wordList() {

        ArrayList<String> wordList = new ArrayList<>();

        for (String word : words.keySet()) {
            boolean inserted = false;
            for (String other : wordList) {
                if (words.get(other) < words.get(word)) {
                    wordList.add(wordList.indexOf(other), word);
                    inserted = true;
                    break;
                }
            }

            if (!inserted) {
                wordList.add(word);
                inserted = true;
            }
        }

        return wordList;
    }

    // Used internally, gets the words from transcripts
    // @param response from a http, current words
    // @returns map of words and their counts (how often they are used)
    private HashMap<String, Integer> createWordList(String response, HashMap<String, Integer> running) {

        HashMap<String, Integer> words = running;

        String pattern = "<text start=\\\"[0-9|.]*\\\" dur=\\\"[0-9|.]*\">([^<]*)<\\/text>";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(response);

        while (matcher.find()) {
            String line = matcher.group(1).toLowerCase();
            String[] wordsInLine = line.split(" ");

            for (String word : wordsInLine) {
                words.put(word, words.getOrDefault(word, 0) + 1);
            }
        }

        return words;
    }
}
