'use strict';

const commonConfig = require('./src/common/configureWebpack');
const { serviceName } = require('./package.json');

const program = require('commander');
program.option('--proxy-env <proxyEnv>', 'Api environment to connect (defaults to dev)', null, 'dev');
program.parse(process.argv);

const envMap = {
  dev: 'localhost:9999',
  test: '',
};

module.exports = {
  ...commonConfig,
  devServer: {
    port: 8083,
    proxy: {
      [serviceName ? `/${serviceName}/api` : '/api']: {
        target: `http://${envMap[program.proxyEnv] || program.proxyEnv}`,
        logLevel: 'debug',
        onProxyRes(proxyRes) {
          proxyRes.headers['Cache-Control'] = 'no-cache, no-store, must-revalidate';
        },
        pathRewrite: { [`^/${serviceName}`]: '' },
        onProxyReq(onProxyReq) {
        }
      },
      [serviceName ? `/${serviceName}/ws` : '/ws']: {
        target: `ws://${envMap[program.proxyEnv] || program.proxyEnv}`,
        logLevel: 'debug',
        pathRewrite: { [`^/${serviceName}`]: '' },
        ws: true,
        secure: false,
        onProxyRes(proxyRes) {
          proxyRes.headers['Cache-Control'] = 'no-cache, no-store, must-revalidate';
        }
      },
    },
  },
};
