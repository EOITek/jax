import { Component, Prop, VueComponentBase } from '@/common/VueComponentBase';
import tableActive from '@/assets/images/table-active.svg';
import table from '@/assets/images/table.svg';
import json from '@/assets/images/json.svg';
import jsonActive from '@/assets/images/json-active.svg';

import { IconType } from '../../../../constants';

@Component()
export default class DataShowType extends VueComponentBase {
  @Prop() readonly defaultType: number;

  currentType: number = null;
  iconList = [
    {
      icon: json,
      iconActive: jsonActive,
      type: IconType.json,
    },
    {
      icon: table,
      iconActive: tableActive,
      type: IconType.table,
    },
    // TODO 暂时去掉，这个版本先不做
    // {
    //   className: 'icon-tubiao',
    //   type: IconType.chart,
    // },
  ];

  created() {
    this.defaultType ?? (this.currentType = this.defaultType);
    this.currentType = this.defaultType;
  }

  handlerClick(type: number) {
    this.currentType = type;
    this.$emit('change', this.currentType);
  }
}
