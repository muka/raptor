/*
 * Copyright 2017 FBK/CREATE-NET
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.createnet.raptor.sdk;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import org.createnet.raptor.models.auth.Role;
import org.createnet.raptor.models.auth.User;
import org.createnet.raptor.sdk.Raptor;
import org.createnet.raptor.models.objects.Device;
import org.createnet.raptor.sdk.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Luca Capra <lcapra@fbk.eu>
 */
public class Utils {

    static final Logger log = LoggerFactory.getLogger(Utils.class);
    static final String settingsFile = "settings.properties";
    static Raptor instance;

    static public Properties loadSettings() {

        ClassLoader classLoader = Utils.class.getClassLoader();
        File file = new File(classLoader.getResource(settingsFile).getFile());
        
        InputStream input;
        try {
            input = new FileInputStream(file);
            Properties prop = new Properties();
            prop.load(input);
            return prop;
            
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    static public Raptor getRaptor() {
        Properties prop = Utils.loadSettings();
        if (instance == null) {
            instance = new Raptor(prop.getProperty("url"), prop.getProperty("username"), prop.getProperty("password"));
            log.debug("Performing login");
            instance.Auth.login();
            log.debug("Logged in");
        }
        return instance;
    }
    
    static public Device createDevice(String name) throws IOException {
        Device d = new Device();
        d.name = name;
        return getRaptor().Device.create(d);
    }
    
    static public void waitFor(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    static public Device createDevice(Device d) {
        Device d1 = getRaptor().Device.create(d);
        waitFor(500);
        return d1;
    }
    
    /**
     * Create a new user and initialize a raptor instance
     * @param username 
     * @param roles 
     * @return  
     */
    static public Raptor createNewInstance(String username, Set<Role> roles) {
        
        User user = getRaptor().Admin.User.create(username, username + Math.random(), username + "@test.raptor.local", roles);
        assert user != null;
        
        Raptor r = new Raptor(new Config(instance.getConfig().getUrl(), username, username));
        r.Auth.login();

        return r;
    }
    
    /**
     * Create a new admin user and initialize a raptor instance
     * @param username 
     * @return  
     */
    static public Raptor createNewInstance(String username) {
        
        String password = username + Math.random();
        User user = getRaptor().Admin.User.create(username, password, username + "@test.raptor.local");
        log.debug("Created user {} : {} with uuid {}", username, password, user.getUuid());
        assert user != null;
        
        Raptor r = new Raptor(new Config(instance.getConfig().getUrl(), username, password));
        r.Auth.login();

        return r;
    }
    
    /**
     * Create a new admin user and initialize a raptor instance
     * @return  
     */
    static public Raptor createNewInstance() {
        
        String username = rndUsername();
        String password = username + Math.random();
        User user = getRaptor().Admin.User.create(username, password, username + "@test.raptor.local");
        log.debug("Created user {} : {} with uuid {}", username, password, user.getUuid());
        assert user != null;
        
        Raptor r = new Raptor(new Config(instance.getConfig().getUrl(), username, password));
        r.Auth.login();

        return r;
    }

    public static String rndUsername() {
        int rnd = ((int) (Math.random() * 100000000)) + (int) System.currentTimeMillis();
        return (rnd % 2 == 0 ? "test_fil_" : "user_ippo_") + rnd;
    }
    
}