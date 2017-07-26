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
package org.createnet.raptor.profile;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.createnet.raptor.models.auth.User;
import org.createnet.raptor.models.objects.Device;
import org.createnet.raptor.models.profile.UserProfile;
import org.createnet.raptor.models.response.JsonErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Luca Capra <lcapra@fbk.eu>
 */
@RestController
@ApiResponses(value = {
    @ApiResponse(
            code = 200,
            message = "Ok"
    )
    ,
    @ApiResponse(
            code = 401,
            message = "Not authorized"
    )
    ,
    @ApiResponse(
            code = 403,
            message = "Forbidden"
    )
    ,
    @ApiResponse(
            code = 500,
            message = "Internal error"
    )
})
@Api(tags = {"UserProfile"})
public class UserProfileController {

    @Autowired
    private UserProfileService profileService;

    @RequestMapping(method = RequestMethod.GET, value = "/{userId}")
    @ApiOperation(
            value = "Return all the user preferences",
            notes = "",
            response = UserProfile.class,
            nickname = "getPreferences"
    )
    @PreAuthorize("hasAnyRole('super_admin', 'admin') or #userId == principal.uuid")
    public ResponseEntity<?> getPreferences(
            @AuthenticationPrincipal User currentUser,
            @PathVariable("userId") String userId
    ) {  
        
        if(userId == null || userId.isEmpty()) {
            return JsonErrorResponse.badRequest();
        }        
        
        List<UserProfile> prefs = profileService.list(userId);
        return ResponseEntity.ok(prefs.stream().map(p -> toJSON(p.getValue())).collect(Collectors.toList()));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{userId}/{name}")
    @ApiOperation(
            value = "Return a user preference by name",
            notes = "",
            response = UserProfile.class,
            nickname = "getPreference"
    )
    @PreAuthorize("hasAnyRole('super_admin', 'admin') or #userId == principal.uuid")
    public ResponseEntity<?> getPreference(
            @AuthenticationPrincipal User currentUser,
            @PathVariable("userId") String userId,
            @PathVariable("name") String name
    ) {
        
        if((userId == null || name == null) || (userId.isEmpty() || name.isEmpty())) {
            return JsonErrorResponse.badRequest();
        }        
        
        UserProfile pref = profileService.get(userId, name);
        if (pref == null) {
            return JsonErrorResponse.entity(HttpStatus.NOT_FOUND, "Not found");
        }
        return ResponseEntity.ok(toJSON(pref.getValue()));
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{userId}/{name}")
    @ApiOperation(
            value = "Set an user preference by name",
            notes = "",
            response = UserProfile.class,
            nickname = "setPreference"
    )
    @PreAuthorize("hasAnyRole('super_admin', 'admin') or #userId == principal.uuid")
    public ResponseEntity<?> setPreference(
            @AuthenticationPrincipal User currentUser,
            @PathVariable("userId") String userId,
            @PathVariable("name") String name,
            @RequestBody JsonNode body
    ) {
        
        if((userId == null || name == null) || (userId.isEmpty() || name.isEmpty())) {
            return JsonErrorResponse.badRequest();
        }
        
        UserProfile pref = new UserProfile(userId, name, body.toString());
        profileService.save(pref);
        return ResponseEntity.ok(toJSON(pref.getValue()));
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{userId}/{name}")
    @ApiOperation(
            value = "Drop an user preference by name",
            notes = "",
            response = UserProfile.class,
            nickname = "deletePreference"
    )
    @PreAuthorize("hasAnyRole('super_admin', 'admin') or #userId == principal.uuid")
    public ResponseEntity<?> deletePreference(
            @AuthenticationPrincipal User currentUser,
            @PathVariable("userId") String userId,
            @PathVariable("name") String name
    ) {

        if((userId == null || name == null) || (userId.isEmpty() || name.isEmpty())) {
            return JsonErrorResponse.badRequest();
        }        
        
        UserProfile pref = profileService.get(userId, name);
        if (pref == null) {
            return JsonErrorResponse.entity(HttpStatus.NOT_FOUND, "Not found");
        }

        profileService.delete(pref);
        return ResponseEntity.accepted().build();
    }

    private JsonNode toJSON(String value) {
            try {
                return Device.getMapper().readTree(value);
            } catch (IOException ex) {
                
            }
            return null;
    }

}
