import G6 from '@antv/g6';
import { merge } from 'lodash-es';

import { JobDefaultIcon, JobRoleType } from '@/constants';
import type { JobDetailModel } from '@/models';
import { truncateText } from '@/helpers';
import debug from '@/assets/images/debug.svg';
import debugActive from '@/assets/images/debug-active.svg';

import { AnchorTypes } from './utils';

const width = 240;
const height = 44;

G6.registerNode('job', {
  draw(cfg: any, group) {
    const jobDetail = cfg as unknown as JobDetailModel;
    const titleText = truncateText(jobDetail.display, 80, 18);
    const { enableDebug } = cfg.conf.jobOpts || {};
    const { isDebugModel } = cfg;
    // 添加一个包围盒用来响应事件
    group.addShape('rect', {
      attrs: {
        width: width + 10,
        height: height + 10,
        x: -5,
        y: -5,
        radius: 2,
        fill: 'transparent',
      },
      capture: true,
      name: 'node-rect-box',
    });
    const keyShape = group.addShape('rect', {
      attrs: {
        width,
        height,
        radius: 2,
        fill: '#ffffff',
        stroke: '#D4D8D9',
        cursor: 'move',
        shadowBlur: 8,
        shadowOffsetX: 1,
        shadowOffsetY: 1,
      },
      capture: true,
      name: 'node-rect',
    });

    group.addShape('rect', {
      attrs: {
        width: 5,
        height,
        radius: 2,
        fill: JobRoleType[cfg.conf.jobViewData.jobRole].bgColor,
        stroke: '#D4D8D9',
        cursor: 'move',
      },
      draggable: true,
      name: 'node-type',
    });

    group.addShape('text', {
      attrs: {
        text: titleText,
        x: 55,
        y: 14,
        fontSize: 16,
        textBaseline: 'top',
        fill: '#000',
        cursor: 'move',
      },
      draggable: true,
      name: 'node-title',
    });

    group.addShape('image', {
      attrs: {
        x: 15,
        y: 12,
        width: 20,
        height: 20,
        cursor: 'move',
        img: cfg.conf.jobViewData.iconUrl || JobDefaultIcon[cfg.conf.jobViewData.jobRole],
      },
      draggable: true,
      name: 'image-shape',
    });

    if (isDebugModel && enableDebug !== undefined ) {
      group.addShape('image', {
        attrs: {
          x: 210,
          y: 12,
          width: 20,
          height: 20,
          cursor: 'pointer',
          img: enableDebug ? debugActive : debug,
        },
        draggable: true,
        name: 'image-debug',
      });
    }

    if (cfg.anchorPointsData.length) {
      cfg.anchorPointsData.forEach((points, index) => {
        const { x, y, model } = points;
        group.addShape('circle', {
          attrs: {
            x: width * x,
            y: height * y,
            r: 20,
            cursor: y ? 'move' : 'crosshair',
            fill: 'transparent',
          },
          visible: false,
          draggable: true,
          capture: true,
          name: `node-anchor-box${index}`,
          anchorType: y ? AnchorTypes.output : AnchorTypes.input,
          anchorPointsId: index,
          inTypesNum: cfg.inTypesNum,
          model,
        });
        group.addShape('circle', {
          attrs: {
            x: width * x,
            y: height * y,
            r: 5,
            cursor: 'crosshair',
            fill: '#fff',
            stroke: y ? 'rgb(30, 84, 214)' : 'rgb(6, 221, 53)',
            lineWidth: 1,
          },
          visible: false,
          draggable: true,
          capture: true,
          name: `node-anchor${index}`,
          anchorType: y ? AnchorTypes.output : AnchorTypes.input,
          anchorPointsId: index,
          inTypesNum: cfg.inTypesNum,
          model,
        });
      });
    };
    return keyShape;
  },
  update(cfg, node) {
    const group = node.getContainer(); // 获取容器
    const shape = group.get('children');
    shape.forEach(nodeShape => {
      if (cfg?.[nodeShape.get('name')]){
        merge(nodeShape, cfg?.[nodeShape.get('name')]);
      };
    });
  },
  // 响应状态变化
  setState(name, value, item) {
    const group = item.getContainer();
    const { jobViewData: { jobRole } } = item.getModel().conf as any;
    const shapeList = group.get('children'); // 顺序根据 draw 时确定
    const shape = shapeList[1];
    if (name === 'debugActive' ) {
      const currentNode = shapeList.find(node => node.get('name') === 'image-debug');
      if (value) {
        currentNode?.attr({
          img: debugActive,
        });
      } else {
        currentNode?.attr({
          img: debug,
        });
      }
    }
    if (name === 'selected') {
      if (value) {
        shape.attr({
          stroke: JobRoleType[jobRole].bgColor,
          lineWidth: 2,
        });
      } else {
        shape.attr({
          stroke: '#D4D8D9',
          lineWidth: 1,
        });
      }
    }
    if (name === 'hover') {
      if (value) {
        shape.attr({ shadowColor: 'rgba(76, 89, 110, 0.3)' });
      } else {
        shape.attr({ shadowColor: '' });
      }
    }
    if (name === 'anchorHover') {
      if (value !== '-1') {

        const currentNode = shapeList.filter(node => node.get('anchorPointsId') === +value
          && node.get('name').startsWith('node-anchor'))[1];

        if (currentNode.get('anchorType') !== AnchorTypes.input) return;

        currentNode.animate(
          {
            r: 8,
            lineWidth: 5,
            opacity: 0.5,
          },
          {
            duration: 1000,
            easing: 'easePolyInOut',
            repeat: true, // repeat
          },
        );
      } else {
        shapeList.filter(node => node.get('name').startsWith('node-anchor')
          && node.get('anchorType') === AnchorTypes.input)
          .forEach(node => {
            node.attr({ r: 5 });
            node.stopAnimate();
          });
      }
    }
  },
}, 'single-node');
