package com.freddiemac.lcax.common.config;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import com.freddiemac.lcax.common.logger.LogFactory;
import com.freddiemac.lcax.common.logger.Logger;

/**
 * The <b>MessageResourceBundle</b> is a Framework class, used to get cached
 * messages for the given key from the Resource Bundle. *
 */
public class MessageResourceBundle {

    /**
     * Variable to Hold a logger instance.
     */
    private static final Logger LOGGER = LogFactory.getLogger(MessageResourceBundle.class);
    /**
     * Variable to Hold a collection of bundles.
     */
    private static Map<String, ResourceBundle> bundleMap = new HashMap<String, ResourceBundle>();

    private static final String GENERAL = "general";
    private static final String MESSAGES = "Messages";

    /**
     * Default Constructor.
     */
    private MessageResourceBundle() {
        // Default Constructor.
    }

    /**
     * Returns the message from the Resource Bundle for a given key.
     *
     * @param key
     *            Name of the key.
     * @return String
     */
    public static String getMessage(String key) {
        ResourceBundle bundle = bundleMap.get(GENERAL);
        if (bundle == null) {
            LOGGER.trace("Resource Bundle is loading from properties now...");
            bundle = ResourceBundle.getBundle(MESSAGES);
            bundleMap.put(GENERAL, bundle);
        } else {
            LOGGER.trace("Resource Bundle loaded from Cache");
        }
        return bundle.getString(key);
    }

    /**
     * Returns the message from the Resource Bundle for a given key and
     * arguments.
     *
     * @param key
     *            Name of the key.
     * @param arguments
     *            Object[].
     * @return String
     */
    public static String getMessage(String key, Object... arguments) {
        ResourceBundle bundle = bundleMap.get(GENERAL);
        if (bundle == null) {
            LOGGER.info("Resource Bundle is loading from properties now...");
            bundle = ResourceBundle.getBundle(MESSAGES);
            bundleMap.put(GENERAL, bundle);
        } else {
            LOGGER.info("Resource Bundle loaded from Cache");
        }
        String value = bundle.getString(key);
        String result = "";
        if (arguments != null) {
            result = MessageFormat.format(value, arguments);
        } else {
            result = value;
        }
        return result;
    }

    /**
     * Returns the message from the Locale specified Resource Bundle for a given
     * key.
     *
     * @param key
     *            Name of the key.
     * @param locale
     *            Locale.
     * @return String
     */
    public static String getMessage(String key, Locale locale) {
        ResourceBundle bundle = bundleMap.get(locale.getLanguage() + "_" + locale.getCountry());
        if (bundle == null) {
            bundle = ResourceBundle.getBundle(MESSAGES, locale);
            bundleMap.put(locale.getLanguage() + "_" + locale.getCountry(), bundle);
        }
        return bundle.getString(key);
    }

    /**
     * Returns the message from the Locale specified Resource Bundle for a given
     * key and arguments.
     *
     * @param key
     *            Name of the key.
     * @param locale
     *            Locale.
     * @param arguments
     *            Object[].
     * @return String
     */
    public static String getMessage(String key, Locale locale, Object... arguments) {
        ResourceBundle bundle = bundleMap.get(locale.getLanguage() + "_" + locale.getCountry());
        if (bundle == null) {
            bundle = ResourceBundle.getBundle(MESSAGES, locale);
            bundleMap.put(locale.getLanguage() + "_" + locale.getCountry(), bundle);
        }
        String value = bundle.getString(key);
        String result = "";
        if (arguments != null) {
            result = MessageFormat.format(value, arguments);
        } else {
            result = value;
        }
        return result;
    }
}