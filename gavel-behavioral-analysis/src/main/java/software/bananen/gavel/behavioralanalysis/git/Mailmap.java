package software.bananen.gavel.behavioralanalysis.git;


import software.bananen.gavel.behavioralanalysis.Author;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class that can be used to map git authors based on a
 * <a href="https://git-scm.com/docs/gitmailmap">.mailmap file</a>.
 */
public final class Mailmap {

    private final Map<Author, Author> mapping = new ConcurrentHashMap<>();

    private static final Pattern PATTERN =
            Pattern.compile("^([^<]+)\\s+<([^>]+)>\\s+([^<]+)\\s+<([^>]+)>$");

    /**
     * Adds a mapping to the mailmap.
     *
     * @param from The author that should be mapped from.
     * @param to   The author that should be mapped to.
     */
    public void addMapping(final Author from, final Author to) {
        mapping.put(from, to);
    }

    /**
     * Maps the author to the appropriate mapping.
     * <p>
     * If no mapping has been defined for the passed author, then the passed in
     * author will be returned.
     *
     * @param from The author that the mapping should be retrieved for.
     * @return The mapped author.
     */
    public Author map(final Author from) {
        return mapping.getOrDefault(from, from);
    }

    /**
     * Checks whether the mailmap is empty.
     *
     * @return True if the mailmap is empty.
     */
    public boolean isEmpty() {
        return mapping.isEmpty();
    }

    /**
     * Retrieves the size of the mailmap.
     *
     * @return The size.
     */
    public int size() {
        return mapping.size();
    }

    /**
     * Parses a line from a .mailmap file and adds the mapping to the mailmap
     * instance.
     * <p>
     * This will filter out all comments (e.g. lines with a leading #).
     * <p>
     * Currently, this only supports a complete .mailmap entry with author name
     * and email on both sides.
     *
     * @param line The line that should be parsed.
     */
    public void parse(final String line) {
        //skipping comment if necessary
        if (line.startsWith("#")) {
            return;
        }

        final Matcher matcher = PATTERN.matcher(line);

        if (matcher.matches()) {
            final Author to =
                    new Author(matcher.group(1).trim(), matcher.group(2).trim());

            final Author from =
                    new Author(matcher.group(3).trim(), matcher.group(4).trim());

            addMapping(from, to);
        }
    }
}
