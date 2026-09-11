import {AuthGuardData, createAuthGuard} from 'keycloak-angular';
import {ActivatedRouteSnapshot, CanActivateFn} from "@angular/router";

const isAccessAllowed = async (route: ActivatedRouteSnapshot, state: any, authData: AuthGuardData) => {
    const {authenticated, grantedRoles} = authData;
    if (!authenticated) return false;
    const requiredRole = route.data?.['role'];
    return requiredRole ? grantedRoles.resourceRoles['gavel']?.includes(requiredRole) : authenticated;
};

export const canActivateAuthRole = createAuthGuard<CanActivateFn>(isAccessAllowed);