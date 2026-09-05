package com.github.paohaijiao.util;

import com.google.protobuf.*;

import java.lang.Enum;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 线程安全的 Any 类型转换器工厂
 *
 * 设计原则：
 * 1. 使用 ConcurrentHashMap 存储转换器
 * 2. 使用 Copy-On-Write 策略保证注册安全
 * 3. 所有公共方法都是线程安全的
 * 4. 支持 gRPC 多线程环境
 *
 * 使用示例：
 * <pre>
 * JQuickAnyTypeConverterFactory factory = JQuickAnyTypeConverterFactory.getInstance();
 *
 * // 可以在多个线程中安全使用
 * executor.submit(() -> {
 *     Any any = factory.toAny("hello");
 *     String str = factory.toStringValue(any);
 * });
 * </pre>
 */
public final class JQuickAnyTypeConverterFactory {

    private static volatile JQuickAnyTypeConverterFactory instance;

    // 单例锁
    private static final Object INSTANCE_LOCK = new Object();

    /**
     * 获取单例实例（线程安全）
     */
    public static JQuickAnyTypeConverterFactory getInstance() {
        if (instance == null) {
            synchronized (INSTANCE_LOCK) {
                if (instance == null) {
                    instance = new JQuickAnyTypeConverterFactory();
                }
            }
        }
        return instance;
    }

    /**
     * 创建新实例（用于自定义配置）
     */
    public static JQuickAnyTypeConverterFactory create() {
        return new JQuickAnyTypeConverterFactory();
    }


    @FunctionalInterface
    public interface ToAnyConverter {
        Any convert(Object value, JQuickAnyTypeConverterFactory factory);
    }

    @FunctionalInterface
    public interface FromAnyConverter {
        Object convert(Any any, JQuickAnyTypeConverterFactory factory);
    }

    /**
     * 线程安全的转换器注册表
     */
    public static class ConverterRegistry {

        // 使用 ConcurrentHashMap 保证线程安全
        private final ConcurrentMap<Class<?>, ToAnyConverter> toAnyConverters;
        private final ConcurrentMap<String, FromAnyConverter> fromAnyConverters;
        private final ConcurrentMap<String, FromAnyConverter> exactFromAnyConverters;

        // 读写锁保护注册操作
        private final ReentrantReadWriteLock lock;
        private volatile boolean sealed = false;

        public ConverterRegistry() {
            this.toAnyConverters = new ConcurrentHashMap<>();
            this.fromAnyConverters = new ConcurrentHashMap<>();
            this.exactFromAnyConverters = new ConcurrentHashMap<>();
            this.lock = new ReentrantReadWriteLock();
        }

        /**
         * 注册 Java -> Any 转换器（线程安全）
         */
        public ConverterRegistry registerToAny(Class<?> clazz, ToAnyConverter converter) {
            checkSealed();
            if (clazz == null || converter == null) {
                return this;
            }
            lock.writeLock().lock();
            try {
                toAnyConverters.put(clazz, converter);
            } finally {
                lock.writeLock().unlock();
            }
            return this;
        }

        /**
         * 注册 Any -> Java 转换器（线程安全）
         */
        public ConverterRegistry registerFromAny(String typeUrlPrefix, FromAnyConverter converter) {
            checkSealed();
            if (typeUrlPrefix == null || typeUrlPrefix.isEmpty() || converter == null) {
                return this;
            }
            lock.writeLock().lock();
            try {
                fromAnyConverters.put(typeUrlPrefix, converter);
            } finally {
                lock.writeLock().unlock();
            }
            return this;
        }

        /**
         * 注册精确 typeUrl 转换器（线程安全）
         */
        public ConverterRegistry registerExactFromAny(String exactTypeUrl, FromAnyConverter converter) {
            checkSealed();
            if (exactTypeUrl == null || exactTypeUrl.isEmpty() || converter == null) {
                return this;
            }
            lock.writeLock().lock();
            try {
                exactFromAnyConverters.put(exactTypeUrl, converter);
            } finally {
                lock.writeLock().unlock();
            }
            return this;
        }

        /**
         * 批量注册（线程安全）
         */
        public ConverterRegistry registerAll(ConverterRegistry other) {
            checkSealed();
            if (other == null) {
                return this;
            }
            lock.writeLock().lock();
            try {
                toAnyConverters.putAll(other.toAnyConverters);
                fromAnyConverters.putAll(other.fromAnyConverters);
                exactFromAnyConverters.putAll(other.exactFromAnyConverters);
            } finally {
                lock.writeLock().unlock();
            }
            return this;
        }

