import Vue from 'vue';

import { InputJson } from './input-json';

export * from '@/common/components';

// 引入ace语法
import 'ace-builds/src-noconflict/mode-javascript';
import 'ace-builds/src-noconflict/mode-sql';

Object.entries({
  InputJson,
  // 第三方库
}).forEach(([name, component]) => Vue.component(name, component));
