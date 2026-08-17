# 📋 Yoedu – Project Summary

## Overview

**Yoedu** is an education management web application (LMS-style) built with React + TypeScript. It provides an admin-facing dashboard to manage students, teachers, courses, and enrollments, backed by a REST API.

---

## 🛠️ Tech Stack

| Category | Technology |
|---|---|
| **Framework** | React 19 |
| **Language** | TypeScript 6 |
| **Build Tool** | Vite 8 |
| **Routing** | React Router DOM v7 |
| **State Management** | Redux Toolkit + React Redux |
| **UI Library** | Ant Design (antd v6) |
| **Styling** | Tailwind CSS v4 (via `@tailwindcss/vite` plugin) |
| **HTTP Client** | Axios |
| **Linting** | ESLint 9 (TypeScript + React Hooks rules) |
| **Formatting** | Prettier |
| **React Query** | 

### Dev Environment

- **Local API**: `http://localhost:3000`
- **Production API**: `https://yoedu-be.onrender.com`
- **Dev Server**: `http://localhost:5173` (`npm run dev`)
- **Path Alias**: `@/` → `src/` (configured in both Vite and `tsconfig`)

---

## 📁 Folder Structure

```
src/
├── App.tsx               # Root: wires up Redux, Theme, Antd, Router
├── main.tsx              # Entry point
├── styles/
│   └── index.css         # Global styles
│
├── app/                  # App-level config (infrastructure layer)
│   ├── init/             # AppInit – bootstraps app on load
│   ├── layouts/
│   │   ├── MainLayout.tsx   # Authenticated shell (sidebar + content)
│   │   └── AuthLayout.tsx   # Public shell (login/register pages)
│   ├── providers/
│   │   ├── antd/         # Ant Design ConfigProvider
│   │   └── theme/        # Custom theme provider (dark/light mode)
│   ├── redux/
│   │   ├── store.ts      # Redux store (currently: auth slice)
│   │   └── hooks.ts      # Typed useAppDispatch / useAppSelector
│   └── router/
│       ├── Routes.tsx        # createBrowserRouter route tree
│       └── ProtectedRoute.tsx  # Auth guard (requireAuth flag)
│
├── features/             # Business domain modules (Feature-Sliced)
│   ├── auth/             # Login, Register, auth slice/thunk
│   ├── dashboard/        # Dashboard overview page
│   ├── students/         # Student CRUD
│   ├── teachers/         # Teacher CRUD
│   ├── courses/          # Course management
│   ├── enrollments/      # Student-course enrollment management
│   ├── users/            # User profile page
│   └── upload/           # File upload feature
│
└── shared/               # Reusable, cross-feature code
    ├── components/       # Generic UI wrappers (12 categories)
    │   ├── avatar/
    │   ├── card/
    │   ├── datepicker/
    │   ├── empty/
    │   ├── input/
    │   ├── modal/
    │   ├── page/
    │   ├── row/
    │   ├── select/
    │   ├── status/
    │   ├── table/
    │   └── upload/
    ├── hooks/
    │   ├── useFormModal.ts     # Reusable form-in-modal state
    │   ├── useNotification.ts  # Ant Design notification helper
    │   └── useTable.ts         # Table pagination/filter/sort logic
    ├── lib/
    │   └── axios.ts      # Axios instance with auth interceptors
    ├── constants/        # App-wide constants
    ├── theme/            # Theme tokens / variables
    ├── types/            # Shared TypeScript types
    │   ├── filter-params.type.ts
    │   ├── form-field.type.ts
    │   ├── form-modal-mode.type.ts
    │   ├── gender.type.ts
    │   ├── http-status.ts
    │   └── status.type.ts
    └── utils/            # Utility/helper functions
```

---

## 🔑 Key Architecture Patterns

### Feature-Sliced Design (FSD-inspired)

Each feature module under `features/` has a consistent internal structure:

```
features/<name>/
  ├── api/        # API call functions (axios)
  ├── constants/  # Feature-specific constants
  ├── pages/      # Page-level components (route targets)
  ├── store/      # Redux slice + thunks (auth only currently)
  ├── types/      # Feature-specific TypeScript types
  └── components/ # Feature-local UI components
```

### Authentication Flow

- JWT-based: access token stored in `localStorage`
- `axiosClient` auto-attaches `Authorization: Bearer <token>` via request interceptor
- On `401 Unauthorized`, clears token and redirects to `/auth/login`
- `ProtectedRoute` guards routes: `requireAuth={false}` for public, default `true` for private

### Routing Structure

```
/auth/login       → LoginPage      (public,    AuthLayout)
/auth/register    → RegisterPage   (public,    AuthLayout)
/                 → DashboardPage  (protected, MainLayout)
/profile          → UserProfilePage
/students         → StudentPage
/teachers         → TeacherPage
/courses          → CoursePage
/enrollments      → EnrollmentPage
```

### State Management

- Redux store currently holds one slice: **`auth`**
- Typed hooks (`useAppDispatch`, `useAppSelector`) in `app/redux/hooks.ts`
- Async auth actions handled via Redux Thunk (`auth-thunk.ts`)

---

## 📐 Code Quality & Conventions

### Naming Conventions

| Case | Usage |
|---|---|
| `UpperCamelCase` | Classes, Interfaces, Types, Enums, React Components |
| `lowerCamelCase` | Variables, Functions, Hooks, Properties |
| `CONSTANT_CASE` | Global static constants |

### ESLint Rules

- `@typescript-eslint/no-unused-vars: warn`
- `react-hooks/rules-of-hooks` + `react-hooks/exhaustive-deps` enforced

### Prettier Config

- Single quotes, semicolons, 100-char line width, trailing commas, 2-space indent

---

## 🚀 NPM Scripts

| Script | Command | Description |
|---|---|---|
| `dev` | `vite` | Start dev server at :5173 |
| `build` | `tsc -b && vite build` | Type-check + production build |
| `lint` | `eslint .` | Run ESLint on all files |
| `preview` | `vite preview` | Preview production build |
