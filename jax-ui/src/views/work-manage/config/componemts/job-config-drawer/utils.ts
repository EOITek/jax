import { get, cloneDeep, isEqual, isObject } from 'lodash-es';

import type { PipelineParameterModel } from '@/models';

interface FormRules {
  regex: any;
  optional: boolean;
  range: string;
}

function isQualifiedListData(item: PipelineParameterModel) {
  return !(Array.isArray(item.value) && item.listParameter);
}
/**
 * 根据数据递归得到所需对象
 *
 * @param sourceData 元数据
 * @param isFilterEmpty 是否过滤无效的空数据
 * @param type 采用哪个字段作为key值
 */
export const getJobParametersFormData = function(sourceData, defaultObj, isFilterEmpty = false, type = 'name') {
  const emptyArr = [undefined, null, ''];

  function deleteKey(item: PipelineParameterModel, prevFormData, fieldType: string) {
    if (!isEqual(item.defaultFormValue, prevFormData[fieldType])) delete prevFormData[fieldType];
  }

  function deleteListKey(item: PipelineParameterModel, prevFormData) {
    if (isFilterEmpty && Array.isArray(prevFormData[item[type]]) && !prevFormData[item[type]].length) {
      deleteKey(item, prevFormData, item[type]);
    }
  }

  /**
   * 根据数据递归得到所需对象
   *
   * @param item 处理的每一项值
   * @param prev 上一次处理的结果
   */
  function getItemObject(item: PipelineParameterModel, prevFormData) {
    switch (item.type[0]) {
      case 'OBJECT':
        prevFormData ??= {};
        const name = item[type] ? item[type] : prevFormData.length;
        prevFormData[name] ??= {};
        item.objectParameters.forEach(ele => getItemObject(ele, prevFormData[name]));
        if (isFilterEmpty && !Object.keys(prevFormData[name]).length) {
          deleteKey(item, prevFormData, name);
        }
        break;
      case 'LIST':
        // 兼容v1的数据，处理值不是数组的异常情况
        if (item.value && isQualifiedListData(item)) {
          prevFormData[item[type]] = item.value;
          deleteListKey(item, prevFormData);
          break;
        }
        prevFormData ??= [];
        prevFormData[item[type]] = [];
        item.viewDataList.forEach(ele => {
          getItemObject(ele as unknown as PipelineParameterModel, prevFormData[item[type]]);
        });
        deleteListKey(item, prevFormData);
        break;
      case 'MAP':
        prevFormData ??= {};
        prevFormData[item[type]] = Object.fromEntries(item.viewDataMap.map(x => [x.key, x.value]));
        if (isFilterEmpty && !Object.keys(prevFormData[item[type]]).length) {
          deleteKey(item, prevFormData, item[type]);
        }
        break;
      default:
        item[type] ? prevFormData[item[type]] = item.value : prevFormData.push(item.value);
        break;
    }

    // 清洗无效数据
    const typeList = ['OBJECT', 'LIST', 'MAP'];
    if (isFilterEmpty && emptyArr.includes(prevFormData[item[type]]) && !typeList.includes(item.type[0])) {
      delete prevFormData[item[type]];
    }
  }

  const formData = cloneDeep(defaultObj);
  sourceData.forEach(parameter => {
    getItemObject(parameter, formData);
  });
  return formData;
};

/**
 * 处理 parameters 数据 给每一层添加value、prop属性，得到用于视图层展示的数据
 *
 * @param sourceData 源数据
 * @param defaultObj 数据的默认值
 * @param prevProp 初始prop值
 */
