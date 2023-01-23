package com.isansys.common;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ErrorCodesTest
{
    @Test
    public void errorCodes_baseIsCorrect() throws Exception
    {
        assertEquals(ErrorCodes.ERROR_CODES, 0xF000);
    }

    @Test
    public void errorCodes_LifetouchLeadsOffIsCorrect() throws Exception
    {
        assertEquals(ErrorCodes.ERROR_CODE__LIFETOUCH_LEADS_OFF, ErrorCodes.ERROR_CODES + 1);
    }

    @Test
    public void errorCodes_LifetouchLeadsOnIsCorrect() throws Exception
    {
        assertEquals(ErrorCodes.ERROR_CODE__LIFETOUCH_LEADS_ON, ErrorCodes.ERROR_CODES + 2);
    }

    @Test
    public void errorCodes_NoninLeadsOffIsCorrect() throws Exception
    {
        assertEquals(ErrorCodes.ERROR_CODE__NONIN_WRIST_OX_LEADS_OFF, ErrorCodes.ERROR_CODES + 3);
    }

    @Test
    public void errorCodes_LifetouchNoTimestampIsCorrect() throws Exception
    {
        assertEquals(ErrorCodes.ERROR_CODE__LIFETOUCH_NO_TIMESTAMP, ErrorCodes.ERROR_CODES + 4);
    }

    @Test
    public void errorCodes_LifetempLeadsOffIsCorrect() throws Exception
    {
        assertEquals(ErrorCodes.ERROR_CODE__LIFETEMP_LEADS_OFF, ErrorCodes.ERROR_CODES + 5);
    }

    @Test
    public void errorCodes_LifetouchNoDataIsCorrect() throws Exception
    {
        assertEquals(ErrorCodes.ERROR_CODE__LIFETOUCH_NO_DATA, ErrorCodes.ERROR_CODES + 0xFFF);
    }
}