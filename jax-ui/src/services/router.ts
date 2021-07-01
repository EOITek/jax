import Vue from 'vue';
import Router, { Route, RouteConfig } from 'vue-router';

import * as views from '@/views';

Vue.use(Router);

const routes: RouteConfig[] = [
  {
    name: 'home',
    path: '/',
    redirect: { name: 'work-manage' },
    component: views.HomeMain,
    children: [
      {
        name: 'job-repo',
        path: 'job-repo',
        component: views.JobRepo,
        redirect: { name: 'job-repo/list' },
        children: [
          {
            name: 'job-repo/list',
            path: 'list',
            component: views.JobRepoList,
          },
          {
            name: 'job-repo/jars',
            path: 'jars',
            component: views.JobRepoJars,
          },
        ],
      },
      {
        name: 'work-manage',
        path: 'work-manage',
        component: views.WorkManageMain,
        redirect: { name: 'work-manage/list', params: { pipelineType: 'streaming' } },
        children: [
          {
            name: 'work-manage/list',
            path: 'list/:pipelineType',
            component: views.WorkManageList,
            props: ({ query, params }) => ({ ...query, ...params }),
          },
        ],
      },
      {
        name: 'WorkManageLogDetail',
        path: 'log-dedail/:name',
        component: views.WorkManageLogDetail,
        props: ({ query, params }) => ({ ...query, ...params }),
      },
      {
        name: 'work-manage/config',
        path: 'work-manage/config/:id',
        component: views.WorkManageConfig,
        props: ({ params, query }) => ({ ...params, ...{ ...query, clone: !!(+query.clone) } }),
      },
      {
        name: 'WorkManageFinkDiagnose',
        path: '/work-manage/fink-diagnose',
        component: views.FinkDiagnose,
        props: ({ query }) => query,
      },
      {
        path: 'system-manage',
        name: 'system-manage',
        component: views.SystemManageMain,
        redirect: '/system-manage/cluster-manage',
        children: [
          {
            path: 'cluster-manage',
            name: 'clusterManage',
            component: views.ClusterManageMain,
          },
          {
            path: 'frame-config',
            name: 'frameConfig',
            component: views.FrameConfigMain,
          },
        ],
      },
      {
        path: 'system-manage/frame-config/created-edit',
        name: 'frameConfigCreatedEdit',
        props: ({ query }) => ({ ...query, view: +query?.view, clone: +query?.clone }),
        component: views.FrameConfigCreatedEdit,
      },
    ],
  },
];

export let $route: Route;

export const $router = new Router({
  mode: 'history',
  base: process.env.BASE_URL,
  routes,
});

$router.afterEach(to => {
  document.body.dataset.page = to.name;
  $route = to;
});
