import js from '@eslint/js';
import tseslint from 'typescript-eslint';
import reactHooks from 'eslint-plugin-react-hooks';

export default tseslint.config(
  { ignores: ['dist'] },

  {
    extends: [
      js.configs.recommended,
      ...tseslint.configs.recommended,
    ],

    files: ['**/*.{ts,tsx}'],

    rules: {
      ...reactHooks.configs.recommended.rules,

      // TS cơ bản
      '@typescript-eslint/no-unused-vars': 'warn', // Cảnh báo về biến không sử dụng
    },
  }
);