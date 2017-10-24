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
package org.createnet.raptor.models.app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.createnet.raptor.models.auth.Role;

/**
 *
 * @author Luca Capra <luca.capra@gmail.com>
 */
public class AppUser {

    protected String id;
    protected List<AppRole> roles = new ArrayList();

    public AppUser() {
    }
    
    public AppUser(String userId) {
        this.id = userId;
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<AppRole> getRoles() {
        return roles;
    }

    public void setRoles(List<AppRole> roles) {
        this.roles = roles;
    }

    public void addRoles(List<AppRole> roles) {
        roles.forEach((role) -> {
            if (!getRoles().contains(role)) {
                getRoles().add(role);
            }
        });
    }

    public void removeRole(AppRole role) {
        if (getRoles().contains(role)) {
            getRoles().remove(role);
        }
    }

    public void addRole(AppRole role) {
        addRoles(Arrays.asList(role));
    }
    
    public boolean hasRole(Role.Roles role) {
        return getRoles().contains(new AppRole(role.name()));
    }

}
