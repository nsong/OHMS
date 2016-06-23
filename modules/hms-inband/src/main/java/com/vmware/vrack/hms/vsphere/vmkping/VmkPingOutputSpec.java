/* ********************************************************************************
 * VmkPingOutputSpec.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.vsphere.vmkping;

import java.io.Serializable;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * Author: Tao Ma Date: 3/3/14
 */
public class VmkPingOutputSpec
    implements Serializable
{
    /*
     * Re-factor hard code string 2014-08-07
     */
    private static final String PING = "\nPING %s: \n";

    private static final long serialVersionUID = 1L;

    @XStreamImplicit
    private VmkPingTrace[] traces;

    @XStreamAlias( "Summary" )
    private VmkPingSummary summary;

    @XStreamImplicit
    private List<NoReplyHostInfo> noReplyHostInfos;

    private VmkPingFault fault;

    /**
     * @return the traces
     */
    public VmkPingTrace[] getTraces()
    {
        return traces;
    }

    /**
     * @param traces the traces to set
     */
    public void setTraces( VmkPingTrace[] traces )
    {
        this.traces = traces;
    }

    /**
     * @return the summary
     */
    public VmkPingSummary getSummary()
    {
        return summary;
    }

    /**
     * @param summary the summary to set
     */
    public void setSummary( VmkPingSummary summary )
    {
        this.summary = summary;
    }

    /**
     * @return the noReplyHostInfos
     */
    public List<NoReplyHostInfo> getNoReplyHostInfos()
    {
        return noReplyHostInfos;
    }

    /**
     * @param noReplyHostInfos the noReplyHostInfos to set
     */
    public void setNoReplyHostInfos( List<NoReplyHostInfo> noReplyHostInfos )
    {
        this.noReplyHostInfos = noReplyHostInfos;
    }

    /**
     * @return the fault
     */
    public VmkPingFault getFault()
    {
        return fault;
    }

    /**
     * @param fault the fault to set
     */
    public void setFault( VmkPingFault fault )
    {
        this.fault = fault;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append( String.format( PING, summary.getHostAddr() ) );
        if ( null != traces )
            for ( VmkPingTrace trace : traces )
            {
                builder.append( trace );
            }
        builder.append( "\n" );
        builder.append( summary );
        if ( null != noReplyHostInfos )
            builder.append( "\n" ).append( noReplyHostInfos );
        if ( null != fault )
            builder.append( "\n" ).append( fault );
        return builder.toString();
    }
}