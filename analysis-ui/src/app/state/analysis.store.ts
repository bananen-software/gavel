import {computed, inject, Injectable, OnDestroy, signal, untracked} from '@angular/core';
import {GraphQlClient} from '../api/graphql.client';
import {CreateWorkspaceRequest, RestClient} from '../api/rest.client';
import {CLASS_DETAIL, CLASSES_BY_PACKAGE, PROJECT_SNAPSHOT, PROJECTS, WORKSPACES,} from '../api/queries';
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
    WorkspacesQuery,
    WorkspaceSummary,
} from '../api/schema.types';
import {num, sum} from '../shared/format';
import {isAnalysisInProgress} from '../shared/ratings';

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
export class AnalysisStore implements OnDestroy {
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
    private requestedWorkspaces = false;

    private readonly rest = inject(RestClient);

    private readonly workspacesState = signal<Loadable<WorkspaceSummary[]>>(idle());

    /**
     * Keys of actions currently in flight, so a button can disable itself
     * without every caller inventing its own loading flag. Keys look like
     * 'analysis:12' or 'locate:3'.
     */
    private readonly busyKeys = signal<ReadonlySet<string>>(new Set<string>());
    private readonly actionErrorState = signal<string | null>(null);

    private pollHandle: ReturnType<typeof setInterval> | null = null;
    private pollsRemaining = 0;

    readonly workspaces = computed(() => this.workspacesState().value ?? []);
    readonly workspacesLoading = computed(() => this.workspacesState().loading);
    readonly workspacesError = computed(() => this.workspacesState().error);
    readonly busy = this.busyKeys.asReadonly();
    readonly actionError = this.actionErrorState.asReadonly();


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


    // ---- Workspaces -------------------------------------------------------

    loadWorkspaces(options: { force?: boolean } = {}): void {
        if (!options.force && this.requestedWorkspaces) return;
        this.requestedWorkspaces = true;
        const current = untracked(this.workspacesState).value;
        this.workspacesState.set({value: current, loading: true, error: null});
        this.gql.query<WorkspacesQuery>(WORKSPACES).subscribe({
            next: (data) =>
                this.workspacesState.set({value: data.workspaces, loading: false, error: null}),
            error: (err: Error) => {
                this.requestedWorkspaces = false;
                this.workspacesState.set({value: null, loading: false, error: err.message});
            },
        });
    }

    createWorkspace(request: CreateWorkspaceRequest, onSuccess?: (id: string) => void): void {
        const key = 'create-workspace';
        if (!this.beginAction(key)) return;
        this.rest.createWorkspace(request).subscribe({
            next: (response) => {
                this.endAction(key);
                this.loadWorkspaces({force: true});
                onSuccess?.(response.id);
            },
            error: (err: Error) => this.endAction(key, err.message),
        });
    }

    locateProjects(workspaceId: string): void {
        const key = `locate:${workspaceId}`;
        if (!this.beginAction(key)) return;
        this.rest.locateProjects(workspaceId).subscribe({
            next: () => {
                this.endAction(key);
                this.loadWorkspaces({force: true});
            },
            error: (err: Error) => this.endAction(key, err.message),
        });
    }

    scheduleAnalysis(projectId: string): void {
        const key = `analysis:${projectId}`;
        if (!this.beginAction(key)) return;
        this.rest.scheduleAnalysis(projectId).subscribe({
            next: () => {
                this.endAction(key);
                // Reflect the queued state immediately rather than waiting for
                // the first poll, so the button does not look inert.
                this.markProjectPending(projectId);
                this.loadWorkspaces({force: true});
                this.startPolling();
            },
            error: (err: Error) => this.endAction(key, err.message),
        });
    }

    dismissActionError(): void {
        this.actionErrorState.set(null);
    }

    // ---- Action and polling plumbing --------------------------------------

    /** Returns false when the same action is already running. */
    private beginAction(key: string): boolean {
        const keys = untracked(this.busyKeys);
        if (keys.has(key)) return false;
        this.actionErrorState.set(null);
        this.busyKeys.set(new Set(keys).add(key));
        return true;
    }

    private endAction(key: string, error?: string): void {
        const keys = new Set(untracked(this.busyKeys));
        keys.delete(key);
        this.busyKeys.set(keys);
        if (error) this.actionErrorState.set(error);
    }

    private markProjectPending(projectId: string): void {
        this.workspacesState.update((state) => {
            if (!state.value) return state;
            return {
                ...state,
                value: state.value.map((workspace) => ({
                    ...workspace,
                    projects: workspace.projects.map((project) =>
                        project.id === projectId ? {...project, analysisStatus: 'PENDING' as const} : project,
                    ),
                })),
            };
        });
    }

    /**
     * Polls while an analysis is running, and only then.
     *
     * One timer, started on demand and stopped as soon as nothing is in
     * progress, with a hard cap so a status that never leaves RUNNING cannot
     * leave a request loop running in the background for the rest of the
     * session.
     */
    startPolling(): void {
        this.pollsRemaining = 120; // ten minutes at a five second interval
        if (this.pollHandle !== null) return;
        this.pollHandle = setInterval(() => this.pollOnce(), 5000);
    }

    stopPolling(): void {
        if (this.pollHandle === null) return;
        clearInterval(this.pollHandle);
        this.pollHandle = null;
    }

    private pollOnce(): void {
        if (this.pollsRemaining-- <= 0) {
            this.stopPolling();
            return;
        }

        const workspaces = untracked(this.workspacesState).value ?? [];
        const snapshot = untracked(this.snapshotState).value;

        const running =
            workspaces.some((workspace) =>
                workspace.projects.some((project) => isAnalysisInProgress(project.analysisStatus)),
            ) || isAnalysisInProgress(snapshot?.analysisStatus ?? null);

        if (!running) {
            this.stopPolling();
            return;
        }

        if (workspaces.length > 0) this.loadWorkspaces({force: true});
        if (snapshot) this.loadProject(snapshot.id, {force: true});
    }

    ngOnDestroy(): void {
        this.stopPolling();
    }

    private patchClasses(packageId: string, state: Loadable<ClassSummary[]>): void {
        this.classesState.update((current) => ({...current, [packageId]: state}));
    }

    private patchClassDetail(classId: string, state: Loadable<ClassDetail>): void {
        this.classDetailState.update((current) => ({...current, [classId]: state}));
    }
}
