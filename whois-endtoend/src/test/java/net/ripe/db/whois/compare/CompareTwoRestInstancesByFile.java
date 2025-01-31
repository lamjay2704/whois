package net.ripe.db.whois.compare;

import net.ripe.db.whois.compare.common.ComparisonExecutorConfig;
import net.ripe.db.whois.compare.common.ComparisonRunnerFactory;
import net.ripe.db.whois.compare.common.TargetInterface;
import net.ripe.db.whois.compare.rest.RestQueryReader;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;

@org.junit.jupiter.api.Tag("ManualTest")
public class CompareTwoRestInstancesByFile {

    @Test
    public void test_deployed_versions() throws Exception {

        FileUtils.deleteDirectory(new File("target/qry/comparison_rest_file"));

        new ComparisonRunnerFactory().createCompareResults(
                    ComparisonExecutorConfig.PRE1,
                    ComparisonExecutorConfig.PRE2,
                    new RestQueryReader("comparison_rest", RestQueryReader.RestResponseType.ALL),
                    new File("target/qry/comparison_rest_file"),
                    TargetInterface.REST)
                .runCompareTest();
    }
}