export const disposeParameters = function(sourceData, defaultObj, prevProp = '', tier = 0) {
  /**
   * 将字段进行排序
   */
  function sourceDataSort(data) {
    const startData = data.filter(ele => !ele.optional).sort((a,b) => a.label.localeCompare(b.label));
    const endData = data.filter(ele => ele.optional).sort((a,b) => a.label.localeCompare(b.label));
    return [...startData, ...endData];
  }

  /**
   * 处理defaultObj 数据
   */
  function disposeDefaultObj(data) {
    const obj = {};
    Object.keys(data).forEach(ele => {
      if (isObject(data[ele])) {
        obj[ele.replace(/\./g, '_')] = disposeDefaultObj(data[ele]);
      }
      obj[ele.replace(/\./g, '_')] = data[ele];
    });
    return obj;
  }

  const newDefaultObj = disposeDefaultObj(defaultObj);

  /**
   * 处理表单验证规则
   *
   * @param param0  { regex: 检验规则, optional: 是否必填 }
   */
  function disposeFormRules({ regex, optional, range }) {
    const dataTypes = [null, '', undefined];
    return {
      trigger: 'blur',
      validator: (rule, value) => {
        if (!optional && dataTypes.includes(value)) {
          return new Error('请填写必填项');
        }

        if (regex && !dataTypes.includes(value) && !new RegExp(regex.slice(1, -1)).test(value)) {
          return new Error('输入错误，请查看规则重新输入');
        }

        if (!(range && !dataTypes.includes(value))) return true;

        const rangeArr = range.split(',');
        if (rangeArr[0].length !== 1) {
          const num = rangeArr[0].slice(1);
          if (rangeArr[0].startsWith('(')) {
            if (value <= num) return new Error(`请输入大于${num}的值`);
          } else {
            if (value < num) return new Error(`请输入小于等于${num}的值`);
          }
        } else if(rangeArr[1].length !== 1) {
          const num = rangeArr[1].slice(0, -1);
          if (rangeArr[1].startsWith('(')) {
            if (value >= num) return new Error(`请输入小于${num}的值`);
          } else {
            if (value > num) return new Error(`请输入小于等于${num}的值`);
          }
        }
        return true;
      },
    };
  };

  /**
   * 处理 OBJECT 类型的数据
   *
   * @param objectItem
   */
  function disposeObject(objectItem) {
    objectItem.objectParameters = sourceDataSort(objectItem.objectParameters);
    objectItem.objectParameters.forEach(ele => {
      ele.tier = objectItem.tier;
      if (['OBJECT', 'LIST', 'MAP'].includes(ele.type[0])) {
        ele.tier += 1;
        // eslint-disable-next-line @typescript-eslint/no-use-before-define
        getJobDataItem(ele, objectItem.prop, ele.tier);
      }
      const formProp = ele.name.replace(/\./g, '_');
      ele.formProp = formProp;
      ele.prop = objectItem.prop + `.${ele.name.replace(/\./g, '_')}`;
      ele.prevProp = objectItem.prevProp;
      ele.value = get(newDefaultObj, ele.prop) ?? null;

      ele.defaultFormValue = get(newDefaultObj, ele.prop) ?? null;
      ele.formRule = disposeFormRules(ele as FormRules);
    });
  };

  /**
   * 处理 LIST 类型的数据
   *
   * @param listItem
   */
  function disposeList(listItem) {
    listItem.value ??= [];
    // 兼容v1的数据，处理值不是数组的异常情况
    if (isQualifiedListData(listItem)) {
      listItem.viewDataList = [];
      return;
    }

    const arr = [];
    listItem.value.forEach(() => {
      arr.push(cloneDeep(listItem.listParameter));
    });
    arr.forEach((ele, index) => {
      const prop = listItem.prop + `[${index}]`;
      ele.prop = prop;
      ele.prevProp = listItem.prop;
      ele.tier = listItem.tier;
      ele.formProp = index;
      ele.value = get(newDefaultObj, ele.prop) ?? null;
      ele.defaultFormValue = get(newDefaultObj, ele.prop) ?? null;
      ele.formRule = disposeFormRules(ele as FormRules);
      if (['OBJECT', 'MAP'].includes(ele.type[0])) {
        ele.tier += 1;
        disposeObject(ele);
      }
    });
    listItem.viewDataList = arr;
  };

  /**
   * 递归处理数据中的每一项
   *
   * @param item
   * @param prev
   */
  function getJobDataItem(item: PipelineParameterModel, prev, prevTier = 0) {
    const formProp = item.name.replace(/\./g, '_');
    item.prevProp = prev;
    prev += prev ? `.${formProp}` : formProp;
    item.prop = prev;
    item.tier = prevTier;
    item.formProp = formProp;
    item.value = get(newDefaultObj, item.prop) ?? null;
    item.defaultFormValue = get(newDefaultObj, item.prop) ?? null;
    item.formRule = disposeFormRules(item as FormRules);
    if (['OBJECT', 'LIST', 'MAP'].includes(item.type[0])) {
      item.tier = prevTier + 1;
      if (item.tier >= 3) item.tier = 0;
    }
    if (item.type[0] === 'OBJECT') disposeObject(item);
    if (item.type[0] === 'LIST') disposeList(item);
    if (item.type[0] === 'MAP') {
      item.viewDataMap = Object.entries(item.value ?? {}).map(x => ({ key: x[0], value: x[1] }));
    }
  };

  sourceData = sourceDataSort(sourceData);

  sourceData.forEach(parameter => {
    getJobDataItem(parameter, prevProp, tier);
  });
  return sourceData;
};
