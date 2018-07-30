/**
 *
 */
package com.jd.laf.extension;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author hexiaofeng
 */
public final class URL implements Serializable {
    private static final long serialVersionUID = -1985165475234910535L;

    public static final String FILE = "file";
    public static final String UTF_8 = "UTF-8";

    // 协议
    private final String protocol;
    // 名称
    private final String user;
    // 密码
    private final String password;
    // 主机
    private final String host;
    // 端口
    private final int port;
    // 路径
    private final String path;
    // 参数
    private final Map<String, String> parameters;

    protected URL() {
        this.protocol = null;
        this.user = null;
        this.password = null;
        this.host = null;
        this.port = 0;
        this.path = null;
        this.parameters = null;
    }

    public URL(String protocol, String host, int port) {
        this(protocol, null, null, host, port, null, null);
    }

    public URL(String protocol, String host, int port, Map<String, String> parameters) {
        this(protocol, null, null, host, port, null, parameters);
    }

    public URL(String protocol, String host, int port, String path) {
        this(protocol, null, null, host, port, path, null);
    }

    public URL(String protocol, String host, int port, String path, Map<String, String> parameters) {
        this(protocol, null, null, host, port, path, parameters);
    }

    public URL(String protocol, String user, String password, String host, int port, String path) {
        this(protocol, user, password, host, port, path, null);
    }

    public URL(String protocol, String user, String password, String host, int port, String path,
               Map<String, String> parameters) {
        this.protocol = protocol;
        this.user = user;
        this.password = password;
        this.host = host;
        this.port = (port < 0 ? 0 : port);
        this.path = path;
        this.parameters = Collections.unmodifiableMap(parameters == null ?
                new HashMap<String, String>() : new HashMap<String, String>(parameters));
    }

    /**
     * 把字符串转化成URL对象
     *
     * @param url 字符串
     * @return 新创建的URL对象
     */
    public static URL valueOf(String url) {
        if (url == null) {
            return null;
        }
        url = url.trim();
        if (url.isEmpty()) {
            return null;
        }
        String protocol = null;
        String user = null;
        String password = null;
        String host = null;
        int port = 0;
        String path = null;
        Map<String, String> parameters = null;

        // cloud://user:password@jss.360buy.com/mq?timeout=60000
        // file:/path/to/file.txt
        // zookeeper://10.10.10.10:2181,10.10.10.11:2181/?retryTimes=3
        // failover://(zookeeper://10.10.10.10:2181,10.10.10.11:2181;zookeeper://20.10.10.10:2181,20.10.10.11:2181)
        // ?interval=1000
        int j = 0;
        int i = url.indexOf(')');
        if (i >= 0) {
            i = url.indexOf('?', i);
        } else {
            i = url.indexOf("?");
        }
        if (i >= 0) {
            if (i < url.length() - 1) {
                String[] parts = url.substring(i + 1).split("&");
                parameters = new HashMap<String, String>();
                for (String part : parts) {
                    part = part.trim();
                    if (!part.isEmpty()) {
                        j = part.indexOf('=');
                        if (j > 0) {
                            if (j == part.length() - 1) {
                                parameters.put(part.substring(0, j), "");
                            } else {
                                parameters.put(part.substring(0, j), part.substring(j + 1));
                            }
                        } else if (j == -1) {
                            parameters.put(part, part);
                        }
                    }
                }
            }
            url = url.substring(0, i);
        }
        i = url.indexOf("://");
        if (i > 0) {
            protocol = url.substring(0, i);
            url = url.substring(i + 3);
        } else if (i < 0) {
            // case: file:/path/to/file.txt
            i = url.indexOf(":/");
            if (i > 0) {
                protocol = url.substring(0, i);
                // 保留路径符号“/”
                url = url.substring(i + 1);
            }
        }
        if (protocol == null || protocol.isEmpty()) {
            throw new IllegalStateException("url missing protocol: " + url);
        }
        if (protocol.equals(FILE)) {
            path = url;
            url = "";
        } else {
            i = url.lastIndexOf(')');
            if (i >= 0) {
                i = url.indexOf('/', i);
            } else {
                i = url.indexOf("/");
            }
            if (i >= 0) {
                path = url.substring(i + 1);
                url = url.substring(0, i);
            }
        }
        i = url.indexOf('(');
        if (i >= 0) {
            j = url.lastIndexOf(')');
            if (j >= 0) {
                url = url.substring(i + 1, j);
            } else {
                url = url.substring(i + 1);
            }
        } else {
            i = url.indexOf("@");
            if (i >= 0) {
                user = url.substring(0, i);
                j = user.indexOf(":");
                if (j >= 0) {
                    password = user.substring(j + 1);
                    user = user.substring(0, j);
                }
                url = url.substring(i + 1);
            }
            String[] values = url.split(":");
            if (values.length == 2) {
                // 排除zookeeper://192.168.1.2:2181,192.168.1.3:2181
                port = Integer.parseInt(values[1]);
                url = values[0];
            }
        }
        if (!url.isEmpty()) {
            host = url;
        }
        return new URL(protocol, user, password, host, port, path, parameters);
    }

