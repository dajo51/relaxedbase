import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { ICrudGetAction, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './sick-leave.reducer';
import { ISickLeave } from 'app/shared/model/sick-leave.model';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface ISickLeaveDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const SickLeaveDetail = (props: ISickLeaveDetailProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const { sickLeaveEntity } = props;
  return (
    <Row>
      <Col md="8">
        <h2>
          SickLeave [<b>{sickLeaveEntity.id}</b>]
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="startDate">Start Date</span>
          </dt>
          <dd>
            {sickLeaveEntity.startDate ? <TextFormat value={sickLeaveEntity.startDate} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="endDate">End Date</span>
          </dt>
          <dd>{sickLeaveEntity.endDate ? <TextFormat value={sickLeaveEntity.endDate} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>Employee</dt>
          <dd>{sickLeaveEntity.employee ? sickLeaveEntity.employee.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/sick-leave" replace color="info">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/sick-leave/${sickLeaveEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

const mapStateToProps = ({ sickLeave }: IRootState) => ({
  sickLeaveEntity: sickLeave.entity,
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(SickLeaveDetail);
