import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput } from 'availity-reactstrap-validation';
import { ICrudGetAction, ICrudGetAllAction, ICrudPutAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { IEmployee } from 'app/shared/model/employee.model';
import { getEntities as getEmployees } from 'app/entities/employee/employee.reducer';
import { getEntity, updateEntity, createEntity, reset } from './vacation-request.reducer';
import { IVacationRequest } from 'app/shared/model/vacation-request.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IVacationRequestUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const VacationRequestUpdate = (props: IVacationRequestUpdateProps) => {
  const [applicantId, setApplicantId] = useState('0');
  const [standInId, setStandInId] = useState('0');
  const [isNew, setIsNew] = useState(!props.match.params || !props.match.params.id);

  const { vacationRequestEntity, employees, loading, updating } = props;

  const handleClose = () => {
    props.history.push('/vacation-request' + props.location.search);
  };

  const checkbox = () => {
    return (
      <AvGroup check>
        <Label id="statusLabel">
          <AvInput id="vacation-request-status" type="checkbox" className="form-check-input" name="status" />
          Status
        </Label>
      </AvGroup>
    )
  }

  useEffect(() => {
    if (isNew) {
      props.reset();
    } else {
      props.getEntity(props.match.params.id);
    }

    props.getEmployees();
  }, []);

  useEffect(() => {
    if (props.updateSuccess) {
      handleClose();
    }
  }, [props.updateSuccess]);

  const saveEntity = (event, errors, values) => {
    values.startDate = convertDateTimeToServer(values.startDate);
    values.endDate = convertDateTimeToServer(values.endDate);

    if (errors.length === 0) {
      const entity = {
        ...vacationRequestEntity,
        ...values,
      };

      if (isNew) {
        props.createEntity(entity);
      } else {
        props.updateEntity(entity);
      }
    }
  };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="relaxedbaseApp.vacationRequest.home.createOrEditLabel">Erstelle einen neuen Urlaubsantrag</h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <AvForm model={isNew ? {} : vacationRequestEntity} onSubmit={saveEntity}>
              {!isNew ? (
                <AvGroup>
                  <Label for="vacation-request-id">ID</Label>
                  <AvInput id="vacation-request-id" type="text" className="form-control" name="id" required readOnly />
                </AvGroup>
              ) : null}
              {props.vacationRequestEntity.id !== undefined? checkbox : ""}
              <AvGroup>
                <Label id="startDateLabel" for="vacation-request-startDate">
                  Startdatum
                </Label>
                <AvInput
                  id="vacation-request-startDate"
                  type="datetime-local"
                  className="form-control"
                  name="startDate"
                  placeholder={'YYYY-MM-DD HH:mm'}
                  value={isNew ? displayDefaultDateTime() : convertDateTimeFromServer(props.vacationRequestEntity.startDate)}
                />
              </AvGroup>
              <AvGroup>
                <Label id="endDateLabel" for="vacation-request-endDate">
                  Enddatum
                </Label>
                <AvInput
                  id="vacation-request-endDate"
                  type="datetime-local"
                  className="form-control"
                  name="endDate"
                  placeholder={'YYYY-MM-DD HH:mm'}
                  value={isNew ? displayDefaultDateTime() : convertDateTimeFromServer(props.vacationRequestEntity.endDate)}
                />
              </AvGroup>
              <AvGroup>
                <Label for="vacation-request-standIn">Vertretung</Label>
                <AvInput id="vacation-request-standIn" type="select" className="form-control" name="standIn.id">
                  <option value="" key="0" />
                  {employees
                    ? employees.map(otherEntity => (
                        <option value={otherEntity.id} key={otherEntity.id}>
                          {otherEntity.firstName} {otherEntity.lastName}
                        </option>
                      ))
                    : null}
                </AvInput>
              </AvGroup>
              <Button tag={Link} id="cancel-save" to="/vacation-request" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">Zurück</span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp; Speichern
              </Button>
            </AvForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

const mapStateToProps = (storeState: IRootState) => ({
  employees: storeState.employee.entities,
  vacationRequestEntity: storeState.vacationRequest.entity,
  loading: storeState.vacationRequest.loading,
  updating: storeState.vacationRequest.updating,
  updateSuccess: storeState.vacationRequest.updateSuccess,
});

const mapDispatchToProps = {
  getEmployees,
  getEntity,
  updateEntity,
  createEntity,
  reset,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(VacationRequestUpdate);
