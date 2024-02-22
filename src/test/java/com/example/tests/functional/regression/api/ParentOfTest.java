package com.example.tests.functional.regression.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.reporters.FailedReporter;
import org.testng.reporters.SuiteHTMLReporter;
import org.testng.reporters.XMLReporter;
import org.testng.reporters.jq.Main;

import java.lang.reflect.Method;

@Listeners({MethodListenerC.class, FailedReporter.class, SuiteHTMLReporter.class, XMLReporter.class, Main.class})
public class ParentOfTest {

    protected Logger _logger = LogManager.getLogger(this.getClass().getSimpleName());

}
