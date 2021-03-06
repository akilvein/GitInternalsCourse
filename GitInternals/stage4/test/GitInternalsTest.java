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
                                "0eee6a98471a350b2c2316313114185ecaf82f0e\n")
                        .setAttach(Arrays.asList(
                                "Enter .git directory location:",
                                "Enter git object hash:",
                                "*COMMIT*",
                                "tree: 79401ddb0e2c0fe0472c813754dd4a8873b66a84",
                                "parents: 12a4717e84b5e414f93cc91ca50a6d5a6c3563a0",
                                "author: Smith mr.smith@matrix original timestamp: 2020-03-29 17:18:20 +03:00",
                                "committer: Cypher cypher@matrix commit timestamp: 2020-03-29 17:25:52 +03:00",
                                "commit message:",
                                "get docs from feature1")),
                new TestCase<List<String>>()
                        .setInput(
                                "test\n" +
                                "490f96725348e92770d3c6bab9ec532564b7ebe0\n")
                        .setAttach(Arrays.asList(
                                "Enter .git directory location:",
                                "Enter git object hash:",
                                "*BLOB*",
                                "fun main() {",
                                "    while(true) {",
                                "        println(\"Hello Hyperskill student!\")",
                                "    }",
                                "} ")),
                new TestCase<List<String>>()
                        .setInput(
                                "test\n" +
                                "fb043556c251cb450a0d55e4ceb1ff35e12029c3\n")
                        .setAttach(Arrays.asList(
                                "Enter .git directory location:",
                                "Enter git object hash:",
                                "*TREE*",
                                "100644 2b26c15c4375d90203783fb4c2a45ff4b571a6 main.kt",
                                "100644 4a8abe7b618ddf9c55adbea359ce891775794a61 readme.txt")),
                new TestCase<List<String>>()
                        .setInput(
                                "test\n" +
                                "ad3a818dc87b9940935b24a5aa93fac00f086bf9\n")
                        .setAttach(Arrays.asList(
                                "Enter .git directory location:",
                                "Enter git object hash:",
                                "*TREE*",
                                "100644 c2d888ec7f6b71b5ec8712b464d1ca7aa9ffc main.kt")),
                new TestCase<List<String>>()
                        .setInput(
                                "test\n" +
                                "109e8050b41bd10b81be0a51a5e67327f5609551\n")
                        .setAttach(Arrays.asList(
                                "Enter .git directory location:",
                                "Enter git object hash:",
                                "*TREE*",
                                "100644 2b26c15c4375d90203783fb4c2a45ff4b571a6 main.kt",
                                "100644 f674b5d3a4c6cef5815b4e72ef2ea1bbe46b786b readme.txt",
                                "40000 74198c849dbbcd51d060c59253a4757eedb9bd12 some-folder")),
                new TestCase<List<String>>()
                        .setInput(
                                "test\n" +
                                "74198c849dbbcd51d060c59253a4757eedb9bd12\n")
                        .setAttach(Arrays.asList(
                                "Enter .git directory location:",
                                "Enter git object hash:",
                                "*TREE*",
                                "100644 83cc1999347924da28890838ad5e698da854a3 qq.txt"))
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
