package com.example.tests.functional.regression.api;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.example.components.utils.installer.GluePropertiesReader;
import com.example.tests.functional.common.TestRetry;

import java.util.concurrent.atomic.AtomicInteger;


@Test(groups = {"Dummy"})
public class TestsToRetryTests extends ParentOfTest {

    private AtomicInteger count = new AtomicInteger(0);

    @BeforeClass
    public void setUp() {
        /*
        This is just a class that parses the app.properties file, and creates a POJO that we can
        read and initialize certain parts of the framework to be able to run on Teamcity.
        */
        new GluePropertiesReader();
    }

    @Test(priority = 0)
    public void test1Passes() {
        _logger.info("this one is", "test1Passes");
    }

    @Test(priority = 1)
    public void test2Fails() {
        _logger.info("this one is {}", "test2Fails");
        if(true) {
            throw new RuntimeException("fail");
        }
    }

    @Test(retryAnalyzer = TestRetry.class, priority = 2)
    public void testPassAfterRetry() {
        _logger.info("this one is {}", "testPassAfterRetry");
        if(count.getAndIncrement() < 1) {
            throw new RuntimeException();
        }
    }

    @Test(priority = 3)
    public void test3Passes() {
        _logger.info("this one is {}", "test3Passes");
    }

    @Test(priority = 4)
    public void test4Fails() {
        _logger.info("this one is {}", "test4Fails");
        if(true) {
            throw new RuntimeException("fail");
        }
    }

    @Test(priority = 5)
    public void test5Passes() {
        _logger.info("this one is {}", "test5Passes");
    }

    @Test(priority = 6)
    public void test6Fails() {
        _logger.info("this one is {}", "test6Fails");
        Assert.fail();
    }
}
