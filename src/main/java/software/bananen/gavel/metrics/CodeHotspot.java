package software.bananen.gavel.metrics;

/**
 * A record that can be used to represent a code hotspot.
 *
 * @param fileName        The file name.
 * @param numberOfChanges The number of changes.
 */
public record CodeHotspot(String fileName, Integer numberOfChanges) {
}
