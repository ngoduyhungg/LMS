import React, { useState } from 'react';
import { Layout, Grid, Drawer } from 'antd';
import { Outlet } from 'react-router-dom';
import AppSidebar from './components/AppSidebar';
import AppHeader from './components/AppHeader';

const { Content } = Layout;
const { useBreakpoint } = Grid;

const MainLayout: React.FC = () => {
  const [collapsed, setCollapsed] = useState(false);
  const [drawerVisible, setDrawerVisible] = useState(false);
  
  const screens = useBreakpoint();
  const isMobile = screens.md === false; 

  return (
    <Layout className="min-h-screen">
      {isMobile ? (
        <Drawer
          placement="left"
          closable={false}
          onClose={() => setDrawerVisible(false)}
          open={drawerVisible}
          styles={{ body: { padding: 0 } }}
          width={260}
        >
          <AppSidebar collapsed={false} isMobile={true} onClose={() => setDrawerVisible(false)} />
        </Drawer>
      ) : (
        <AppSidebar collapsed={collapsed} isMobile={false} />
      )}

      <Layout className="transition-all duration-300">
        <AppHeader
          collapsed={collapsed}
          setCollapsed={setCollapsed}
          isMobile={isMobile}
          drawerVisible={drawerVisible}
          setDrawerVisible={setDrawerVisible}
        />
        <Content className="overflow-x-hidden p-0 md:p-4">
          <div className="bg-white min-h-full rounded-none md:rounded-lg">
            <Outlet />
          </div>
        </Content>
      </Layout>
    </Layout>
  );
};

export default MainLayout;