    /**
     * URL编码
     *
     * @param value 字符串
     * @return 编码后的字符串
     * @throws UnsupportedEncodingException
     */
    public static String encode(final String value) throws UnsupportedEncodingException {
        return encode(value, UTF_8);
    }

    /**
     * URL编码
     *
     * @param value   字符串
     * @param charset 字符集
     * @return 编码后的字符串
     * @throws UnsupportedEncodingException
     */
    public static String encode(final String value, final String charset) throws UnsupportedEncodingException {
        if (value == null || value.isEmpty()) {
            return "";
        }
        return URLEncoder.encode(value, charset);
    }

    /**
     * URL解码
     *
     * @param value 编码后的字符串
     * @return 解码字符串
     * @throws UnsupportedEncodingException
     */
    public static String decode(String value) throws UnsupportedEncodingException {
        return decode(value, UTF_8);
    }

    /**
     * URL解码
     *
     * @param value 编码后的字符串
     * @return 解码字符串
     * @throws UnsupportedEncodingException
     */
    public static String decode(String value, String charset) throws UnsupportedEncodingException {
        if (value == null || value.isEmpty()) {
            return "";
        }
        return URLDecoder.decode(value, charset == null || charset.isEmpty() ? UTF_8 : charset);
    }

    public String getProtocol() {
        return protocol;
    }

    public URL setProtocol(String protocol) {
        return new URL(protocol, user, password, host, port, path, getParameters());
    }

    public String getUser() {
        return user;
    }

    public URL setUser(String user) {
        return new URL(protocol, user, password, host, port, path, getParameters());
    }

    public String getPassword() {
        return password;
    }

    public URL setPassword(String password) {
        return new URL(protocol, user, password, host, port, path, getParameters());
    }

    public String getHost() {
        return host;
    }

    public URL setHost(String host) {
        return new URL(protocol, user, password, host, port, path, getParameters());
    }

    public int getPort() {
        return port;
    }

    public URL setPort(int port) {
        return new URL(protocol, user, password, host, port, path, getParameters());
    }

    public String getAddress() {
        return port <= 0 ? host : host + ":" + port;
    }

    public URL setAddress(String address) {
        int i = address.lastIndexOf(':');
        String host;
        int port = this.port;
        if (i >= 0) {
            host = address.substring(0, i);
            port = Integer.parseInt(address.substring(i + 1));
        } else {
            host = address;
        }
        return new URL(protocol, user, password, host, port, path, getParameters());
    }

    public InetSocketAddress getSocketAddress() {
        return new InetSocketAddress(host, port);
    }

    public String getPath() {
        return path;
    }

    public URL setPath(String path) {
        return new URL(protocol, user, password, host, port, path, getParameters());
    }

