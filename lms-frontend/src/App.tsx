import { Provider } from 'react-redux';
import { store } from './app/redux/store';

import ThemeProvider from './app/providers/theme/ThemeProvider';
import AntdProvider from './app/providers/antd/AntdProvider';

import { RouterProvider } from 'react-router-dom';
import { router } from './app/router/routes';
import AppInit from './app/init/AppInit';
import { ReactQueryProvider } from './app/providers/react-query/ReactQueryProvider';

function App() {
  return (
    <Provider store={store}>
      <ReactQueryProvider>
        <ThemeProvider>
          <AntdProvider>
            <AppInit>
              <RouterProvider router={router} />
            </AppInit>
          </AntdProvider>
        </ThemeProvider>
      </ReactQueryProvider>
    </Provider>
  );
}

export default App;
