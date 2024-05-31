public class Cli {
    static final String programName = "card";

    private String link;
    private String language;

    public String link() {
        return this.link;
    }

    public String language() {
        return this.language;
    }

    private static String help() {
        return new String("""
                 [--Russian] <url>      Creates deck from a Russian video or playlist
                 [--Spanish] <url>      Creates deck from a Spanish video or playlist
                """);

    }

    public Cli(String[] args) {
        if (args.length < 2) {
            String helpMenu = help();
            System.out.printf("%s: Missing Argument(s)\nusage:\n%s", programName, helpMenu);
            System.exit(1);
        } else if (args.length > 3) {
            String helpMenu = help();
            System.out.printf("%s: Found unexpected arguments(s)\nusage:\n%s", programName, helpMenu);
            System.exit(1);
        }

        if (!args[0].toLowerCase().matches("--(russian|spanish)")) {
            System.out.printf("Unknown language `%s`\n", args[0]);
            System.exit(1);

        }
        language = args[0].toLowerCase().substring(2);

        if (!args[1].matches("https:\\/\\/(youtu\\.be|www\\.youtube\\.com|youtube\\.com).*")) {
            System.out.printf("Expected URL found `%s`\n", args[1]);
            System.exit(1);
        }

        link = args[1];
    }

}
