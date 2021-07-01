import type { Select } from 'element-ui';
import { cloneDeep } from 'lodash-es';

import { Component, Prop, Ref, VueComponentBase } from '@/common/VueComponentBase';

import type { SharpSelectorNextDataValModel, SharpSelectorNextSubOptionModel } from '../../../../models';

@Component()
export default class VCascader extends VueComponentBase {
  @Prop() readonly item: SharpSelectorNextDataValModel<(string | number | boolean)[]>;
  @Prop() readonly options: SharpSelectorNextSubOptionModel[];
  @Prop() readonly size: string;

  @Ref() readonly Select: Select & {toggleMenu: () => void};
  @Ref() readonly subSelect: Select & {toggleMenu: () => void};

  editing = false;
  isChanging = false;

  get displayText() {
    if (!this.item.value || !this.item.value.length || !this.options || !this.options.length) return [];

    const option = this.options.find(x => x.value === this.item.value[0]);
    if (!option) return [];

    const displayText = [];
    const depthChildren = (opt: SharpSelectorNextSubOptionModel, depth: number) => {
      let data = opt;
      for (let i = 0; i < depth; i++) {
        data = data.children.find(x => x.value === this.item.value[i + 1]);
      }
      return data;
    };
    this.item.value.forEach((x, depth) => {
      if (depthChildren(option, depth)) {
        const brother: SharpSelectorNextSubOptionModel[] = !depth
          ? cloneDeep(this.options)
          : displayText[depth - 1].children;
        displayText.push({
          label: depthChildren(option, depth).label,
          brother, // 当前的兄弟为其父级的children
          children: depthChildren(option, depth).children || undefined,
        });
      } else {
        this.item.value.length = depth;
        return;
      }
    });
    return displayText;
  }

  mounted() {
    if (this.Select) this.Select.toggleMenu();
  }

  // 初次选择
  change(val: any) {
    this.item.value = [val];
    this.subOption();
  }

  // 修改已经设置好的值
  changeValue(item: SharpSelectorNextSubOptionModel, depth: number) {
    // 选在原来的选项
    if (item.value === this.item.value[depth]) return;

    // 需要替换的长度， 因为如果祖先级发生改变，其后代需要清空
    const replaceLen = this.item.value.length - depth;
    this.item.value.splice(depth, replaceLen, item.value);
    this.subOption();
  }

  // 选择下一级
  async selectNext(child: SharpSelectorNextSubOptionModel) {
    this.isChanging = true;
    this.item.value.push(child.value);
    await this.$nextTick();
    this.subOption();
  }

  async subOption() {
    this.isChanging = false;
    if (this.displayText.lastItem.children) {
      this.editing = true;
      await this.$nextTick();
      this.subSelect.toggleMenu();
    } else {
      this.$emit('call');
    }
  }

  async editSubOpt() {
    this.editing = true;
    await this.$nextTick();
    this.subSelect.toggleMenu();
  }

  visibleChange(val: boolean) {
    if (!val) {
      this.editing = false;
      if (!this.isChanging) this.$emit('call');
    }
  }
}
