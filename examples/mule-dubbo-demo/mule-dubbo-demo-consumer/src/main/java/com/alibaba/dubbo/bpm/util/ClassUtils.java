/**
 * 
 */
package com.alibaba.dubbo.bpm.util;

import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yihaijun
 *
 */
public class ClassUtils extends org.apache.commons.lang.ClassUtils {
    private static final Map<String, Class<?>> primitiveTypeNameMap = new HashMap<String, Class<?>>(32);

    /**
     * Load a class with a given name. <p/> It will try to load the class in the
     * following order:
     * <ul>
     * <li>From
     * {@link Thread#getContextClassLoader() Thread.currentThread().getContextClassLoader()}
     * <li>Using the basic {@link Class#forName(java.lang.String) }
     * <li>From
     * {@link Class#getClassLoader() ClassLoaderUtil.class.getClassLoader()}
     * <li>From the {@link Class#getClassLoader() callingClass.getClassLoader() }
     * </ul>
     *
     * @param className    The name of the class to load
     * @param callingClass The Class object of the calling object
     * @return The Class instance
     * @throws ClassNotFoundException If the class cannot be found anywhere.
     */
    public static Class loadClass(final String className, final Class<?> callingClass) throws ClassNotFoundException
    {
        return loadClass(className, callingClass, Object.class);
    }

    /**
     * Load a class with a given name. <p/> It will try to load the class in the
     * following order:
     * <ul>
     * <li>From
     * {@link Thread#getContextClassLoader() Thread.currentThread().getContextClassLoader()}
     * <li>Using the basic {@link Class#forName(java.lang.String) }
     * <li>From
     * {@link Class#getClassLoader() ClassLoaderUtil.class.getClassLoader()}
     * <li>From the {@link Class#getClassLoader() callingClass.getClassLoader() }
     * </ul>
     *
     * @param className    The name of the class to load
     * @param callingClass The Class object of the calling object
     * @param type the class type to expect to load
     * @return The Class instance
     * @throws ClassNotFoundException If the class cannot be found anywhere.
     */
    public static <T extends Class> T loadClass(final String className, final Class<?> callingClass, T type) throws ClassNotFoundException
    {
        if (className.length() <= 8)
        {
            // Could be a primitive - likely.
            if (primitiveTypeNameMap.containsKey(className))
            {
                return (T) primitiveTypeNameMap.get(className);
            }
        }
        
        Class<?> clazz = AccessController.doPrivileged(new PrivilegedAction<Class<?>>()
        {
            public Class<?> run()
            {
                try
                {
                    final ClassLoader cl = Thread.currentThread().getContextClassLoader();
                    return cl != null ? cl.loadClass(className) : null;

                }
                catch (ClassNotFoundException e)
                {
                    return null;
                }
            }
        });

        if (clazz == null)
        {
            clazz = AccessController.doPrivileged(new PrivilegedAction<Class<?>>()
            {
                public Class<?> run()
                {
                    try
                    {
                        return Class.forName(className);
                    }
                    catch (ClassNotFoundException e)
                    {
                        return null;
                    }
                }
            });
        }

        if (clazz == null)
        {
            clazz = AccessController.doPrivileged(new PrivilegedAction<Class<?>>()
            {
                public Class<?> run()
                {
                    try
                    {
                        return ClassUtils.class.getClassLoader().loadClass(className);
                    }
                    catch (ClassNotFoundException e)
                    {
                        return null;
                    }
                }
            });
        }

        if (clazz == null)
        {
            clazz = AccessController.doPrivileged(new PrivilegedAction<Class<?>>()
            {
                public Class<?> run()
                {
                    try
                    {
                        return callingClass.getClassLoader().loadClass(className);
                    }
                    catch (ClassNotFoundException e)
                    {
                        return null;
                    }
                }
            });
        }

        if (clazz == null)
        {
            throw new ClassNotFoundException(className);
        }

        if(type.isAssignableFrom(clazz))
        {
            return (T)clazz;
        }
        else
        {
            throw new IllegalArgumentException(String.format("Loaded class '%s' is not assignable from type '%s'", clazz.getName(), type.getName()));
        }
    }

    /**
     * Load a given resource. <p/> This method will try to load the resource using
     * the following methods (in order):
     * <ul>
     * <li>From
     * {@link Thread#getContextClassLoader() Thread.currentThread().getContextClassLoader()}
     * <li>From
     * {@link Class#getClassLoader() ClassUtils.class.getClassLoader()}
     * <li>From the {@link Class#getClassLoader() callingClass.getClassLoader() }
     * </ul>
     *
     * @param resourceName The name of the resource to load
     * @param callingClass The Class object of the calling object
     *
     * @return A URL pointing to the resource to load or null if the resource is not found
     */
    public static URL getResource(final String resourceName, final Class<?> callingClass)
    {
        URL url = AccessController.doPrivileged(new PrivilegedAction<URL>()
        {
            public URL run()
            {
                final ClassLoader cl = Thread.currentThread().getContextClassLoader();
                return cl != null ? cl.getResource(resourceName) : null;
            }
        });

        if (url == null)
        {
            url = AccessController.doPrivileged(new PrivilegedAction<URL>()
            {
                public URL run()
                {
                    return ClassUtils.class.getClassLoader().getResource(resourceName);
                }
            });
        }

        if (url == null)
        {
            url = AccessController.doPrivileged(new PrivilegedAction<URL>()
            {
                public URL run()
                {
                    return callingClass.getClassLoader().getResource(resourceName);
                }
            });
        }

        return url;
    }
}
