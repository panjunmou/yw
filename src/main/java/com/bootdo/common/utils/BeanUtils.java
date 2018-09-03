package com.bootdo.common.utils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.beanutils.converters.LongConverter;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ReflectionUtils;

public class BeanUtils {

    private static Logger logger = LoggerFactory.getLogger(BeanUtils.class);

    public static ConvertUtilsBean convertUtilsBean = new ConvertUtilsBean();

    private static BeanUtilsBean beanUtilsBean = new BeanUtilsBean(convertUtilsBean, new PropertyUtilsBean());

    public static boolean isEmpty (Object o) {
        if (o == null)
            return true;
        if ((o instanceof String)) {
            if (((String) o).trim().length() == 0) {
                return true;
            }
        } else if ((o instanceof Collection)) {
            if (((Collection) o).isEmpty()) {
                return true;
            }
        } else if (o.getClass().isArray()) {
            if (((Object[]) o).length == 0) {
                return true;
            }
        } else if ((o instanceof Map)) {
            if (((Map) o).isEmpty()) {
                return true;
            }
        } else if ((o instanceof Long)) {
            if ((Long) o == null) {
                return true;
            }
        } else if ((o instanceof Short)) {
            if ((Short) o == null) {
                return true;
            }
        } else {
            return false;
        }
        return false;
    }

    public static boolean isNotEmpty (Object o) {
        return !isEmpty(o);
    }

    public static boolean isNotEmpty (Long o) {
        return !isEmpty(o);
    }

