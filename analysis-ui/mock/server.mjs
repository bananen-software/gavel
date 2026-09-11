import { createServer } from 'node:http';

/**
 * A dependency-free stand-in for the real backend, so the UI can be run and
 * looked at before it is wired up. It does not parse GraphQL — it matches on
 * the operation name and returns plausible data for that shape.
 *
 *   node mock/server.mjs        # listens on 8080, same port as Spring Boot
 */

const PORT = 8080;

const PACKAGE_NAMES = [
  'org.example.petclinic.owner',
  'org.example.petclinic.vet',
  'org.example.petclinic.visit',
  'org.example.petclinic.system',
  'org.example.petclinic.model',
  'org.example.petclinic.web.rest',
  'org.example.petclinic.persistence',
  'org.example.petclinic.security',
  'org.example.petclinic.reporting',
  'org.example.petclinic.integration.billing',
  'org.example.petclinic.integration.messaging',
  'org.example.petclinic.util',
];

const CLASS_STEMS = [
  'OwnerController', 'OwnerRepository', 'PetService', 'VisitValidator',
  'BillingGateway', 'ReportBuilder', 'SecurityConfig', 'AuditListener',
  'MessageDispatcher', 'CacheWarmer', 'LegacyImporter', 'MetricsCollector',
  'ScheduleResolver', 'TemplateRenderer', 'NotificationSender', 'DataMigrator',
];

const AUTHORS = [
  { id: '1', name: 'Anna Weber', email: 'anna.weber@example.org' },
  { id: '2', name: 'Milan Novak', email: 'm.novak@example.org' },
  { id: '3', name: 'Priya Raman', email: 'priya@example.org' },
  { id: '4', name: 'Tom Fisher', email: 'tfisher@example.org' },
];

const RULES = [
  ['CyclomaticComplexity', 'Method exceeds the configured complexity threshold.', 'HIGH', 'pmd'],
  ['GodClass', 'Class has too many responsibilities.', 'HIGH', 'pmd'],
  ['UnusedPrivateField', 'Private field is never read.', 'LOW', 'pmd'],
  ['EmptyCatchBlock', 'Exception is swallowed without handling.', 'MEDIUM', 'spotbugs'],
  ['LongParameterList', 'Method signature takes too many parameters.', 'MEDIUM', 'checkstyle'],
  ['MissingJavadoc', 'Public type has no documentation.', 'LOW', 'checkstyle'],
];

/** Deterministic pseudo-random so reloads show the same numbers. */
function rng(seed) {
  let state = seed >>> 0;
  return () => {
    state = (state * 1664525 + 1013904223) >>> 0;
    return state / 0x100000000;
  };
}

const pick = (random, list) => list[Math.floor(random() * list.length)];
const between = (random, min, max) => Math.floor(min + random() * (max - min));

function buildPackage(index) {
  const random = rng(index * 7919 + 13);
  const types = between(random, 4, 60);
  const linesOfCode = types * between(random, 40, 220);
  const veryHigh = between(random, 0, Math.max(1, Math.round(types * 0.2)));
  const high = between(random, 0, Math.max(1, Math.round(types * 0.3)));
  const medium = between(random, 0, Math.max(1, Math.round(types * 0.4)));
  const low = Math.max(0, types - veryHigh - high - medium);
  const findings = between(random, 5, 400);
  const highPriority = Math.round(findings * (0.05 + random() * 0.25));
  const complexityOrdinal = Math.min(4, Math.round((veryHigh * 3 + high * 2) / Math.max(1, types) * 6));
  const ratings = ['EMPTY', 'MOSTLY_SIMPLE', 'BALANCED', 'COMPLEX', 'HIGHLY_COMPLEX'];
  const instability = Number(random().toFixed(2));
  const abstractness = Number((random() * 0.7).toFixed(2));

  return {
    id: String(index + 1),
    name: PACKAGE_NAMES[index % PACKAGE_NAMES.length],
    complexity: veryHigh * 40 + high * 18 + medium * 7 + low * 2,
    complexityRating: ratings[complexityOrdinal],
    complexityOrdinal,
    numberOfTypes: types,
    defectDensity: Number((findings / linesOfCode).toFixed(4)),
    highDefectDensity: Number((highPriority / linesOfCode).toFixed(4)),
    linesOfCode,
    linesOfComments: Math.round(linesOfCode * (0.08 + random() * 0.3)),
    commentToCodeRatio: Number((0.08 + random() * 0.3).toFixed(2)),
    numberOfVeryHighComplexityTypes: veryHigh,
    numberOfHighComplexityTypes: high,
    numberOfMediumComplexityTypes: medium,
    numberOfLowComplexityTypes: low,
    numberOfHighPriorityFindings: highPriority,
    totalNumberOfFindings: findings,
    size: pick(random, ['SMALL', 'MEDIUM', 'LARGE', 'VERY_LARGE']),
    stratum: pick(random, ['SURFACE', 'INTERMEDIATE', 'DEEP', 'SEDIMENT', 'NONE']),
    stratumOrdinal: between(random, 0, 4),
    relationalCohesion: {
      rating: pick(random, ['LOW', 'GOOD', 'HIGH']),
      numberOfTypes: types,
      numberOfInternalRelationships: between(random, 0, types * 3),
      relationalCohesion: Number((random() * 4).toFixed(2)),
    },
    componentDependency: {
      afferentCoupling: between(random, 0, 30),
      efferentCoupling: between(random, 0, 25),
      abstractness,
      instability,
      distance: Number(Math.abs(abstractness + instability - 1).toFixed(2)),
    },
  };
}

