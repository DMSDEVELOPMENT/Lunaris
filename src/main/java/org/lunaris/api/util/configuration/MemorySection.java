package org.lunaris.api.util.configuration;

import org.lunaris.util.Validate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by RINES on 21.04.17.
 */
public class MemorySection implements ConfigurationSection {
    
    protected final Map<String, Object> map = new LinkedHashMap<>();
    private final Configuration root;
    private final ConfigurationSection parent;
    private final String path;
    private final String fullPath;
    
    protected MemorySection() {
        if(!(this instanceof Configuration))
            throw new IllegalStateException("Can't construct a root MemorySection when it's not a Configuration!");
        this.path = "";
        this.fullPath = "";
        this.parent = null;
        this.root = (Configuration) this;
    }
    
    protected MemorySection(ConfigurationSection parent, String path) {
        Validate.notNull(parent, "Parent configuration can't be null!");
        Validate.notNull(path, "Path can't be null!");
        this.path = path;
        this.parent = parent;
        this.root = parent.getRoot();
        Validate.notNull(root, "Path can't be orphaned!");
        this.fullPath = createPath(parent, path);
    }

    @Override
    public Set<String> getKeys(boolean deep) {
        Set<String> result = new LinkedHashSet<String>();
        Configuration root = getRoot();
        if(root != null && root.options().isCopyDefaults()) {
            ConfigurationSection defaults = getDefaultSection();
            if(defaults != null)
                result.addAll(defaults.getKeys(deep));
        }
        mapChildrenKeys(result, this, deep);
        return result;
    }

    @Override
    public Map<String, Object> getValues(boolean deep) {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        Configuration root = getRoot();
        if(root != null && root.options().isCopyDefaults()) {
            ConfigurationSection defaults = getDefaultSection();
            if(defaults != null)
                result.putAll(defaults.getValues(deep));
        }
        mapChildrenValues(result, this, deep);
        return result;
    }

    @Override
    public boolean contains(String path) {
        return get(path) != null;
    }

    @Override
    public boolean isSet(String path) {
        Configuration root = getRoot();
        if(root == null)
            return false;
        if(root.options().isCopyDefaults())
            return contains(path);
        return get(path, null) != null;
    }

    @Override
    public String getCurrentPath() {
        return fullPath;
    }

    @Override
    public String getName() {
        return path;
    }

    @Override
    public Configuration getRoot() {
        return root;
    }

    @Override
    public ConfigurationSection getParent() {
        return parent;
    }

    @Override
    public Object get(String path) {
        return get(path, getDefault(path));
    }

    @Override
    public Object get(String path, Object def) {
        Validate.notNull(path, "Path can't be null!");
        if(path.length() == 0)
            return this;
        Configuration root = getRoot();
        if(root == null)
            throw new IllegalStateException("Can't access section without the root");
        char separator = root.options().getPathSeparator();
        int i1 = -1, i2;
        ConfigurationSection section = this;
        while((i1 = path.indexOf(separator, i2 = i1 + 1)) != -1) {
            section = section.getConfigurationSection(path.substring(i2, i1));
            if(section == null)
                return def;
        }
        String key = path.substring(i2);
        if(section == this) {
            Object result = map.get(key);
            return (result == null) ? def : result;
        }
        return section.get(key, def);
    }

    @Override
    public void set(String path, Object value) {
        Validate.notEmpty(path, "Can't set to an empty path");
        Configuration root = getRoot();
        if(root == null)
            throw new IllegalStateException("Can't use section without the root");
        char separator = root.options().getPathSeparator();
        int i1 = -1, i2;
        ConfigurationSection section = this;
        while((i1 = path.indexOf(separator, i2 = i1 + 1)) != -1) {
            String node = path.substring(i2, i1);
            ConfigurationSection subSection = section.getConfigurationSection(node);
            if(subSection == null)
                section = section.createSection(node);
            else
                section = subSection;
            
        }
        String key = path.substring(i2);
        if(section == this)
            if(value == null)
                map.remove(key);
            else
                map.put(key, value);
        else
            section.set(key, value);
    }

