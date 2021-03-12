import { Moment } from 'moment';
import { IEmployee } from 'app/shared/model/employee.model';

export interface IEvent {
  id?: number;
  title?: string;
  description?: string;
  location?: string;
  startDate?: string;
  endDate?: string;
  inviteOnly?: boolean;
  participant?: IEmployee;
  host?: IEmployee;
}

export const defaultValue: Readonly<IEvent> = {
  inviteOnly: false,
};
