import org.hyperskill.hstest.v6.stage.BaseStageTest;
import org.hyperskill.hstest.v6.testcase.CheckResult;
import org.hyperskill.hstest.v6.testcase.TestCase;
import gitinternals.MainKt;

import java.util.Arrays;
import java.util.List;

class CheckFailException extends Exception {
    public CheckFailException(String s) {
        super(s);
    }
}


public class GitInternalsTest extends BaseStageTest<List<String>> {

    public GitInternalsTest() {
        super(MainKt.class);
    }

    @Override
    public List<TestCase<List<String>>> generate() {

        return List.of(
                new TestCase<List<String>>()
                        .setInput(
                                "test/objects/61/8383db6d7ee3bd2e97b871205f113b6a3ba854\n")
                        .setAttach(Arrays.asList(
                                "Enter git object location:",
                                "blob 14",
                                "Hello world! ")),
                new TestCase<List<String>>()
                        .setInput(
                                "test/objects/a8/7a4a0e9fcf5a8a091c54909b674ac2a051f5e8\n")
                        .setAttach(Arrays.asList(
                                "Enter git object location:",
                                "blob 24",
                                "first line",
                                "second line ")),
                new TestCase<List<String>>()
                        .setInput(
                                "test/objects/4a/8abe7b618ddf9c55adbea359ce891775794a61\n")
                        .setAttach(Arrays.asList(
                                "Enter git object location:",
                                "blob 35",
                                "first line",
                                "second line",
                                "third line "))
        );
    }

    @Override
    public CheckResult check(String reply, List<String> expectedOutput) {
        List<String> lines = Arrays.asList(reply.split("(\\r\\n|\\r|\\n)"));

        if (lines.size() != expectedOutput.size()) {
            return CheckResult.FALSE(String.format(
                    "Number of lines in your output (%d) does not match expected value(%d)",
                    lines.size(), expectedOutput.size()));
        }

        for (int i = 0; i < lines.size(); i++) {
            if (!lines.get(i).equals(expectedOutput.get(i))) {
                return CheckResult.FALSE(String.format(
                        "Output text at line (%d) (%s) does not match expected (%s)",
                        i, lines.get(i), expectedOutput.get(i)));
            }
        }


        return CheckResult.TRUE("Well done!");
    }
}
