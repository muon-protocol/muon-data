package net.muon.data.gateio;

import java.util.Collection;
import java.util.Date;

public class GateioSubscriptionMessage
{
    private long time;
    private String channel = "spot.trades";
    private String event = "subscribe";
    private Collection<String> payload;

    public GateioSubscriptionMessage(Collection<String> payload)
    {
        this.payload = payload;
        this.time = new Date().getTime() / 1000;
    }

    public long getTime()
    {
        return time;
    }

    public void setTime(long time)
    {
        this.time = time;
    }

    public String getChannel()
    {
        return channel;
    }

    public void setChannel(String channel)
    {
        this.channel = channel;
    }

    public String getEvent()
    {
        return event;
    }

    public void setEvent(String event)
    {
        this.event = event;
    }

    public Collection<String> getPayload()
    {
        return payload;
    }

    public void setPayload(Collection<String> payload)
    {
        this.payload = payload;
    }
}
