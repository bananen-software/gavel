// package-complexity-dashboard.component.ts
import {Component, ElementRef, Input, OnInit, ViewChild} from '@angular/core';
import {Apollo, gql} from 'apollo-angular';
import * as d3 from 'd3';
import {DecimalPipe, NgIf} from "@angular/common";
import {FormsModule} from "@angular/forms";

interface Class {
  id: string;
  packageId: number;
  name: string;
  programmingLanguage: string;
  complexity: number;
  complexityRating: string;
  totalLinesOfCode: number;
  totalLinesOfComments: number;
  numberOfResponsibilities: number;
  totalNumberOfFindings: number;
  defectDensity: number;
  highDefectDensity: number;
  numberOfChanges: number;
  numberOfAuthors: number;
}

interface Package {
  id: string;
  name: string;
  complexity: number;
  complexityRating: string;
  complexityOrdinal: number;
  numberOfTypes: number;
  linesOfCode: number;
  size: string;
  classes: Class[];
  totalNumberOfFindings: number;
  defectDensity: number;
  highDefectDensity: number;
}

interface BubbleData {
  id: string;
  name: string;
  complexity: number;
  complexityRating: string;
  radius: number;
  x?: number;
  y?: number;
  color: string;
  type: 'package' | 'class';
  packageId?: number;
  packageName?: string;
  defectDensity: number;
  highDefectDensity: number;
  // Package specific fields
  numberOfTypes?: number;
  linesOfCode?: number;
  classCount?: number;
  // Class specific fields
  totalLinesOfCode?: number;
  programmingLanguage?: string;
  numberOfResponsibilities?: number;
  totalNumberOfFindings?: number;
  numberOfChanges?: number;
  numberOfAuthors?: number;
}

interface FilterState {
  complexity: { min: number; max: number };
  linesOfCode: { min: number; max: number };
  classCount: { min: number; max: number };
  defectDensity: { min: number; max: number };
}

const GET_PACKAGES_WITH_CLASSES = gql`
  query GetPackagesWithClasses($projectId: Int!) {
    packagesByProject(projectId: $projectId) {
      id
      name
      complexity
      complexityRating
      complexityOrdinal
      numberOfTypes
      linesOfCode
      size
      totalNumberOfFindings
      defectDensity
      highDefectDensity
      classes {
        id
        packageId
        name
        programmingLanguage
        complexity
        complexityRating
        totalLinesOfCode
        totalLinesOfComments
        numberOfResponsibilities
        totalNumberOfFindings
        defectDensity
        highDefectDensity
        numberOfChanges
        numberOfAuthors
      }
    }
  }
`;

