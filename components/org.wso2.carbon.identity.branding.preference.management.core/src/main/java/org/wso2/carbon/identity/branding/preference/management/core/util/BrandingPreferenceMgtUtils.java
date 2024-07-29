/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com).
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.branding.preference.management.core.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.carbon.identity.branding.preference.management.core.constant.BrandingPreferenceMgtConstants;
import org.wso2.carbon.identity.branding.preference.management.core.exception.BrandingPreferenceMgtClientException;
import org.wso2.carbon.identity.branding.preference.management.core.exception.BrandingPreferenceMgtServerException;
import org.wso2.carbon.identity.branding.preference.management.core.model.BrandingPreference;

import java.util.LinkedHashMap;

import static org.wso2.carbon.identity.branding.preference.management.core.constant.BrandingPreferenceMgtConstants.CONFIGS;
import static org.wso2.carbon.identity.branding.preference.management.core.constant.BrandingPreferenceMgtConstants.IS_BRANDING_ENABLED;
import static org.wso2.carbon.identity.branding.preference.management.core.constant.BrandingPreferenceMgtConstants.LOCAL_CODE_SEPARATOR;
import static org.wso2.carbon.identity.branding.preference.management.core.constant.BrandingPreferenceMgtConstants.RESOURCE_NAME_SEPARATOR;

/**
 * Util class for branding preference management.
 */
public class BrandingPreferenceMgtUtils {

    private static final Log log = LogFactory.getLog(BrandingPreferenceMgtUtils.class);

    /**
     * Check whether the given string is a valid JSON or not.
     *
     * @param stringJSON Input String.
     * @return True if the input string is a valid JSON.
     */
    public static boolean isValidJSONString(String stringJSON) {

        if (StringUtils.isBlank(stringJSON)) {
            return false;
        }
        try {
            JSONObject objectJSON = new JSONObject(stringJSON);
            if (objectJSON.length() == 0) {
                return false;
            }
        } catch (JSONException exception) {
            // If the preference string is not in the valid json format JSONException will be thrown.
            if (log.isDebugEnabled()) {
                log.debug("Invalid json string. Error occurred while validating preference string", exception);
            }
            return false;
        }
        return true;
    }

    /**
     * This method can be used to generate a BrandingPreferenceMgtClientException from
     * BrandingPreferenceMgtConstants.ErrorMessages object when no exception is thrown.
     *
     * @param error BrandingPreferenceMgtConstants.ErrorMessages.
     * @param data  data to replace if message needs to be replaced.
     * @return BrandingPreferenceMgtClientException.
     */
    public static BrandingPreferenceMgtClientException handleClientException(
            BrandingPreferenceMgtConstants.ErrorMessages error, String... data) {

        String message = populateMessageWithData(error, data);
        return new BrandingPreferenceMgtClientException(message, error.getCode());
    }

    public static BrandingPreferenceMgtClientException handleClientException(
            BrandingPreferenceMgtConstants.ErrorMessages error, String data, Throwable e) {

        String message = populateMessageWithData(error, data);
        return new BrandingPreferenceMgtClientException(message, error.getCode(), e);
    }

    /**
     * This method can be used to generate a BrandingPreferenceMgtServerException from
     * BrandingPreferenceMgtConstants.ErrorMessages object when no exception is thrown.
     *
     * @param error SecretConstants.ErrorMessages.
     * @param data  data to replace if message needs to be replaced.
     * @return BrandingPreferenceMgtClientException.
     */
    public static BrandingPreferenceMgtServerException handleServerException(
            BrandingPreferenceMgtConstants.ErrorMessages error, String... data) {

        String message = populateMessageWithData(error, data);
        return new BrandingPreferenceMgtServerException(message, error.getCode());
    }

    public static BrandingPreferenceMgtServerException handleServerException(
            BrandingPreferenceMgtConstants.ErrorMessages error, String data, Throwable e) {

        String message = populateMessageWithData(error, data);
        return new BrandingPreferenceMgtServerException(message, error.getCode(), e);
    }

    public static BrandingPreferenceMgtServerException handleServerException(
            BrandingPreferenceMgtConstants.ErrorMessages error, Throwable e) {

        String message = populateMessageWithData(error);
        return new BrandingPreferenceMgtServerException(message, error.getCode(), e);
    }

    /**
     * Replace '_' with '-' in the locale code for support both the locale code formats like en-US & en_US.
     *
     * @param locale Locale code.
     * @return Formatted locale code.
     */
    public static String getFormattedLocale(String locale) {

        String formattedLocale = locale;
        if (StringUtils.isNotBlank(locale)) {
            formattedLocale = locale.replace(RESOURCE_NAME_SEPARATOR, LOCAL_CODE_SEPARATOR);
        }
        return formattedLocale;
    }

    /**
     * Check whether the given branding preference is published or not.
     *
     * @param brandingPreference Branding preference that needs to be checked.
     * @return True if the branding preference is published.
     */
    public static boolean isBrandingPublished(BrandingPreference brandingPreference) {

        JSONObject preferences = new JSONObject((LinkedHashMap) brandingPreference.getPreference());

        // If configs.isBrandingEnabled is not found in preferences, it is assumed that branding is enabled by default.
        return !preferences.has(CONFIGS) ||
                preferences.getJSONObject(CONFIGS).optBoolean(IS_BRANDING_ENABLED, true);
    }

    private static String populateMessageWithData(BrandingPreferenceMgtConstants.ErrorMessages error, String... data) {

        String message;
        if (data != null && data.length != 0) {
            message = String.format(error.getMessage(), data);
        } else {
            message = error.getMessage();
        }
        return message;
    }

    private static String populateMessageWithData(BrandingPreferenceMgtConstants.ErrorMessages error) {

        return error.getMessage();
    }
}