    public String getAbsolutePath() {
        if (path != null && path.charAt(0) != '/') {
            return "/" + path;
        }
        return path;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    /**
     * 获取字符串参数值
     *
     * @param key 参数名称
     * @return 参数值
     */
    public String getString(final String key) {
        return parameters.get(key);
    }

    /**
     * 获取字符串参数值
     *
     * @param key 参数名称
     * @param def 默认值
     * @return 参数值
     */
    public String getString(final String key, final String def) {
        String value = getString(key);
        if (value == null || value.isEmpty()) {
            return def;
        }
        return value;
    }

    /**
     * 获取URL解码后的参数值
     *
     * @param key 参数名称
     * @return 参数值
     * @throws UnsupportedEncodingException
     */
    public String getDecoded(final String key) throws UnsupportedEncodingException {
        return decode(getString(key));
    }

    /**
     * 获取URL解码后的参数值
     *
     * @param key     参数名称
     * @param charset 字符集
     * @return 参数值
     * @throws UnsupportedEncodingException
     */
    public String getDecoded(final String key, final String charset) throws UnsupportedEncodingException {
        return decode(getString(key), charset);
    }

    /**
     * 获取字符串参数值
     *
     * @param key     参数名称
     * @param def     默认值
     * @param charset 字符集
     * @return 参数值
     * @throws UnsupportedEncodingException
     */
    public String getDecoded(final String key, final String def, final String charset) throws UnsupportedEncodingException {
        return getDecoded(getString(key, def), charset);
    }

    /**
     * 获取日期参数值，日期是从EPOCH的毫秒数
     *
     * @param key 参数名称
     * @param def 默认值
     * @return 参数值
     */
    public Date getDate(final String key, final Date def) {
        Long value = getLong(key, null);
        if (value == null) {
            return def;
        }
        return new Date(value);
    }

    /**
     * 获取日期参数值，日期格式为字符串
     *
     * @param key    参数名称
     * @param format 日期格式
     * @return 参数值
     */
    public Date getDate(final String key, final SimpleDateFormat format) {
        return getDate(key, format, null);
    }

    /**
     * 获取日期参数值，日期格式为字符串
     *
     * @param key    参数名称
     * @param format 日期格式
     * @param def    默认值
     * @return 参数值
     */
    public Date getDate(final String key, final SimpleDateFormat format, final Date def) {
        String value = getString(key);
        if (value == null || value.isEmpty() || format == null) {
            return def;
        }
        try {
            return format.parse(key);
        } catch (ParseException e) {
            return def;
        }
    }

    /**
     * 获取单精度浮点数参数值
     *
     * @param key 参数名称
     * @return 参数值
     */
    public Float getFloat(final String key) {
        return getFloat(key, null);
    }

    /**
     * 获取单精度浮点数参数值
     *
     * @param key 参数名称
     * @param def 默认值
     * @return 参数值
     */
    public Float getFloat(final String key, final Float def) {
        String value = getString(key);
        if (value == null || value.isEmpty()) {
            return def;
        }
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    /**
     * 获取双精度浮点数参数值
     *
     * @param key 参数名称
     * @return 参数值
     */
    public Double getDouble(final String key) {
        return getDouble(key, null);
    }

    /**
     * 获取双精度浮点数参数值
     *
     * @param key 参数名称
     * @param def 默认值
     * @return 参数值
     */
    public Double getDouble(final String key, final Double def) {
        String value = getString(key);
        if (value == null || value.isEmpty()) {
            return def;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    /**
     * 获取长整形参数值
     *
     * @param key 参数名称
     * @return 参数值
     */
    public Long getLong(final String key) {
        return getLong(key, null);
    }

    /**
     * 获取长整形参数值
     *
     * @param key 参数名称
     * @param def 默认值
     * @return 参数值
     */
    public Long getLong(final String key, final Long def) {
        String value = getString(key);
        if (value == null || value.isEmpty()) {
            return def;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    /**
     * 获取整形参数值
     *
     * @param key 参数名称
     * @return 参数值
     */
    public Integer getInteger(final String key) {
        return getInteger(key, null);
    }

    /**
     * 获取整形参数值
     *
     * @param key 参数名称
     * @param def 默认值
     * @return 参数值
     */
    public Integer getInteger(final String key, final Integer def) {
        String value = getString(key);
        if (value == null || value.isEmpty()) {
            return def;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    /**
     * 获取短整形参数值
     *
     * @param key 参数名称
     * @return 参数值
     */
    public Short getShort(final String key) {
        return getShort(key, null);
    }

    /**
     * 获取短整形参数值
     *
     * @param key 参数名称
     * @param def 默认值
     * @return 参数值
     */
    public Short getShort(final String key, final Short def) {
        String value = getString(key);
        if (value == null || value.isEmpty()) {
            return def;
        }
        try {
            return Short.parseShort(value);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    /**
     * 获取字节参数值
     *
     * @param key 参数名称
     * @return 参数值
     */
    public Byte getByte(final String key) {
        return getByte(key, null);
    }


    /**
     * 获取字节参数值
     *
     * @param key 参数名称
     * @param def 默认值
     * @return 参数值
     */
    public Byte getByte(final String key, final Byte def) {
        String value = getString(key);
        if (value == null || value.isEmpty()) {
            return def;
        }
        try {
            return Byte.parseByte(value);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    /**
     * 获取不二参数值
     *
     * @param key 参数名称
     * @return 参数值
     */
    public Boolean getBoolean(final String key) {
        return getBoolean(key, null);
    }

    /**
     * 获取不二参数值
     *
     * @param key 参数名称
     * @param def 默认值
     * @return 参数值
     */
    public Boolean getBoolean(final String key, final Boolean def) {
        String value = getString(key);
        if (value == null || value.isEmpty()) {
            return def;
        }
        return Boolean.parseBoolean(value);
    }

    /**
     * 获取长整形自然数参数值
     *
     * @param key 参数名称
     * @param def 默认值
     * @return 参数值
     */
    public Long getNatural(final String key, final Long def) {
        Long value = getLong(key, def);
        if (value != null && value < 0) {
            return def;
        }
        return value;
    }

    /**
     * 获取整形自然数参数值
     *
     * @param key 参数名称
     * @param def 默认值
     * @return 参数值
     */
    public Integer getNatural(final String key, final Integer def) {
        Integer value = getInteger(key, def);
        if (value != null && value < 0) {
            return def;
        }
        return value;
    }

    /**
     * 获取短整形自然数参数值
     *
     * @param key 参数名称
     * @param def 默认值
     * @return 参数值
     */
    public Short getNatural(final String key, final Short def) {
        Short value = getShort(key, def);
        if (value != null && value < 0) {
            return def;
        }
        return value;
    }

    /**
     * 获取字节自然数参数值
     *
     * @param key 参数名称
     * @param def 默认值
     * @return 参数值
     */
    public Byte getNatural(final String key, final Byte def) {
        Byte value = getByte(key, def);
        if (value != null && value < 0) {
            return def;
        }
        return value;
    }

    /**
     * 获取长整形正整数参数值
     *
     * @param key 参数名称
     * @param def 默认值
     * @return 参数值
     */
    public Long getPositive(final String key, final Long def) {
        Long value = getLong(key, def);
        if (value != null && value <= 0) {
            return def;
        }
        return value;
    }

    /**
     * 获取整形正整数参数值
     *
     * @param key 参数名称
     * @param def 默认值
     * @return 参数值
     */
    public Integer getPositive(final String key, final Integer def) {
        Integer value = getInteger(key, def);
        if (value != null && value <= 0) {
            return def;
        }
        return value;
    }

    /**
     * 获取短整形正整数参数值
     *
     * @param key 参数名称
     * @param def 默认值
     * @return 参数值
     */
    public Short getPositive(final String key, final Short def) {
        Short value = getShort(key, def);
        if (value != null && value <= 0) {
            return def;
        }
        return value;
    }

    /**
     * 获取字节正整数参数值
     *
     * @param key 参数名称
     * @param def 默认值
     * @return 参数值
     */
    public Byte getPositive(final String key, final Byte def) {
        Byte value = getByte(key, def);
        if (value != null && value <= 0) {
            return def;
        }
        return value;
    }

    /**
     * 判断参数是否存在
     *
     * @param key 参数名称
     * @return <li>true 存在</li>
     * <li>false 不存在</li>
     */
    public boolean contains(final String key) {
        String value = getString(key);
        return value != null && !value.isEmpty();
    }

    /**
     * 添加布尔参数
     *
     * @param key   参数名称
     * @param value 值
     * @return 新创建的URL对象
     */
    public URL add(final String key, final boolean value) {
        return add(key, String.valueOf(value));
    }

    /**
     * 添加字符参数
     *
     * @param key   参数名称
     * @param value 值
     * @return 新创建的URL对象
     */
    public URL add(final String key, final char value) {
        return add(key, String.valueOf(value));
    }

    /**
     * 添加字节参数
     *
     * @param key   参数名称
     * @param value 值
     * @return 新创建的URL对象
     */
    public URL add(final String key, final byte value) {
        return add(key, String.valueOf(value));
    }

    /**
     * 添加短整数参数
     *
     * @param key   参数名称
     * @param value 值
     * @return 新创建的URL对象
     */
    public URL add(final String key, final short value) {
        return add(key, String.valueOf(value));
    }

    /**
     * 添加整数参数
     *
     * @param key   参数名称
     * @param value 值
     * @return 新创建的URL对象
     */
    public URL add(final String key, final int value) {
        return add(key, String.valueOf(value));
    }

    /**
     * 添加长整数参数
     *
     * @param key   参数名称
     * @param value 值
     * @return 新创建的URL对象
     */
    public URL add(final String key, final long value) {
        return add(key, String.valueOf(value));
    }

    /**
     * 添加单精度浮点数参数
     *
     * @param key   参数名称
     * @param value 值
     * @return 新创建的URL对象
     */
    public URL add(final String key, final float value) {
        return add(key, String.valueOf(value));
    }

    /**
     * 添加双精度浮点数参数
     *
     * @param key   参数名称
     * @param value 值
     * @return 新创建的URL对象
     */
    public URL add(final String key, final double value) {
        return add(key, String.valueOf(value));
    }

    /**
     * 添加数字参数
     *
     * @param key   参数名称
     * @param value 值
     * @return 新创建的URL对象
     */
    public URL add(final String key, final Number value) {
        return add(key, value == null ? (String) null : String.valueOf(value));
    }

    /**
     * 添加字符序列参数
     *
     * @param key   参数名称
     * @param value 值
     * @return 新创建的URL对象
     */
    public URL add(final String key, final CharSequence value) {
        return add(key, value == null ? (String) null : value.toString());
    }

    /**
     * 添加字符串参数
     *
     * @param key   参数名称
     * @param value 值
     * @return 新创建的URL对象
     */
    public URL add(final String key, final String value) {
        if (key == null || key.isEmpty()) {
            return this;
        }
        Map<String, String> map = new HashMap<String, String>(getParameters());
        map.put(key, value);
        return new URL(protocol, user, password, host, port, path, map);
    }

    /**
     * 添加参数
     *
     * @param parameters 参数
     * @return 新创建的URL对象
     */
    public URL add(final Map<String, String> parameters) {
        if (parameters == null || parameters.isEmpty()) {
            return this;
        }
        Map<String, String> map = new HashMap<String, String>(getParameters());
        map.putAll(parameters);
        return new URL(protocol, user, password, host, port, path, map);
    }

    /**
     * 添加字符串参数
     *
     * @param key   参数名称
     * @param value 值
     * @return 新创建的URL对象
     * @throws UnsupportedEncodingException
     */
    public URL addEncoded(final String key, final String value) throws UnsupportedEncodingException {
        return add(key, encode(value));
    }

    /**
     * 添加字符串参数
     *
     * @param key     参数名称
     * @param value   值
     * @param charset 字符集
     * @return 新创建的URL对象
     * @throws UnsupportedEncodingException
     */
    public URL addEncoded(final String key, final String value, final String charset) throws UnsupportedEncodingException {
        return add(key, encode(value, charset));
    }

    /**
     * 添加字符串参数
     *
     * @param key   参数名称
     * @param value 值
     * @return 新创建的URL对象
     * @throws UnsupportedEncodingException
     */
    public URL addEncoded(final String key, final CharSequence value) throws UnsupportedEncodingException {
        return add(key, encode(value == null ? null : value.toString()));
    }

    /**
     * 添加字符串参数
     *
     * @param key     参数名称
     * @param value   值
     * @param charset 字符集
     * @return 新创建的URL对象
     * @throws UnsupportedEncodingException
     */
    public URL addEncoded(final String key, final CharSequence value, final String charset) throws UnsupportedEncodingException {
        return add(key, encode(value == null ? null : value.toString(), charset));
    }

    /**
     * 添加布尔参数
     *
     * @param key   参数名称
     * @param value 值
     * @return 新创建的URL对象
     */
    public URL addIfAbsent(final String key, final boolean value) {
        return addIfAbsent(key, String.valueOf(value));
    }

    /**
     * 添加字符参数
     *
     * @param key   参数名称
     * @param value 值
     * @return 新创建的URL对象
     */
    public URL addIfAbsent(final String key, final char value) {
        return addIfAbsent(key, String.valueOf(value));
    }

    /**
     * 添加字节参数
     *
     * @param key   参数名称
     * @param value 值
     * @return 新创建的URL对象
     */
    public URL addIfAbsent(final String key, final byte value) {
        return addIfAbsent(key, String.valueOf(value));
    }

    /**
     * 添加短整数参数
     *
     * @param key   参数名称
     * @param value 值
     * @return 新创建的URL对象
     */
    public URL addIfAbsent(final String key, final short value) {
        return addIfAbsent(key, String.valueOf(value));
    }

    /**
     * 添加整数参数
     *
     * @param key   参数名称
     * @param value 值
     * @return 新创建的URL对象
     */
    public URL addIfAbsent(final String key, final int value) {
        return addIfAbsent(key, String.valueOf(value));
    }

    /**
     * 添加长整数参数
     *
     * @param key   参数名称
     * @param value 值
     * @return 新创建的URL对象
     */
    public URL addIfAbsent(final String key, final long value) {
        return addIfAbsent(key, String.valueOf(value));
    }

    /**
     * 添加单精度浮点数参数
     *
     * @param key   参数名称
     * @param value 值
     * @return 新创建的URL对象
     */
    public URL addIfAbsent(final String key, final float value) {
        return addIfAbsent(key, String.valueOf(value));
    }

    /**
     * 添加双精度浮点数参数
     *
     * @param key   参数名称
     * @param value 值
     * @return 新创建的URL对象
     */
    public URL addIfAbsent(final String key, final double value) {
        return addIfAbsent(key, String.valueOf(value));
    }

    /**
     * 添加数字参数
     *
     * @param key   参数名称
     * @param value 值
     * @return 新创建的URL对象
     */
    public URL addIfAbsent(final String key, final Number value) {
        return addIfAbsent(key, value == null ? (String) null : String.valueOf(value));
    }

    /**
     * 添加字符序列参数
     *
     * @param key   参数名称
     * @param value 值
     * @return 新创建的URL对象
     */
    public URL addIfAbsent(final String key, final CharSequence value) {
        return addIfAbsent(key, value == null ? (String) null : value.toString());
    }

    /**
     * 如果参数不存在，则添加字符串参数
     *
     * @param key   参数名称
     * @param value 值
     * @return 新创建的URL对象
     */
    public URL addIfAbsent(final String key, final String value) {
        if (key == null || key.isEmpty() || value == null || value.isEmpty()) {
            return this;
        }
        if (contains(key)) {
            return this;
        }
        Map<String, String> map = new HashMap<String, String>(getParameters());
        map.put(key, value);
        return new URL(protocol, user, password, host, port, path, map);
    }


    /**
     * 添加不存在的参数
     *
     * @param parameters 参数
     * @return 新创建的URL对象
     */
    public URL addIfAbsent(final Map<String, String> parameters) {
        if (parameters == null || parameters.isEmpty()) {
            return this;
        }
        Map<String, String> map = new HashMap<String, String>(parameters);
        map.putAll(getParameters());
        return new URL(protocol, user, password, host, port, path, map);
    }

    /**
     * 删除参数
     *
     * @param key 参数
     * @return 新的URL对象
     */
    public URL remove(final String key) {
        if (key == null || key.isEmpty()) {
            return this;
        }
        return remove(key);
    }

    /**
     * 删除参数
     *
     * @param keys 参数
     * @return 新的URL对象
     */
    public URL remove(final Collection<String> keys) {
        if (keys == null || keys.size() == 0) {
            return this;
        }
        return remove(keys.toArray(new String[keys.size()]));
    }

    /**
     * 删除参数
     *
     * @param keys 参数
     * @return 新的URL对象
     */
    public URL remove(final String... keys) {
        if (keys == null || keys.length == 0) {
            return this;
        }
        Map<String, String> parameters = getParameters();
        Map<String, String> map = new HashMap<String, String>(parameters);
        for (String key : keys) {
            map.remove(key);
        }
        if (map.size() == parameters.size()) {
            return this;
        }
        return new URL(protocol, user, password, host, port, path, map);
    }

    /**
     * 删除所有参数
     *
     * @return 新的URL对象
     */
    public URL remove() {
        return new URL(protocol, user, password, host, port, path, new HashMap<String, String>());
    }

    /**
     * 转换成字符串，不包括用户信息
     *
     * @return 字符串表示
     */
    public String toString() {
        return toString(false, true); // no show user and password
    }

    /**
     * 构建字符串
     *
     * @param user       是否要带用户
     * @param parameter  是否要带参数
     * @param parameters 指定参数
     * @return 字符串
     */
    public String toString(final boolean user, final boolean parameter, final String... parameters) {
        StringBuilder buf = new StringBuilder();
        if (protocol != null && !protocol.isEmpty()) {
            buf.append(protocol).append("://");
        }
        if (user && this.user != null && !this.user.isEmpty()) {
            buf.append(this.user);
            if (password != null && !password.isEmpty()) {
                buf.append(':').append(password);
            }
            buf.append('@');
        }
        if (host != null && !host.isEmpty()) {
            buf.append(host);
            if (port > 0) {
                buf.append(':').append(port);
            }
        }
        if (path != null && !path.isEmpty()) {
            buf.append('/').append(path);
        }
        if (parameter) {
            append(buf, true, parameters);
        }
        return buf.toString();
    }

    /**
     * 追加参数
     *
     * @param buf        缓冲器
     * @param concat     是否追加参数连接符号"?"
     * @param parameters 参数名称
     */
    protected void append(final StringBuilder buf, final boolean concat, final String[] parameters) {
        Map<String, String> map = getParameters();
        if (map != null && !map.isEmpty()) {
            Set<String> includes = (parameters == null || parameters.length == 0 ? null : new HashSet<String>(
                    Arrays.asList(parameters)));
            boolean first = true;
            String key;
            // 按照字符串排序
            for (Map.Entry<String, String> entry : new TreeMap<String, String>(map).entrySet()) {
                key = entry.getKey();
                if (key != null && key.length() > 0 && (includes == null || includes.contains(key))) {
                    if (first) {
                        if (concat) {
                            buf.append('?');
                        }
                        first = false;
                    } else {
                        buf.append('&');
                    }
                    buf.append(key).append('=');
                    if (entry.getValue() != null) {
                        buf.append(entry.getValue().trim());
                    }
                }
            }
        }
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((protocol == null) ? 0 : protocol.hashCode());
        result = prime * result + ((host == null) ? 0 : host.hashCode());
        result = prime * result + port;
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        URL other = (URL) obj;
        if (protocol == null) {
            if (other.protocol != null) {
                return false;
            }
        } else if (!protocol.equals(other.protocol)) {
            return false;
        }
        if (host == null) {
            if (other.host != null) {
                return false;
            }
        } else if (!host.equals(other.host)) {
            return false;
        }
        if (port != other.port) {
            return false;
        }
        if (path == null) {
            if (other.path != null) {
                return false;
            }
        } else if (!path.equals(other.path)) {
            return false;
        }
        return true;
    }
}
