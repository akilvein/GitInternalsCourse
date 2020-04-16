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
                                "cat-file\n" +
                                "0eee6a98471a350b2c2316313114185ecaf82f0e\n")
                        .setAttach(Arrays.asList(
                                "Enter .git directory location:",
                                "Enter command:",
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
                                "cat-file\n" +
                                "490f96725348e92770d3c6bab9ec532564b7ebe0\n")
                        .setAttach(Arrays.asList(
                                "Enter .git directory location:",
                                "Enter command:",
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
                                "cat-file\n" +
                                "fb043556c251cb450a0d55e4ceb1ff35e12029c3\n")
                        .setAttach(Arrays.asList(
                                "Enter .git directory location:",
                                "Enter command:",
                                "Enter git object hash:",
                                "*TREE*",
                                "100644 2b26c15c4375d90203783fb4c2a45ff4b571a6 main.kt",
                                "100644 4a8abe7b618ddf9c55adbea359ce891775794a61 readme.txt")),
                new TestCase<List<String>>()
                        .setInput(
                                "test\n" +
                                "list-branches\n")
                        .setAttach(Arrays.asList(
                                "Enter .git directory location:",
                                "Enter command:",
                                "  feature1",
                                "  feature2",
                                "* master")),
                new TestCase<List<String>>()
                        .setInput(
                                "test\n" +
                                "log\n" +
                                "master")
                        .setAttach(Arrays.asList(
                                "Enter .git directory location:",
                                "Enter command:",
                                "Enter branch name:",
                                "Commit: fd362f3f305819d17b4359444aa83e17e7d6924a",
                                "fd362f3f305819d17b4359444aa83e17e7d6924a",
                                "Neo neo@matrix commit timestamp: 2020-04-04 11:42:08 +03:00",
                                "add subfolder",
                                "",
                                "Commit: 39a0337532d7720acc90497043e2ade92c386939",
                                "39a0337532d7720acc90497043e2ade92c386939",
                                "Neo neo@matrix commit timestamp: 2020-04-04 09:59:23 +03:00",
                                "this commit message will have multiple lines",
                                "we need multiple lines commit message for test purposes",
                                "3",
                                "4",
                                "5",
                                "",
                                "Commit: 6d537a47eddc11f866bcc2013703bf31bfcf9ed8",
                                "6d537a47eddc11f866bcc2013703bf31bfcf9ed8",
                                "Cypher cypher@matrix commit timestamp: 2020-03-29 17:29:08 +03:00",
                                "Merge branch 'feature2'",
                                "",
                                "Commit: 31d679c1c2b8fc787ae014c501d4fa6545faa138",
                                "31d679c1c2b8fc787ae014c501d4fa6545faa138",
                                "Neo neo@matrix commit timestamp: 2020-03-29 17:21:48 +03:00",
                                "Merge branch 'feature1'",
                                "",
                                "Commit: 4107cf41cf55c4fd38e9da8f3d08d1eaefc3254a",
                                "4107cf41cf55c4fd38e9da8f3d08d1eaefc3254a",
                                "Neo mr.anderson@matrix commit timestamp: 2020-03-29 17:13:44 +03:00",
                                "continue work",
                                "",
                                "Commit: 12a4717e84b5e414f93cc91ca50a6d5a6c3563a0",
                                "12a4717e84b5e414f93cc91ca50a6d5a6c3563a0",
                                "Neo mr.anderson@matrix commit timestamp: 2020-03-29 17:12:52 +03:00",
                                "start kotlin project",
                                "",
                                "Commit: 73324685d9dbd1fdda87f3c5c6f77d79c1b769c2",
                                "73324685d9dbd1fdda87f3c5c6f77d79c1b769c2",
                                "Neo mr.anderson@matrix commit timestamp: 2020-03-29 17:10:52 +03:00",
                                "initial commit")),
                new TestCase<List<String>>()
                        .setInput(
                                "test\n" +
                                "log\n" +
                                "feature2")
                        .setAttach(Arrays.asList(
                                "Enter .git directory location:",
                                "Enter command:",
                                "Enter branch name:",
                                "Commit: 97e638cc1c7135580c3ff93162e727148e1bad05",
                                "97e638cc1c7135580c3ff93162e727148e1bad05",
                                "Cypher cypher@matrix commit timestamp: 2020-03-29 17:27:35 +03:00",
                                "break our software",
                                "",
                                "Commit: 0eee6a98471a350b2c2316313114185ecaf82f0e",
                                "0eee6a98471a350b2c2316313114185ecaf82f0e",
                                "Cypher cypher@matrix commit timestamp: 2020-03-29 17:25:52 +03:00",
                                "get docs from feature1",
                                "",
                                "Commit: 12a4717e84b5e414f93cc91ca50a6d5a6c3563a0",
                                "12a4717e84b5e414f93cc91ca50a6d5a6c3563a0",
                                "Neo mr.anderson@matrix commit timestamp: 2020-03-29 17:12:52 +03:00",
                                "start kotlin project",
                                "",
                                "Commit: 73324685d9dbd1fdda87f3c5c6f77d79c1b769c2",
                                "73324685d9dbd1fdda87f3c5c6f77d79c1b769c2",
                                "Neo mr.anderson@matrix commit timestamp: 2020-03-29 17:10:52 +03:00",
                                "initial commit"))
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
