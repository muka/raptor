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

package org.createnet.raptor.client.admin;

import java.util.UUID;
import org.createnet.raptor.client.Raptor;
import org.createnet.raptor.client.Utils;
import org.createnet.raptor.models.auth.User;
import org.createnet.raptor.models.exception.RequestException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Luca Capra <luca.capra@fbk.eu>
 */
public class UserAdminTest {
    
    final Logger log = LoggerFactory.getLogger(UserAdminTest.class);
    
    public static Raptor raptor;

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }


    @Before
    public void setUp() {
        raptor = Utils.getRaptor();
    }

    @After
    public void tearDown() {
    }
   
    private String rndUsername() {
        int rnd = ((int)(Math.random() * 100000000)) + (int)System.currentTimeMillis();
        return (rnd % 2 == 0 ? "test_fil_" : "user_ippo_") + rnd; 
    }
    
    @Test
    public void createUser()  {
        
        String username = rndUsername();
        log.debug("Create user {}", username);
        User user = raptor.Admin.User.create(username, "pass_" + rndUsername(), "test@test.raptor.local");
        
        assertEquals(username, user.getUsername());
        assertNotNull(user.getUuid());
    }
    
    @Test
    public void createAnotherUser()  {
        
        String username = rndUsername();
        log.debug("Create user {}", username);
        
        User user = new User();

        user.setUsername(username);
        user.setPassword("secret_" + rndUsername());
        user.setEmail("foobar+" + username + "@test.raptor.local");
        user.setId(123456L);
        
        String uuid = UUID.randomUUID().toString();
        user.setUuid(uuid);
        
        User newUser = raptor.Admin.User.create(user);
        
        assertEquals(username, newUser.getUsername());
        
        assertNull(newUser.getId());
        
        assertNotNull(newUser.getUuid());
        assertNotEquals(uuid, newUser.getUuid());

    }

    @Test
    public void updateUser()  {
        
        String email = "test@test.raptor.local";
        String username = rndUsername();
        log.debug("Create user {}", username);
        User user = raptor.Admin.User.create(username, "pass_" + rndUsername(), email);
        
        user.setEmail("newemail@example.com");
        user.setEnabled(false);
        
        User updatedUser = raptor.Admin.User.update(user);
        
        assertNotEquals(email, updatedUser.getEmail());
        assertEquals(false, updatedUser.getEnabled());
        
    }
    
    
    @Test
    public void changeDuplicatedUsername()  {
        
        String username1 = rndUsername();
        log.debug("Create user1 {}", username1);
        User user1 = raptor.Admin.User.create(username1, "pass_" + rndUsername(), "test@test.raptor.local");
        
        String username2 = rndUsername();
        log.debug("Create user2 {}", username2);
        User user2 = raptor.Admin.User.create(username2, "pass_" + rndUsername(), "test@test.raptor.local");
        
        user1.setUsername(username2);
        
        try {
            User updatedUser1 = raptor.Admin.User.update(user1);
        }
        catch(RequestException ex) {
            assertEquals(400, ex.status);
            return;
        }

        throw new RuntimeException("Duplicated username allowed?");
    }
    
    
    @Test
    public void deleteUser()  {
        
        String username1 = rndUsername();
        log.debug("Create user1 {}", username1);
        User user1 = raptor.Admin.User.create(username1, "pass_" + rndUsername(), "test@test.raptor.local");
        
        raptor.Admin.User.delete(user1);
        
        try {
            raptor.Admin.User.get(user1.getUuid());
            throw new RuntimeException("User should not exists");
        } catch(RequestException ex) {
            assertEquals(404, ex.status);
        }
        
    }
    
    
}