@Component({
  selector: 'app-package-complexity-dashboard',
  template: `
    <div class="dashboard-container">
      <div class="dashboard-header">
        <div class="header-left">
          <h3>{{ getViewTitle() }}</h3>
          <div class="breadcrumb" *ngIf="currentView === 'classes'">
            <span class="package-path">📦 {{ currentPackageName }}</span>
          </div>
        </div>
        <div class="controls">
          <div class="navigation" *ngIf="currentView === 'classes'">
            <button (click)="navigateToPackages()" class="back-btn">
              ← Back to Packages
            </button>
          </div>
          <button (click)="toggleFilters()" class="filter-btn" [class.active]="showFilters">
            🔧 Filters
          </button>
          <button (click)="toggleFullscreen()" class="fullscreen-btn"
                  [title]="isFullscreen ? 'Exit Fullscreen' : 'Enter Fullscreen'">
            {{ isFullscreen ? '⤥' : '⛶' }}
          </button>
          <div class="zoom-controls">
            <button (click)="zoomIn()" class="zoom-btn" title="Zoom In">+</button>
            <button (click)="zoomOut()" class="zoom-btn" title="Zoom Out">−</button>
            <button (click)="resetZoom()" class="zoom-btn reset" title="Reset Zoom">⌂</button>
          </div>
        </div>
      </div>

      <!-- Filter Panel -->
      <div class="filter-panel" [class.visible]="showFilters">
        <div class="filter-content">
          <div class="filter-section" *ngIf="currentView === 'packages'">
            <h4>Lines of Code</h4>
            <div class="slider-group">
              <label>Min: {{ filters.linesOfCode.min | number }}</label>
              <input type="range"
                     [min]="dataRanges.linesOfCode.min"
                     [max]="dataRanges.linesOfCode.max"
                     [(ngModel)]="filters.linesOfCode.min"
                     (input)="applyFilters()">
              <label>Max: {{ filters.linesOfCode.max | number }}</label>
              <input type="range"
                     [min]="dataRanges.linesOfCode.min"
                     [max]="dataRanges.linesOfCode.max"
                     [(ngModel)]="filters.linesOfCode.max"
                     (input)="applyFilters()">
            </div>
          </div>

          <div class="filter-section" *ngIf="currentView === 'packages'">
            <h4>Class Count</h4>
            <div class="slider-group">
              <label>Min: {{ filters.classCount.min }}</label>
              <input type="range"
                     [min]="dataRanges.classCount.min"
                     [max]="dataRanges.classCount.max"
                     [(ngModel)]="filters.classCount.min"
                     (input)="applyFilters()">
              <label>Max: {{ filters.classCount.max | number }}</label>
              <input type="range"
                     [min]="dataRanges.classCount.min"
                     [max]="dataRanges.classCount.max"
                     [(ngModel)]="filters.classCount.max"
                     (input)="applyFilters()">
            </div>
          </div>

          <div class="filter-section">
            <h4>Complexity Score</h4>
            <div class="slider-group">
              <label>Min: {{ filters.complexity.min }}</label>
              <input type="range"
                     [min]="dataRanges.complexity.min"
                     [max]="dataRanges.complexity.max"
                     [(ngModel)]="filters.complexity.min"
                     (input)="applyFilters()">
              <label>Max: {{ filters.complexity.max | number }}</label>
              <input type="range"
                     [min]="dataRanges.complexity.min"
                     [max]="dataRanges.complexity.max"
                     [(ngModel)]="filters.complexity.max"
                     (input)="applyFilters()">
            </div>
          </div>

          <div class="filter-section" *ngIf="currentView === 'packages'">
            <h4>Defect Density per KLOC</h4>
            <div class="slider-group">
              <label>Min: {{ filters.defectDensity.min }}</label>
              <input type="range"
                     [min]="dataRanges.defectDensity.min"
                     [max]="dataRanges.defectDensity.max"
                     [(ngModel)]="filters.defectDensity.min"
                     (input)="applyFilters()">
              <label>Max: {{ filters.defectDensity.max | number }}</label>
              <input type="range"
                     [min]="dataRanges.defectDensity.min"
                     [max]="dataRanges.defectDensity.max"
                     [(ngModel)]="filters.defectDensity.max"
                     (input)="applyFilters()">
            </div>
          </div>

          <div class="filter-actions">
            <button (click)="resetFilters()" class="reset-btn">Reset Filters</button>
          </div>
        </div>
      </div>

      <div class="chart-container">
        <svg #svgElement></svg>
        <div class="tooltip" #tooltip></div>
        <div class="zoom-info">
          <span *ngIf="currentView === 'packages'">Scroll to zoom • Drag to pan • Double-click package to explore classes</span>
          <span *ngIf="currentView === 'classes'">Scroll to zoom • Drag to pan • {{ currentClassCount }}
            classes in {{ currentPackageName }}</span>
        </div>
      </div>
      <div class="chart-info">
        <span *ngIf="currentView === 'packages'"><strong>Packages:</strong> {{ filteredCount }}
          of {{ totalCount }}</span>
        <span *ngIf="currentView === 'classes'"><strong>Classes:</strong> {{ filteredCount }}
          in {{ currentPackageName }}</span>
        <span><strong>Size:</strong> {{ getSizeLabel() }}</span>
        <span><strong>Color:</strong> Complexity Level</span>
      </div>
    </div>
  `,
  standalone: true,
  imports: [
    DecimalPipe,
    FormsModule,
    NgIf
  ],
  styles: [`
    .dashboard-container {
      width: 100%;
      height: 100vh;
      display: flex;
      flex-direction: column;
      font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
      background: #fafafa;
    }

    .dashboard-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 12px 16px;
      background: white;
      border-bottom: 1px solid #e5e7eb;
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
      z-index: 10;
      min-height: 60px;
    }

    .header-left {
      display: flex;
      flex-direction: column;
      gap: 4px;
    }

    .dashboard-header h3 {
      margin: 0;
      color: #374151;
      font-weight: 600;
      font-size: 18px;
      transition: all 0.3s ease;
    }

    .breadcrumb {
      display: flex;
      align-items: center;
    }

    .package-path {
      background: #f3f4f6;
      padding: 4px 8px;
      border-radius: 12px;
      font-size: 11px;
      color: #6b7280;
      font-weight: 500;
    }

    .controls {
      display: flex;
      align-items: center;
      gap: 16px;
    }

    .navigation {
      display: flex;
      align-items: center;
    }

    .back-btn {
      background: #3b82f6;
      color: white;
      border: none;
      padding: 8px 16px;
      border-radius: 6px;
      font-size: 13px;
      cursor: pointer;
      transition: all 0.2s;
      font-weight: 500;
      display: flex;
      align-items: center;
      gap: 6px;
    }

    .back-btn:hover {
      background: #2563eb;
      transform: translateY(-1px);
      box-shadow: 0 2px 4px rgba(59, 130, 246, 0.3);
    }

    .filter-btn {
      background: #6b7280;
      color: white;
      border: none;
      padding: 8px 12px;
      border-radius: 6px;
      font-size: 12px;
      cursor: pointer;
      transition: all 0.2s;
      font-weight: 500;
    }

    .filter-btn:hover, .filter-btn.active {
      background: #374151;
      transform: translateY(-1px);
    }

    .fullscreen-btn {
      background: #10b981;
      color: white;
      border: none;
      padding: 8px 12px;
      border-radius: 6px;
      font-size: 16px;
      cursor: pointer;
      transition: all 0.2s;
      font-weight: 500;
      width: 40px;
      height: 36px;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .fullscreen-btn:hover {
      background: #059669;
      transform: translateY(-1px);
    }

    .zoom-controls {
      display: flex;
      gap: 4px;
      background: #f3f4f6;
      border-radius: 6px;
      padding: 4px;
    }

    .zoom-btn {
      width: 32px;
      height: 32px;
      border: none;
      background: white;
      border-radius: 4px;
      cursor: pointer;
      font-size: 16px;
      font-weight: 600;
      display: flex;
      align-items: center;
      justify-content: center;
      transition: all 0.2s;
      color: #374151;
    }

    .zoom-btn:hover {
      background: #e5e7eb;
      transform: translateY(-1px);
    }

    .zoom-btn.reset {
      font-size: 14px;
    }

    /* Filter Panel Styles */
    .filter-panel {
      background: white;
      border-bottom: 1px solid #e5e7eb;
      max-height: 0;
      overflow: hidden;
      transition: max-height 0.3s ease;
    }

    .filter-panel.visible {
      max-height: 300px;
    }

    .filter-content {
      padding: 16px;
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      gap: 20px;
      align-items: start;
    }

    .filter-section h4 {
      margin: 0 0 8px 0;
      font-size: 14px;
      font-weight: 600;
      color: #374151;
    }

    .checkbox-item input[type="checkbox"] {
      display: none;
    }

    .slider-group {
      display: flex;
      flex-direction: column;
      gap: 6px;
    }

    .slider-group label {
      font-size: 11px;
      color: #6b7280;
      font-weight: 500;
    }

    .slider-group input[type="range"] {
      width: 100%;
      height: 4px;
      border-radius: 2px;
      background: #e5e7eb;
      outline: none;
      cursor: pointer;
    }

    .slider-group input[type="range"]::-webkit-slider-thumb {
      appearance: none;
      width: 16px;
      height: 16px;
      border-radius: 50%;
      background: #3b82f6;
      cursor: pointer;
    }

    .filter-actions {
      display: flex;
      flex-direction: column;
      gap: 8px;
      align-items: flex-start;
    }

    .reset-btn {
      background: #ef4444;
      color: white;
      border: none;
      padding: 6px 12px;
      border-radius: 4px;
      font-size: 11px;
      cursor: pointer;
      transition: all 0.2s;
    }

    .reset-btn:hover {
      background: #dc2626;
      transform: translateY(-1px);
    }

    .filter-count {
      font-size: 11px;
      color: #6b7280;
      font-weight: 500;
    }

    .chart-container {
      flex: 1;
      position: relative;
      overflow: hidden;
      background: white;
      border-radius: 8px;
      margin: 8px;
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
    }

    svg {
      width: 100%;
      height: 100%;
      cursor: grab;
    }

    .tooltip {
      position: absolute;
      background: rgba(0, 0, 0, 0.9);
      color: white;
      padding: 12px 14px;
      border-radius: 6px;
      font-size: 12px;
      pointer-events: none;
      opacity: 0;
      transition: opacity 0.2s;
      z-index: 1000;
      min-width: 240px;
      width: fit-content;
      line-height: 1.4;
    }

    .tooltip.visible {
      opacity: 1;
    }

    .zoom-info {
      position: absolute;
      bottom: 12px;
      left: 12px;
      background: rgba(0, 0, 0, 0.7);
      color: white;
      padding: 6px 10px;
      border-radius: 4px;
      font-size: 11px;
      opacity: 0.8;
    }

    .chart-info {
      padding: 8px 16px;
      background: white;
      border-top: 1px solid #e5e7eb;
      display: flex;
      gap: 24px;
      font-size: 12px;
      color: #6b7280;
      margin: 0 8px 8px 8px;
      border-radius: 0 0 8px 8px;
    }

    .chart-info span {
      margin: 0;
    }

    /* Responsive design for smaller screens */
    @media (max-width: 768px) {
      .dashboard-header {
        flex-direction: column;
        gap: 8px;
        padding: 8px 12px;
        min-height: auto;
      }

      .controls {
        width: 100%;
        justify-content: space-between;
        flex-wrap: wrap;
      }

      .navigation {
        width: 100%;
        justify-content: center;
        margin-bottom: 8px;
      }

      .legend {
        gap: 8px;
      }

      .chart-info {
        flex-direction: column;
        gap: 4px;
      }

      .dashboard-header h3 {
        font-size: 16px;
      }

      .filter-content {
        grid-template-columns: 1fr;
      }
    }

    @media (max-height: 600px) {
      .dashboard-header {
        padding: 8px 12px;
      }

      .chart-info {
        padding: 6px 12px;
      }
    }
  `]
})
export class PackageComplexityDashboardComponent implements OnInit {
  @ViewChild('svgElement', {static: true}) svgElement!: ElementRef<SVGElement>;
  @ViewChild('tooltip', {static: true}) tooltip!: ElementRef<HTMLElement>;
  @Input() projectId: number = 1;

