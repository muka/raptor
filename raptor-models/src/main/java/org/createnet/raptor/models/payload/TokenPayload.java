/*
 * Copyright 2017 Luca Capra <luca.capra@fbk.eu>.
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
package org.createnet.raptor.models.payload;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.createnet.raptor.models.acl.permission.Permissions;
import org.createnet.raptor.models.auth.Token;
import org.createnet.raptor.models.objects.Device;
import org.createnet.raptor.models.objects.RaptorComponent;

/**
 *
 * @author Luca Capra <luca.capra@fbk.eu>
 */
public class TokenPayload extends AbstractPayload {

    public Token token;

    public TokenPayload() {
    }

    public TokenPayload(Token obj, Permissions op) {
        token = obj;
        type = MessageType.token;
        this.op = op;
    }

    @Override
    public String toString() {
        try {
            return Device.getMapper().writeValueAsString(this);
        } catch (JsonProcessingException ex) {
            throw new RaptorComponent.ParserException(ex);
        }
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

}