    @Override
    public ConfigurationSection createSection(String path) {
        Validate.notEmpty(path, "Can't create section at empty path");
        Configuration root = getRoot();
        if(root == null)
            throw new IllegalStateException("Can't create section without the root");
        char separator = root.options().getPathSeparator();
        int i1 = -1, i2;
        ConfigurationSection section = this;
        while((i1 = path.indexOf(separator, i2 = i1 + 1)) != -1) {
            String node = path.substring(i2, i1);
            ConfigurationSection subSection = section.getConfigurationSection(node);
            if(subSection == null)
                section = section.createSection(node);
            else
                section = subSection;
            
        }
        String key = path.substring(i2);
        if(section == this) {
            ConfigurationSection result = new MemorySection(this, key);
            map.put(key, result);
            return result;
        }
        return section.createSection(key);
    }

    @Override
    public ConfigurationSection createSection(String path, Map<?, ?> values) {
        ConfigurationSection section = createSection(path);
        map.entrySet().forEach(e -> {
            String key = e.getKey();
            if(e.getValue() instanceof Map)
                section.createSection(key, (Map<?, ?>) e.getValue());
            else
                section.set(key, e.getValue());
        });
        return section;
    }

    @Override
    public ConfigurationSection getConfigurationSection(String path) {
        Object val = get(path, null);
        if(val != null)
            return (val instanceof ConfigurationSection) ? (ConfigurationSection) val : null;
        val = get(path, getDefault(path));
        return (val instanceof ConfigurationSection) ? createSection(path) : null;
    }

    @Override
    public boolean isConfigurationSection(String path) {
        return get(path) instanceof ConfigurationSection;
    }

    @Override
    public ConfigurationSection getDefaultSection() {
        Configuration root = getRoot();
        Configuration defaults = root == null ? null : root.getDefaults();
        if(defaults != null && defaults.isConfigurationSection(getCurrentPath()))
            return defaults.getConfigurationSection(getCurrentPath());
        return null;
    }

    @Override
    public void addDefault(String path, Object value) {
        Validate.notNull(path, "Path can't be null");
        Configuration root = getRoot();
        if(root == null)
            throw new IllegalStateException("Can't add default without root");
        if(root == this)
            throw new UnsupportedOperationException("Unsupported addDefault(String, Object) implementation");
        root.addDefault(createPath(this, path), value);
    }

    @Override
    public String getString(String path) {
        Object def = getDefault(path);
        return getString(path, def != null ? def.toString() : null);
    }

    @Override
    public String getString(String path, String def) {
        Object val = get(path, def);
        return (val != null) ? val.toString() : def;
    }

    @Override
    public boolean isString(String path) {
        return get(path) instanceof String;
    }

    @Override
    public int getInt(String path) {
        Object def = getDefault(path);
        return getInt(path, (def instanceof Number) ? toInt(def) : 0);
    }

    @Override
    public int getInt(String path, int def) {
        Object val = get(path, def);
        return (val instanceof Number) ? toInt(val) : def;
    }

    @Override
    public boolean isInt(String path) {
        Object val = get(path);
        return val instanceof Integer;
    }

    @Override
    public boolean getBoolean(String path) {
        Object def = getDefault(path);
        return getBoolean(path, (def instanceof Boolean) ? (Boolean) def : false);
    }

    @Override
    public boolean getBoolean(String path, boolean def) {
        Object val = get(path, def);
        return (val instanceof Boolean) ? (Boolean) val : def;
    }

    @Override
    public boolean isBoolean(String path) {
        Object val = get(path);
        return val instanceof Boolean;
    }

    @Override
    public double getDouble(String path) {
        Object def = getDefault(path);
        return getDouble(path, (def instanceof Number) ? toDouble(def) : 0);
    }

    @Override
    public double getDouble(String path, double def) {
        Object val = get(path, def);
        return (val instanceof Number) ? toDouble(val) : def;
    }

    @Override
    public boolean isDouble(String path) {
        Object val = get(path);
        return val instanceof Double;
    }

