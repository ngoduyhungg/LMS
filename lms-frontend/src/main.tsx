import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';

/* Import styles Tailwincss */
import './styles/index.css';
import App from './App.tsx';

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <App />
  </StrictMode>,
);