  private svg: any;
  private g: any;
  private simulation: any;
  private zoom: any;
  private width = 800;
  private height = 600;

  // Data management
  private packageData: Package[] = [];
  private currentBubbleData: BubbleData[] = [];
  private filteredData: BubbleData[] = [];
  currentView: 'packages' | 'classes' = 'packages';
  currentPackageName: string = '';
  currentClassCount: number = 0;
  totalCount: number = 0;
  filteredCount: number = 0;

  // Filter state
  showFilters = false;
  isFullscreen = false;
  filters: FilterState = {
    complexity: {min: 1, max: 100},
    linesOfCode: {min: 1, max: 100000},
    classCount: {min: 1, max: 50},
    defectDensity: {min: 1, max: 10000},
  };

  dataRanges = {
    complexity: {min: 1, max: 100},
    linesOfCode: {min: 1, max: 100000},
    classCount: {min: 1, max: 50},
    defectDensity: {min: 1, max: 10000}
  };

  constructor(private apollo: Apollo) {
  }

  ngOnInit() {
    this.setupZoom();
    this.loadData();
    this.handleResize();
  }

  private handleResize() {
    window.addEventListener('resize', () => {
      this.updateDimensions();
      if (this.filteredData.length > 0) {
        this.updateSimulation();
      }
    });
  }

