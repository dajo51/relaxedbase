import { IVacationRequest } from 'app/shared/model/vacation-request.model';
import { ISickLeave } from 'app/shared/model/sick-leave.model';
import { IEvent } from 'app/shared/model/event.model';

export interface IEmployee {
  id?: number;
  firstName?: string;
  lastName?: string;
  email?: string;
  phoneNumber?: string;
  team?: string;
  position?: IEmployee;
  vacationRequests?: IVacationRequest[];
  sickLeaves?: ISickLeave[];
  events?: IEvent[];
}

export const defaultValue: Readonly<IEmployee> = {};
