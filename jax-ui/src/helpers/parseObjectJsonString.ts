export function parseObjectJsonString(str: string): Record<string, any> {
  if (!str) return null;
  try {
    const json = JSON.parse(str);
    if (Object.prototype.toString.call(json) === '[object Object]') {
      return json;
    } else {
      return null;
    }
  } catch {
    return null;
  }
}
