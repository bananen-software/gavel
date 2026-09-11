import { inject, Injectable, isDevMode } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { catchError, Observable, tap, throwError } from 'rxjs';

export interface CreateWorkspaceRequest {
  name: string;
  path: string;
  excludedPaths: string[];
  basePackage: string;
}

export interface WorkspaceCreatedResponse {
  id: string;
}

/**
 * The command side of the backend.
 *
 * GraphQL answers questions here; the REST controllers perform actions. Keeping
 * them in separate clients keeps that split visible — anything in this file
 * changes server state, everything in GraphQlClient only reads.
 *
 * Paths are relative for the same reason the GraphQL endpoint is: one build
 * works in dev through the proxy and in production from the Spring Boot jar.
 * Note that in dev `proxy.conf.mjs` has to distinguish these calls from the
 * Angular routes that share their prefix — see the comment in that file.
 */
@Injectable({ providedIn: 'root' })
export class RestClient {
  private readonly http = inject(HttpClient);

  createWorkspace(request: CreateWorkspaceRequest): Observable<WorkspaceCreatedResponse> {
    return this.post<WorkspaceCreatedResponse>('/workspaces', request);
  }

  /** Scans the workspace directory for projects. Returns no body. */
  locateProjects(workspaceId: string): Observable<void> {
    return this.post<void>(`/workspaces/${encodeURIComponent(workspaceId)}`, {});
  }

  /** Queues an analysis run. The project's analysisStatus moves to PENDING. */
  scheduleAnalysis(projectId: string): Observable<void> {
    return this.post<void>(`/projects/${encodeURIComponent(projectId)}`, {});
  }

  private post<T>(path: string, body: unknown): Observable<T> {
    if (isDevMode()) {
      console.info(`[rest] → POST ${path}`, body);
    }
    return this.http.post<T>(path, body).pipe(
      tap(() => {
        if (isDevMode()) console.info(`[rest] ← POST ${path}`);
      }),
      catchError((error: HttpErrorResponse) => throwError(() => new Error(describe(error)))),
    );
  }
}

/**
 * Spring's ResponseStatusException carries its reason in `message`, but only
 * when `server.error.include-message=always` is set — the default since Boot 2.3
 * is to strip it. So fall back through everything the error body might contain
 * rather than showing the user an empty string.
 */
function describe(error: HttpErrorResponse): string {
  if (error.status === 0) {
    return 'Could not reach the backend. Is it running on port 8080?';
  }

  const body: unknown = error.error;
  if (typeof body === 'string' && body.trim()) return body;

  if (body && typeof body === 'object') {
    const record = body as Record<string, unknown>;
    for (const key of ['message', 'detail', 'error'] as const) {
      const value = record[key];
      if (typeof value === 'string' && value.trim()) return value;
    }
  }

  return `The request failed with status ${error.status}.`;
}
