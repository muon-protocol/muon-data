package net.muon.data.app.crypto.configuration;

abstract class AbstractHttpProperties
{
    private boolean enabled = false;

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }
}
