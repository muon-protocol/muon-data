package net.muon.data.core;

import com.google.common.base.Strings;

import java.util.Map;

public class PropertyPathValueResolver
{
    private final boolean blankIfException;

    public PropertyPathValueResolver()
    {
        blankIfException = true;
    }

    public PropertyPathValueResolver(boolean blankIfException)
    {
        this.blankIfException = blankIfException;
    }

    public <T> T get(Map<String, Object> root, String propertyPath)
    {
        try {
            if (Strings.isNullOrEmpty(propertyPath))
                return null;
            Map<String, Object> m = root;
            var tokens = propertyPath.split("\\.");
            for (int i = 0; i < tokens.length; i++) {
                Object t = tokens[i];
                if (!m.containsKey(t))
                    throw new IllegalStateException("Invalid property " + t);
                Object v = m.get(t);
                if (i == tokens.length - 1)
                    return (T) v;
                m = (Map<String, Object>) v;
            }
            return (T) m;
        } catch (RuntimeException e) {
            if (blankIfException) {
                return null;
            }
            throw e;
        }
    }

    @Override
    public String toString()
    {
        return "r";
    }
}
