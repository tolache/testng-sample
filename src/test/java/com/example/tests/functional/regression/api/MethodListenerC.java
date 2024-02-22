package com.example.tests.functional.regression.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.*;

public class MethodListenerC implements IInvokedMethodListener {

    Logger _logger = LogManager.getLogger(getClass().getSimpleName());
    String _currentClassName;
    private static int runCount = 0;
    private static int skipCount = 0;
    private static int failCount = 0;
    private static int allCount = 0;

    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult iTestResult, ITestContext context) {
        String methodName = iTestResult.getMethod().getMethodName();

        if (allCount == 0) {
            allCount = context.getSuite().getAllMethods().size();
        }

        if (!method.getTestMethod().getConstructorOrMethod().getMethod().getDeclaringClass().getSimpleName().equals(_currentClassName)) {
            if (null != _currentClassName) {
                tc_closeBlock(_currentClassName);
            }
            _currentClassName = method.getTestMethod().getConstructorOrMethod().getMethod().getDeclaringClass().getSimpleName();
            tc_openBlock(_currentClassName);
        }

        tc_openBlock(methodName, String.format("running %s", method.isTestMethod() ? "test" : "configuration method"));
        _logger.debug("Starting [{}] in class [{}]", methodName, _currentClassName);
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult iTestResult, ITestContext context) {
        String methodName = method.getTestMethod().getMethodName();
        if (iTestResult.getStatus() == ITestResult.FAILURE) {
            if (iTestResult.getThrowable().getClass().getSimpleName().equals(SkipException.class.getSimpleName())) {
                _logger.info("Test method [{}] in class [{}] was skipped, reason: {}",
                             iTestResult.getMethod().getMethodName(),
                             iTestResult.getTestClass().getRealClass().getSimpleName(),
                             iTestResult.getThrowable().getMessage());

                if (method.isTestMethod()) {
                    skipCount++;
                }
            } else {
                failCount++;
                tc_Logfail(method.getTestMethod().getMethodName());
            }
        }

        tc_closeBlock(methodName);

        if (method.isTestMethod()) {
            runCount++;
            _logger.debug("######################## Finished running {} with result {} ########################", methodName, method.getTestResult().getStatus());
            _logger.debug(String.format("Executed %s of %s: Passed: %s | Failed: %s | Skipped: %s", runCount, allCount, runCount - failCount - skipCount, failCount, skipCount));

            tc_Progress(String.format("######################## Finished running %s with result %s ########################", methodName, method.getTestResult().getStatus()));
            tc_Progress(String.format("Executed %s of %s: Passed: %s | Failed: %s | Skipped: %s", runCount, allCount, runCount - failCount - skipCount, failCount, skipCount));
        }

        // if it's a method annotated with @AfterSuite means it's the last method ran and we need to close the class block
        if (iTestResult.getMethod().isAfterSuiteConfiguration()) {
            tc_closeBlock(_currentClassName);
        }
    }

    public void tc_openBlock(String blockLabel) {
        System.out.println((String.format("##teamcity[blockOpened name='<%s>']", tc_escaped(blockLabel))));
    }

    public void tc_closeBlock(String blockLabel) {
        System.out.println((String.format("##teamcity[blockClosed name='<%s>']", tc_escaped(blockLabel))));
    }

    public void tc_openBlock(String blockLabel, String blockDescription) {
        System.out.println((String.format("##teamcity[blockOpened name='<%s>' description='<%s>']", tc_escaped(blockLabel), tc_escaped(blockDescription))));
    }

    public void tc_Progress(String content) {
        System.out.println((String.format("##teamcity[progressMessage '<%s>']", tc_escaped(content))));
    }

    public void tc_Logfail(String testName) {
        System.out.println((String.format("##teamcity[message text='%s failed' errorDetails='Check the api-debug.log for more information' status='ERROR']", tc_escaped(testName))));
    }

    private static String tc_escaped(String blockLabel) {
        return blockLabel
                .replace("|", "||")
                .replace("'", "|'")
                .replace("\"", "|'")
                .replaceAll("\n", "|n")
                .replaceAll("\r", "|r")
                .replace("[", "|[")
                .replace("]", "|]");
    }
}
