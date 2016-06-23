/* ********************************************************************************
 * HmsUpgradeUtilTest.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Calendar;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.vmware.vrack.hms.common.resource.UpgradeStatusCode;
import com.vmware.vrack.hms.common.rest.model.UpgradeStatus;

/**
 * <code>HmsUpgradeUtilTest</code><br>
 *
 * @author VMware, Inc.
 */
public class HmsUpgradeUtilTest
{
    /** The user home. */
    private String userHome = null;

    /**
     * Instantiates a new hms upgrade util test.
     */
    public HmsUpgradeUtilTest()
    {
        userHome = System.getProperty( "user.home" );
    }

    /**
     * Sets the up.
     *
     * @throws Exception the exception
     */
    @Before
    public void setUp()
        throws Exception
    {
    }

    /**
     * Tear down.
     *
     * @throws Exception the exception
     */
    @After
    public void tearDown()
        throws Exception
    {
    }

    /**
     * Test.
     */
    @Test
    public void testSaveUpgradeStatus()
    {
        UpgradeStatus upgradeStatus = null;
        String upgradeStatusFileName = null;
        Calendar cal = Calendar.getInstance();
        String timeInMillis = Long.toString( cal.getTimeInMillis() );
        // both null
        assertFalse( HmsUpgradeUtil.saveUpgradeStatus( upgradeStatusFileName, null ) );
        String id = UUID.randomUUID().toString();
        UpgradeStatusCode statusCode = UpgradeStatusCode.HMS_UPGRADE_INITIATED;
        upgradeStatus = new UpgradeStatus();
        upgradeStatus.setId( id );
        upgradeStatus.setStatusCode( statusCode );
        // fileName is null
        assertFalse( HmsUpgradeUtil.saveUpgradeStatus( upgradeStatusFileName, upgradeStatus ) );
        upgradeStatusFileName = userHome + File.separator + timeInMillis + ".json";
        // UpgradeStatus in null
        assertFalse( HmsUpgradeUtil.saveUpgradeStatus( upgradeStatusFileName, null ) );
        assertTrue( HmsUpgradeUtil.saveUpgradeStatus( upgradeStatusFileName, upgradeStatus ) );
        File upgradeStatusFile = new File( upgradeStatusFileName );
        assertTrue( upgradeStatusFile.exists() && upgradeStatusFile.isFile() );
        assertTrue( upgradeStatusFile.delete() );
    }

    /**
     * Test load upgrade status.
     */
    @Test
    public void testLoadUpgradeStatus()
    {
        String upgradeStatusFileName = null;
        assertNull( HmsUpgradeUtil.loadUpgradeStatus( upgradeStatusFileName ) );
        Calendar cal = Calendar.getInstance();
        String timeInMillis = Long.toString( cal.getTimeInMillis() );
        String id = UUID.randomUUID().toString();
        UpgradeStatusCode statusCode = UpgradeStatusCode.HMS_UPGRADE_INITIATED;
        UpgradeStatus upgradeStatus = new UpgradeStatus();
        upgradeStatus.setId( id );
        upgradeStatus.setStatusCode( statusCode );
        upgradeStatusFileName = userHome + File.separator + timeInMillis + ".json";
        assertTrue( HmsUpgradeUtil.saveUpgradeStatus( upgradeStatusFileName, upgradeStatus ) );
        File upgradeStatusFile = new File( upgradeStatusFileName );
        assertTrue( upgradeStatusFile.exists() && upgradeStatusFile.isFile() );
        UpgradeStatus status = HmsUpgradeUtil.loadUpgradeStatus( upgradeStatusFileName );
        assertNotNull( status );
        assertEquals( status.getId(), upgradeStatus.getId() );
        assertEquals( status.getStatusCode(), upgradeStatus.getStatusCode() );
        assertTrue( upgradeStatusFile.delete() );
    }
}