        /**
         * 获取 ToAny 转换器（线程安全，使用快照）
         */
        public ToAnyConverter getToAnyConverter(Class<?> clazz) {
            if (clazz == null) {
                return null;
            }

            // 先读快照，减少锁竞争
            ToAnyConverter converter = toAnyConverters.get(clazz);
            if (converter != null) {
                return converter;
            }

            // 遍历查找（ConcurrentHashMap 的遍历是弱一致性的，但足够安全）
            for (Map.Entry<Class<?>, ToAnyConverter> entry : toAnyConverters.entrySet()) {
                if (entry.getKey().isAssignableFrom(clazz)) {
                    return entry.getValue();
                }
            }
            return null;
        }

        /**
         * 获取 FromAny 转换器（线程安全）
         */
        public FromAnyConverter getFromAnyConverter(String typeUrl) {
            if (typeUrl == null || typeUrl.isEmpty()) {
                return null;
            }

            // 1. 精确匹配
            FromAnyConverter converter = exactFromAnyConverters.get(typeUrl);
            if (converter != null) {
                return converter;
            }

            // 2. 前缀匹配
            for (Map.Entry<String, FromAnyConverter> entry : fromAnyConverters.entrySet()) {
                if (typeUrl.startsWith(entry.getKey())) {
                    return entry.getValue();
                }
            }
            return null;
        }

        /**
         * 密封注册表（阻止后续修改）
         */
        public ConverterRegistry seal() {
            this.sealed = true;
            return this;
        }

        /**
         * 是否已密封
         */
        public boolean isSealed() {
            return sealed;
        }

        /**
         * 创建不可变副本
         */
        public ConverterRegistry immutableCopy() {
            ConverterRegistry copy = new ConverterRegistry();
            copy.toAnyConverters.putAll(this.toAnyConverters);
            copy.fromAnyConverters.putAll(this.fromAnyConverters);
            copy.exactFromAnyConverters.putAll(this.exactFromAnyConverters);
            copy.seal();
            return copy;
        }

        private void checkSealed() {
            if (sealed) {
                throw new IllegalStateException("Registry is sealed and cannot be modified");
            }
        }

        /**
         * 获取转换器数量（用于调试）
         */
        public int getToAnyConverterCount() {
            return toAnyConverters.size();
        }

        public int getFromAnyConverterCount() {
            return fromAnyConverters.size() + exactFromAnyConverters.size();
        }
    }


    private final ConverterRegistry registry;
    private final DateTimeFormatter isoFormatter;

    // 缓存常用的 Any 实例（线程安全）
    private static final Any NULL_ANY;
    private static final Any EMPTY_ANY;

    static {
        NULL_ANY = Any.pack(Value.newBuilder().setNullValue(NullValue.NULL_VALUE).build());
        EMPTY_ANY = Any.getDefaultInstance();
    }

    /**
     * 私有构造函数，使用默认注册表
     */
    private JQuickAnyTypeConverterFactory() {
        this(createDefaultRegistry());
    }

    /**
     * 构造函数，使用自定义注册表
     */
    public JQuickAnyTypeConverterFactory(ConverterRegistry registry) {
        this.registry = registry != null ? registry : createDefaultRegistry();
        this.isoFormatter = DateTimeFormatter.ISO_INSTANT;
    }

