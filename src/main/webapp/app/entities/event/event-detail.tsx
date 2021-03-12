import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { ICrudGetAction, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './event.reducer';
import { IEvent } from 'app/shared/model/event.model';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IEventDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const EventDetail = (props: IEventDetailProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const { eventEntity } = props;
  return (
    <Row>
      <Col md="8">
        <h2>
          Event [<b>{eventEntity.id}</b>]
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="title">Title</span>
          </dt>
          <dd>{eventEntity.title}</dd>
          <dt>
            <span id="description">Description</span>
          </dt>
          <dd>{eventEntity.description}</dd>
          <dt>
            <span id="location">Location</span>
          </dt>
          <dd>{eventEntity.location}</dd>
          <dt>
            <span id="startDate">Start Date</span>
          </dt>
          <dd>{eventEntity.startDate ? <TextFormat value={eventEntity.startDate} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="endDate">End Date</span>
          </dt>
          <dd>{eventEntity.endDate ? <TextFormat value={eventEntity.endDate} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="inviteOnly">Invite Only</span>
          </dt>
          <dd>{eventEntity.inviteOnly ? 'true' : 'false'}</dd>
          <dt>Participant</dt>
          <dd>{eventEntity.participant ? eventEntity.participant.id : ''}</dd>
          <dt>Host</dt>
          <dd>{eventEntity.host ? eventEntity.host.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/event" replace color="info">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/event/${eventEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

const mapStateToProps = ({ event }: IRootState) => ({
  eventEntity: event.entity,
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(EventDetail);
