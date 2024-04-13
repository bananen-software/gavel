package software.bananen.gavel.experimental.domaintopics;

import java.util.*;

public class DomainTopics {

    private final Map<String, Collection<Word>> domainTopics = new HashMap<>();

    public void addWord(final String packageName, final String word) {
        if (!domainTopics.containsKey(packageName)) {
            domainTopics.put(packageName, new ArrayList<>());
        }

        final Collection<Word> topics = domainTopics.get(packageName);

        final Optional<Word> existingWord =
                topics.stream()
                        .filter(topic -> topic.getWord().equals(word))
                        .findFirst();

        if (existingWord.isPresent()) {
            existingWord.get().inc();
        } else {
            Word newWord = new Word(word);

            newWord.inc();

            topics.add(newWord);
        }
    }

    public Collection<Map.Entry<String, Collection<Word>>> entries() {
        return domainTopics.entrySet();
    }
}
