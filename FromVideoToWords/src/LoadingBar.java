public class LoadingBar {

    private int done = 0;
    private int total;
    private String message;

    private double percentDone() {
        return (double) this.done / (double) this.total;

    }

    private String progressBar(int done, int total) {

        final int PROGRESS_BAR_LENGTH = 50;

        double percentDone = (double) done / (double) total;
        int barCount = (int) (percentDone * PROGRESS_BAR_LENGTH);

        String arrow = "";
        for (int i = 1; i < barCount; i++) {
            arrow += "=";
        }

        arrow += ">";

        return String.format("[%-50s]", arrow);
    }

    public LoadingBar(String message, int total) {
        this.total = total;
        this.message = message;
        tick();
    }

    public void tick() {
        System.out.printf("\r%s %d%% %s, %d/%d", message, (int) (100 * percentDone()), progressBar(done, total),
                done, total);
        this.done += 1;
    }

    public void complete() {
        while (done < total) {
            tick();
        }
    }

}