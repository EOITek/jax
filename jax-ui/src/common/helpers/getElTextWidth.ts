let ctx: CanvasRenderingContext2D = null;

export function getElTextWidth(value: string, font: string) {
  if (!value) return 0;

  ctx = ctx || document.createElement('canvas').getContext('2d');
  ctx.font = font;
  return ctx.measureText(value).width;
}
