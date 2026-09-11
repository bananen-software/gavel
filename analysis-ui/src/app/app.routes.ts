import {Routes} from '@angular/router';
import {canActivateAuthRole} from "./guards/keycloakGuard";

export const routes: Routes = [
    {
        path: '',
        pathMatch: 'full',
        canActivate: [canActivateAuthRole],
        loadComponent: () =>
            import('./views/workspace-list.component').then((m) => m.WorkspaceListComponent),
    },
    {
        path: 'projects/:projectId',
        canActivate: [canActivateAuthRole],
        loadComponent: () =>
            import('./views/project-shell.component').then((m) => m.ProjectShellComponent),
        children: [
            {
                path: '',
                canActivate: [canActivateAuthRole],
                loadComponent: () =>
                    import('./views/project-overview.component').then((m) => m.ProjectOverviewComponent),
            },
            {
                path: 'packages/:packageId',
                canActivate: [canActivateAuthRole],
                loadComponent: () =>
                    import('./views/package-detail.component').then((m) => m.PackageDetailComponent),
            },
            {
                path: 'packages/:packageId/classes/:classId',
                canActivate: [canActivateAuthRole],
                loadComponent: () =>
                    import('./views/class-detail.component').then((m) => m.ClassDetailComponent),
            },
        ],
    },
    {path: '**', redirectTo: ''},
];
