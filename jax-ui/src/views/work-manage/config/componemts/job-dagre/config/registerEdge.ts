import G6 from '@antv/g6';

import strawActive from '@/assets/images/straw-active.svg';
import straw from '@/assets/images/straw.svg';

G6.registerEdge(
  'circle-running',
  {
    getPath(points) {
      const path = [];
      path.push(['M', points[0].x, points[0].y]);
      path.push([
        'C',
        points[1].x - 30,
        points[1].y + 30,
        points[3].x,
        points[3].y - 30,
        points[3].x,
        points[3].y,
      ]);
      return path;
    },
    afterDraw(cfg: any, group) {
      const enableMock = cfg?.conf?.enableMock;
      const { isDebugModel, isStreaming } = cfg;
      if (!isDebugModel) return;

      // 获取图形组中的第一个图形，在这里就是边的路径图形
      const shape = group.get('children')[0];
      const startPoint = shape.getPoint(0);
      const midPoint = shape.getPoint(0.5);
      group.addShape('image', {
        attrs: {
          x: midPoint.x - 10,
          y: midPoint.y - 10,
          width: 20,
          height: 20,
          cursor: 'pointer',
          img: enableMock ? strawActive : straw,
        },
        visible: isStreaming,
        draggable: true,
        name: 'image-straw',
      });

      group.addShape('circle', {
        attrs: {
          x: startPoint.x,
          y: startPoint.y,
          fill: '#1890ff',
          r: 3,
        },
        visible: false,
        name: `circle-shape`,
      });

    },
    // 复写setState方法
    setState(name, value: string | boolean, item) {
      const group = item.getContainer();
      const shapeList = group.get('children');
      const shape = shapeList[0];
      const shape1 = shapeList[1];
      const shape2 = shapeList[2];

      // 监听 running 状态
      if (name === 'running') {
        // running 状态为 true 时
        if (value) {
          shape2.show();
          shape2.animate(
            ratio => {
              const tmpPoint = shape.getPoint(ratio) || { x: 0, y: 0 };
              return {
                x: tmpPoint.x,
                y: tmpPoint.y,
              };
            },
            {
              repeat: true,
              duration: 6000 - (1500 * +value),
              easing: 'easeQuadInOut',
            },
          );
        } else {
          // 结束动画
          shape2.stopAnimate();
          shape2.hide();
        }
      }

      // 监听选中状态
      if (name === 'selected') {
        if (value) {
          shape.attr({
            stroke: '#39ca74',
            lineWidth: 2,
            endArrow: {
              path: 'M 0,0 L 8,4 L 8,-4 Z',
              fill: '#39ca74',
            },
          });
        } else {
          shape.attr({
            stroke: '#636363',
            lineWidth: 1,
            endArrow: {
              path: 'M 0,0 L 8,4 L 8,-4 Z',
              fill: '#636363',
            },
          });
        }
      }

      // 监听滴管选中状态
      if (name === 'strawActive') {
        shape1?.attr({
          img: value ? strawActive : straw,
        });
      }
    },
  },
  'cubic',
); // 该自定义边继承内置三阶贝塞尔曲线 cubic
