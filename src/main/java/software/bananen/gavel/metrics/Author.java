package software.bananen.gavel.metrics;

/**
 * A record that can be used to represent the data associated with a specific
 * author.
 *
 * @param name  The name of the author.
 * @param email The email address of the author.
 */
public record Author(String name, String email) {
}