    public static boolean isNumber (Object o) {
        if (o == null)
            return false;
        if ((o instanceof Number)) {
            return true;
        }
        if ((o instanceof String)) {
            try {
                Double.parseDouble((String) o);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }

    public static Object populateEntity (Map map, Object entity) throws IllegalAccessException,
            InvocationTargetException {
        beanUtilsBean.populate(entity, map);
        return entity;
    }

    /**
     * 验证类是否存在
     *
     * @param className 类名需要报名+类名
     * @return
     */
    public static boolean validClass (String className) {
        try {
            // 类存在
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
        }
        return false;
    }

    public static boolean isInherit (Class cls, Class parentClass) {
        return parentClass.isAssignableFrom(cls);
    }

    public static Object cloneBean (Object bean) {
        try {
            return beanUtilsBean.cloneBean(bean);
        } catch (Exception e) {
            handleReflectionException(e);
        }
        return null;
    }

    public static List<String> scanPackages (String basePackages) throws IllegalArgumentException {
        ResourcePatternResolver rl = new PathMatchingResourcePatternResolver();
        MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(rl);
        List result = new ArrayList();
        String[] arrayPackages = basePackages.split(",");
        try {
            for (int j = 0; j < arrayPackages.length; j++) {
                String packageToScan = arrayPackages[j];
                String packagePart = packageToScan.replace('.', '/');
                String classPattern = "classpath*:/" + packagePart + "*.class";
                Resource[] resources = rl.getResources(classPattern);
                for (int i = 0; i < resources.length; i++) {
                    Resource resource = resources[i];
                    MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                    String className = metadataReader.getClassMetadata().getClassName();
                    result.add(className);
                }
            }
        } catch (Exception e) {
            new IllegalArgumentException("scan pakcage class error,pakcages:" + basePackages);
        }

        return result;
    }

    public static void copyNotNullProperties (Object dest, Object orig) {
        if (dest == null) {
            logger.error("No destination bean specified");
            return;
        }
        if (orig == null) {
            logger.error("No origin bean specified");
            return;
        }

        try {
            if ((orig instanceof DynaBean)) {
                DynaProperty[] origDescriptors = ((DynaBean) orig).getDynaClass().getDynaProperties();

                for (int i = 0; i < origDescriptors.length; i++) {
                    String name = origDescriptors[i].getName();
                    if ((beanUtilsBean.getPropertyUtils().isReadable(orig, name))
                            && (beanUtilsBean.getPropertyUtils().isWriteable(dest, name))) {
                        Object value = ((DynaBean) orig).get(name);
                        beanUtilsBean.copyProperty(dest, name, value);
                    }
                }
            } else if ((orig instanceof Map)) {
                Iterator entries = ((Map) orig).entrySet().iterator();
                while (entries.hasNext()) {
                    Map.Entry entry = (Map.Entry) entries.next();
                    String name = (String) entry.getKey();
                    if (beanUtilsBean.getPropertyUtils().isWriteable(dest, name))
                        beanUtilsBean.copyProperty(dest, name, entry.getValue());
                }
            } else {
                PropertyDescriptor[] origDescriptors = beanUtilsBean.getPropertyUtils().getPropertyDescriptors(orig);

                for (int i = 0; i < origDescriptors.length; i++) {
                    String name = origDescriptors[i].getName();
                    if (!"class".equals(name)) {
                        if ((beanUtilsBean.getPropertyUtils().isReadable(orig, name))
                                && (beanUtilsBean.getPropertyUtils().isWriteable(dest, name)))
                            try {
                                Object value = beanUtilsBean.getPropertyUtils().getSimpleProperty(orig, name);
                                if (value != null)
                                    beanUtilsBean.copyProperty(dest, name, value);
                            } catch (NoSuchMethodException e) {
                                e.printStackTrace();
                            }
                    }
                }
            }
        } catch (Exception ex) {
            handleReflectionException(ex);
        }
    }

    public static <T> T copyProperties (Class<T> destClass, Object orig) {
        Object target = null;
        try {
            target = destClass.newInstance();
            copyProperties(target, orig);
            return (T) target;
        } catch (Exception e) {
            handleReflectionException(e);
        }
        return null;
    }

    public static void copyProperties (Object dest, Object orig) {
        try {
            beanUtilsBean.copyProperties(dest, orig);
        } catch (Exception e) {
            handleReflectionException(e);
        }
    }

    public static void copyProperty (Object bean, String name, Object value) {
        try {
            beanUtilsBean.copyProperty(bean, name, value);
        } catch (Exception e) {
            handleReflectionException(e);
        }
    }

    /**
     * copy 指定属性
     *
     * @param des        目的对象
     * @param src        源对象
     * @param properties 属性名称
     */
    public static void copySpecifiedProperties (Object des, Object src, String... properties) {
        try {
            for (String property : properties) {
                beanUtilsBean.copyProperty(des, property, PropertyUtils.getProperty(src, property));

            }
        } catch (Exception e) {
            handleReflectionException(e);
        }
    }

    /**
     * 只复制简单对象，
     *
     * @param des
     * @param src
     * @param ignoreProperties
     */
    public static void copySimpleProperties (Object des, Object src) {
        PropertyDescriptor[] origDescriptors =
                getPropertyUtils().getPropertyDescriptors(src);
        for (int i = 0; i < origDescriptors.length; i++) {
            String name = origDescriptors[i].getName();
            if ("class".equals(name)) {
                continue; // No point in trying to set an object's class
            }

            Class clz = origDescriptors[i].getPropertyType();
            if (clz.isEnum() || clz.getName().startsWith("java.lang") || clz.getName().startsWith("java.sql")) {
                if (BeanUtils.getPropertyUtils().isReadable(src, name) &&
                        getPropertyUtils().isWriteable(des, name)) {
                    try {
                        Object value =
                                getPropertyUtils().getSimpleProperty(src, name);
                        copyProperty(des, name, value);
                    } catch (Exception e) {
                        // Should not happen
                    }
                }
            }
        }
    }

    public static Map describe (Object bean) {
        try {
            return beanUtilsBean.describe(bean);
        } catch (Exception e) {
            handleReflectionException(e);
        }
        return null;
    }

    public static String[] getArrayProperty (Object bean, String name) {
        try {
            return beanUtilsBean.getArrayProperty(bean, name);
        } catch (Exception e) {
            handleReflectionException(e);
        }
        return null;
    }

    public static ConvertUtilsBean getConvertUtils () {
        return beanUtilsBean.getConvertUtils();
    }

    public static String getIndexedProperty (Object bean, String name, int index) {
        try {
            return beanUtilsBean.getIndexedProperty(bean, name, index);
        } catch (Exception e) {
            handleReflectionException(e);
        }
        return null;
    }

    public static String getIndexedProperty (Object bean, String name) {
        try {
            return beanUtilsBean.getIndexedProperty(bean, name);
        } catch (Exception e) {
            handleReflectionException(e);
        }
        return null;
    }

    public static String getMappedProperty (Object bean, String name, String key) {
        try {
            return beanUtilsBean.getMappedProperty(bean, name, key);
        } catch (Exception e) {
            handleReflectionException(e);
        }
        return null;
    }

    public static String getMappedProperty (Object bean, String name) {
        try {
            return beanUtilsBean.getMappedProperty(bean, name);
        } catch (Exception e) {
            handleReflectionException(e);
        }
        return null;
    }

    public static String getNestedProperty (Object bean, String name) {
        try {
            return beanUtilsBean.getNestedProperty(bean, name);
        } catch (Exception e) {
            handleReflectionException(e);
        }
        return null;
    }

    public static String getProperty (Object bean, String name) {
        try {
            return beanUtilsBean.getProperty(bean, name);
        } catch (Exception e) {
            handleReflectionException(e);
        }
        return null;
    }

    public static PropertyUtilsBean getPropertyUtils () {
        try {
            return beanUtilsBean.getPropertyUtils();
        } catch (Exception e) {
            handleReflectionException(e);
        }
        return null;
    }

    public static String getSimpleProperty (Object bean, String name) {
        try {
            return beanUtilsBean.getSimpleProperty(bean, name);
        } catch (Exception e) {
            handleReflectionException(e);
        }
        return null;
    }

    public static void populate (Object bean, Map properties) {
        try {
            beanUtilsBean.populate(bean, properties);
        } catch (Exception e) {
            handleReflectionException(e);
        }
    }

    public static void setProperty (Object bean, String name, Object value) {
        try {
            beanUtilsBean.setProperty(bean, name, value);
        } catch (Exception e) {
            handleReflectionException(e);
        }
    }

    private static void handleReflectionException (Exception e) {
        ReflectionUtils.handleReflectionException(e);
    }

    static {
        convertUtilsBean.register(new BeanDateConnverter(), Date.class);
        convertUtilsBean.register(new LongConverter(null), Long.class);
    }

}
