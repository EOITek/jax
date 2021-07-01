export function truncateText(text: string, maxWidth: number, fontSize = 12) {
  const ctx = ((fn: { context: CanvasRenderingContext2D | OffscreenCanvasRenderingContext2D }) => {
    if (!fn.context) {
      try {
        const canvas = new OffscreenCanvas(0, 0);
        fn.context = canvas.getContext('2d');
      } catch {
        const canvas = document.createElement('canvas');
        fn.context = canvas.getContext('2d');
      }
      fn.context.font = `san-serif ${fontSize}px`;
    }
    return fn.context;
  })(truncateText as any);

  if (ctx.measureText(text).width > maxWidth) {
    do {
      text = text.slice(0, -1);
    } while (ctx.measureText(text).width > maxWidth);
    return text + '…';
  } else {
    return text;
  }
}
