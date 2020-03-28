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
                                "test/objects/98/0a0d5f19a64b4b30a87d4206aade58726b60e3\n")
                        .setAttach(Arrays.asList(
                                "Enter git object location:",
                                "blob 13",
                                "Hello World!")),
                new TestCase<List<String>>()
                        .setInput(
                                "test/objects/6c/c3bfadc1cef136840e08ff98dea7388c84a7bc\n")
                        .setAttach(Arrays.asList(
                                "Enter git object location:",
                                "blob 10",
                                "first line")),
                new TestCase<List<String>>()
                        .setInput(
                                "test/objects/9c/40573a110ce8bd9f51180a5bb9d1a75e1a5a72\n")
                        .setAttach(Arrays.asList(
                                "Enter git object location:",
                                "blob 22",
                                "first line",
                                "second line")),
                new TestCase<List<String>>()
                        .setInput(
                                "test/objects/27/2b2e47277179ab1fe0b9bfde13d43a14adc21f\n")
                        .setAttach(Arrays.asList(
                                "Enter git object location:",
                                "blob 33",
                                "first line",
                                "second line",
                                "third line"))
        );
    }

    @Override
    public CheckResult check(String reply, List<String> expectedOutput) {
        List<String> lines = Arrays.asList(reply.split("(\\r\\n|\\r|\\n)"));
//        lines = lines.subList(2, lines.size());

        if (lines.size() != expectedOutput.size()) {
            return CheckResult.FALSE(String.format(
                    "Number of lines in your output (%d) does not match expected value(%d)",
                    lines.size(), expectedOutput.size()));
        }

        for (int i = 0; i < lines.size(); i++) {
            if (!lines.get(i).equals(expectedOutput.get(i))) {
                return CheckResult.FALSE(String.format(
                        "Text at line (%d) (%s) does not match expected (%s)",
                        i, lines.get(i), expectedOutput.get(i)));
            }
        }


        return CheckResult.TRUE;
    }
}
