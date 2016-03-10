/**
 * Copyright 2016 IBM Corp.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ibm.mfp.sample.socialogin;

import com.ibm.mfp.security.checks.base.UserAuthenticationSecurityCheckConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Creates and initializes the supported vendors.<br/>
 * The configuration properties of the security check are delivered to the vendors according to
 * their {@link LoginVendor#getConfigurationPropertyNames()} values.
 *
 * @author artem on 3/3/16.
 */
public class SocialLoginConfiguration extends UserAuthenticationSecurityCheckConfig {

    public static final String KEEP_ORIGINAL_TOKEN = "keepOriginalToken";

    private boolean keepOriginalToken;
    private Map<String, LoginVendor> vendors;

    /**
     * Create the vendors each with its relevant properties
     *
     * @param properties security check configuration includes the properties of all vendors
     */
    public SocialLoginConfiguration(Properties properties) {
        super(properties);
        blockedStateExpirationSec = 1;
        keepOriginalToken = Boolean.parseBoolean(getStringProperty(KEEP_ORIGINAL_TOKEN, properties, "false"));

        createVendors();
        for (LoginVendor vendor : vendors.values()) {
            Properties vendorConfig = new Properties();
            for (String property : vendor.getConfigurationPropertyNames()) {
                String value = getStringProperty(property, properties, null);
                vendorConfig.setProperty(property, value);
            }
            vendor.setConfiguration(vendorConfig);
        }

    }

    /**
     * Get only the vendors that are enabled according to their configurations
     *
     * @return map with vendor name as a key and the vendor as a value
     */
    public Map<String, LoginVendor> getEnabledVendors() {
        Map<String, LoginVendor> res = new HashMap<>();
        for (Map.Entry<String, LoginVendor> entry : vendors.entrySet()) {
            if (entry.getValue().isEnabled())
                res.put(entry.getKey(), entry.getValue());
        }
        return res;
    }

    public boolean isKeepOriginalToken() {
        return keepOriginalToken;
    }

    private void createVendors() {
        vendors = new HashMap<>();
        vendors.put("google", new GoogleSupport());
        vendors.put("facebook", new FacebookSupport());
    }
}
