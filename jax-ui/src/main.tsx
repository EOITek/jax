import 'core-js';
import Vue from 'vue';
import 'font-awesome/scss/font-awesome.scss';

import '@/app';
import '@/components';
import '@/directives';

import { Component } from '@/common/VueComponentBase';
import { $router, _updateVueInstance } from '@/services';

Component.registerHooks([
  'beforeRouteEnter',
  'beforeRouteUpdate',
  'beforeRouteLeave',
]);

import ElementUI from 'element-ui';

Vue.use(ElementUI, { size: 'small' });

import VueVirtualScroller from 'vue-virtual-scroller';
import 'vue-virtual-scroller/dist/vue-virtual-scroller.css';

Vue.use(VueVirtualScroller);

Vue.config.productionTip = false;

function initVue(entity = null) {
  const data = {
    loading: {
      status: 0,
      text: '',
    },
    modals: [],
    drawers: [],
    tooltips: [],
    userInfo: entity,
  };
  _updateVueInstance(data as any);
  new Vue({
    data,
    router: $router,
    render() {
      return (
        <div id="app">
          <vue-progressbar ref="progressbar" />
          <router-view />
          {this.modals.map(attrs => <sharp-modal {...{ attrs }} />)}
          {this.drawers.map(attrs => <sharp-drawer {...{ attrs }} />)}
          {this.tooltips.map(attrs => <sharp-tooltip  {...{ attrs }} />)}
          {this.loading.status > 0 ? <v-loading /> : null}
        </div>
      );
    },
    created() {
      _updateVueInstance(this);
    },
  }).$mount('#app');
}

try {
  initVue();
} catch(e) {
  initVue();
}
