package com.photoshare.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.stripPrefix;
import static org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions.lb;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;

@Configuration
public class GatewayRouteConfig {

    @Bean
    public RouterFunction<ServerResponse> gatewayRoutes() {

        return route("auth-service-route")
                .route(path("/api/v1/auth/**"), http())
                .filter(lb("auth-service"))
                .build()

                .and(route("user-service-route")
                        .route(path("/api/v1/users/**"), http())
                        .filter(lb("user-service"))
                        .build())

                .and(route("media-service-route")
                        .route(path("/api/v1/media/**"), http())
                        .filter(lb("media-service"))
                        .build())

                .and(route("post-service-route")
                        .route(path("/api/v1/posts/**"), http())
                        .filter(lb("post-service"))
                        .build())

                .and(route("social-service-route")
                        .route(path("/api/v1/social/**"), http())
                        .filter(lb("social-service"))
                        .build())

                // Swagger / OpenAPI documentation routes

                .and(route("auth-docs-route")
                        .route(path("/auth/v3/api-docs"), http())
                        .before(stripPrefix(1))
                        .filter(lb("auth-service"))
                        .build())

                .and(route("user-docs-route")
                        .route(path("/user/v3/api-docs"), http())
                        .before(stripPrefix(1))
                        .filter(lb("user-service"))
                        .build())

                .and(route("media-docs-route")
                        .route(path("/media/v3/api-docs"), http())
                        .before(stripPrefix(1))
                        .filter(lb("media-service"))
                        .build())

                .and(route("post-docs-route")
                        .route(path("/post/v3/api-docs"), http())
                        .before(stripPrefix(1))
                        .filter(lb("post-service"))
                        .build())

                .and(route("social-docs-route")
                        .route(path("/social/v3/api-docs"), http())
                        .before(stripPrefix(1))
                        .filter(lb("social-service"))
                        .build());
    }
}
