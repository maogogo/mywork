import React from 'react';
import { Router, Route } from 'dva/router';
import App from './routes/App';


const Routers = () => {

	const routes = [{
		path: '/',
		component: App
	}]

	return (
		<Router routes={routes} />
	)

}

export default Routers;