    /**
     * 创建默认转换器注册表（线程安全）
     */
    private static ConverterRegistry createDefaultRegistry() {
        ConverterRegistry registry = new ConverterRegistry();

        // NULL
        registry.registerToAny(null, (value, factory) -> factory.packNull());

        // 基础类型
        registry.registerToAny(String.class, (value, factory) ->
                Any.pack(StringValue.of((String) value)));

        registry.registerToAny(Integer.class, (value, factory) ->
                Any.pack(Int32Value.of((Integer) value)));
        registry.registerToAny(int.class, (value, factory) ->
                Any.pack(Int32Value.of((Integer) value)));

        registry.registerToAny(Long.class, (value, factory) ->
                Any.pack(Int64Value.of((Long) value)));
        registry.registerToAny(long.class, (value, factory) ->
                Any.pack(Int64Value.of((Long) value)));

        registry.registerToAny(Double.class, (value, factory) ->
                Any.pack(DoubleValue.of((Double) value)));
        registry.registerToAny(double.class, (value, factory) ->
                Any.pack(DoubleValue.of((Double) value)));

        registry.registerToAny(Float.class, (value, factory) ->
                Any.pack(FloatValue.of((Float) value)));
        registry.registerToAny(float.class, (value, factory) ->
                Any.pack(FloatValue.of((Float) value)));

        registry.registerToAny(Boolean.class, (value, factory) ->
                Any.pack(BoolValue.of((Boolean) value)));
        registry.registerToAny(boolean.class, (value, factory) ->
                Any.pack(BoolValue.of((Boolean) value)));

        registry.registerToAny(Byte.class, (value, factory) ->
                Any.pack(BytesValue.of(ByteString.copyFrom(new byte[]{(Byte) value}))));
        registry.registerToAny(byte.class, (value, factory) ->
                Any.pack(BytesValue.of(ByteString.copyFrom(new byte[]{(Byte) value}))));

        registry.registerToAny(Short.class, (value, factory) ->
                Any.pack(Int32Value.of((Short) value)));
        registry.registerToAny(short.class, (value, factory) ->
                Any.pack(Int32Value.of((Short) value)));

        registry.registerToAny(Character.class, (value, factory) ->
                Any.pack(StringValue.of(String.valueOf((Character) value))));
        registry.registerToAny(char.class, (value, factory) ->
                Any.pack(StringValue.of(String.valueOf((Character) value))));

        // 大数字
        registry.registerToAny(BigInteger.class, (value, factory) ->
                Any.pack(StringValue.of(value.toString())));
        registry.registerToAny(BigDecimal.class, (value, factory) ->
                Any.pack(StringValue.of(value.toString())));

        // 时间类型
        registry.registerToAny(Date.class, (value, factory) -> {
            Date date = (Date) value;
            return Any.pack(Timestamp.newBuilder()
                    .setSeconds(date.getTime() / 1000)
                    .setNanos((int) (date.getTime() % 1000) * 1_000_000)
                    .build());
        });

        registry.registerToAny(Instant.class, (value, factory) -> {
            Instant instant = (Instant) value;
            return Any.pack(Timestamp.newBuilder()
                    .setSeconds(instant.getEpochSecond())
                    .setNanos(instant.getNano())
                    .build());
        });

        registry.registerToAny(LocalDateTime.class, (value, factory) -> {
            Instant instant = ((LocalDateTime) value).toInstant(ZoneOffset.UTC);
            return Any.pack(Timestamp.newBuilder()
                    .setSeconds(instant.getEpochSecond())
                    .setNanos(instant.getNano())
                    .build());
        });

        // 枚举
        registry.registerToAny(Enum.class, (value, factory) ->
                Any.pack(StringValue.of(((Enum<?>) value).name())));

        // 字节数组
        registry.registerToAny(byte[].class, (value, factory) ->
                Any.pack(BytesValue.of(ByteString.copyFrom((byte[]) value))));

        // 列表
        registry.registerToAny(List.class, (value, factory) ->
                factory.packList((List<?>) value));
        registry.registerToAny(Set.class, (value, factory) ->
                factory.packList(new ArrayList<>((Set<?>) value)));

        // Map
        registry.registerToAny(Map.class, (value, factory) ->
                factory.packMap((Map<?, ?>) value));

        // 数组
        registry.registerToAny(Object[].class, (value, factory) ->
                factory.packList(Arrays.asList((Object[]) value)));
        registry.registerToAny(int[].class, (value, factory) -> {
            int[] arr = (int[]) value;
            List<Integer> list = new ArrayList<>(arr.length);
            for (int v : arr) list.add(v);
            return factory.packList(list);
        });
        registry.registerToAny(long[].class, (value, factory) -> {
            long[] arr = (long[]) value;
            List<Long> list = new ArrayList<>(arr.length);
            for (long v : arr) list.add(v);
            return factory.packList(list);
        });
        registry.registerToAny(double[].class, (value, factory) -> {
            double[] arr = (double[]) value;
            List<Double> list = new ArrayList<>(arr.length);
            for (double v : arr) list.add(v);
            return factory.packList(list);
        });
        registry.registerToAny(float[].class, (value, factory) -> {
            float[] arr = (float[]) value;
            List<Float> list = new ArrayList<>(arr.length);
            for (float v : arr) list.add(v);
            return factory.packList(list);
        });
        registry.registerToAny(boolean[].class, (value, factory) -> {
            boolean[] arr = (boolean[]) value;
            List<Boolean> list = new ArrayList<>(arr.length);
            for (boolean v : arr) list.add(v);
            return factory.packList(list);
        });

        // Protobuf Message
        registry.registerToAny(Message.class, (value, factory) ->
                Any.pack((Message) value));

        // NULL
        registry.registerFromAny("type.googleapis.com/google.protobuf.Value",
                (any, factory) -> {
                    try {
                        Value v = any.unpack(Value.class);
                        switch (v.getKindCase()) {
                            case NULL_VALUE: return null;
                            case STRING_VALUE: return v.getStringValue();
                            case NUMBER_VALUE: return v.getNumberValue();
                            case BOOL_VALUE: return v.getBoolValue();
                            default: return null;
                        }
                    } catch (InvalidProtocolBufferException e) {
                        return null;
                    }
                });

        // 基础类型
        registry.registerFromAny("type.googleapis.com/google.protobuf.StringValue",
                (any, factory) -> factory.unpack(any, StringValue.class).getValue());
        registry.registerFromAny("type.googleapis.com/google.protobuf.Int32Value",
                (any, factory) -> factory.unpack(any, Int32Value.class).getValue());
        registry.registerFromAny("type.googleapis.com/google.protobuf.Int64Value",
                (any, factory) -> factory.unpack(any, Int64Value.class).getValue());
        registry.registerFromAny("type.googleapis.com/google.protobuf.DoubleValue",
                (any, factory) -> factory.unpack(any, DoubleValue.class).getValue());
        registry.registerFromAny("type.googleapis.com/google.protobuf.FloatValue",
                (any, factory) -> factory.unpack(any, FloatValue.class).getValue());
        registry.registerFromAny("type.googleapis.com/google.protobuf.BoolValue",
                (any, factory) -> factory.unpack(any, BoolValue.class).getValue());
        registry.registerFromAny("type.googleapis.com/google.protobuf.BytesValue",
                (any, factory) -> factory.unpack(any, BytesValue.class).getValue().toByteArray());

        // 时间类型
        registry.registerFromAny("type.googleapis.com/google.protobuf.Timestamp",
                (any, factory) -> {
                    Timestamp ts = factory.unpack(any, Timestamp.class);
                    return Instant.ofEpochSecond(ts.getSeconds(), ts.getNanos());
                });

        // 列表
        registry.registerFromAny("type.googleapis.com/google.protobuf.ListValue",
                (any, factory) -> factory.fromListValue(factory.unpack(any, ListValue.class)));

        // Map
        registry.registerFromAny("type.googleapis.com/google.protobuf.Struct",
                (any, factory) -> factory.fromStruct(factory.unpack(any, Struct.class)));

        return registry.seal();
    }


