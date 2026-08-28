export function generateId(): string {
  return Math.random().toString(36).substring(2, 11) + Date.now().toString(36);
}

export function getCurrentDate(): Date {
  return new Date();
}

export function formatDate(date: Date): string {
  return date.toLocaleDateString('pt-BR');
}

export function formatDateTime(date: Date): string {
  return date.toLocaleString('pt-BR');
}

export function deepClone<T>(obj: T): T {
  return JSON.parse(JSON.stringify(obj));
}

export function chunk<T>(array: T[], size: number): T[][] {
  const chunks: T[][] = [];
  for (let i = 0; i < array.length; i += size) {
    chunks.push(array.slice(i, i + size));
  }
  return chunks;
}

export function unique<T>(array: T[]): T[] {
  return Array.from(new Set(array));
}
