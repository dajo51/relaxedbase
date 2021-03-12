import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import SickLeave from './sick-leave';
import SickLeaveDetail from './sick-leave-detail';
import SickLeaveUpdate from './sick-leave-update';
import SickLeaveDeleteDialog from './sick-leave-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={SickLeaveUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={SickLeaveUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={SickLeaveDetail} />
      <ErrorBoundaryRoute path={match.url} component={SickLeave} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={SickLeaveDeleteDialog} />
  </>
);

export default Routes;
