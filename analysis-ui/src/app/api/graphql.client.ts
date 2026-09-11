import {inject, Injectable, isDevMode} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {map, Observable, tap} from 'rxjs';

interface GraphQlResponse<T> {
    data: T | null;
    errors?: { message: string }[];
}

/**
 * A GraphQL client in a few dozen lines.
 *
 * There is no normalised cache here on purpose: the app issues one snapshot
 * query and a handful of lazy detail queries, so a cache would add a second
 * state model without saving a single request. Caching lives in the store,
 * keyed by id, where it is easy to reason about.
 *
 * The endpoint is relative so one build works in dev (via proxy.conf.json),
 * from the Spring Boot jar, and behind any reverse proxy.
 */
@Injectable({providedIn: 'root'})
export class GraphQlClient {
    private readonly http = inject(HttpClient);
    private readonly endpoint = '/graphql';

    /**
     * Dev-mode instrumentation. A request that never settles looks identical in
     * the network tab whether it is one slow query or the two-hundredth copy of a
     * fast one, so every operation is counted and timed. Watch the console:
     * a rising `#` on the same operation means something is re-firing; a single
     * request with a long duration means the backend is the slow part.
     */
    private readonly callCounts = new Map<string, number>();

    query<T>(document: string, variables: Record<string, unknown> = {}): Observable<T> {
        const operation = operationName(document);
        const startedAt = performance.now();

        if (isDevMode()) {
            const count = (this.callCounts.get(operation) ?? 0) + 1;
            this.callCounts.set(operation, count);
            console.info(
                `[graphql] → ${operation} #${count}`,
                variables,
                count > 5 ? '⚠️ repeated — check for an effect re-firing' : '',
            );
        }

        return this.http
            .post<GraphQlResponse<T>>(this.endpoint, {query: document, variables})
            .pipe(
                tap({
                    next: () => {
                        if (isDevMode()) {
                            const ms = Math.round(performance.now() - startedAt);
                            console.info(`[graphql] ← ${operation} in ${ms}ms`);
                        }
                    },
                    error: (err: unknown) => {
                        if (isDevMode()) {
                            const ms = Math.round(performance.now() - startedAt);
                            console.info(`[graphql] ✕ ${operation} failed after ${ms}ms`, err);
                        }
                    },
                }),
                map((response) => {
                    if (response.errors?.length) {
                        throw new Error(response.errors.map((e) => e.message).join('; '));
                    }
                    if (response.data == null) {
                        throw new Error('The server returned no data.');
                    }
                    return response.data;
                }),
            );
    }
}

const operationName = (document: string): string =>
    /query\s+(\w+)/.exec(document)?.[1] ?? 'anonymous';