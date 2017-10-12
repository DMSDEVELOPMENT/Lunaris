package org.lunaris.api.util.configuration.yaml;

import org.lunaris.api.util.configuration.Configuration;
import org.lunaris.api.util.configuration.FileConfigurationOptions;

/**
 * Created by RINES on 21.04.17.
 */
public class YamlConfigurationOptions extends FileConfigurationOptions {
    
    private int indent = 2;

    public YamlConfigurationOptions(Configuration configuration) {
        super(configuration);
    }

    public int getIndent() {
        return indent;
    }

    public void setIndent(int indent) {
        this.indent = indent;
    }
}
