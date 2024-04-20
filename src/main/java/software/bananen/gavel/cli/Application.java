package software.bananen.gavel.cli;

import picocli.CommandLine;

/**
 * The main application for the CLI.
 */
public class Application {

    /**
     * The main entrypoint for the application.
     *
     * @param args The arguments passed to the CLI.
     * @throws Throwable Might be thrown in case that an exception occurs.
     */
    public static void main(final String[] args) throws Throwable {
        final int exitCode = new CommandLine(new RunAnalysisCommand()).execute(args);
        System.exit(exitCode);
    }
}
