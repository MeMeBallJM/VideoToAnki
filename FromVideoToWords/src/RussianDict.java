import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RussianDict extends Dict {

    HttpClient client = HttpClient.newHttpClient();

    @Override
    public ArrayList<String> translate(String word) {
        ArrayList<String> translations = new ArrayList<>();
        try {

            String link = String.format("https://en.openrussian.org/ru/%s", word);
            String response = Client.httpGet(link, client);

            String pattern = "<p class=\"tl\">([a-zA-Z ]+)";
            Pattern regex = Pattern.compile(pattern);
            Matcher matcher = regex.matcher(response);

            while (matcher.find()) {
                translations.add(matcher.group(1));
            }

        } catch (Exception error) {
            return null;
        }

        return translations;
    }

    @Override
    public ArrayList<String[]> conjugation(String word) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ArrayList<String> examples(String word) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ArrayList<String[]> cases(String word) {
        // TODO Auto-generated method stub
        return null;
    }

}