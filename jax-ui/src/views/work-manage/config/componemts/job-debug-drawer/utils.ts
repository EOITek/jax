/**
 * 让滚动条到元素的最底部
 *
 * @param element 要控制的元素
 */
export const scrollToBottom = function(element: HTMLElement) {
  element.scrollTop = element.scrollHeight;
};

/**
 * 获取不同数据量下的动画档位
 *
 * @param dataNum 数据量
 */
export const getAnimationGears = function(dataNum) {
  const maxNum = 50;
  if (dataNum <= Math.ceil(maxNum * 0.33)) return 1;
  if (dataNum <= Math.ceil(maxNum * 0.66)) return 2;
  if (dataNum <= maxNum) return 3;
};
