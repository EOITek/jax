import type { Select } from 'element-ui';

import { Component, Inreactive, Prop, Ref, VueComponentBase, Watch } from '@/common/VueComponentBase';

import * as components from '../components';
import type { SharpSelectorNextDataModel, SharpSelectorNextOptionModel } from '../../../models';

/**
 * 用法：
 * <sharp-selector-next :value="data" :options="options" @change="change" />
 * 组件内置 'input' | 'select' | 'date' | 'dateRange' | 'cascader' 组件同时支持自定义拓展组件用于筛选条件的设置
 *
 * @prop value; // 筛选条件
 * @prop options; // 筛选项列表
 * @prop size; // 由于空间限制，目前仅支持 mini
 * @prop filterIcon; // 前置小图标
 * @event change(value) {}; // 筛选条件变化触发的事件
 */

@Component()
export default class SharpSelectorNext extends VueComponentBase {
  /**
   * 生成的过滤条件
   */
  @Prop() readonly value: SharpSelectorNextDataModel;
  @Prop() readonly options: SharpSelectorNextOptionModel[];
  @Prop() readonly size = 'mini';
  @Prop() readonly filterIcon = 'el-icon-search';

  @Ref() readonly Select: Select & {toggleMenu: () => void};

  @Inreactive readonly components = components;

  adding = false;     // 新增状态
  selectValue = {};   // 占位用
  searchContent = ''; // 添加筛选条件的搜索内容

  get filterOptions() {
    const text = this.searchContent && this.searchContent.startsWith('!')
      ? this.searchContent.slice(1)
      : this.searchContent;
    return text
      ? this.options.filter(x => x.name.includes(text))
      : this.options;
  }

  @Watch('adding')
  async addingChange(val: boolean) {
    if (val) {
      await this.$nextTick();
      this.Select.toggleMenu();
    }
  };

  curOption(key: string) {
    return this.options.find(x => x.key === key);
  }

  close(key: string) {
    this.$delete(this.value, key);
    this.$emit('change', this.value, key);
  }

  add(val: SharpSelectorNextOptionModel) {
    this.adding = false;
    this.$set(this.value, val.key, {
      not: this.searchContent.startsWith('!'),
      value: null,
    });
    this.searchContent = ''; // 清空搜索条件 重置选择列表
  }

  changeCondition(option: SharpSelectorNextOptionModel, key: string) {
    const not = this.value[key].not;
    this.$delete(this.value, key);
    this.$set(this.value, option.key, {
      not,
      value: null,
    });
  }

  // 搜索
  filterMethod(val: string) {
    this.searchContent = val.trim();
  }

  clear() {
    Object.keys(this.value).forEach(key => {
      this.$delete(this.value, key);
    });
    this.call('clear');
  }

  call(origin: string) {
    this.$emit('change', this.value, origin);
  }
}
