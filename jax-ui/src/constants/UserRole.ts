export const UserRole = Object.freeze({
  superAdmin: { label: '管理员', value: 'SA' },
  user: { label: '普通用户', value: 'U' },
});

export const UserRoleList = Object.values(UserRole);

export const UserRoleValue: {
  [key in keyof typeof UserRole]: string;
} = Object.fromEntries(Object.entries(UserRole).map(([key, { value }]) => [key, value])) as any;
