import java.util.ArrayList;

public abstract class Dict {

    public enum Gender {
        Masculine,
        Feminine,
        Neuter,
        Plural,
    }

    public abstract ArrayList<String> translate(String word);

    // TODO find type for the audio;
    // public abstract pronunciation();

    public abstract ArrayList<String> examples(String word);

    public abstract ArrayList<String[]> cases(String word);

    public abstract ArrayList<String[]> conjugation(String word);

    // TODO actually make this, w/ the right type
    // public Image image(String word) {

    // }

}
