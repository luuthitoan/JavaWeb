package com.example.shopapp.filters;

import com.example.shopapp.component.JwtTokenUtil;
import com.example.shopapp.model.Role;
import com.example.shopapp.model.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
    @Value("${api.prefix}")
    private String apiPrefix;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsService userDetailsService;
    @Override
    protected void doFilterInternal(@NonNull  HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        try {
            if (isByPassToken(request)) {
                filterChain.doFilter(request, response); //enable bypass
            } else {
                final String authHeader = request.getHeader("Authorization");
                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                    return;
                }
                final String token = authHeader.substring(7); //get token
                final String userIdentifier = jwtTokenUtil.extractUserIdentifier(token);
                if (userIdentifier != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    User userDetails = (User) userDetailsService.loadUserByUsername(userIdentifier);
                    if (jwtTokenUtil.validateToken(token, userDetails)) {
                        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                        //set user
                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                }
                filterChain.doFilter(request, response);
            }
        }
        catch (Exception e)
        {

        }
    }
    private boolean isByPassToken(@NonNull HttpServletRequest request)
    {
        final List<Pair<String,String>> byPassTokens = Arrays.asList(
                Pair.of(String.format("%s/roles",apiPrefix),"GET"),
                Pair.of(String.format("%s/products",apiPrefix),"GET"),
                Pair.of(String.format("%s/categories",apiPrefix),"GET"),
                Pair.of(String.format("%s/auth/register",apiPrefix),"POST"),
                Pair.of(String.format("%s/auth/login",apiPrefix),"POST"),
                Pair.of(String.format("%s/auth/outbound/authentication", apiPrefix),"POST"),
                Pair.of(String.format("%s/vnpay/vnpay-payment", apiPrefix),"GET")
        );
        String requestPath = request.getServletPath();
        String requestMethod = request.getMethod();
        if (requestPath.equals(String.format("%s/orders", apiPrefix))
                && requestMethod.equals("GET")) {
            return true;
        }
        for(Pair<String,String> byPassToken:byPassTokens)
        {
            if(request.getServletPath().contains(byPassToken.getFirst())&&
            request.getMethod().equals(byPassToken.getSecond()))
            {
                return true;
            }
        }
        return false;
    }

}
