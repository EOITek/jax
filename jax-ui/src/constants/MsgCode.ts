// 状态码
export const MsgCode = Object.freeze({
  success: '0000',             // 处理成功返回码
  notUnique: '0010',           // 有重复记录返回码,{*}记录重复
  notExist: '0020',            // 不存在记录的返回码,{*}记录不存在
  notNull: '0030',             // 不允许为空的返回码
  notMatch: '0040',            // 类型不匹配的返回码或者数据输入不一致
  overLength: '0050',          // 超过最大长度的返回码
  timeOut: '0060',             // 超时
  timeError: '0061',
  addError: '0100',            // 数据库插入错误情况返回码  01**
  delError: '0200',            // 数据库删除错误情况返回码  02**
  delInUse: '0210',
  updateError: '0300',         // 数据库修改错误情况返回码  03**
  conflictNeedConfirm: '0320', // 数据冲突需要确认是否覆盖
  alreadyExist: '0021',        // 已存在 业务上的唯一
  queryError: '0400',          // 查询失败
  charsetError: '0601',        // 文件乱码无法预览
  unAuthorized: '5000',        // 没有权限的操作
  forbidden: '6000',
  dbFail: '0900',              // 数据库操作失败
});