  private updateDimensions() {
    const container = this.svgElement.nativeElement.parentElement!;
    this.width = container.clientWidth;
    this.height = container.clientHeight;

    if (this.svg) {
      this.svg.attr('width', this.width).attr('height', this.height);
    }

    if (this.simulation) {
      this.simulation
        .force('center', d3.forceCenter(this.width / 2, this.height / 2))
        .alpha(0.3)
        .restart();
    }
  }

  private setupZoom() {
    this.zoom = d3.zoom()
      .scaleExtent([0.3, 8])
      .on('zoom', (event) => {
        this.g.attr('transform', event.transform);
      })
      .on('start', () => {
        this.svgElement.nativeElement.classList.add('panning');
      })
      .on('end', () => {
        this.svgElement.nativeElement.classList.remove('panning');
      });
  }

  private loadData() {
    this.apollo.watchQuery<{ packagesByProject: Package[] }>({
      query: GET_PACKAGES_WITH_CLASSES,
      variables: {projectId: this.projectId}
    }).valueChanges.subscribe(result => {
      if (result.data?.packagesByProject) {
        this.packageData = result.data.packagesByProject;
        this.initializeFilters();
        this.navigateToPackages();
      }
    });
  }

  private initializeFilters() {
    // Calculate data ranges for packages
    const packages = this.packageData;

    if (packages.length === 0) return;

    const complexities = packages.map(p => p.complexity).filter(c => c != null);
    const linesOfCode = packages.map(p => p.linesOfCode).filter(loc => loc != null);
    const classCounts = packages.map(p => p.classes ? p.classes.length : 0);
    const defectDensityValues = packages.map(p => p.defectDensity);

    this.dataRanges.complexity.min = 1;
    this.dataRanges.complexity.max = Math.max(...complexities);
    this.dataRanges.linesOfCode.min = 1;
    this.dataRanges.linesOfCode.max = Math.max(...linesOfCode);
    this.dataRanges.classCount.min = 1;
    this.dataRanges.classCount.max = Math.max(...classCounts);
    this.dataRanges.defectDensity.min = 0;
    this.dataRanges.defectDensity.max = Math.max(...defectDensityValues);

    // Set initial filter values to show all data
    this.filters.complexity.min = this.dataRanges.complexity.min;
    this.filters.complexity.max = this.dataRanges.complexity.max;
    this.filters.linesOfCode.min = this.dataRanges.linesOfCode.min;
    this.filters.linesOfCode.max = this.dataRanges.linesOfCode.max;
    this.filters.classCount.min = this.dataRanges.classCount.min;
    this.filters.classCount.max = this.dataRanges.classCount.max;
    this.filters.defectDensity.min = this.dataRanges.defectDensity.min
    this.filters.defectDensity.max = this.dataRanges.defectDensity.max

    console.log('Filter ranges initialized:', this.dataRanges);
    console.log('Initial filter values:', this.filters);
  }

