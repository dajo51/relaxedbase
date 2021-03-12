import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import VacationRequest from './vacation-request';
import VacationRequestDetail from './vacation-request-detail';
import VacationRequestUpdate from './vacation-request-update';
import VacationRequestDeleteDialog from './vacation-request-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={VacationRequestUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={VacationRequestUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={VacationRequestDetail} />
      <ErrorBoundaryRoute path={match.url} component={VacationRequest} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={VacationRequestDeleteDialog} />
  </>
);

export default Routes;
