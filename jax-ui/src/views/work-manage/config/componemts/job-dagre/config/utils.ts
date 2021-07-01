
export const AnchorTypes = {
  input: 0,
  output: 1,
};

/**
 * 随机生成新id
 *
 * @param name 节点名称
 */
export const setNewId = function(name = '') {
  return name + Date.now() + Math.floor(Math.random() * 100000);
};

/**
 * 获取节点名称
 *
 * @param str 节点名称
 */
export const getNodeName = function(str: string) {
  const idx = str.lastIndexOf('.') + 1;
  if (idx >= 0) return str.slice(idx);
  return str;
};
