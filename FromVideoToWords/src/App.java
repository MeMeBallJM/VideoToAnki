import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App {

    static final String programName = "card";

    public static String help() {
        return new String("""
                 [--video] <url>      Creates deck using a youtube video
                 [--playlist] <url>   Creates deck using a youtube playlist
                """);

    }

    public static String httpGet(String uri) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(uri))
                .build();

        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

        return response.body().toString();
    }

    public static String prompt(String[] args) {
        if (args.length == 0) {
            String helpMenu = help();
            System.out.printf("%s: Missing Argument(s)\nusage:\n%s", programName, helpMenu);
            System.exit(1);
        }

        String link = null;

        boolean isFlag = args[0].matches("--.*");
        if (isFlag) {
            String flag = args[0];

            boolean knownFlag = flag.matches("--(playlist|video|help)");

            if (!knownFlag) {
                System.out.printf("Unknown flag `%s`\n", flag);
                System.exit(1);
            }

            if (flag.matches("--help")) {
                String helpMenu = help();
                System.out.println(helpMenu);
                System.exit(0);
            }

            if (flag.matches("--(playlist|video)")) {

                if (args.length != 2) {
                    System.out.printf("%s: Missing URL\nusage:\n%s", programName, help());
                    System.exit(1);
                }

                link = args[1];
            }
        } else {
            link = args[0];
        }

        if (!link.matches("https:\\/\\/(youtu\\.be|www\\.youtube\\.com|youtube\\.com).*")) {
            System.out.printf("Expected URL found `%s`\n", link);
            System.exit(1);
        }
        return link;

    }

    public static String videoStub(String link) {
        String pattern = "(?:watch\\?v=|youtu\\.be\\/|\\?list=)([A-z|0-9|-]*).*";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(link);
        matcher.find();

        String stub = matcher.group(1);

        return stub;
    }

    public static ArrayList<String> videos(String link) throws Exception {

        ArrayList<String> stubs = new ArrayList<>();

        if (!link.matches(".*playlist.*")) {
            stubs.add(videoStub(link));
        } else {
            System.out.println(link);
            String reqUrl = String.format("https://www.youtube.com/playlist?list=%s", videoStub(link));
            String response = httpGet(reqUrl);

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
        String url = prompt(args);
        // System.out.println(url);
        // System.out.println(videoStub(url));
        for (String link : videos(url)) {
            System.out.println(link);
        }

    }

}
