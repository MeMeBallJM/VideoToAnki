import java.io.File;
import java.io.FileWriter;
import java.net.http.*;
import java.util.regex.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

public class App {

    static Scanner input = new Scanner(System.in);

    // Gets the video yt id
    // @param the link
    // @returns the yt id
    private static String videoId(String link) {
        String pattern = "(?:watch\\?v=|youtu\\.be\\/|\\?list=)([A-z|0-9|-]*).*";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(link);
        matcher.find();

        String stub = matcher.group(1);

        return stub;
    }

    // gets all the youtube ids from a yt playlist
    // @param the playlist link
    // @returns a list of yt ids
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

    // Prompts the user to search for a word that was added
    // @param nothing
    // @returns nothing
    private static void searchForWord() throws Exception {
        System.out.print("Find word: ");
        String word = input.nextLine();

        if (word.equals("exit")) {
            return;
        }

        Scanner fileOutput = new Scanner(new File("output.txt"));

        int count = 1;
        boolean wordFound = false;
        while (fileOutput.hasNextLine()) {
            String line = fileOutput.nextLine();
            if (line.startsWith(word + " ")) {
                System.out.printf("%s is the %dth most common word from the videos.\n", word, count);
                wordFound = true;
                break;
            }

            count += 1;
        }

        if (!wordFound) {
            System.out.printf("Couldn't find %s from the cards\n", word);
        }

        fileOutput.close();

        searchForWord();

    }

    public static void main(String[] args) throws Exception {
        Cli prompt = new Cli(args);

        if (!prompt.language().equals("russian")) {
            System.out.println("Currently only Russian is supported, Spanish coming soon.");
            System.exit(1);
        }

        HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build();
        HashSet<String> videoIds = playlistVideoIds(prompt.link(), client);

        WordCompiler wordCompiler = new WordCompiler(videoIds, client);

        ArrayList<String> wordList = wordCompiler.wordList();

        String dst = "/Users/joshualevymorton/Library/Application Support/Anki2/User 1/collection.media";

        Dict russianDict = new RussianDict();
        AnkiFormatter.audioAll(wordList, russianDict, client, dst);
        AnkiFormatter.imageAll(wordList, russianDict, client, dst);
        String output = AnkiFormatter.formatAll(wordList, russianDict);

        try {
            FileWriter file = new FileWriter("output.txt");
            file.write(output);
            file.close();
        } catch (Exception error) {
            System.out.println("Failed to create file");
        }

        searchForWord();

    }
}
