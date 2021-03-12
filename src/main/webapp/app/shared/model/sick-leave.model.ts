import { Moment } from 'moment';
import { IEmployee } from 'app/shared/model/employee.model';

export interface ISickLeave {
  id?: number;
  startDate?: string;
  endDate?: string;
  employee?: IEmployee;
}

export const defaultValue: Readonly<ISickLeave> = {};
