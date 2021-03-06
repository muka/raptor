package org.createnet.raptor.common.authentication;

import org.createnet.raptor.common.configuration.TokenHelper;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import org.createnet.raptor.models.configuration.RaptorConfiguration;
import org.createnet.raptor.sdk.Raptor;
import org.createnet.raptor.sdk.api.AuthClient;
import org.createnet.raptor.sdk.exception.AuthenticationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.GenericFilterBean;

public class RestTokenFilter extends GenericFilterBean {

    protected final static Logger logger = LoggerFactory.getLogger(RestTokenFilter.class);

    public RaptorConfiguration config;
    public TokenHelper tokenHelper;

    public RestTokenFilter(RaptorConfiguration config, TokenHelper tokenHelper) {
        this.config = config;
        this.tokenHelper = tokenHelper;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String authToken = httpRequest.getHeader(config.getAuth().getHeader());
        
        MDC.put("host", request.getRemoteHost());
        
        if (authToken != null && !authToken.isEmpty()) {
            try {

                logger.debug("Attempting token authentication..");

                Raptor client = new Raptor(config.getUrl(), tokenHelper.extractToken(authToken));
                AuthClient.LoginState state = client.Auth().login();

                logger.debug("login ok, authenticated user `{}`", state.user.getUsername());

                UserDetails userDetails = new RaptorUserDetails(state.user);

                LoginAuthenticationToken authentication = new LoginAuthenticationToken(userDetails, authToken, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));
                
                MDC.put("user", userDetails.getUsername());
                
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (AuthenticationFailedException ex) {
                logger.debug("Invalid login token provided: {}", ex.getMessage());
            } catch (Exception ex) {
                logger.warn("Login operation failure: {}", ex.getMessage());
            }
        }

        chain.doFilter(request, response);

    }

}
