import {computed, inject, Injectable, signal, untracked} from '@angular/core';
import {GraphQlClient} from '../api/graphql.client';
import {CLASS_DETAIL, CLASSES_BY_PACKAGE, PROJECT_SNAPSHOT, PROJECTS,} from '../api/queries';
import type {
  ClassDetail,
  ClassDetailQuery,
  ClassesByPackageQuery,
  ClassSummary,
  PackageSummary,
  ProjectRef,
  ProjectSnapshot,
  ProjectSnapshotQuery,
  ProjectsQuery,
} from '../api/schema.types';
import {num, ratio, sum} from '../shared/format';

interface Loadable<T> {
    value: T | null;
    loading: boolean;
    error: string | null;
}

const idle = <T>(): Loadable<T> => ({value: null, loading: false, error: null});

/**
 * All application state, in signals, in one service.
 *
 * The API has no filtering, sorting or pagination, so every ranked view is
 * derived here from a snapshot that was fetched once. That is the trade this
 * whole app is built on: one round trip, then instant interaction.
 */
@Injectable({providedIn: 'root'})
export class AnalysisStore {
    private readonly gql = inject(GraphQlClient);

    private readonly projectsState = signal<Loadable<ProjectRef[]>>(idle());
    private readonly snapshotState = signal<Loadable<ProjectSnapshot>>(idle());
    private readonly classesState = signal<Record<string, Loadable<ClassSummary[]>>>({});
    private readonly classDetailState = signal<Record<string, Loadable<ClassDetail>>>({});

    private debugCalls = 0;

    /**
     * In-flight bookkeeping deliberately kept in plain fields rather than signals.
     *
     * These are read by the loaders, and the loaders are called from effects. A
     * signal read inside an effect is a tracked read, so guarding on signal state
     * would make each loader subscribe to the very signal it is about to write —
     * an effect that retriggers itself forever, firing a request every pass.
     * Plain fields are invisible to the reactive graph, which is exactly right
     * for "have I already asked for this?".
     */
    private requestedProjects = false;
    private requestedProjectId: string | null = null;
    private readonly requestedPackageIds = new Set<string>();
    private readonly requestedClassIds = new Set<string>();

    readonly projects = computed(() => this.projectsState().value ?? []);
    readonly projectsLoading = computed(() => this.projectsState().loading);
    readonly projectsError = computed(() => this.projectsState().error);

    readonly project = computed(() => this.snapshotState().value);
    readonly snapshotLoading = computed(() => this.snapshotState().loading);
    readonly snapshotError = computed(() => this.snapshotState().error);

    readonly packages = computed<PackageSummary[]>(() => this.project()?.packages ?? []);

    packageById(id: string) {
        return computed(() => this.packages().find((p) => p.id === id) ?? null);
    }

    /** Project-level rollups. The API has no aggregate query, so they live here. */
    readonly totals = computed(() => {
        const packages = this.packages();
        const linesOfCode = sum(packages, (p) => p.linesOfCode);
        const linesOfComments = sum(packages, (p) => p.linesOfComments);
        return {
            packages: packages.length,
            types: sum(packages, (p) => p.numberOfTypes),
            linesOfCode,
            linesOfComments,
            commentToCodeRatio: ratio(linesOfComments, linesOfCode),
            findings: sum(packages, (p) => p.totalNumberOfFindings),
            highPriorityFindings: sum(packages, (p) => p.numberOfHighPriorityFindings),
        };
    });

    readonly complexityMix = computed(() => {
        const packages = this.packages();
        return {
            low: sum(packages, (p) => p.numberOfLowComplexityTypes),
            medium: sum(packages, (p) => p.numberOfMediumComplexityTypes),
            high: sum(packages, (p) => p.numberOfHighComplexityTypes),
            veryHigh: sum(packages, (p) => p.numberOfVeryHighComplexityTypes),
        };
    });

    /** Packages sorted by complexity, descending. Drives the navigation rail. */
    readonly packagesByComplexity = computed(() =>
        [...this.packages()].sort((a, b) => num(b.complexity) - num(a.complexity)),
    );

    classesFor(packageId: string) {
        return computed(() => this.classesState()[packageId] ?? idle<ClassSummary[]>());
    }

    classDetail(classId: string) {
        return computed(() => this.classDetailState()[classId] ?? idle<ClassDetail>());
    }

    loadProjects(): void {
        if (this.requestedProjects) return;
        this.requestedProjects = true;
        this.projectsState.set({value: untracked(this.projectsState).value, loading: true, error: null});
        this.gql.query<ProjectsQuery>(PROJECTS).subscribe({
            next: (data) => this.projectsState.set({value: data.projects, loading: false, error: null}),
            error: (err: Error) => {
                this.requestedProjects = false;
                this.projectsState.set({value: null, loading: false, error: err.message});
            },
        });
    }

    loadProject(id: string, options: { force?: boolean } = {}): void {
        if (!options.force && this.requestedProjectId === id) return;
        this.requestedProjectId = id;
        const current = untracked(this.snapshotState).value;
        this.snapshotState.set({value: options.force ? current : null, loading: true, error: null});
        this.gql.query<ProjectSnapshotQuery>(PROJECT_SNAPSHOT, {id}).subscribe({
            next: (data) => {
                if (!data.projectById) {
                    this.snapshotState.set({
                        value: null,
                        loading: false,
                        error: `No project with id ${id}.`,
                    });
                    return;
                }
                this.snapshotState.set({value: data.projectById, loading: false, error: null});
            },
            error: (err: Error) => {
                // Cleared so navigating back to this project retries rather than
                // showing a stale error forever.
                this.requestedProjectId = null;
                this.snapshotState.set({value: null, loading: false, error: err.message});
            },
        });
    }

    loadClasses(packageId: string): void {
        if (this.requestedPackageIds.has(packageId)) return;
        this.requestedPackageIds.add(packageId);
        this.patchClasses(packageId, {value: null, loading: true, error: null});
        this.gql.query<ClassesByPackageQuery>(CLASSES_BY_PACKAGE, {packageId}).subscribe({
            next: (data) =>
                this.patchClasses(packageId, {
                    value: data.classesByPackage,
                    loading: false,
                    error: null,
                }),
            error: (err: Error) => {
                this.requestedPackageIds.delete(packageId);
                this.patchClasses(packageId, {value: null, loading: false, error: err.message});
            },
        });
    }

    loadClassDetail(classId: string): void {
        if (this.requestedClassIds.has(classId)) return;
        this.requestedClassIds.add(classId);
        this.patchClassDetail(classId, {value: null, loading: true, error: null});
        this.gql.query<ClassDetailQuery>(CLASS_DETAIL, {classId}).subscribe({
            next: (data) => {
                if (!data.classById) {
                    this.patchClassDetail(classId, {
                        value: null,
                        loading: false,
                        error: `No class with id ${classId}.`,
                    });
                    return;
                }
                this.patchClassDetail(classId, {value: data.classById, loading: false, error: null});
            },
            error: (err: Error) => {
                this.requestedClassIds.delete(classId);
                this.patchClassDetail(classId, {value: null, loading: false, error: err.message});
            },
        });
    }

    private patchClasses(packageId: string, state: Loadable<ClassSummary[]>): void {
        this.classesState.update((current) => ({...current, [packageId]: state}));
    }

    private patchClassDetail(classId: string, state: Loadable<ClassDetail>): void {
        this.classDetailState.update((current) => ({...current, [classId]: state}));
    }
}
