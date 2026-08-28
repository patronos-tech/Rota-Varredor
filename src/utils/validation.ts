// Validation utilities

export interface ValidationError {
  field: string;
  message: string;
}

export class Validator {
  private errors: ValidationError[] = [];

  reset(): void {
    this.errors = [];
  }

  isValid(): boolean {
    return this.errors.length === 0;
  }

  getErrors(): ValidationError[] {
    return this.errors;
  }

  required(field: string, value: any): this {
    if (value === null || value === undefined || value === '') {
      this.errors.push({ field, message: `${field} é obrigatório` });
    }
    return this;
  }

  minValue(field: string, value: number, min: number): this {
    if (value < min) {
      this.errors.push({ field, message: `${field} deve ser no mínimo ${min}` });
    }
    return this;
  }

  maxValue(field: string, value: number, max: number): this {
    if (value > max) {
      this.errors.push({ field, message: `${field} deve ser no máximo ${max}` });
    }
    return this;
  }

  minLength(field: string, value: string, min: number): this {
    if (value.length < min) {
      this.errors.push({ field, message: `${field} deve ter no mínimo ${min} caracteres` });
    }
    return this;
  }

  email(field: string, value: string): this {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(value)) {
      this.errors.push({ field, message: `${field} deve ser um email válido` });
    }
    return this;
  }

  custom(field: string, predicate: boolean, message: string): this {
    if (!predicate) {
      this.errors.push({ field, message });
    }
    return this;
  }
}
