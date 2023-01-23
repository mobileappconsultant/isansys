package com.isansys.appiumtests.utils;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.io.IOException;

import static com.isansys.appiumtests.utils.TestHelper.TestRailResult.FAILED;

/**
 * Created by Rory on 06/07/2018.
 */

public class AppiumTestWatcher extends TestWatcher
{
    TestHelper m_helper;


    public void initialise(TestHelper helper)
    {
        m_helper = helper;
    }


    @Override
    protected void failed(Throwable t, Description description)
    {
        try
        {
            m_helper.updateTestRail(FAILED);

            m_helper.captureScreenShot("TEST_FAILURE");
        }
        catch(IOException ex)
        {
            System.out.println("Could not capture screenshot: " + ex.toString());
        }
    }

    @Override
    protected void finished(Description description)
    {
        if(m_helper != null)
        {
            m_helper.quitDriver();

            m_helper.spoofCommandIntent_endExistingSession();
        }
    }
}
