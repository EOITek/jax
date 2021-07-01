import ResizeObserver from 'resize-observer-polyfill';

import { Component, Prop, Ref, VueComponentBase } from '../../VueComponentBase';

@Component()
export default class VCollapseText extends VueComponentBase {
  @Prop() readonly content: string;
  @Prop({ default: 2 }) readonly lineClamp: number;

  @Ref() readonly contentBox: HTMLDivElement & { _component: VCollapseText };

  static ro: ResizeObserver;

  isExceed = false;
  isWarp = true;

  get computedClass() {
    return this.isWarp ? `text-line-clamp` : 'text-wrap';
  }

  async mounted() {
    await this.$nextTick();
    this.contentBox._component = this;
    if (!VCollapseText.ro) {
      VCollapseText.ro = new ResizeObserver(function(this: void, entries: ResizeObserverEntry[]) {
        entries.forEach(entry => {
          const that = (entry.target as HTMLDivElement & { _component: VCollapseText })._component;
          if (that.isWarp) that.isExceed = entry.contentRect.height < entry.target.scrollHeight;
        });
      });
    }
    VCollapseText.ro.observe(this.contentBox);
  }

  beforeDestroy() {
    VCollapseText.ro?.unobserve(this.contentBox);
  }

  collapseChange() {
    if (!this.isExceed) return;
    this.isWarp = !this.isWarp;
  }
}