  navigateToPackages() {
    this.currentView = 'packages';
    this.currentPackageName = '';
    this.currentClassCount = 0;
    this.currentBubbleData = this.processPackageData(this.packageData);
    this.totalCount = this.currentBubbleData.length;

    this.applyFilters();
  }

  private showClasses(packageData: Package) {
    this.currentView = 'classes';
    this.currentPackageName = packageData.name;
    this.currentClassCount = packageData.classes.length;
    this.currentBubbleData = this.processClassData(packageData);
    this.totalCount = this.currentBubbleData.length;

    this.applyFilters();
  }

  toggleFilters() {
    this.showFilters = !this.showFilters;
  }

  toggleFullscreen() {
    if (!this.isFullscreen) {
      // Enter fullscreen
      const elem = document.getElementsByTagName("app-package-complexity-dashboard")[0];
      if (elem.requestFullscreen) {
        elem.requestFullscreen();
      } else if ((elem as any).webkitRequestFullscreen) {
        (elem as any).webkitRequestFullscreen();
      } else if ((elem as any).msRequestFullscreen) {
        (elem as any).msRequestFullscreen();
      }
    } else {
      // Exit fullscreen
      if (document.exitFullscreen) {
        document.exitFullscreen();
      } else if ((document as any).webkitExitFullscreen) {
        (document as any).webkitExitFullscreen();
      } else if ((document as any).msExitFullscreen) {
        (document as any).msExitFullscreen();
      }
    }

    // Listen for fullscreen changes
    document.addEventListener('fullscreenchange', () => {
      this.isFullscreen = !!document.fullscreenElement;
      setTimeout(() => this.updateDimensions(), 100);
    });

    document.addEventListener('webkitfullscreenchange', () => {
      this.isFullscreen = !!(document as any).webkitFullscreenElement;
      setTimeout(() => this.updateDimensions(), 100);
    });
  }

