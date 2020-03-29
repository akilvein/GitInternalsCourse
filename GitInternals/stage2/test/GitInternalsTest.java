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
                                "test\n" +
                                "980a0d5f19a64b4b30a87d4206aade58726b60e3\n")
                        .setAttach(Arrays.asList(
                                "Enter .git directory location:",
                                "Enter git object hash:",
                                "type:blob length:13")),
                new TestCase<List<String>>()
                        .setInput(
                                "test\n" +
                                "3853917edcd00d84c82ccb9b679f7b0d14ca7902\n")
                        .setAttach(Arrays.asList(
                                "Enter .git directory location:",
                                "Enter git object hash:",
                                "type:commit length:209")),
                new TestCase<List<String>>()
                        .setInput(
                                "test\n" +
                                "7912066edef8097c06b2b71064ba5ee31849592c\n")
                        .setAttach(Arrays.asList(
                                "Enter .git directory location:",
                                "Enter git object hash:",
                                "type:tree length:36")),
                new TestCase<List<String>>()
                        .setInput(
                                "test\n" +
                                "a720ad76243e69f0a5f55418ec30ff21757e40bd\n")
                        .setAttach(Arrays.asList(
                                "Enter .git directory location:",
                                "Enter git object hash:",
                                "type:tree length:101")),
                new TestCase<List<String>>()
                        .setInput(
                                "test\n" +
                                "617593c7bc820f0f606b0f2f9801a08bb880087c\n")
                        .setAttach(Arrays.asList(
                                "Enter .git directory location:",
                                "Enter git object hash:",
                                "type:commit length:190"))
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
