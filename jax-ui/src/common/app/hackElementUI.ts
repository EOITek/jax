import { Dropdown, InputNumber, Link, MessageBox, Slider } from 'element-ui';

(Link as any).props.underline.default = false;
(InputNumber as any).props.controlsPosition.default = 'right';
(InputNumber as any).props.max.default = Number.MAX_SAFE_INTEGER;
(InputNumber as any).props.min.default = Number.MIN_SAFE_INTEGER;
(Slider as any).props.inputControlsPosition.default = 'right';
(Dropdown as any).props.trigger.default = 'click';
MessageBox.setDefaults({ closeOnClickModal: false });