    /**
     * 将 Java 对象转换为 Any（线程安全）
     */
    public Any toAny(Object value) {
        if (value == null) {
            return NULL_ANY;
        }

        Class<?> clazz = value.getClass();

        // 1. 精确匹配
        ToAnyConverter converter = registry.getToAnyConverter(clazz);
        if (converter != null) {
            return converter.convert(value, this);
        }

        // 2. 检查是否是 Message
        if (value instanceof Message) {
            return Any.pack((Message) value);
        }

        // 3. 检查是否是数组
        if (clazz.isArray() && !clazz.getComponentType().isPrimitive()) {
            return packList(Arrays.asList((Object[]) value));
        }

        throw new IllegalArgumentException("Unsupported type: " + clazz.getName());
    }

    /**
     * 将 Any 转换为 Java 对象（线程安全）
     */
    public Object fromAny(Any any) {
        if (any == null || any == EMPTY_ANY) {
            return null;
        }

        String typeUrl = any.getTypeUrl();
        FromAnyConverter converter = registry.getFromAnyConverter(typeUrl);
        if (converter != null) {
            return converter.convert(any, this);
        }

        return any;
    }

    /**
     * 类型安全的转换（线程安全）
     */
    @SuppressWarnings("unchecked")
    public <T> T toType(Any any, Class<T> targetType) {
        if (any == null || any == EMPTY_ANY) {
            return null;
        }
        if (targetType == null) {
            throw new IllegalArgumentException("Target type cannot be null");
        }

        Object value = fromAny(any);
        if (value == null) {
            return null;
        }
        if (targetType.isInstance(value)) {
            return (T) value;
        }
        return convertToType(value, targetType);
    }


    /**
     * 转换为整数（线程安全）
     */
    public Integer toInt(Any any) {
        if (any == null || any == EMPTY_ANY) {
            return null;
        }
        Object value = fromAny(any);
        if (value == null) return null;
        if (value instanceof Number) return ((Number) value).intValue();
        if (value instanceof String) return Integer.parseInt((String) value);
        throw new IllegalArgumentException("Cannot convert to Integer: " + value.getClass());
    }