function buildClass(packageId, index) {
  const seed = Number(packageId) * 1000 + index;
  const random = rng(seed);
  const loc = between(random, 20, 1400);
  const complexity = between(random, 1, 320);
  const ratings = ['LOW', 'MEDIUM', 'HIGH', 'VERY_HIGH'];
  const rating = ratings[Math.min(3, Math.floor(complexity / 80))];
  const findings = between(random, 0, 60);

  return {
    id: String(seed),
    packageId,
    name: `${PACKAGE_NAMES[(Number(packageId) - 1) % PACKAGE_NAMES.length]}.${pick(random, CLASS_STEMS)}${index}`,
    programmingLanguage: 'JAVA',
    lastModified: new Date(Date.now() - between(random, 0, 700) * 86400000).toISOString(),
    numberOfChanges: between(random, 1, 260),
    numberOfAuthors: between(random, 1, 5),
    size: pick(random, ['SMALL', 'MEDIUM', 'LARGE', 'VERY_LARGE']),
    complexity,
    complexityRating: rating,
    totalLinesOfCode: loc,
    totalLinesOfComments: Math.round(loc * random() * 0.4),
    commentToCodeRatio: Number((random() * 0.4).toFixed(2)),
    numberOfResponsibilities: between(random, 1, 12),
    status: random() > 0.96 ? 'DELETED' : 'ACTIVE',
    totalNumberOfFindings: findings,
    numberOfHighPriorityFindings: Math.round(findings * random() * 0.4),
    defectDensity: Number((findings / loc).toFixed(4)),
    highDefectDensity: Number((findings / loc / 3).toFixed(4)),
    stratum: pick(random, ['SURFACE', 'INTERMEDIATE', 'DEEP', 'SEDIMENT', 'NONE']),
  };
}

function buildClassDetail(classId) {
  const random = rng(Number(classId) + 4242);
  const base = buildClass(String(Math.floor(Number(classId) / 1000)), Number(classId) % 1000);
  const commits = between(random, 6, 40);

  let running = between(random, 5, 40);
  const contributions = Array.from({ length: commits }, (_, i) => {
    const added = between(random, -6, 22);
    running = Math.max(1, running + added);
    return {
      id: `${classId}-${i}`,
      timestamp: new Date(Date.now() - (commits - i) * between(random, 3, 30) * 86400000).toISOString(),
      vcsIdentifier: Math.random().toString(16).slice(2, 12),
      authorId: pick(random, AUTHORS).id,
      author: pick(random, AUTHORS),
      complexity: {
        complexity: running,
        rating: running > 240 ? 'VERY_HIGH' : running > 160 ? 'HIGH' : running > 80 ? 'MEDIUM' : 'LOW',
        addedComplexity: Math.max(0, added),
      },
    };
  });

  const findings = Array.from({ length: base.totalNumberOfFindings }, (_, i) => {
    const [ruleName, ruleDescription, severity, tool] = pick(random, RULES);
    return {
      id: `${classId}-f${i}`,
      description: `${ruleName} reported at line ${between(random, 10, base.totalLinesOfCode)}.`,
      ruleName,
      ruleDescription,
      severity,
      tool,
    };
  });

  return { ...base, complexity: running, contributions, findings };
}

const PROJECTS = [
  { id: '1', name: 'petclinic', analysisStatus: 'COMPLETED', lastAnalyzed: new Date().toISOString() },
  { id: '2', name: 'billing-service', analysisStatus: 'RUNNING', lastAnalyzed: new Date(Date.now() - 86400000).toISOString() },
];

function resolve(query, variables) {
  if (query.includes('query Projects')) {
    return { projects: PROJECTS };
  }
  if (query.includes('query ProjectSnapshot')) {
    const project = PROJECTS.find((p) => p.id === String(variables.id));
    if (!project) return { projectById: null };
    return {
      projectById: {
        ...project,
        packages: PACKAGE_NAMES.map((_, i) => buildPackage(i)),
      },
    };
  }
  if (query.includes('query ClassesByPackage')) {
    const random = rng(Number(variables.packageId) * 31);
    const total = between(random, 6, 44);
    return {
      classesByPackage: Array.from({ length: total }, (_, i) =>
        buildClass(String(variables.packageId), i),
      ),
    };
  }
  if (query.includes('query ClassDetail')) {
    return { classById: buildClassDetail(variables.classId) };
  }
  return null;
}

createServer((req, res) => {
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Headers', 'content-type');

  if (req.method === 'OPTIONS') {
    res.writeHead(204).end();
    return;
  }
  if (req.method !== 'POST' || !req.url?.startsWith('/graphql')) {
    res.writeHead(404).end();
    return;
  }

  let body = '';
  req.on('data', (chunk) => (body += chunk));
  req.on('end', () => {
    try {
      const { query, variables = {} } = JSON.parse(body);
      const data = resolve(query, variables);
      res.writeHead(200, { 'content-type': 'application/json' });
      res.end(
        JSON.stringify(
          data ? { data } : { errors: [{ message: 'Unknown operation.' }] },
        ),
      );
    } catch {
      res.writeHead(400, { 'content-type': 'application/json' });
      res.end(JSON.stringify({ errors: [{ message: 'Malformed request body.' }] }));
    }
  });
}).listen(PORT, () => {
  console.log(`Mock analysis API on http://localhost:${PORT}/graphql`);
});
