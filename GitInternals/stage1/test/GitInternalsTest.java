import org.hyperskill.hstest.v6.stage.BaseStageTest;
import org.hyperskill.hstest.v6.testcase.CheckResult;
import org.hyperskill.hstest.v6.testcase.TestCase;
import gitinternals.MainKt;

import java.util.List;

class CheckFailException extends Exception {
    public CheckFailException(String s) {
        super(s);
    }
}

class Output {
}

public class GitInternalsTest extends BaseStageTest<Output> {

    public GitInternalsTest() {
        super(MainKt.class);
    }

    @Override
    public List<TestCase<Output>> generate() {

        return List.of(
                new TestCase<Output>()
                        .setInput("qq\n")
                        .setAttach(new Output())
        );
    }

    @Override
    public CheckResult check(String reply, Output expectedFile) {
        return CheckResult.TRUE;
    }
}
