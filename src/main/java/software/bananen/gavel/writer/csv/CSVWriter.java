package software.bananen.gavel.writer.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.function.Function;

/**
 * A helper class that can be used to write content to CSV files.
 */
public class CSVWriter {

    /**
     * Writes the given data to a file in CSV format.
     *
     * @param file       The file.
     * @param headers    The headers of the CSV
     * @param extractors The functions used to extract data from the records.
     * @param records    The records holding the data.
     * @param <T>        The type of the records.
     * @throws IOException May be thrown in case that the file could not be written.
     */
    public <T> void write(final File file,
                          final Collection<String> headers,
                          final Collection<Function<T, ?>> extractors,
                          Collection<T> records) throws IOException {
        if (headers.size() != extractors.size()) {
            throw new IllegalArgumentException("The headers and extractors do not match");
        }

        try (FileWriter writer = new FileWriter(file)) {

            final CSVFormat format =
                    CSVFormat.DEFAULT.builder()
                            .setHeader(headers.toArray(new String[0]))
                            .build();

            try (final CSVPrinter printer = new CSVPrinter(writer, format)) {
                for (final T record : records) {
                    printer.printRecord(extractors.stream().map(ex -> ex.apply(record)).toArray());
                }
            }
        }
    }
}
