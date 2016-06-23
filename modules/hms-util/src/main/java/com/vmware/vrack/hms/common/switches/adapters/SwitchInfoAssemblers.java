/* ********************************************************************************
 * SwitchInfoAssemblers.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.switches.adapters;

import java.util.List;

import com.vmware.vrack.hms.common.resource.fru.FruOperationalStatus;
import com.vmware.vrack.hms.common.rest.model.SwitchInfo;
import com.vmware.vrack.hms.common.rest.model.switches.NBSwitchInfo;
import com.vmware.vrack.hms.common.servernodes.api.NodeAdminStatus;
import com.vmware.vrack.hms.common.switches.api.SwitchBgpConfig;
import com.vmware.vrack.hms.common.switches.api.SwitchLacpGroup;
import com.vmware.vrack.hms.common.switches.api.SwitchMclagInfo;
import com.vmware.vrack.hms.common.switches.api.SwitchNode.SwitchRoleType;
import com.vmware.vrack.hms.common.switches.api.SwitchOspfConfig;
import com.vmware.vrack.hms.common.switches.api.SwitchPort;
import com.vmware.vrack.hms.common.switches.api.SwitchSensorInfo;
import com.vmware.vrack.hms.common.switches.api.SwitchVlan;

public final class SwitchInfoAssemblers
{
    public static NBSwitchInfo toSwitchInfo( List<SwitchPort> ports, List<SwitchLacpGroup> lags, List<SwitchVlan> vlans,
                                             SwitchOspfConfig ospf, SwitchBgpConfig bgp, SwitchMclagInfo mcLag,
                                             SwitchInfo switchAttributes, SwitchSensorInfo sensorInfo )
    {
        NBSwitchInfo lInfo = new NBSwitchInfo();
        /* All that can be configured except ports */
        lInfo.setConfig( SwitchConfigAssemblers.toSwitchConfig( lags, vlans, ospf, bgp, mcLag, switchAttributes ) );
        /* Port specific information and configurations */
        lInfo.setPorts( SwitchPortInfoAssemblers.toSwitchPortInfos( ports ) );
        /* Global switch attributes */
        if ( switchAttributes != null )
        {
            lInfo.setComponentIdentifier( switchAttributes.getComponentIdentifier() );
            lInfo.setFirmwareName( switchAttributes.getFirmwareName() );
            lInfo.setFirmwareVersion( switchAttributes.getFirmwareVersion() );
            lInfo.setFruId( switchAttributes.getFruId() );
            lInfo.setIpAddress( switchAttributes.getIpAddress() );
            lInfo.setLocation( switchAttributes.getLocation() );
            lInfo.setMangementPort( switchAttributes.getMangementPort() );
            lInfo.setOperationalStatus( extractOperationalStatus( switchAttributes ) );
            lInfo.setOsName( switchAttributes.getOsName() );
            lInfo.setOsVersion( switchAttributes.getOsVersion() );
            lInfo.setRole( toSwitchRoleType( switchAttributes.getRole() ) );
            lInfo.setSwitchId( switchAttributes.getSwitchId() );
        }
        lInfo.setSensors( SwitchSensorInfoAssemblers.toSwitchSensorInfo( sensorInfo ) );
        return lInfo;
    }

    public static NBSwitchInfo toSwitchInfo( SwitchInfo switchAttributes )
    {
        NBSwitchInfo lInfo = new NBSwitchInfo();
        /* Global switch attributes */
        if ( switchAttributes != null )
        {
            lInfo.setComponentIdentifier( switchAttributes.getComponentIdentifier() );
            lInfo.setFirmwareName( switchAttributes.getFirmwareName() );
            lInfo.setFirmwareVersion( switchAttributes.getFirmwareVersion() );
            lInfo.setFruId( switchAttributes.getFruId() );
            lInfo.setIpAddress( switchAttributes.getIpAddress() );
            lInfo.setLocation( switchAttributes.getLocation() );
            lInfo.setMangementPort( switchAttributes.getMangementPort() );
            lInfo.setOperationalStatus( extractOperationalStatus( switchAttributes ) );
            lInfo.setOsName( switchAttributes.getOsName() );
            lInfo.setOsVersion( switchAttributes.getOsVersion() );
            lInfo.setRole( toSwitchRoleType( switchAttributes.getRole() ) );
            lInfo.setSwitchId( switchAttributes.getSwitchId() );
            lInfo.setAdminStatus( NodeAdminStatus.OPERATIONAL );
        }
        return lInfo;
    }

    private static NBSwitchInfo.SwitchRoleType toSwitchRoleType( SwitchRoleType roleType )
    {
        NBSwitchInfo.SwitchRoleType lRoleType = null;
        if ( roleType == null )
            return null;
        switch ( roleType )
        {
            case MANAGEMENT:
                lRoleType = NBSwitchInfo.SwitchRoleType.MANAGEMENT;
                break;
            case SPINE:
                lRoleType = NBSwitchInfo.SwitchRoleType.SPINE;
                break;
            case TOR:
                lRoleType = NBSwitchInfo.SwitchRoleType.TOR;
                break;
            default:
                break;
        }
        return lRoleType;
    }

    private static FruOperationalStatus extractOperationalStatus( SwitchInfo switchAttributes )
    {
        FruOperationalStatus lStatus = FruOperationalStatus.UnKnown;
        switch ( switchAttributes.getOperational_status() )
        {
            case "true":
                lStatus = FruOperationalStatus.Operational;
                break;
            default:
                lStatus = FruOperationalStatus.NonOperational;
                break;
        }
        return lStatus;
    }
}