  resetFilters() {
    this.filters = {
      complexity: {min: this.dataRanges.complexity.min, max: this.dataRanges.complexity.max},
      linesOfCode: {min: this.dataRanges.linesOfCode.min, max: this.dataRanges.linesOfCode.max},
      classCount: {min: this.dataRanges.classCount.min, max: this.dataRanges.classCount.max},
      defectDensity: {min: this.dataRanges.defectDensity.min, max: this.dataRanges.defectDensity.max},
    };
    console.log('Filters reset to:', this.filters);
    this.applyFilters();
  }

  applyFilters() {
    if (!this.currentBubbleData || this.currentBubbleData.length === 0) {
      this.filteredData = [];
      this.filteredCount = 0;
      return;
    }

    this.filteredData = this.currentBubbleData.filter(item => {
      // Complexity score filter
      if (item.complexity < this.filters.complexity.min || item.complexity > this.filters.complexity.max) {
        return false;
      }

      // View-specific filters
      if (this.currentView === 'packages' && item.type === 'package') {
        // Lines of code filter
        const linesOfCode = item.linesOfCode || 0;
        if (linesOfCode < this.filters.linesOfCode.min || linesOfCode > this.filters.linesOfCode.max) {
          return false;
        }

        // Class count filter
        const classCount = item.classCount || 0;
        if (classCount < this.filters.classCount.min || classCount > this.filters.classCount.max) {
          return false;
        }

        // Defect density filter
        const defectDensity = item.defectDensity || 0;
        if (defectDensity < this.filters.defectDensity.min || defectDensity > this.filters.defectDensity.max) {
          return false;
        }
      }

      return true;
    });

    this.filteredCount = this.filteredData.length;
    this.createVisualization();
  }

  private createVisualization() {
    // Clear existing content
    d3.select(this.svgElement.nativeElement).selectAll("*").remove();

    // Set up dimensions
    this.updateDimensions();

    // Create SVG
    this.svg = d3.select(this.svgElement.nativeElement)
      .attr('width', this.width)
      .attr('height', this.height)
      .call(this.zoom);

    // Create main group for zoom/pan
    this.g = this.svg.append('g');

    if (this.filteredData.length === 0) {
      // Show "no data" message
      this.g.append('text')
        .attr('x', this.width / 2)
        .attr('y', this.height / 2)
        .attr('text-anchor', 'middle')
        .attr('dominant-baseline', 'central')
        .style('font-size', '16px')
        .style('fill', '#6b7280')
        .text('No items match the current filters');
      return;
    }

    // Create scales
    const colorScale = d3.scaleOrdinal<string>()
      .domain(['LOW', 'MOSTLY_SIMPLE', 'MEDIUM', 'BALANCED', 'COMPLEX', 'HIGH', 'HIGHLY_COMPLEX', 'VERY_HIGH'])
      .range(['#1e40af', '#1d4ed8', '#6366f1', '#8b5cf6', '#a855f7', '#e879f9', '#f43f5e', '#dc2626']);

    // Create force simulation
    const forceStrength = this.currentView === 'packages' ? -80 : -50;
    this.simulation = d3.forceSimulation(this.filteredData)
      .force('charge', d3.forceManyBody().strength(forceStrength))
      .force('center', d3.forceCenter(this.width / 2, this.height / 2))
      .force('collision', d3.forceCollide().radius((d: any) => d.radius + 3))
      .force('x', d3.forceX(this.width / 2).strength(0.05))
      .force('y', d3.forceY(this.height / 2).strength(0.05));

    // Create bubbles
    const bubbles = this.g.selectAll('.bubble')
      .data(this.filteredData)
      .enter()
      .append('circle')
      .attr('class', (d: BubbleData) => `bubble ${d.type}`)
      .attr('r', (d: BubbleData) => d.radius)
      .attr('fill', (d: BubbleData) => colorScale(d.complexityRating.toUpperCase()))
      .attr('opacity', 0.85)

    // Create labels for bubbles
    const minLabelRadius = this.currentView === 'packages' ? 25 : 12;
    const labels = this.g.selectAll('.bubble-label')
      .data(this.filteredData.filter((d: BubbleData) => d.radius > minLabelRadius))
      .enter()
      .append('text')
      .text((d: BubbleData) => this.truncateText(d.name, d.radius))
      .style('font-size', (d: BubbleData) => {
        const baseSize = this.currentView === 'packages' ? 12 : 10;
        return Math.min(d.radius / 2.5, baseSize) + 'px';
      })
      .style('text-anchor', 'middle')
      .style('dominant-baseline', 'central')

    // Add interactions
    if (this.currentView === 'packages') {
      bubbles
        .on('dblclick', (event: any, d: BubbleData) => {
          event.stopPropagation();
          const packageData = this.packageData.find(p => p.id === d.id);
          if (packageData && packageData.classes.length > 0) {
            this.showClasses(packageData);
          }
        });
    }

    // Add hover interactions
    bubbles
      .on('mouseover', (event: any, d: BubbleData) => {
        this.showTooltip(event, d);
      })
      .on('mousemove', (event: any) => {
        this.moveTooltip(event);
      })
      .on('mouseout', () => {
        this.hideTooltip();
      });

    // Update positions on simulation tick
    this.simulation.on('tick', () => {
      bubbles
        .attr('cx', (d: any) => d.x)
        .attr('cy', (d: any) => d.y);

      labels
        .attr('x', (d: any) => d.x)
        .attr('y', (d: any) => d.y);
    });

    // Reset zoom when switching views
    this.resetZoom();
  }