    @Override
    public long getLong(String path) {
        Object def = getDefault(path);
        return getLong(path, (def instanceof Number) ? toLong(def) : 0);
    }

    @Override
    public long getLong(String path, long def) {
        Object val = get(path, def);
        return (val instanceof Number) ? toLong(val) : def;
    }

    @Override
    public boolean isLong(String path) {
        Object val = get(path);
        return val instanceof Long;
    }

    @Override
    public float getFloat(String path) {
        Object def = getDefault(path);
        return getFloat(path, (def instanceof Number) ? toFloat(def) : 0);
    }

    @Override
    public float getFloat(String path, float def) {
        Object val = get(path, def);
        return (val instanceof Number) ? toFloat(val) : def;
    }

    @Override
    public boolean isFloat(String path) {
        Object val = get(path);
        return val instanceof Float;
    }

    @Override
    public List<?> getList(String path) {
        Object def = getDefault(path);
        return getList(path, (def instanceof List) ? (List<?>) def : null);
    }

    @Override
    public List<?> getList(String path, List<?> def) {
        Object val = get(path, def);
        return (List<?>) ((val instanceof List) ? val : def);
    }

    @Override
    public boolean isList(String path) {
        Object val = get(path);
        return val instanceof List;
    }

    @Override
    public List<String> getStringList(String path) {
        List<?> list = getList(path);
        if(list == null)
            return new ArrayList<>(0);
        return list.stream().filter(o -> o instanceof String || isPrimitiveWrapper(o))
                .map(String::valueOf).collect(Collectors.toList());
    }

    @Override
    public List<Integer> getIntegerList(String path) {
        List<?> list = getList(path);
        if(list == null)
            return new ArrayList<>(0);
        List<Integer> result = new ArrayList<>();
        for(Object o : list)
            if(o instanceof Integer)
                result.add((Integer) o);
            else if(o instanceof String)
                try {
                    result.add(Integer.parseInt((String) o));
                }catch(NumberFormatException ex) {}
            else if(o instanceof Character)
                result.add((int) ((Character) o));
            else if(o instanceof Number)
                result.add(((Number) o).intValue());
        return result;
    }

    @Override
    public List<Boolean> getBooleanList(String path) {
        List<?> list = getList(path);
        if(list == null)
            return new ArrayList<>(0);
        List<Boolean> result = new ArrayList<>();
        for(Object object : list)
            if(object instanceof Boolean)
                result.add((Boolean) object);
            else if(object instanceof String)
                if(Boolean.TRUE.toString().equals(object))
                    result.add(true);
                else if (Boolean.FALSE.toString().equals(object))
                    result.add(false);
        return result;
    }

    @Override
    public List<Double> getDoubleList(String path) {
        List<?> list = getList(path);
        if(list == null)
            return new ArrayList<>(0);
        List<Double> result = new ArrayList<>();
        for(Object object : list)
            if(object instanceof Double)
                result.add((Double) object);
            else if(object instanceof String)
                try {
                    result.add(Double.valueOf((String) object));
                } catch (NumberFormatException ex) {
                }
            else if (object instanceof Character)
                result.add((double) ((Character) object));
            else if (object instanceof Number)
                result.add(((Number) object).doubleValue());
        return result;
    }

    @Override
    public List<Long> getLongList(String path) {
        List<?> list = getList(path);
        if(list == null)
            return new ArrayList<>(0);
        List<Long> result = new ArrayList<>();
        for(Object object : list)
            if(object instanceof Long)
                result.add((Long) object);
            else if(object instanceof String)
                try {
                    result.add(Long.valueOf((String) object));
                } catch (NumberFormatException ex) {
                }
            else if (object instanceof Character)
                result.add((long) ((Character) object));
            else if (object instanceof Number)
                result.add(((Number) object).longValue());
        return result;
    }

