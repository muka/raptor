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
package org.createnet.raptor.auth.services;

import java.util.Arrays;
import java.util.List;
import org.createnet.raptor.auth.acl.AbstractAclService;
import org.createnet.raptor.models.acl.permission.RaptorPermission;
import org.createnet.raptor.models.auth.AclDevice;
import org.createnet.raptor.models.auth.AclTreeNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Service;

/**
 * @author Luca Capra <lcapra@fbk.eu>
 */
@Service
public class AclTreeService extends AbstractAclService<AclTreeNode> {

    private final Logger logger = LoggerFactory.getLogger(AclTreeService.class);

    @Autowired
    AuthTreeService treeService;

    @Override
    public List<Permission> getDefaultPermissions() {
        return Arrays.asList(
                RaptorPermission.READ,
                RaptorPermission.WRITE
        );
    }

    @Override
    public AclTreeNode load(Long id) {
        return treeService.get(id);
    }

    @Override
    @Retryable(maxAttempts = 3, value = AclManagerService.AclManagerException.class, backoff = @Backoff(delay = 500, multiplier = 3))
    public void register(AclTreeNode subj) {
        
        List<Permission> permissions = getPermissions(subj);
        
        if (subj.getSid().getUser().getUuid().equals(subj.getOwner().getUuid())) {
            permissions.add(RaptorPermission.ADMINISTRATION);
        }

        savePermissions(subj, permissions);
    }
    
}