  private updateSimulation() {
    if (this.simulation) {
      this.simulation
        .force('center', d3.forceCenter(this.width / 2, this.height / 2))
        .force('x', d3.forceX(this.width / 2).strength(0.05))
        .force('y', d3.forceY(this.height / 2).strength(0.05))
        .alpha(0.3)
        .restart();
    }
  }

  private processPackageData(packages: Package[]): BubbleData[] {
    const maxLoc = d3.max(packages, d => d.linesOfCode) || 1;
    const minLoc = d3.min(packages, d => d.linesOfCode) || 1;

    const maxRadius = Math.min(this.width, this.height) * 0.12;
    const minRadius = Math.max(20, maxRadius * 0.3);

    const radiusScale = d3.scaleSqrt()
      .domain([minLoc, maxLoc])
      .range([minRadius, maxRadius]);

    return packages.map(pkg => ({
      id: pkg.id,
      name: pkg.name,
      complexity: pkg.complexity,
      complexityRating: pkg.complexityRating,
      radius: radiusScale(pkg.linesOfCode),
      color: this.getComplexityColor(pkg.complexityRating),
      type: 'package' as const,
      numberOfTypes: pkg.numberOfTypes,
      linesOfCode: pkg.linesOfCode,
      classCount: pkg.classes.length,
      defectDensity: pkg.defectDensity,
      highDefectDensity: pkg.highDefectDensity,
      totalNumberOfFindings: pkg.totalNumberOfFindings
    }));
  }

  private processClassData(parentPackage: Package): BubbleData[] {
    const classes = parentPackage.classes;

    if (classes.length === 0) return [];

    const maxLoc = d3.max(classes, d => d.totalLinesOfCode) || 1;
    const minLoc = d3.min(classes, d => d.totalLinesOfCode) || 1;

    const maxRadius = Math.min(this.width, this.height) * 0.08;
    const minRadius = Math.max(15, maxRadius * 0.4);

    const radiusScale = d3.scaleSqrt()
      .domain([minLoc, maxLoc])
      .range([minRadius, maxRadius]);

    return classes.map(cls => ({
      id: cls.id,
      name: cls.name,
      complexity: cls.complexity,
      complexityRating: cls.complexityRating,
      radius: radiusScale(cls.totalLinesOfCode),
      color: this.getComplexityColor(cls.complexityRating),
      type: 'class' as const,
      packageId: cls.packageId,
      packageName: parentPackage.name,
      totalLinesOfCode: cls.totalLinesOfCode,
      programmingLanguage: cls.programmingLanguage,
      numberOfResponsibilities: cls.numberOfResponsibilities,
      totalNumberOfFindings: cls.totalNumberOfFindings,
      defectDensity: cls.defectDensity,
      highDefectDensity: cls.highDefectDensity,
      numberOfAuthors: cls.numberOfAuthors,
      numberOfChanges: cls.numberOfChanges
    }));
  }

