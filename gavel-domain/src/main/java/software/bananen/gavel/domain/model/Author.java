package software.bananen.gavel.domain.model;

/**
 * A record that can be used to represent the data associated with a specific
 * author.
 *
 * @param name  The name of the author.
 * @param email The email address of the author.
 */
public record Author(String name, String email) {

    /**
     * Builds a string representation of the author.
     *
     * @return The string representation.
     */
    public String asString() {
        return String.format("%s <%s>", name, email);
    }
}
