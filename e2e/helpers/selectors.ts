export function xmlContains(xml: string, value: string): boolean {
  return xml.includes(`text="${escapeXml(value)}"`) || xml.includes(`content-desc="${escapeXml(value)}"`);
}

export function escapeXml(value: string): string {
  return value.replaceAll("&", "&amp;").replaceAll("\"", "&quot;");
}

export function findNodeCenterByText(xml: string, value: string): { x: number; y: number } | null {
  const escaped = escapeXml(value).replaceAll(/[.*+?^${}()|[\]\\]/g, "\\$&");
  const pattern = new RegExp(`text="${escaped}"[\\s\\S]*?bounds="\\[(\\d+),(\\d+)\\]\\[(\\d+),(\\d+)\\]"`);
  const match = xml.match(pattern);
  if (!match) {
    return null;
  }
  const [, left, top, right, bottom] = match;
  return {
    x: Math.round((Number(left) + Number(right)) / 2),
    y: Math.round((Number(top) + Number(bottom)) / 2),
  };
}

export function findNodeCenterByResourceId(xml: string, id: string): { x: number; y: number } | null {
  const escaped = escapeXml(id).replaceAll(/[.*+?^${}()|[\]\\]/g, "\\$&");
  const pattern = new RegExp(`resource-id="[^"]*?${escaped}"[\\s\\S]*?bounds="\\[(\\d+),(\\d+)\\]\\[(\\d+),(\\d+)\\]"`);
  const match = xml.match(pattern);
  if (!match) {
    return null;
  }
  const [, left, top, right, bottom] = match;
  return {
    x: Math.round((Number(left) + Number(right)) / 2),
    y: Math.round((Number(top) + Number(bottom)) / 2),
  };
}
