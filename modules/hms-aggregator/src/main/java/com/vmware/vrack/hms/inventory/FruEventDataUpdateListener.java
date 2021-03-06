/* ********************************************************************************
 * FruEventDataUpdateListener.java
 * 
 * Copyright © 2013 - 2016 VMware, Inc. All Rights Reserved.

 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, without warranties or
 * conditions of any kind, EITHER EXPRESS OR IMPLIED. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * *******************************************************************************/

package com.vmware.vrack.hms.inventory;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.vmware.vrack.common.event.Event;
import com.vmware.vrack.common.event.enums.EventComponent;
import com.vmware.vrack.hms.aggregator.switches.HmsSwitchManager;
import com.vmware.vrack.hms.aggregator.util.AggregatorUtil;
import com.vmware.vrack.hms.common.resource.fru.FruOperationalStatus;
import com.vmware.vrack.hms.common.rest.model.ServerInfo;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.controller.HMSLocalServerRestService;
import com.vmware.vrack.hms.controller.HmsSwitchRestService;

/**
 * HMS FRU Event Data update listener
 */
@SuppressWarnings( "deprecation" )
@Component
public class FruEventDataUpdateListener
    implements ApplicationListener<FruEventStateChangeMessage>
{

    @Autowired
    private HmsDataCache hmsDataCache;

    @Autowired
    private HMSLocalServerRestService hmsService;

    @Autowired
    private HmsSwitchManager switchManager;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private HmsSwitchRestService hmsSwitchRestService;

    @Value( "${esxi.ssh.retry.count}" )
    private int sshRetryCount;

    @Value( "${esxi.ssh.retry.delay}" )
    private int sshRetryDelay;

    private static Logger logger = LoggerFactory.getLogger( FruEventDataUpdateListener.class );

    public HmsDataCache getHmsDataCache()
    {
        return hmsDataCache;
    }

    public void setHmsDataCache( HmsDataCache hmsDataCache )
    {
        this.hmsDataCache = hmsDataCache;
    }

    /**
     * Listener for the HMS Event data update
     *
     * @param message
     */
    @Override
    public void onApplicationEvent( FruEventStateChangeMessage message )
    {
        // read event and update the cache.

        String nodeId = null;
        String switchId = null;
        try
        {
            boolean updateCache = true;
            for ( Event event : message.getListEvent() )
            {
                if ( event.getHeader().getComponentIdentifier().containsKey( EventComponent.SERVER ) )
                    nodeId = event.getHeader().getComponentIdentifier().get( EventComponent.SERVER );
                else if ( event.getHeader().getComponentIdentifier().containsKey( EventComponent.SWITCH ) )
                    switchId = event.getHeader().getComponentIdentifier().get( EventComponent.SWITCH );

                switch ( event.getHeader().getEventName() )
                {
                    case CPU_CAT_ERROR:
                    case CPU_MACHINE_CHECK_ERROR:
                    case CPU_INITIALIZATION_ERROR:
                    case CPU_POST_FAILURE:
                    case CPU_THERMAL_TRIP:
                        hmsService.getCpuInfo( nodeId );
                        updateCache = false;
                        break;
                    case HDD_DOWN:
                    case HDD_UP:
                    case SSD_DOWN:
                    case SSD_UP:
                        hmsService.getHddInfo( nodeId );
                        updateCache = false;
                        break;
                    case DIMM_ECC_ERROR:
                    case DIMM_THERMAL_TRIP:
                        hmsService.getMemoryInfo( nodeId );
                        updateCache = false;
                        break;
                    case NIC_PORT_DOWN:
                    case NIC_PORT_UP:
                    case NIC_PACKET_DROP_ABOVE_THRESHOLD:
                        hmsService.getNicInfo( nodeId );
                        updateCache = false;
                        break;
                    case STORAGE_CONTROLLER_DOWN:
                    case STORAGE_CONTROLLER_UP:
                        hmsService.getStorageControllerInfo( nodeId );
                        updateCache = false;
                        break;
                    case SERVER_UP:
                    case SERVER_DOWN:
                    case SERVER_PCIE_ERROR:
                    case SERVER_POST_ERROR:
                        hmsService.getHostInfo( nodeId );
                        updateCache = false;
                        break;
                    case MANAGEMENT_SWITCH_UP:
                    case MANAGEMENT_SWITCH_DOWN:
                    case TOR_SWITCH_UP:
                    case TOR_SWITCH_DOWN:
                    case SPINE_SWITCH_UP:
                    case SPINE_SWITCH_DOWN:
                        hmsSwitchRestService.getSwitchInfo( switchId );
                        updateCache = false;
                        break;
                    case MANAGEMENT_SWITCH_PORT_UP:
                    case MANAGEMENT_SWITCH_PORT_DOWN:
                    case TOR_SWITCH_PORT_UP:
                    case TOR_SWITCH_PORT_DOWN:
                    case SPINE_SWITCH_PORT_UP:
                    case SPINE_SWITCH_PORT_DOWN:
                        switchManager.getSwitchAllPortInfoList( switchId );
                        updateCache = false;
                        break;
                    case BMC_NOT_REACHABLE:
                        setHostNotDiscoverableInHmsDataCache( nodeId );
                        updateCache = false;
                        break;
                    default:
                        break;
                }

                // break from for loop as it's already updated the cache
                if ( updateCache == false )
                    break;
            }
        }
        catch ( Exception e )
        {
            logger.error( "Error in the HMS event data update listener ", e );
        }
    }

    /**
     * Sets hosts discoverable and operational to false when bmc is not reachable
     * 
     * @param hostId
     */
    private void setHostNotDiscoverableInHmsDataCache( String hostId )
    {
        try
        {
            Map<String, ServerInfo> serverInfoMap = hmsDataCache.getServerInfoMap();
            ServerInfo serverInfo = serverInfoMap.get( hostId );

            // Will update the discovery and operation status by considering
            // ESXI reachable status as well.
            ServerNode serverNode = InventoryLoader.getInstance().getNode( hostId );
            boolean isInbandReachable = AggregatorUtil.isEsxiHostReachable( serverNode, sshRetryCount, sshRetryDelay );

            logger.debug( "Node: {} inband ip: {} reachability status: {}", hostId, serverNode.getIbIpAddress(),
                          isInbandReachable );

            if ( serverInfo != null && !isInbandReachable )
            {
                serverInfo.setDiscoverable( false );
                serverInfo.setOperationalStatus( FruOperationalStatus.NonOperational );
                context.publishEvent( new ServerDataChangeMessage( serverInfo, ServerComponent.SERVER ) );
            }
            else
            {
                logger.warn( String.format( "Unable to update hms data cache and publish event for host [%s] as serverInfo is Null",
                                            hostId ) );
            }
        }
        catch ( Exception e )
        {
            logger.warn( String.format( "Unable to update hms data cache and publish event for host [%s]", hostId ),
                         e );
        }
    }
}
