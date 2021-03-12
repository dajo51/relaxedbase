import { Moment } from 'moment';
import { IEmployee } from 'app/shared/model/employee.model';

export interface IVacationRequest {
  id?: number;
  status?: boolean;
  startDate?: string;
  endDate?: string;
  applicant?: IEmployee;
  standIn?: IEmployee;
}

export const defaultValue: Readonly<IVacationRequest> = {
  status: false,
};
