import source from '@/assets/images/source.svg';
import process from '@/assets/images/process.svg';
import sink from '@/assets/images/sink.svg';

const JobRoleType = Object.freeze({
  source:  { value: 'source', label: '输入', bgColor: '#B581E4' },
  process: { value: 'process', label: '计算', bgColor: '#13A9FA' },
  sink:    { value: 'sink', label: '输出', bgColor: '#FE5AA8' },
  share:   { value: 'share', label: '分享', bgColor: '#FE5AA8' },
});

const JobRoleTypeValue: {
  [key in keyof typeof JobRoleType]: string;
} = Object.fromEntries(Object.entries(JobRoleType).map(x => [x[0], x[1].value])) as any;

const JobDefaultIcon = {
  source,
  process,
  sink,
};

export {
  JobRoleType,
  JobRoleTypeValue,
  JobDefaultIcon,
};
