package software.bananen.gavel.experimental.domaintopics;

public class Word {

    private final String word;
    private int count = 0;

    public Word(String word) {
        this.word = word;
    }

    public String getWord() {
        return word;
    }

    public void inc() {
        count++;
    }

    public int count() {
        return count;
    }
}