    /**
     * 转换为长整数（线程安全）
     */
    public Long toLong(Any any) {
        if (any == null || any == EMPTY_ANY) {
            return null;
        }
        Object value = fromAny(any);
        if (value == null) return null;
        if (value instanceof Number) return ((Number) value).longValue();
        if (value instanceof String) return Long.parseLong((String) value);
        throw new IllegalArgumentException("Cannot convert to Long: " + value.getClass());
    }

    /**
     * 转换为布尔值（线程安全）
     */
    public Boolean toBoolean(Any any) {
        if (any == null || any == EMPTY_ANY) {
            return null;
        }
        Object value = fromAny(any);
        if (value == null) return null;
        if (value instanceof Boolean) return (Boolean) value;
        if (value instanceof String) return Boolean.parseBoolean((String) value);
        throw new IllegalArgumentException("Cannot convert to Boolean: " + value.getClass());
    }

    /**
     * 转换为列表（线程安全）
     */
    @SuppressWarnings("unchecked")
    public List<Object> toList(Any any) {
        if (any == null || any == EMPTY_ANY) {
            return Collections.emptyList();
        }
        Object value = fromAny(any);
        if (value instanceof List) {
            return (List<Object>) value;
        }
        if (value instanceof Object[]) {
            return Arrays.asList((Object[]) value);
        }
        if (value == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(value);
    }

    /**
     * 转换为 Map（线程安全）
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> toMap(Any any) {
        if (any == null || any == EMPTY_ANY) {
            return Collections.emptyMap();
        }
        Object value = fromAny(any);
        if (value instanceof Map) {
            return (Map<String, Object>) value;
        }
        return Collections.emptyMap();
    }

    /**
     * 判断是否为 NULL（线程安全）
     */
    public boolean isNull(Any any) {
        if (any == null || any == EMPTY_ANY) {
            return true;
        }
        if (any.is(Value.class)) {
            try {
                Value v = any.unpack(Value.class);
                return v.getKindCase() == Value.KindCase.NULL_VALUE;
            } catch (InvalidProtocolBufferException e) {
                return false;
            }
        }
        return false;
    }

    /**
     * 创建 NULL 值（线程安全，返回缓存实例）
     */
    public Any packNull() {
        return NULL_ANY;
    }

    /**
     * 获取注册表（线程安全，返回不可变副本）
     */
    public ConverterRegistry getRegistry() {
        return registry.immutableCopy();
    }

    protected <T extends Message> T unpack(Any any, Class<T> clazz) {
        try {
            return any.unpack(clazz);
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException("Failed to unpack " + clazz.getSimpleName(), e);
        }
    }

    protected Any packList(List<?> list) {
        if (list == null || list.isEmpty()) {
            return Any.pack(ListValue.getDefaultInstance());
        }
        ListValue.Builder listBuilder = ListValue.newBuilder();
        for (Object item : list) {
            listBuilder.addValues(toValue(item));
        }
        return Any.pack(listBuilder.build());
    }

    protected Any packMap(Map<?, ?> map) {
        if (map == null || map.isEmpty()) {
            return Any.pack(Struct.getDefaultInstance());
        }
        Struct.Builder structBuilder = Struct.newBuilder();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            String key = entry.getKey().toString();
            structBuilder.putFields(key, toValue(entry.getValue()));
        }
        return Any.pack(structBuilder.build());
    }

    protected Value toValue(Object obj) {
        Value.Builder builder = Value.newBuilder();
        if (obj == null) {
            builder.setNullValue(NullValue.NULL_VALUE);
        } else if (obj instanceof String) {
            builder.setStringValue((String) obj);
        } else if (obj instanceof Number) {
            builder.setNumberValue(((Number) obj).doubleValue());
        } else if (obj instanceof Boolean) {
            builder.setBoolValue((Boolean) obj);
        } else if (obj instanceof List) {
            builder.setListValue(toListValue((List<?>) obj));
        } else if (obj instanceof Map) {
            builder.setStructValue(toStruct((Map<?, ?>) obj));
        } else {
            builder.setStringValue(obj.toString());
        }
        return builder.build();
    }

    protected ListValue toListValue(List<?> list) {
        ListValue.Builder builder = ListValue.newBuilder();
        for (Object item : list) {
            builder.addValues(toValue(item));
        }
        return builder.build();
    }

