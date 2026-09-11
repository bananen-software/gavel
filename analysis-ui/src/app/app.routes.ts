import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    loadComponent: () =>
      import('./views/workspace-list.component').then((m) => m.WorkspaceListComponent),
  },
  {
    path: 'projects/:projectId',
    loadComponent: () =>
      import('./views/project-shell.component').then((m) => m.ProjectShellComponent),
    children: [
      {
        path: '',
        loadComponent: () =>
          import('./views/project-overview.component').then((m) => m.ProjectOverviewComponent),
      },
      {
        path: 'packages/:packageId',
        loadComponent: () =>
          import('./views/package-detail.component').then((m) => m.PackageDetailComponent),
      },
      {
        path: 'packages/:packageId/classes/:classId',
        loadComponent: () =>
          import('./views/class-detail.component').then((m) => m.ClassDetailComponent),
      },
    ],
  },
  { path: '**', redirectTo: '' },
];
