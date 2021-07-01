import { OptsApi } from '@/apis';
import { Component, VueComponentBase } from '@/common/VueComponentBase';
import { PaginationConfig } from '@/helpers';
import type { OptsModel } from '@/models';
import { $loading, $message, $modal } from '@/services';

@Component()
export default class FrameConfigMain extends VueComponentBase {
  pageConf = new PaginationConfig();
  tableAllData: OptsModel[] = [];
  loading = 0;

  created() {
    this.fetchData();
  }

  async fetchData() {
    ++this.loading;
    const { data: { entity } } = await OptsApi.getAll().finally(() => --this.loading);
    entity.forEach(ele => {
      ele.optsName = ele.sparkOptsName || ele.flinkOptsName;
    });
    this.tableAllData = entity;
    this.pageConf.totalItems = entity.length;
  }

  get tableData() {
    return this.pageConf.filterData(this.tableAllData);
  }

  async deleteFream({optsName, optsType}) {
    $modal({
      props: {
        title: '提示',
        width: 579,
      },
      component: {
        render() {
          return <p>是否确认删除{optsName}框架</p>;
        },
        methods: {
          async onOk() {
            $loading.increase();
            const { data: { retMsg } } = await OptsApi.delete({
              optsName,
              optsType,
            }).finally($loading.decrease);
            $message.success(retMsg);
          },
        },
      },
    }).then(() => {
      this.fetchData();
    });
  }
}