    protected Struct toStruct(Map<?, ?> map) {
        Struct.Builder builder = Struct.newBuilder();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            builder.putFields(entry.getKey().toString(), toValue(entry.getValue()));
        }
        return builder.build();
    }

    protected List<Object> fromListValue(ListValue listValue) {
        if (listValue == null) {
            return Collections.emptyList();
        }
        List<Object> result = new ArrayList<>(listValue.getValuesCount());
        for (Value v : listValue.getValuesList()) {
            result.add(fromValue(v));
        }
        return result;
    }

    protected Map<String, Object> fromStruct(Struct struct) {
        if (struct == null) {
            return Collections.emptyMap();
        }
        Map<String, Object> result = new LinkedHashMap<>();
        for (Map.Entry<String, Value> entry : struct.getFieldsMap().entrySet()) {
            result.put(entry.getKey(), fromValue(entry.getValue()));
        }
        return result;
    }

    protected Object fromValue(Value value) {
        if (value == null) {
            return null;
        }
        switch (value.getKindCase()) {
            case NULL_VALUE:
                return null;
            case STRING_VALUE:
                return value.getStringValue();
            case NUMBER_VALUE:
                return value.getNumberValue();
            case BOOL_VALUE:
                return value.getBoolValue();
            case LIST_VALUE:
                return fromListValue(value.getListValue());
            case STRUCT_VALUE:
                return fromStruct(value.getStructValue());
            default:
                return null;
        }
    }

    @SuppressWarnings("unchecked")
    protected <T> T convertToType(Object value, Class<T> targetType) {
        if (value == null) return null;

        if (value instanceof String) {
            String str = (String) value;
            if (targetType == Integer.class || targetType == int.class) {
                return (T) Integer.valueOf(str);
            }
            if (targetType == Long.class || targetType == long.class) {
                return (T) Long.valueOf(str);
            }
            if (targetType == Double.class || targetType == double.class) {
                return (T) Double.valueOf(str);
            }
            if (targetType == Float.class || targetType == float.class) {
                return (T) Float.valueOf(str);
            }
            if (targetType == Boolean.class || targetType == boolean.class) {
                return (T) Boolean.valueOf(str);
            }
            if (targetType == Byte.class || targetType == byte.class) {
                return (T) Byte.valueOf(str);
            }
            if (targetType == Short.class || targetType == short.class) {
                return (T) Short.valueOf(str);
            }
        }

        if (value instanceof Number) {
            Number num = (Number) value;
            if (targetType == Integer.class || targetType == int.class) {
                return (T) Integer.valueOf(num.intValue());
            }
            if (targetType == Long.class || targetType == long.class) {
                return (T) Long.valueOf(num.longValue());
            }
            if (targetType == Double.class || targetType == double.class) {
                return (T) Double.valueOf(num.doubleValue());
            }
            if (targetType == Float.class || targetType == float.class) {
                return (T) Float.valueOf(num.floatValue());
            }
            if (targetType == Short.class || targetType == short.class) {
                return (T) Short.valueOf(num.shortValue());
            }
            if (targetType == Byte.class || targetType == byte.class) {
                return (T) Byte.valueOf(num.byteValue());
            }
        }

        throw new IllegalArgumentException("Cannot convert " + value.getClass() + " to " + targetType);
    }

    public List<Any> toAnyList(Collection<?> values) {
        if (values == null || values.isEmpty()) {
            return Collections.emptyList();
        }
        List<Any> result = new ArrayList<>(values.size());
        for (Object value : values) {
            result.add(toAny(value));
        }
        return Collections.unmodifiableList(result);
    }

    public List<Object> fromAnyList(Collection<Any> anys) {
        if (anys == null || anys.isEmpty()) {
            return Collections.emptyList();
        }
        List<Object> result = new ArrayList<>(anys.size());
        for (Any any : anys) {
            result.add(fromAny(any));
        }
        return Collections.unmodifiableList(result);
    }

    public Map<String, Any> toAnyMap(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, Any> result = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            result.put(entry.getKey(), toAny(entry.getValue()));
        }
        return Collections.unmodifiableMap(result);
    }

    public Map<String, Object> fromAnyMap(Map<String, Any> map) {
        if (map == null || map.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, Object> result = new LinkedHashMap<>();
        for (Map.Entry<String, Any> entry : map.entrySet()) {
            result.put(entry.getKey(), fromAny(entry.getValue()));
        }
        return Collections.unmodifiableMap(result);
    }
}