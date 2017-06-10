import React, { PropTypes } from 'react';
import { connect } from 'dva';
import { Layout, Menu, Icon, Button } from 'antd';

const { Header, Sider, Content } = Layout;

const App = ({state}) => {

	
	function handleOk() {
		//alert(11)
console.log(state)
		//app.tt = !app.tt
		//console.log(app)
	}

	return (
		<Layout style={{ height: '100vh' }}>
        <Sider
          trigger={null}
          collapsible
          collapsed={false}
        >
          <Menu theme="dark" mode="inline" defaultSelectedKeys={['1']}>
            <Menu.Item key="1">
              <Icon type="user" />
              <span className="nav-text">nav 1</span>
            </Menu.Item>
            <Menu.Item key="2">
              <Icon type="video-camera" />
              <span className="nav-text">nav 2</span>
            </Menu.Item>
            <Menu.Item key="3">
              <Icon type="upload" />
              <span className="nav-text">nav 3</span>
            </Menu.Item>
          </Menu>
        </Sider>
        <Layout>
          <Header style={{ background: '#fff', padding: 0 }}>
            <Icon
              className="trigger"
              
            />
          </Header>
          <Content style={{ margin: '24px 16px', padding: 24, background: '#fff', minHeight: 280 }}>
            <Button type="primary" onClick={handleOk}>sfefe</Button>
          </Content>
        </Layout>
      </Layout>
	)
}

function mapStateToProps({ state }) {
  return {state};
}

export default connect(mapStateToProps)(App);