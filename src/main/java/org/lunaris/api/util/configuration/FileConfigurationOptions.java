package org.lunaris.api.util.configuration;

/**
 * Created by RINES on 21.04.17.
 */
public class FileConfigurationOptions extends ConfigurationOptions {

    private String header;
    private boolean copyHeader = true;

    public FileConfigurationOptions(Configuration configuration) {
        super(configuration);
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public boolean isCopyHeader() {
        return copyHeader;
    }

    public void setCopyHeader(boolean copyHeader) {
        this.copyHeader = copyHeader;
    }
}
