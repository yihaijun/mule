/**
 * 
 */
package com.alibaba.dubbo.bpm.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author yihaijun
 *
 */
public class SystemUtils extends org.apache.commons.lang.SystemUtils{
    // class logger
    protected static final Log logger = LogFactory.getLog(SystemUtils.class);

    // bash prepends: declare -x
    // zsh prepends: typeset -x
    private static final String[] UNIX_ENV_PREFIXES = new String[]{"declare -", "typeset -"};

    // the environment of the VM process
    private static Map environment = null;

    public static String getenv(String name)
    {
        return (String) SystemUtils.getenv().get(name);
    }


    /**
     * Get the operating system environment variables. This should work for Windows
     * and Linux.
     *
     * @return Map<String, String> or an empty map if there was an error.
     */
    public static synchronized Map getenv()
    {
        if (environment == null)
        {
            try
            {
                if (SystemUtils.IS_JAVA_1_4)
                {
                    // fallback to external process
                    environment = Collections.unmodifiableMap(getenvJDK14());
                }
                else
                {
                    // the following runaround is necessary since we still want to
                    // compile on JDK 1.4
                    Class target = System.class;
                    Method envMethod = target.getMethod("getenv", ArrayUtils.EMPTY_CLASS_ARRAY);
                    environment = Collections.unmodifiableMap((Map) envMethod.invoke(target, (Object[]) null));
                }
            }
            catch (Exception ex)
            {
                logger.error("Could not access OS environment: ", ex);
                environment = Collections.EMPTY_MAP;
            }
        }

        return environment;
    }

    private static Map getenvJDK14() throws Exception
    {
        Map env = new HashMap();
        Process process = null;

        try
        {
            boolean isUnix = true;
            String command;

            if (SystemUtils.IS_OS_WINDOWS)
            {
                command = "cmd /c set";
                isUnix = false;
            }
            else
            {
                command = "env";
            }

            process = Runtime.getRuntime().exec(command);
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = br.readLine()) != null)
            {
                for (int prefix = 0; prefix < UNIX_ENV_PREFIXES.length; prefix++)
                {
                    if (line.startsWith(UNIX_ENV_PREFIXES[prefix]))
                    {
                        line = line.substring(UNIX_ENV_PREFIXES[prefix].length());
                    }
                }

                int index = -1;
                if ((index = line.indexOf('=')) > -1)
                {
                    String key = line.substring(0, index).trim();
                    String value = line.substring(index + 1).trim();
                    // remove quotes, if any
                    if (isUnix && value.length() > 1 && (value.startsWith("\"") || value.startsWith("'")))
                    {
                        value = value.substring(1, value.length() - 1);
                    }
                    env.put(key, value);
                }
                else
                {
                    env.put(line, StringUtils.EMPTY);
                }
            }
        }
        catch (Exception e)
        {
            throw e; // bubble up
        }
        finally
        {
            if (process != null)
            {
                process.destroy();
            }
        }

        return env;
    }
}
