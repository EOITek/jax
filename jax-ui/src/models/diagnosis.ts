export interface DiagnosisModel {
  detects: DiagnosisDetect[];
}

export interface DiagnosisDetect {
  detectId: string;
  status: string;
  errorMessage?: any;
}
