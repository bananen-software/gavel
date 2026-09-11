import type { CodegenConfig } from '@graphql-codegen/cli';

/**
 * Optional. `src/app/api/schema.types.ts` is hand-written so the project builds
 * without a codegen step, but the shapes match what this config emits — point
 * `schema` at your backend and swap the file when you want it generated.
 *
 *   npm i -D @graphql-codegen/cli @graphql-codegen/typescript
 *   npx graphql-codegen
 */
const config: CodegenConfig = {
  schema: './schema.graphqls',
  generates: {
    'src/app/api/schema.types.ts': {
      plugins: ['typescript'],
      config: {
        skipTypename: true,
        enumsAsTypes: true,
        scalars: { ID: 'string' },
        maybeValue: 'T | null',
      },
    },
  },
};

export default config;
