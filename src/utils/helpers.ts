// UUID generation
import 'react-native-get-random-values';
import { v4 as uuidv4 } from 'uuid';

export function generateId(): string {
  return uuidv4();
}

// Date utilities
export function getCurrentDate(): Date {
  return new Date();
}

export function formatDate(date: Date): string {
  return date.toLocaleDateString('pt-BR');
}

export function formatDateTime(date: Date): string {
  return date.toLocaleString('pt-BR');
}

// Object utilities
export function deepClone<T>(obj: T): T {
  return JSON.parse(JSON.stringify(obj));
}

// Array utilities
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