    @Override
    public List<Float> getFloatList(String path) {
        List<?> list = getList(path);
        if(list == null)
            return new ArrayList<>(0);
        List<Float> result = new ArrayList<>();
        for(Object object : list)
            if(object instanceof Float)
                result.add((Float) object);
            else if(object instanceof String)
                try {
                    result.add(Float.valueOf((String) object));
                } catch (NumberFormatException ex) {
                }
            else if (object instanceof Character)
                result.add((float) ((Character) object));
            else if (object instanceof Number)
                result.add(((Number) object).floatValue());
        return result;
    }

    @Override
    public List<Map<?, ?>> getMapList(String path) {
        List<?> list = getList(path);
        List<Map<?, ?>> result = new ArrayList<>();
        if(list == null)
            return result;
        list.stream().filter(o -> o instanceof Map).map(o -> (Map<?, ?>) o).forEach(result::add);
        return result;
    }
    
    protected boolean isPrimitiveWrapper(Object input) {
        return input instanceof Integer || input instanceof Boolean ||
                input instanceof Character || input instanceof Byte ||
                input instanceof Short || input instanceof Double ||
                input instanceof Long || input instanceof Float;
    }

    protected Object getDefault(String path) {
        Validate.notNull(path, "Path can't be null");
        Configuration root = getRoot();
        Configuration defaults = root == null ? null : root.getDefaults();
        return (defaults == null) ? null : defaults.get(createPath(this, path));
    }
    
    private static int toInt(Object object) {
        if(object instanceof Number)
            return ((Number) object).intValue();
        try {
            return Integer.parseInt(object.toString());
        }catch(Exception ex) {}
        return 0;
    }

    private static float toFloat(Object object) {
        if(object instanceof Number)
            return ((Number) object).floatValue();
        try {
            return Float.parseFloat(object.toString());
        }catch(Exception ex) {}
        return 0f;
    }

    private static double toDouble(Object object) {
        if(object instanceof Number)
            return ((Number) object).doubleValue();
        try {
            return Double.parseDouble(object.toString());
        }catch(Exception ex) {}
        return 0d;
    }

    private static long toLong(Object object) {
        if(object instanceof Number)
            return ((Number) object).longValue();
        try {
            return Long.parseLong(object.toString());
        }catch(Exception ex) {}
        return 0l;
    }
    
    protected void mapChildrenKeys(Set<String> output, ConfigurationSection section, boolean deep) {
        if(section instanceof MemorySection)
            ((MemorySection) section).map.entrySet().forEach(e -> {
                output.add(createPath(section, e.getKey(), this));
                if(deep && e.getValue() instanceof ConfigurationSection)
                    mapChildrenKeys(output, (ConfigurationSection) e.getValue(), deep);
            });
        else
            section.getKeys(deep).forEach(key -> output.add(createPath(section, key, this)));
    }
    
    protected void mapChildrenValues(Map<String, Object> output, ConfigurationSection section, boolean deep) {
        if(section instanceof MemorySection)
            ((MemorySection) section).map.entrySet().forEach(e -> {
                output.put(createPath(section, e.getKey(), this), e.getValue());
                if(deep && e.getValue() instanceof ConfigurationSection)
                    mapChildrenValues(output, (ConfigurationSection) e.getValue(), deep);
            });
        else
            section.getValues(deep).entrySet().forEach(e -> output.put(createPath(section, e.getKey(), this), e.getValue()));
    }
    
    public static String createPath(ConfigurationSection section, String key) {
        return createPath(section, key, (section == null) ? null : section.getRoot());
    }
    
    public static String createPath(ConfigurationSection section, String key, ConfigurationSection relativeTo) {
        Validate.notNull(section, "Can't create path without a section");
        Configuration root = section.getRoot();
        if(root == null)
            throw new IllegalStateException("Can't create path without the root");
        char separator = root.options().getPathSeparator();
        StringBuilder builder = new StringBuilder();
        for(ConfigurationSection parent = section; parent != null && parent != relativeTo; parent = parent.getParent()) {
            if(builder.length() > 0)
                builder.insert(0, separator);
            builder.insert(0, parent.getName());
        }
        if(key != null && key.length() > 0) {
            if(builder.length() > 0)
                builder.append(separator);
            builder.append(key);
        }
        return builder.toString();
    }

}
