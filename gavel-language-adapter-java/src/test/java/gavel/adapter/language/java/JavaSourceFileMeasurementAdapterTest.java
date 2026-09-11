package gavel.adapter.language.java;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import software.bananen.gavel.domain.model.FileDiff;
import software.bananen.gavel.domain.ports.driven.ClassCodeUnitMetrics;
import software.bananen.gavel.domain.ports.driven.MethodCodeUnitMetrics;
import software.bananen.gavel.domain.service.MeasureWhitespaceComplexityService;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JavaSourceFileMeasurementAdapterTest {
    private static final JavaSourceFileMeasurementAdapter MEASUREMENT_ADAPTER =
            new JavaSourceFileMeasurementAdapter(new MeasureWhitespaceComplexityService());

    @Test
    void measureLibrarySystem() throws Exception {
        final String fileName = "LibrarySystem.java";
        final byte[] javaSourceCode = readResourceFileContent(fileName);

        final FileDiff diff = mock(FileDiff.class);

        when(diff.loadContent()).thenReturn(javaSourceCode);

        final var measurement = MEASUREMENT_ADAPTER.generateFor(diff);

        final SoftAssertions assertions = new SoftAssertions();

        assertions.assertThat(measurement.linesOfCode()).isEqualTo(404);
        assertions.assertThat(measurement.complexity()).isEqualTo(3434);
        assertions.assertThat(measurement.codeUnits()).containsExactly(
                new ClassCodeUnitMetrics(
                        "LibrarySystem",
                        "example",
                        List.of(
                                new ClassCodeUnitMetrics(
                                        "Book",
                                        "example",
                                        List.of(
                                                new ClassCodeUnitMetrics(
                                                        "BookCategory",
                                                        "example",
                                                        List.of(new MethodCodeUnitMetrics(
                                                                        "updatePopularityRank",
                                                                        "updatePopularityRank()",
                                                                        "c644baba8b6b6f942bf2a8f3084c9a0f",
                                                                        5,
                                                                        1,
                                                                        12
                                                                ),
                                                                new MethodCodeUnitMetrics(
                                                                        "displayCategoryInfo",
                                                                        "displayCategoryInfo()",
                                                                        "3ae3ad35d62338e6a27055e305a7d50f",
                                                                        5,
                                                                        0,
                                                                        12
                                                                ),
                                                                new MethodCodeUnitMetrics(
                                                                        "getCategoryName",
                                                                        "getCategoryName()",
                                                                        "8fa337b67def8df6c94157df16e14676",
                                                                        4,
                                                                        0,
                                                                        4
                                                                ),
                                                                new MethodCodeUnitMetrics(
                                                                        "setCategoryName",
                                                                        "setCategoryName(String)",
                                                                        "fded80aefff91dbb949881b5acaa49c5",
                                                                        3,
                                                                        0,
                                                                        4
                                                                ),
                                                                new MethodCodeUnitMetrics(
                                                                        "getDescription",
                                                                        "getDescription()",
                                                                        "6360ee9526d72b24d7d4647771483dcd",
                                                                        3,
                                                                        0,
                                                                        4
                                                                ),
                                                                new MethodCodeUnitMetrics(
                                                                        "setDescription",
                                                                        "setDescription(String)",
                                                                        "2ebafaea4cedc787bb27fc15d077db67",
                                                                        3,
                                                                        0,
                                                                        4
                                                                ),
                                                                new MethodCodeUnitMetrics(
                                                                        "getPopularityRank",
                                                                        "getPopularityRank()",
                                                                        "5d4fc5bf7b398d0250075c52db1d828d",
                                                                        3,
                                                                        0,
                                                                        4
                                                                )),
                                                        "62cc26cc2cca975ac0cbc07b216827fd",
                                                        48,
                                                        2,
                                                        192
                                                ),
                                                new MethodCodeUnitMetrics(
                                                        "displayBookInfo",
                                                        "displayBookInfo()",
                                                        "5e21969621a738892bc08afcf77152b4",
                                                        10,
                                                        0,
                                                        36
                                                ),
                                                new MethodCodeUnitMetrics(
                                                        "isOverdue",
                                                        "isOverdue()",
                                                        "eec7f18a715402eeb1151ddb6daa524c",
                                                        6,
                                                        0,
                                                        20
                                                ),
                                                new MethodCodeUnitMetrics(
                                                        "setCategory",
                                                        "setCategory(String, String)",
                                                        "6797ffae5d4c779799fadca4eee5c0fc",
                                                        4,
                                                        0,
                                                        8
                                                ),
                                                new MethodCodeUnitMetrics(
                                                        "getTitle",
                                                        "getTitle()",
                                                        "86a8f3e4f382d7fb69330a3094d25618",
                                                        4,
                                                        0,
                                                        4
                                                ),
                                                new MethodCodeUnitMetrics(
                                                        "getAuthor",
                                                        "getAuthor()",
                                                        "33318fdfeecbceee373ba537f7e2a0c9",
                                                        3,
                                                        0,
                                                        4
                                                ),
                                                new MethodCodeUnitMetrics(
                                                        "getIsbn",
                                                        "getIsbn()",
                                                        "4f72fc9990495a11dcd068a6b6ed7a03",
                                                        3,
                                                        0,
                                                        4
                                                ),
                                                new MethodCodeUnitMetrics(
                                                        "getPublicationYear",
                                                        "getPublicationYear()",
                                                        "207e93d261de08166185903779953696",
                                                        3,
                                                        0,
                                                        4
                                                ),
                                                new MethodCodeUnitMetrics(
                                                        "isAvailable",
                                                        "isAvailable()",
                                                        "7e3f771c85db04075a72fd033addb9db",
                                                        3,
                                                        0,
                                                        4
                                                ),
                                                new MethodCodeUnitMetrics(
                                                        "setAvailable",
                                                        "setAvailable(boolean)",
                                                        "2a85d828513c0f731caff02f21f9b7ea",
                                                        3,
                                                        0,
                                                        4
                                                ),
                                                new MethodCodeUnitMetrics(
                                                        "getBorrowDate",
                                                        "getBorrowDate()",
                                                        "f9eef6d71d18732ad9eb6348d58d8703",
                                                        3,
                                                        0,
                                                        4
                                                ),
                                                new MethodCodeUnitMetrics(
                                                        "setBorrowDate",
                                                        "setBorrowDate(LocalDate)",
                                                        "6759ac2a8d1266e0df9683a302137928",
                                                        3,
                                                        0,
                                                        4
                                                )
                                        ),
                                        "c7eff2b56999cdb57de74e88568e9a69",
                                        131,
                                        4,
                                        700
                                ),
                                new ClassCodeUnitMetrics(
                                        "Member",
                                        "example",
                                        List.of(
                                                new ClassCodeUnitMetrics(
                                                        "MembershipLevel",
                                                        "example",
                                                        List.of(
                                                                new MethodCodeUnitMetrics(
                                                                        "updateLevel",
                                                                        "updateLevel(Member)",
                                                                        "b41e2fc6b720aee4ec92c850545f8661",
                                                                        11,
                                                                        0,
                                                                        48
                                                                ),
                                                                new MethodCodeUnitMetrics(
                                                                        "setToBronzeLevel",
                                                                        "setToBronzeLevel()",
                                                                        "f680b6bd58f0f0e3b4b7161c00203d62",
                                                                        6,
                                                                        0,
                                                                        16
                                                                ),
                                                                new MethodCodeUnitMetrics(
                                                                        "setToSilverLevel",
                                                                        "setToSilverLevel()",
                                                                        "8f4c50d83b7b219288a968add0ee9c95",
                                                                        6,
                                                                        0,
                                                                        16
                                                                ),
                                                                new MethodCodeUnitMetrics(
                                                                        "setToGoldLevel",
                                                                        "setToGoldLevel()",
                                                                        "556e25c6f97678b690e1761f37e80c47",
                                                                        6,
                                                                        0,
                                                                        16
                                                                ),
                                                                new MethodCodeUnitMetrics(
                                                                        "displayLevelInfo",
                                                                        "displayLevelInfo()",
                                                                        "8d93cb01cc06fa496f3bf53030fa056d",
                                                                        6,
                                                                        0,
                                                                        16
                                                                ),
                                                                new MethodCodeUnitMetrics(
                                                                        "calculateFine",
                                                                        "calculateFine(int)",
                                                                        "f239ab7201b29fc0e36b7950c2b1a220",
                                                                        3,
                                                                        0,
                                                                        4
                                                                ),
                                                                new MethodCodeUnitMetrics(
                                                                        "getLevelName",
                                                                        "getLevelName()",
                                                                        "53df4ae8bee15f1ed0f00504a95086ec",
                                                                        4,
                                                                        0,
                                                                        4
                                                                ),
                                                                new MethodCodeUnitMetrics(
                                                                        "getMaxBooksAllowed",
                                                                        "getMaxBooksAllowed()",
                                                                        "bc330d8e1be729092dfe4fc79c3c817a",
                                                                        3,
                                                                        0,
                                                                        4
                                                                ),
                                                                new MethodCodeUnitMetrics(
                                                                        "getBorrowPeriodDays",
                                                                        "getBorrowPeriodDays()",
                                                                        "9c00cc50f5449bde61dc78fab2266bae",
                                                                        3,
                                                                        0,
                                                                        4
                                                                ),
                                                                new MethodCodeUnitMetrics(
                                                                        "getFinePerDay",
                                                                        "getFinePerDay()",
                                                                        "c18e5a1a159e19659ba73809a8ccaec3",
                                                                        3,
                                                                        0,
                                                                        4
                                                                )
                                                        ),
                                                        "e755e13a720c2586f8e16ae0ac62beb7",
                                                        79,
                                                        1,
                                                        392
                                                ),
                                                new MethodCodeUnitMetrics(
                                                        "displayMemberInfo",
                                                        "displayMemberInfo()",
                                                        "5b32cbd137ad0060f8897dfe9ff1db57",
                                                        9,
                                                        0,
                                                        28
                                                ),
                                                new MethodCodeUnitMetrics(
                                                        "incrementBorrowedBooks",
                                                        "incrementBorrowedBooks()",
                                                        "8b9b52ce126ebaa0a71689983e1e605c",
                                                        4,
                                                        0,
                                                        8
                                                ),
                                                new MethodCodeUnitMetrics(
                                                        "decrementBorrowedBooks",
                                                        "decrementBorrowedBooks()",
                                                        "7f5f4400d3a170953130dd3cfeee4e2a",
                                                        5,
                                                        0,
                                                        16
                                                ),
                                                new MethodCodeUnitMetrics(
                                                        "canBorrowMore",
                                                        "canBorrowMore()",
                                                        "a219fa17573a5abaed14b86c4b2cc504",
                                                        3,
                                                        0,
                                                        4
                                                ),
                                                new MethodCodeUnitMetrics(
                                                        "getMemberId",
                                                        "getMemberId()",
                                                        "9050c0e6e01b6d8e156795f74afb58c3",
                                                        4,
                                                        0,
                                                        4
                                                ),
                                                new MethodCodeUnitMetrics(
                                                        "getName",
                                                        "getName()",
                                                        "0842df54e9c59062f959c6a9c0682e2b",
                                                        3,
                                                        0,
                                                        4
                                                ),
                                                new MethodCodeUnitMetrics(
                                                        "getEmail",
                                                        "getEmail()",
                                                        "c180ea7b113b4964dfa1aae87983d936",
                                                        3,
                                                        0,
                                                        4
                                                ),
                                                new MethodCodeUnitMetrics(
                                                        "getPhoneNumber",
                                                        "getPhoneNumber()",
                                                        "066045990fd30fc36bb8a3f76e73fb7e",
                                                        3,
                                                        0,
                                                        4
                                                ),
                                                new MethodCodeUnitMetrics(
                                                        "getMembershipDate",
                                                        "getMembershipDate()",
                                                        "055264fef623ba0b8cdeb074abc2a60c",
                                                        3,
                                                        0,
                                                        4
                                                ),
                                                new MethodCodeUnitMetrics(
                                                        "getBorrowedBooksCount",
                                                        "getBorrowedBooksCount()",
                                                        "b2b2f49a8a3f12ce35ec9f08be53724d",
                                                        3,
                                                        0,
                                                        4
                                                )
                                        ),
                                        "5e9a97386dd239412d376571f7d08d9e",
                                        159,
                                        3,
                                        984

                                )
                                ,
                                new MethodCodeUnitMetrics(
                                        "addBook",
                                        "addBook(String, String, String, int)",
                                        "099fc20d15d53078c210f1a77f153159",
                                        6,
                                        0,
                                        12
                                ),
                                new MethodCodeUnitMetrics(
                                        "registerMember",
                                        "registerMember(String, String, String)",
                                        "7a7135534d3a4e14637a7028dc7e15cf",
                                        6,
                                        0,
                                        16
                                ),
                                new MethodCodeUnitMetrics(
                                        "borrowBook",
                                        "borrowBook(String, String)",
                                        "ec47b86a3e2ddd77e02615468ddf7752",
                                        13,
                                        0,
                                        68
                                ),
                                new MethodCodeUnitMetrics(
                                        "returnBook",
                                        "returnBook(String, String)",
                                        "aae894f40024cf58a414c39623054252",
                                        13,
                                        0,
                                        68
                                ),
                                new MethodCodeUnitMetrics(
                                        "displayLibraryInfo",
                                        "displayLibraryInfo()",
                                        "6a055582afcb782a9d55e45bd11470da",
                                        7,
                                        0,
                                        20
                                ),
                                new MethodCodeUnitMetrics(
                                        "findMemberById",
                                        "findMemberById(String)",
                                        "6cecd22e5b742fc4ed246434fd82a2e7",
                                        3,
                                        0,
                                        4
                                ),
                                new MethodCodeUnitMetrics(
                                        "findBookByIsbn",
                                        "findBookByIsbn(String)",
                                        "e32f0fd81ed087f02da6c76bac039450",
                                        3,
                                        0,
                                        4
                                ),
                                new MethodCodeUnitMetrics(
                                        "getAvailableBookCount",
                                        "getAvailableBookCount()",
                                        "8870d6d1592a849dd46e6106fbe1461f",
                                        3,
                                        0,
                                        4
                                ),
                                new MethodCodeUnitMetrics(
                                        "getLibraryName",
                                        "getLibraryName()",
                                        "694f2501fde48b221163e5a6c454e522",
                                        4,
                                        0,
                                        4
                                ),
                                new MethodCodeUnitMetrics(
                                        "getAddress",
                                        "getAddress()",
                                        "e945eb59d3ea153169014f51e12745ba",
                                        3,
                                        0,
                                        4
                                ),
                                new MethodCodeUnitMetrics(
                                        "getTotalLibraries",
                                        "getTotalLibraries()",
                                        "b27ee2954a99e19d8919d598a1bb8ce0",
                                        3,
                                        0,
                                        4
                                ),
                                new MethodCodeUnitMetrics(
                                        "main",
                                        "main(String[])",
                                        "1df31a8d971437fa6e5a3bb258548923",
                                        14,
                                        3,
                                        44
                                )
                        ),
                        "dcebdae16836a091509b043226783b87",
                        410,
                        17,
                        3234
                )
        );

        assertions.assertAll();
    }

    private byte[] readResourceFileContent(final String fileName) throws IOException, URISyntaxException {
        return Files.readAllBytes(
                Paths.get(
                        Objects.requireNonNull(getClass().getClassLoader().getResource(fileName)).toURI()));
    }
}