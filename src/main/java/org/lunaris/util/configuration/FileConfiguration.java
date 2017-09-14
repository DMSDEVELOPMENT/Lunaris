package org.lunaris.util.configuration;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;

import org.lunaris.util.Validate;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

/**
 * Created by RINES on 21.04.17.
 */
public abstract class FileConfiguration extends MemoryConfiguration {
    
    @Deprecated
    public static final boolean UTF8_OVERRIDE, UTF_BIG, SYSTEM_UTF;
    
    static {
        final byte[] testBytes = Base64Coder.decode("ICEiIyQlJicoKSorLC0uLzAxMjM0NTY3ODk6Ozw9Pj9AQUJDREVGR0hJSktMTU5PUFFSU1RVVldYWVpbXF1eX2BhYmNkZWZnaGlqa2xtbm9wcXJzdHV2d3h5ent8fX4NCg==");
        final String testString = " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\r\n";
        final Charset defaultCharset = Charset.defaultCharset();
        final String resultString = new String(testBytes, defaultCharset);
        final boolean trueUTF = defaultCharset.name().contains("UTF");
        UTF8_OVERRIDE = !testString.equals(resultString) || defaultCharset.equals(Charset.forName("US-ASCII"));
        SYSTEM_UTF = trueUTF || UTF8_OVERRIDE;
        UTF_BIG = trueUTF && UTF8_OVERRIDE;
    }

    public FileConfiguration() {
        super();
    }
    
    public FileConfiguration(Configuration defaults) {
        super(defaults);
    }
    
    public abstract String saveToString();
    
    public abstract void loadFromString(String contents);
    
    public abstract String buildHeader();
    
    public void save(File file) throws IOException {
        Validate.notNull(file, "File can't be null");
        Files.createParentDirs(file);
        String data = saveToString();
        try(Writer writer = new OutputStreamWriter(new FileOutputStream(file), UTF8_OVERRIDE && !UTF_BIG ? Charsets.UTF_8 : Charset.defaultCharset())) {
            writer.write(data);
        }
    }
    
    public void save(String file) throws IOException {
        Validate.notNull(file, "File can't be null");
        save(new File(file));
    }
    
    public void load(File file) throws IOException {
        Validate.notNull(file, "File can't be null");
        final FileInputStream stream = new FileInputStream(file);
        load(new InputStreamReader(stream, UTF8_OVERRIDE && !UTF_BIG ? Charsets.UTF_8 : Charset.defaultCharset()));
    }
    
    @Deprecated
    public void load(InputStream stream) throws IOException {
        Validate.notNull(stream, "Stream can't be null");
        load(new InputStreamReader(stream, UTF8_OVERRIDE ? Charsets.UTF_8 : Charset.defaultCharset()));
    }
    
    public void load(Reader reader) throws IOException {
        BufferedReader input = reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader);
        StringBuilder builder = new StringBuilder();
        try {
            String line;
            while((line = input.readLine()) != null) {
                builder.append(line);
                builder.append('\n');
            }
        }finally {
            input.close();
        }
        loadFromString(builder.toString());
    }
    
    public void load(String file) throws IOException {
        Validate.notNull(file, "File can't be null");
        load(new File(file));
    }
    
    @Override
    public FileConfigurationOptions options() {
        if(options == null)
            options = new FileConfigurationOptions(this);
        return (FileConfigurationOptions) options;
    }

    public Object getOrSet(String key, Object def) {
        if(!isSet(key)) {
            set(key, def);
            return def;
        }
        return get(key);
    }

    public String getOrSetString(String key, String def) {
        return (String) getOrSet(key, def);
    }

    public int getOrSetInt(String key, int def) {
        return (int) getOrSet(key, def);
    }

    public byte getOrSetByte(String key, byte def) {
        return (byte) (int) getOrSet(key, def);
    }

    public boolean getOrSetBoolean(String key, boolean def) {
        return (boolean) getOrSet(key, def);
    }
    
}
