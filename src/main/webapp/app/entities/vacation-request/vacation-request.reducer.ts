import axios from 'axios';
import { ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { IVacationRequest, defaultValue } from 'app/shared/model/vacation-request.model';

export const ACTION_TYPES = {
  FETCH_VACATIONREQUEST_LIST: 'vacationRequest/FETCH_VACATIONREQUEST_LIST',
  FETCH_VACATIONREQUEST: 'vacationRequest/FETCH_VACATIONREQUEST',
  CREATE_VACATIONREQUEST: 'vacationRequest/CREATE_VACATIONREQUEST',
  UPDATE_VACATIONREQUEST: 'vacationRequest/UPDATE_VACATIONREQUEST',
  DELETE_VACATIONREQUEST: 'vacationRequest/DELETE_VACATIONREQUEST',
  RESET: 'vacationRequest/RESET',
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IVacationRequest>,
  entity: defaultValue,
  updating: false,
  totalItems: 0,
  updateSuccess: false,
};

export type VacationRequestState = Readonly<typeof initialState>;

// Reducer

export default (state: VacationRequestState = initialState, action): VacationRequestState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.FETCH_VACATIONREQUEST_LIST):
    case REQUEST(ACTION_TYPES.FETCH_VACATIONREQUEST):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true,
      };
    case REQUEST(ACTION_TYPES.CREATE_VACATIONREQUEST):
    case REQUEST(ACTION_TYPES.UPDATE_VACATIONREQUEST):
    case REQUEST(ACTION_TYPES.DELETE_VACATIONREQUEST):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true,
      };
    case FAILURE(ACTION_TYPES.FETCH_VACATIONREQUEST_LIST):
    case FAILURE(ACTION_TYPES.FETCH_VACATIONREQUEST):
    case FAILURE(ACTION_TYPES.CREATE_VACATIONREQUEST):
    case FAILURE(ACTION_TYPES.UPDATE_VACATIONREQUEST):
    case FAILURE(ACTION_TYPES.DELETE_VACATIONREQUEST):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload,
      };
    case SUCCESS(ACTION_TYPES.FETCH_VACATIONREQUEST_LIST):
      return {
        ...state,
        loading: false,
        entities: action.payload.data,
        totalItems: parseInt(action.payload.headers['x-total-count'], 10),
      };
    case SUCCESS(ACTION_TYPES.FETCH_VACATIONREQUEST):
      return {
        ...state,
        loading: false,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.CREATE_VACATIONREQUEST):
    case SUCCESS(ACTION_TYPES.UPDATE_VACATIONREQUEST):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.DELETE_VACATIONREQUEST):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: {},
      };
    case ACTION_TYPES.RESET:
      return {
        ...initialState,
      };
    default:
      return state;
  }
};

const apiUrl = 'api/vacation-requests';

// Actions

export const getEntities: ICrudGetAllAction<IVacationRequest> = (page, size, sort) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return {
    type: ACTION_TYPES.FETCH_VACATIONREQUEST_LIST,
    payload: axios.get<IVacationRequest>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`),
  };
};

export const getEntity: ICrudGetAction<IVacationRequest> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_VACATIONREQUEST,
    payload: axios.get<IVacationRequest>(requestUrl),
  };
};

export const createEntity: ICrudPutAction<IVacationRequest> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_VACATIONREQUEST,
    payload: axios.post(apiUrl, cleanEntity(entity)),
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<IVacationRequest> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_VACATIONREQUEST,
    payload: axios.put(apiUrl, cleanEntity(entity)),
  });
  return result;
};

export const deleteEntity: ICrudDeleteAction<IVacationRequest> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_VACATIONREQUEST,
    payload: axios.delete(requestUrl),
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET,
});