  private getComplexityColor(rating: string): string {
    switch (rating?.toLowerCase()) {
      case 'low':
        return '#22c55e';
      case 'medium':
        return '#eab308';
      case 'high':
        return '#ef4444';
      default:
        return '#94a3b8';
    }
  }

  private truncateText(text: string, radius: number): string {
    const maxLength = Math.max(3, Math.floor(radius / 2.5));
    if (text.length <= maxLength) return text;

    if (radius < 15) {
      return text.length > 4 ? text.substring(0, 3) + '…' : text;
    }

    if (radius < 25) {
      return text.length > 8 ? text.substring(0, 7) + '…' : text;
    }

    return text.substring(0, maxLength) + '…';
  }

  // Helper methods for template
  getViewTitle(): string {
    return this.currentView === 'packages' ? 'Package Overview' : 'Class Details';
  }

  getSizeLabel(): string {
    return this.currentView === 'packages' ? 'Lines of Code' : 'Total Lines of Code';
  }

  // Zoom control methods
  zoomIn() {
    this.svg.transition().duration(300).call(
      this.zoom.scaleBy, 1.5
    );
  }

  zoomOut() {
    this.svg.transition().duration(300).call(
      this.zoom.scaleBy, 1 / 1.5
    );
  }

  resetZoom() {
    this.svg.transition().duration(500).call(
      this.zoom.transform,
      d3.zoomIdentity
    );
  }

  private showTooltip(event: any, d: BubbleData) {
    const tooltip = this.tooltip.nativeElement;

    if (d.type === 'package') {
      tooltip.innerHTML = `
        <div><strong>${d.name}</strong></div>
        <div>Lines of Code: ${d.linesOfCode?.toLocaleString()}</div>
        <div>Number of Types: ${d.numberOfTypes}</div>
        <div>Complexity: ${d.complexityRating} (${d.complexity})</div>
        <div>Findings: ${d.totalNumberOfFindings ? d.totalNumberOfFindings : 0}</div>
        <div>Defect Density per KLOC: ${d.defectDensity.toFixed(2)}</div>
        <div>High Defect Density per KLOC: ${d.highDefectDensity.toFixed(2)}</div>
        ${this.currentView === 'packages' ? '<div style="margin-top: 8px; font-style: italic; opacity: 0.8;">Double-click to explore classes</div>' : ''}
      `;
    } else {
      tooltip.innerHTML = `
        <div><strong>${d.name}</strong></div>
        <div>Package: ${d.packageName}</div>
        <div>Lines of Code: ${d.totalLinesOfCode?.toLocaleString()}</div>
        <div>Responsibilities: ${d.numberOfResponsibilities}</div>
        <div>Complexity: ${d.complexityRating} (${d.complexity})</div>
        <div>Findings: ${d.totalNumberOfFindings}</div>
        <div>Defect Density per KLOC: ${d.defectDensity.toFixed(2)}</div>
        <div>High Defect Density per KLOC: ${d.highDefectDensity.toFixed(2)}</div>
        <div>Number of Authors: ${d.numberOfAuthors}</div>
        <div>Number of Changes: ${d.numberOfChanges}</div>
      `;
    }

    tooltip.classList.add('visible');
    this.moveTooltip(event);
  }

  private moveTooltip(event: any) {
    const tooltip = this.tooltip.nativeElement;
    const rect = this.svgElement.nativeElement.getBoundingClientRect();
    const x = event.clientX - rect.left;
    const y = event.clientY - rect.top;

    const tooltipRect = tooltip.getBoundingClientRect();
    const adjustedX = x + tooltipRect.width > this.width ? x - tooltipRect.width - 10 : x + 10;
    const adjustedY = y - tooltipRect.height < 0 ? y + 10 : y - tooltipRect.height - 10;

    tooltip.style.left = adjustedX + 'px';
    tooltip.style.top = adjustedY + 'px';
  }

  private hideTooltip() {
    this.tooltip.nativeElement.classList.remove('visible');
  }
}
