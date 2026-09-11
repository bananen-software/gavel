import {ApplicationConfig, provideZonelessChangeDetection} from '@angular/core';
import {provideHttpClient, withInterceptors} from '@angular/common/http';
import {provideRouter, withComponentInputBinding, withInMemoryScrolling} from '@angular/router';
import {en_US, provideNzI18n} from 'ng-zorro-antd/i18n';
import {routes} from './app.routes';
import {
    createInterceptorCondition,
    INCLUDE_BEARER_TOKEN_INTERCEPTOR_CONFIG,
    IncludeBearerTokenCondition,
    includeBearerTokenInterceptor,
    provideKeycloak
} from "keycloak-angular";

const urlCondition = createInterceptorCondition<IncludeBearerTokenCondition>({
    urlPattern: /^(http:\/\/localhost:8080)(\/.*)?$/i,
});

export const appConfig: ApplicationConfig = {
    providers: [
        provideKeycloak({
            config: {
                url: 'http://localhost:8888',
                realm: 'gavel',
                clientId: 'gavel',
            },
            initOptions: {
                onLoad: 'login-required', // or 'login-required'
                silentCheckSsoRedirectUri: window.location.origin + '/silent-check-sso.html',
                checkLoginIframe: false
            },
        }),
        provideZonelessChangeDetection(),
        provideHttpClient(withInterceptors([includeBearerTokenInterceptor])),
        {
            provide: INCLUDE_BEARER_TOKEN_INTERCEPTOR_CONFIG,
            useValue: [urlCondition],
        },
        provideRouter(
            routes,
            // Route params arrive as component inputs, so nothing injects ActivatedRoute.
            withComponentInputBinding(),
            withInMemoryScrolling({scrollPositionRestoration: 'top'}),
        ),
        provideNzI18n(en_US)
    ],
};
